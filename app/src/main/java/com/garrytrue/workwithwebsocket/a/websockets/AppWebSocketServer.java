package com.garrytrue.workwithwebsocket.a.websockets;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 10.11.15.
 */
public class AppWebSocketServer extends WebSocketServer {
    private static final String TAG = "AppWebSocketServer";

    public AppWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "onOpen() called with: " + "conn = [" + conn + "], handshake = [" + handshake + "]");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "onClose() called with: " + "conn = [" + conn + "], code = [" + code + "], reason = [" + reason + "], remote = [" + remote + "]");

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG, "onMessage() called with: " + "conn = [" + conn + "], message = [" + message + "]");

    }
    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        Log.d(TAG, "onMessage() called with: " + "conn = [" + conn + "], message = [" + data+ "]");
        Log.d(TAG, "onMessage: Buffer Lenght "+ data.array().length);


    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d(TAG, "onError() called with: " + "conn = [" + conn + "], ex = [" + ex + "]");

    }
}
