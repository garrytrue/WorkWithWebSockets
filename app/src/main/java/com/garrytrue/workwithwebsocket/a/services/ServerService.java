package com.garrytrue.workwithwebsocket.a.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.garrytrue.workwithwebsocket.R;
import com.garrytrue.workwithwebsocket.a.websockets.AppWebSocketServer;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class ServerService extends Service {
    private static final String TAG = "ServerService";
    private String mServerAddress;
    private WebSocketServer mWebSocketServer;

    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.d(TAG, "onStartCommand() called with: " + "intent = [" + intent + "], flag = [" + flag + "], startId = [" + startId + "]");
        mServerAddress = intent.getStringExtra(getString(R
                .string.bundle_key_inet_address));
        Log.d(TAG, "onStartCommand: Uri for connection " + mServerAddress);
        initWebSocketServer(mServerAddress);
        return START_REDELIVER_INTENT;
    }

    private void initWebSocketServer(String address) {
        mWebSocketServer = new AppWebSocketServer(getSocketAddress(address));
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
}
