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
import com.garrytrue.workwithwebsocket.a.events.EventHaveProblem;
import com.garrytrue.workwithwebsocket.a.events.EventImageSent;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import de.greenrobot.event.EventBus;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 09.11.15.
 */
public class ClientService extends Service {
    private static final String TAG = "ClientService";
    private Uri mImageUri;
    private AppWebSocketClient mSocketClient;

    private WebSocketCallback mCallback = new WebSocketCallback() {
        @Override
        public void onMessageReceived(ByteBuffer buffer) {
            throw new UnsupportedOperationException("Not used");
        }

        @Override
        public void onOpenConnection() {
            Log.d(TAG, "onOpenConnection: Connection is OPEN");
            EventBus.getDefault().post(new EventConnectionOpen());
            messageSender.run();
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
            throw new UnsupportedOperationException("Not used");
        }
    };
    private Runnable messageSender = new Runnable() {
        @Override
        public void run() {
            try {
                byte[] imArr = getImageByteArray(mImageUri);
                if (imArr.length == 0) {
                    EventBus.getDefault().post(new EventHaveProblem(getString(R.string.err_could_not_read_image)));
                    return;
                }
                SecretKey secretKey = DecoderEncoderUtils.generateKey();
                String strKey = DecoderEncoderUtils.keyToString(secretKey);
                Log.d(TAG, "run: STR KEY " + strKey);
                mSocketClient.send(strKey);
                byte[] encodeArr = DecoderEncoderUtils.encodeByteArray(imArr, secretKey);
                mSocketClient.send(encodeArr);
            } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
                // TODO: 13.11.15 Problem with encode. Notify User.
                Log.e(TAG, "MessageSender ", ex);
                EventBus.getDefault().post(new EventHaveProblem(getString(R.string.err_could_not_encode_image)));
            } catch (IOException ex) {
                EventBus.getDefault().post(new EventHaveProblem(getString(R.string.err_could_not_read_image)));
            }
            EventBus.getDefault().post(new EventImageSent());
            stopSelf();
        }

        private byte[] getImageByteArray(Uri imageUri) throws IOException {
            assert (imageUri != null);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
    };

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        if (intent.getAction() == Constants.ACTION_START_CONNECTION && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            String address = bundle.getString(getString(R.string.bundle_key_inet_address));
            Log.d(TAG, "onStartCommand: Address " + address);
            mImageUri = Uri.parse(bundle.getString(getString(R.string
                    .bundle_key_msg_data)));
            try {
                initWebSocketClient(intent.getStringExtra(getString(R.string.bundle_key_inet_address)));
            } catch (URISyntaxException e) {
                Log.e(TAG, "onStartCommand: ", e);
                EventBus.getDefault().post(new EventHaveProblem(getString(R.string.msg_wrong_uri)));
                stopSelf();
            }
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
        Log.d(TAG, "onDestroy: ");
        BitmapFileUtils.deleteCachedFiles(this);
        if (mSocketClient != null && mSocketClient.isSocketOpen())
            mSocketClient.close();
        super.onDestroy();
    }

    private void initWebSocketClient(String address) throws URISyntaxException {
        mSocketClient = new AppWebSocketClient(new URI("ws://" + address), mCallback);
        mSocketClient.connect();
    }

}
