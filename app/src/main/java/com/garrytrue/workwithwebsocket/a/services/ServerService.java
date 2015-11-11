package com.garrytrue.workwithwebsocket.a.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.interfaces.OnTaskCompliteListener;
import com.garrytrue.workwithwebsocket.a.interfaces.WebSocketCallback;
import com.garrytrue.workwithwebsocket.a.utils.BitmapUtils;
import com.garrytrue.workwithwebsocket.a.websockets.AppWebSocketServer;

import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class ServerService extends Service {
    private static final String TAG = "ServerService";
    private String mServerAddress;
    private WebSocketServer mWebSocketServer;
    private WebSocketCallback mCallback = new WebSocketCallback() {
        @Override
        public void gotMessage(ByteBuffer buffer) {
            ByfferWorker mByfferWorker = new ByfferWorker(buffer, mTaskCompliteListener);
            mByfferWorker.run();
        }

        @Override
        public void gotOpenConnection() {

        }

        @Override
        public void gotCloseConnection(String reason) {

        }

        @Override
        public void gotError(Exception ex) {

        }
    };
    private OnTaskCompliteListener mTaskCompliteListener = new OnTaskCompliteListener() {
        @Override
        public void onTaskComplited(Uri uri) {
            Log.d(TAG, "onTaskComplited: FILE URI " + uri);
            // TODO: 11.11.15 Need notify UI about new file
        }
    };


    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.d(TAG, "onStartCommand() called with: " + "intent = [" + intent + "], flag = [" + flag + "], startId = [" + startId + "]");
        mServerAddress = intent.getStringExtra(getString(R
                .string.bundle_key_inet_address));
        Log.d(TAG, "onStartCommand: Uri for connection " + mServerAddress);
        initWebSocketServer(mServerAddress);
        return START_REDELIVER_INTENT;
    }

    private void initWebSocketServer(String address) {
        mWebSocketServer = new AppWebSocketServer(getSocketAddress(address), mCallback);
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

    private class ByfferWorker implements Runnable {
        private ByteBuffer mByteBuffer;
        private WeakReference<OnTaskCompliteListener> mTaskCompliteListenerRef;

        public ByfferWorker(ByteBuffer buffer, OnTaskCompliteListener listener) {
            mByteBuffer = buffer;
            mTaskCompliteListenerRef = new WeakReference<>(listener);
        }

        private Uri saveBitmap(ByteBuffer byteBuffer) {
            File file = new File(getApplicationContext().getFilesDir(),
                    BitmapUtils.DOWNLOADED_BMP_FILE_NAME);
            if (file.exists()) {
                Log.d(TAG, "doInBackground: file is exist");
                file.delete();
            }
            BitmapUtils.saveToFile(byteBuffer.array(), new File(getApplicationContext()
                    .getFilesDir(),
                    BitmapUtils.DOWNLOADED_BMP_FILE_NAME));
            return Uri.fromFile(file);
        }

        @Override
        public void run() {
            if (mTaskCompliteListenerRef != null) {
                mTaskCompliteListenerRef.get().onTaskComplited(saveBitmap(mByteBuffer));
            }
        }
    }
}


