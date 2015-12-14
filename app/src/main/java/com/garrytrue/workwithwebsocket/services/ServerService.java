package com.garrytrue.workwithwebsocket.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.activities.MainActivity;
import com.garrytrue.workwithwebsocket.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.events.EventHaveProblem;
import com.garrytrue.workwithwebsocket.events.EventImageReceived;
import com.garrytrue.workwithwebsocket.interfaces.OnTaskCompleteListener;
import com.garrytrue.workwithwebsocket.interfaces.WebSocketCallback;
import com.garrytrue.workwithwebsocket.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.utils.BitmapFileUtils;
import com.garrytrue.workwithwebsocket.utils.Constants;
import com.garrytrue.workwithwebsocket.utils.DecoderEncoderUtils;
import com.garrytrue.workwithwebsocket.websockets.AppWebSocketServer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import javax.crypto.SecretKey;

import de.greenrobot.event.EventBus;


public class ServerService extends Service {

    private static final String TAG = ServerService.class.getSimpleName();

    private final BufferWorker mBufferWorker = new BufferWorker();
    private AppWebSocketServer mWebSocketServer;

    private final WebSocketCallback mServerCallback = new WebSocketCallback() {
        @Override
        public void onMessageReceived(ByteBuffer buffer) {
            Log.d(TAG, "onMessageReceived: Got ByteBuffer");
            mBufferWorker.setByteBuffer(buffer);
            mBufferWorker.setTaskCompleteListener(mTaskCompleteListener);
            new Thread(mBufferWorker).start();
        }

        @Override
        public void onOpenConnection() {

        }

        @Override
        public void onCloseConnection(String reason) {
            EventBus.getDefault().post(new EventConnectionClosed(reason));

        }

        @Override
        public void onError(Exception ex) {
            EventBus.getDefault().post(new EventConnectionError(ex.getMessage()));
        }

        @Override
        public void onMessageReceived(String msg) {
            Log.d(TAG, "onMessageReceived: Got Key");
            mBufferWorker.setKey(msg);

        }
    };
    private final OnTaskCompleteListener mTaskCompleteListener = new OnTaskCompleteListener() {
        @Override
        public void onTaskCompleted(Uri uri) {
            sendNotification();
            Log.d(TAG, "onTaskCompleted: FILE URI " + uri);
            new PreferencesManager(getApplicationContext()).putDownloadedImageUri(uri);
            EventBus.getDefault().post(new EventImageReceived());
        }
    };


    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.d(TAG, "onStartCommand() called with: " + "intent = [" + intent + "], flag = [" + flag + "], startId = [" + startId + "]");
        String serverAddress = intent.getStringExtra(Constants.BUNDLE_KEY_DEVICE_IP);
        Log.d(TAG, "onStartCommand: Uri for connection " + serverAddress);
        initWebSocketServer(serverAddress);
        return START_NOT_STICKY;
    }

    private void initWebSocketServer(String address) {
        mWebSocketServer = new AppWebSocketServer(getSocketAddress(address), mServerCallback);
        mWebSocketServer.start();
    }

    private InetSocketAddress getSocketAddress(String address) {
        String[] arr = address.split(":");
        Log.d(TAG, "getSocketAddress: " + arr[0] + arr[1]);
        Integer port = Integer.parseInt(arr[1]);
        Log.d(TAG, "INET_SOCKET_ADDRESS: " + new InetSocketAddress(arr[0], port).toString());
        return new InetSocketAddress(arr[0], port);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        BitmapFileUtils.deleteCachedFiles(this);
        if (mWebSocketServer != null)
            try {
                mWebSocketServer.stop();
            } catch (IOException | InterruptedException e) {
                Log.e(TAG, "onDestroy: ", e);
            }
        super.onDestroy();
    }


    private class BufferWorker implements Runnable {
        private ByteBuffer mByteBuffer;
        private WeakReference<OnTaskCompleteListener> mTaskCompleteListenerRef;
        private String mStrKey;

        public void setByteBuffer(ByteBuffer buffer) {
            mByteBuffer = buffer;
        }

        public void setTaskCompleteListener(OnTaskCompleteListener listener) {
            mTaskCompleteListenerRef = new WeakReference<>(listener);
        }

        public void setKey(String key) {
            mStrKey = key;
        }

        public BufferWorker() {
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        private Uri saveBitmapToCache(byte[] byteArr) {
            File file = new File(getApplicationContext().getCacheDir(),
                    BitmapFileUtils.TEMP_DOWNLOADED_FILE_NAME);
            if (file.exists()) {
                Log.d(TAG, "doInBackground: file is exist");
                file.delete();
            }
            try {
                BitmapFileUtils.saveToFile(new File(getApplicationContext()
                        .getCacheDir(),
                        BitmapFileUtils.TEMP_DOWNLOADED_FILE_NAME), byteArr);
                return Uri.fromFile(file);
            }catch (IOException ex){
                EventBus.getDefault().post(new EventHaveProblem(getString(R.string.err_could_not_save_img)));
            }
            return null;
        }

        @Override
        public void run() {
            try {
                SecretKey key = DecoderEncoderUtils.keyFromString(mStrKey);
                byte[] decodedArr = DecoderEncoderUtils.decodeByteArray(mByteBuffer.array(), key);
                Uri uri = saveBitmapToCache(decodedArr);
                if (mTaskCompleteListenerRef != null) {
                    mTaskCompleteListenerRef.get().onTaskCompleted(uri);
                }
            } catch (Exception ex) {
                Log.e(TAG, "BufferWorker: ", ex);
                EventBus.getDefault().post(new EventHaveProblem(getString(R.string.err_could_not_decode_image)));
            }
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.msg_got_new_image))
                .setContentText(getString(R.string.msg_receive_image))
                .setContentIntent(prepareIntent())
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.incom_msg));
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, builder.build());
    }

    private PendingIntent prepareIntent() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_NO_CREATE
        );
    }
}


