package com.garrytrue.workwithwebsocket.interfaces;

import java.nio.ByteBuffer;

public interface WebSocketCallback {

    void onMessageReceived(ByteBuffer buffer);

    void onOpenConnection();

    void onCloseConnection(String reason);

    void onError(Exception ex);

    void onMessageReceived(String msg);


}
