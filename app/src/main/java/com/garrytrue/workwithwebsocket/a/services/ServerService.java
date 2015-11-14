package com.garrytrue.workwithwebsocket.a.services;

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
import com.garrytrue.workwithwebsocket.a.activities.MainActivity;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.a.events.EventImageReciered;
import com.garrytrue.workwithwebsocket.a.interfaces.OnTaskCompliteListener;
import com.garrytrue.workwithwebsocket.a.interfaces.WebSocketCallback;
import com.garrytrue.workwithwebsocket.a.preference.PreferencesManager;
import com.garrytrue.workwithwebsocket.a.utils.BitmapFileUtils;
import com.garrytrue.workwithwebsocket.a.utils.DecoderEncoderUtils;
import com.garrytrue.workwithwebsocket.a.websockets.AppWebSocketServer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import javax.crypto.SecretKey;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class ServerService extends Service {
    private static final String TAG = "ServerService";
    private BufferWorker mByfferWorker = new BufferWorker();
    private  AppWebSocketServer mWebSocketServer;
    private WebSocketCallback mServerCallback = new WebSocketCallback() {
        @Override
        public void onMessageRecieve(ByteBuffer buffer) {
            Log.d(TAG, "onMessageRecieve: Got ByteBuffer");
            mByfferWorker.setByteBuffer(buffer);
            mByfferWorker.setTaskCompliteListener(mTaskCompliteListener);
            mByfferWorker.run();
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
        public void onMessageRecieve(String msg) {
            Log.d(TAG, "onMessageRecieve: Got Key");
            mByfferWorker.setKey(msg);

        }
    };
    private OnTaskCompliteListener mTaskCompliteListener = new OnTaskCompliteListener() {
        @Override
        public void onTaskComplited(Uri uri) {
            sendNotification();
            Log.d(TAG, "onTaskComplited: FILE URI " + uri);
            new PreferencesManager(getApplicationContext()).putDownloadedImageUri(uri);
            EventBus.getDefault().post(new EventImageReciered());
        }
    };


    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.d(TAG, "onStartCommand() called with: " + "intent = [" + intent + "], flag = [" + flag + "], startId = [" + startId + "]");
        String serverAddress = intent.getStringExtra(getString(R
                .string.bundle_key_inet_address));
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
        InetSocketAddress addess = new InetSocketAddress(arr[0], port);
        Log.d(TAG, "INETSOCKETADDRESS: " + addess.toString());
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
        if(mWebSocketServer != null)
            try {
                mWebSocketServer.stop();
            } catch (IOException e) {
                Log.e(TAG, "onDestroy: ", e);
            } catch (InterruptedException e) {
                Log.e(TAG, "onDestroy: ", e);
            }
        super.onDestroy();
    }


    private class BufferWorker implements Runnable {
        private ByteBuffer mByteBuffer;
        private WeakReference<OnTaskCompliteListener> mTaskCompliteListenerRef;
        private String mStrKey;

        public void setByteBuffer(ByteBuffer buffer) {
            mByteBuffer = buffer;
        }

        public void setTaskCompliteListener(OnTaskCompliteListener listener) {
            mTaskCompliteListenerRef = new WeakReference<>(listener);
        }

        public void setKey(String key) {
            mStrKey = key;
        }

        public BufferWorker() {
        }

        private Uri saveBitmapToCache(byte[] byteArr) {
            File file = new File(getApplicationContext().getCacheDir(),
                    BitmapFileUtils.TEMP_DOWNLOADED_FILE_NAME);
            if (file.exists()) {
                Log.d(TAG, "doInBackground: file is exist");
                file.delete();
            }
            BitmapFileUtils.saveToFile(new File(getApplicationContext()
                    .getCacheDir(),
                    BitmapFileUtils.TEMP_DOWNLOADED_FILE_NAME), byteArr);
            return Uri.fromFile(file);
        }

        @Override
        public void run() {
//            if (TextUtils.isEmpty(mStrKey) || mByteBuffer == null || mTaskCompliteListenerRef ==
//                    null) {
//                throw new IllegalArgumentException("Firstly set key, ByteBuffer and " +
//                        "OnTaskCompliteListener");
//            }
            try {
                SecretKey key = DecoderEncoderUtils.keyFromString(mStrKey);
                byte[] decodedArr = DecoderEncoderUtils.decodeByteArray(mByteBuffer.array(), key);
                Uri uri = saveBitmapToCache(decodedArr);
                if (mTaskCompliteListenerRef != null) {
                    mTaskCompliteListenerRef.get().onTaskComplited(uri);
                }
            } catch (Exception ex) {
                Log.e(TAG, "BufferWorker: ", ex);
                // TODO: 13.11.15 Notify User about problem with decode image
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


