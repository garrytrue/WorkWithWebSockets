package com.garrytrue.workwithwebsocket.a.websockets;

import android.util.Log;

import com.garrytrue.workwithwebsocket.a.interfaces.WebSocketCallback;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class AppWebSocketServer extends WebSocketServer {
    private static final String TAG = "AppWebSocketServer";
    private WeakReference<WebSocketCallback> mCallback;

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
        if (mCallback != null) {
            mCallback.get().onCloseConnection(reason);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG, "onMessage() called with: " + "conn = [" + conn + "], message = [" + message + "]");
        if (mCallback != null) {
            Log.d(TAG, "onMessage: GOT_FUCK_MSG");
            mCallback.get().onMessageRecieve(message);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        Log.d(TAG, "onMessage() SERVER called with: " + "conn = [" + conn + "], message = [" +
                data + "]");
        Log.d(TAG, "onMessage: SERVER Buffer Lenght " + data.array().length);
        if (mCallback != null) {
            Log.d(TAG, "onMessage: GOT_FUCK_MSG");
            mCallback.get().onMessageRecieve(data);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d(TAG, "onError() called with: " + "conn = [" + conn + "], ex = [" + ex + "]");
        if (mCallback != null) {
            mCallback.get().onError(ex);
        }
    }
}
