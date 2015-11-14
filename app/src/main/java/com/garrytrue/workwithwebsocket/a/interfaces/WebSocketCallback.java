package com.garrytrue.workwithwebsocket.a.interfaces;

import java.nio.ByteBuffer;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public interface WebSocketCallback {

    void onMessageRecieve(ByteBuffer buffer);

    void onOpenConnection();

    void onCloseConnection(String reason);

    void onError(Exception ex);

    void onMessageRecieve(String msg);


}
