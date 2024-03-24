package com.example.smartdoorbell;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
public class SocketManager {
    private static Socket mSocket;
    private SocketManager(String SERVER_URL) {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public static Socket getInstance(String SERVER_URL) {
        if (mSocket == null) {
            new SocketManager(SERVER_URL);
        }
        return mSocket;
    }
}
