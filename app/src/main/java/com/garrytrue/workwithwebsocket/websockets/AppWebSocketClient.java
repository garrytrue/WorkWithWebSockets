package com.garrytrue.workwithwebsocket.websockets;

import android.util.Log;

import com.garrytrue.workwithwebsocket.interfaces.WebSocketCallback;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.ref.WeakReference;
import java.net.URI;

public class AppWebSocketClient extends WebSocketClient {
    private static final String TAG = AppWebSocketClient.class.getSimpleName();

    private WeakReference<WebSocketCallback> mCallback;

    public AppWebSocketClient(URI serverURI, WebSocketCallback callback) {
        super(serverURI);
        mCallback = new WeakReference<>(callback);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen() called with: " + "handshakedata = [" + handshakedata + "]");
        if (mCallback.get() != null) {
            Log.d(TAG, "onOpen: CONNECTION_OPEN");
            mCallback.get().onOpenConnection();
        }
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage() called with: " + "message = [" + message + "]");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose() called with: " + "code = [" + code + "], reason = [" + reason + "], remote = [" + remote + "]");
        if (mCallback.get() != null)
            mCallback.get().onCloseConnection(reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError() called with: " + "ex = [" + ex + "]");
        if (mCallback.get() != null)
            mCallback.get().onError(ex);
    }
    public boolean isSocketOpen(){
        return getConnection().isOpen();
    }
}
