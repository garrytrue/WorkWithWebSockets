package com.garrytrue.workwithwebsocket.a.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.websockets.AppWebSocketClient;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 09.11.15.
 */
public class ClientService extends Service {
    private static final String TAG = "ClientService";
    private String mServerAddress;
    public static final int COMMAND_SEND_MSG_TO_SERVICE = 12;
    public static final int COMMAND_RECIVE_MSG_FROM_SERVICE = 13;
    private Messenger mMessenger = new Messenger(new CommunicationHandler());

    private WebSocketClient mClient;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.d(TAG, "onStartCommand() called with: " + "intent = [" + intent + "], flag = [" + flag + "], startId = [" + startId + "]");
        mServerAddress = intent.getStringExtra(getString(R
                .string.bundle_key_inet_address));
        Log.d(TAG, "onStartCommand: Uri for connection " + mServerAddress);
        initWebSocketClient(mServerAddress);
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mMessenger.getBinder();
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

    class CommunicationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            Log.d(TAG, "handleMessage: From ClientService WHAT " + what);
            switch (what) {
                case COMMAND_SEND_MSG_TO_SERVICE:
                    String data = msg.getData().getString(getString(R.string.bundle_key_msg_data)
                            , "EMPTY_VALUE");
                    Log.d(TAG, "handleMessage: data ");
                    mClient.send(data);
                    break;
            }

        }
    }


}
