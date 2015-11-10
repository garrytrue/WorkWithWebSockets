package com.garrytrue.workwithwebsocket.a.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.utils.Constants;
import com.garrytrue.workwithwebsocket.a.websockets.AppWebSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class ClientIntentService extends IntentService {
    private static final String TAG = "ClientIntentService";
    private AppWebSocketClient mClient;
    private Uri mImageUri;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ClientIntentService() {
        super("ClientIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Constants.ACTION_START_CONNECTION:
                Bundle bundle = intent.getExtras();
                if(bundle != null) {
                    String address = bundle.getString(getString(R.string.bundle_key_inet_address));
                    Log.d(TAG, "onHandleIntent: Address " + address);
                    initWebSocketClient(intent.getStringExtra(getString(R.string.bundle_key_inet_address)));
                    mImageUri = stringToUri(bundle.getString(getString(R.string
                            .bundle_key_msg_data)));
                    Thread thread = new Thread(worker);
                    thread.start();
                }
                break;
            case Constants.ACTION_SEND_MSG:
                String data = intent.getStringExtra(getString(R.string.bundle_key_msg_data));
                Log.d(TAG, "onHandleIntent: data " + data);
                mClient.send(intent.getStringExtra(getString(R.string.bundle_key_msg_data)));
        }
    }

    private void initWebSocketClient(String address) {
        URI uri;
        try {
            uri = new URI("ws://" + address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // TODO: 09.11.15 need notify UI about problem
            return;
        }
        mClient = new AppWebSocketClient(uri);
        mClient.connect();
    }

    private Uri stringToUri(String str) {
       Uri uri = Uri.parse(str);
        return uri;
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
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    Runnable worker = new Runnable() {
        @Override
        public void run() {
            byte[] arr = getImageByteArray(mImageUri);
            if(arr != null){
                Log.d(TAG, "run: ArrayLenght "+ arr.length);
                if (mClient.getConnection().isOpen())
                    mClient.send(arr);
            }
        }
    };
}
