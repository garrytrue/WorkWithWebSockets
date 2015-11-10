package com.garrytrue.workwithwebsocket.a.websockets;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class AppWebSocketClient extends WebSocketClient {
    private static final String TAG = "AppWebSocketClient";
    private String mMassage;

    public AppWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen() called with: " + "handshakedata = [" + handshakedata + "]");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage() called with: " + "message = [" + message + "]");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose() called with: " + "code = [" + code + "], reason = [" + reason + "], remote = [" + remote + "]");
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError() called with: " + "ex = [" + ex + "]");
    }
    public void setMsg(String msg){
        mMassage = msg;
    }
}
