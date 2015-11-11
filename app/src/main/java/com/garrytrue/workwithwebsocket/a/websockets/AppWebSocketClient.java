package com.garrytrue.workwithwebsocket.a.websockets;

import android.util.Log;

import com.garrytrue.workwithwebsocket.a.interfaces.WebSocketCallback;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.ref.WeakReference;
import java.net.URI;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class AppWebSocketClient extends WebSocketClient {
    private static final String TAG = "AppWebSocketClient";
    private WeakReference<WebSocketCallback> mCallback;

    public AppWebSocketClient(URI serverURI, WebSocketCallback callback) {
        super(serverURI);
        mCallback = new WeakReference<>(callback);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen() called with: " + "handshakedata = [" + handshakedata + "]");
        if (mCallback != null) {
            Log.d(TAG, "onOpen: CONNECTION_OPEN");
            mCallback.get().gotOpenConnection();
        }
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage() called with: " + "message = [" + message + "]");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose() called with: " + "code = [" + code + "], reason = [" + reason + "], remote = [" + remote + "]");
        if (mCallback != null)
            mCallback.get().gotCloseConnection(reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError() called with: " + "ex = [" + ex + "]");
        if (mCallback != null)
            mCallback.get().gotError(ex);
    }
}
