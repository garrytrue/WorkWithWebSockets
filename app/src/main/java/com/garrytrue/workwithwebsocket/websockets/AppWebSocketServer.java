package com.garrytrue.workwithwebsocket.websockets;

import android.util.Log;

import com.garrytrue.workwithwebsocket.interfaces.WebSocketCallback;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class AppWebSocketServer extends WebSocketServer {
    private static final String TAG = AppWebSocketServer.class.getSimpleName();

    private final  WeakReference<WebSocketCallback> mCallback;

    public AppWebSocketServer(InetSocketAddress address, WebSocketCallback callback) {
        super(address);
        mCallback = new WeakReference<>(callback);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "onOpen() called with: " + "conn = [" + conn + "], handshake = [" + handshake + "]");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "onClose() called with: " + "conn = [" + conn + "], code = [" + code + "], reason = [" + reason + "], remote = [" + remote + "]");
        if (mCallback.get() != null) {
            mCallback.get().onCloseConnection(reason);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG, "onMessage() called with: " + "conn = [" + conn + "], message = [" + message + "]");
        if (mCallback.get() != null) {
            Log.d(TAG, "onMessage: GOT_MSG");
            mCallback.get().onMessageReceived(message);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        Log.d(TAG, "onMessage() SERVER called with: " + "conn = [" + conn + "], message = [" +
                data + "]");
        Log.d(TAG, "onMessage: SERVER Buffer Lenght " + data.array().length);
        if (mCallback.get() != null) {
            Log.d(TAG, "onMessage: GOT_MSG");
            mCallback.get().onMessageReceived(data);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d(TAG, "onError() called with: " + "conn = [" + conn + "], ex = [" + ex + "]");
        if (mCallback.get() != null) {
            mCallback.get().onError(ex);
        }
    }
}
