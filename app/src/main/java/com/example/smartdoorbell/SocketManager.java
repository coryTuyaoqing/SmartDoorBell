package com.example.smartdoorbell;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
public class SocketManager {
    private static final String SERVER_URL = "http://192.168.0.107:80";
    private static Socket mSocket;
    private SocketManager() {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public static Socket getInstance() {
        if (mSocket == null) {
            new SocketManager();
        }
        return mSocket;
    }
}
