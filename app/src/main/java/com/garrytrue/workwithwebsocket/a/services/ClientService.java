package com.garrytrue.workwithwebsocket.a.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionClosed;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionError;
import com.garrytrue.workwithwebsocket.a.events.EventConnectionOpen;
import com.garrytrue.workwithwebsocket.a.events.EventImageSended;
import com.garrytrue.workwithwebsocket.a.events.EventProblemParsURI;
import com.garrytrue.workwithwebsocket.a.interfaces.WebSocketCallback;
import com.garrytrue.workwithwebsocket.a.utils.BitmapFileUtils;
import com.garrytrue.workwithwebsocket.a.utils.Constants;
import com.garrytrue.workwithwebsocket.a.utils.DecoderEncoderUtils;
import com.garrytrue.workwithwebsocket.a.websockets.AppWebSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import javax.crypto.SecretKey;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 09.11.15.
 */
public class ClientService extends Service {
    private static final String TAG = "ClientService";
    public static final int EMPTY_ARRAY_LENGHT = 0;
    private Uri mImageUri;
    private AppWebSocketClient mSocketClient;

    private WebSocketCallback mCallback = new WebSocketCallback() {
        @Override
        public void gotMessage(ByteBuffer buffer) {
        }

        @Override
        public void gotOpenConnection() {
            Log.d(TAG, "gotOpenConnection: Connection is OPEN");
            EventBus.getDefault().post(new EventConnectionOpen());
            messageSender.run();
        }

        @Override
        public void gotCloseConnection(String reason) {
            EventBus.getDefault().post(new EventConnectionClosed(reason));
        }

        @Override
        public void gotError(Exception ex) {
            EventBus.getDefault().post(new EventConnectionError(ex.getMessage()));
        }

        @Override
        public void gotMessage(String msg) {

        }
    };
    private Runnable messageSender = new Runnable() {
        @Override
        public void run() {
            SecretKey key = null;
            String strKey = null;
            byte[] imArr = getImageByteArray(mImageUri);
            if (imArr.length > EMPTY_ARRAY_LENGHT) {
                try {
                    key = DecoderEncoderUtils.generateKey();
                    strKey = DecoderEncoderUtils.keyToString(key);
                    Log.d(TAG, "run: STR KEY " + strKey);
                    mSocketClient.send(strKey);
                    byte[] encodeArr = DecoderEncoderUtils.encodeByteArray(imArr, key);
                    mSocketClient.send(encodeArr);
                } catch (Exception ex) {
                    // TODO: 13.11.15 Problem with encode. Notify User.
                    Log.e(TAG, "MessageSender ", ex);
                }
            } else {
                // TODO: 13.11.15 Havent image. Notify User.
            }
            EventBus.getDefault().post(new EventImageSended());
stopSelf();
        }
    };

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        switch (intent.getAction()) {
            case Constants.ACTION_START_CONNECTION:
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String address = bundle.getString(getString(R.string.bundle_key_inet_address));
                    Log.d(TAG, "onStartCommand: Address " + address);
                    mImageUri = Uri.parse(bundle.getString(getString(R.string
                            .bundle_key_msg_data)));
                    try {
                        initWebSocketClient(intent.getStringExtra(getString(R.string.bundle_key_inet_address)));
                    } catch (URISyntaxException e) {
                        Log.e(TAG, "onStartCommand: ", e);
                        // TODO: 11.11.15 Notify UI about problem with server address
                        EventBus.getDefault().post(new EventProblemParsURI());
                    }
                }
                break;
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        BitmapFileUtils.deleteCachedFiles(this);
        if(mSocketClient != null && mSocketClient.isSocketOpen())
        mSocketClient.close();
        super.onDestroy();
    }

    private void initWebSocketClient(String address) throws URISyntaxException {
        URI uri;
        uri = new URI("ws://" + address);
        mSocketClient = new AppWebSocketClient(uri, mCallback);
        mSocketClient.connect();
    }

    private byte[] getImageByteArray(Uri imageUri) {
        assert (imageUri != null);
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
