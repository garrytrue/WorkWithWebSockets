package com.garrytrue.workwithwebsocket.a.interfaces;

import java.nio.ByteBuffer;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public interface WebSocketCallback {

    void gotMessage(ByteBuffer buffer);

    void gotOpenConnection();

    void gotCloseConnection(String reason);

    void gotError(Exception ex);


}
