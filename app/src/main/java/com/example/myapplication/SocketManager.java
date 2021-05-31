package com.example.myapplication;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    String token = "YOUR_TOKEN";
    String url = "YOUR_URL";


    private static SocketManager socketManger;

    Socket socket;
    Callback<Boolean> onConnect;

    public void init(Callback<Boolean> onConnect) {
        this.onConnect = onConnect;
        connectToSocket();
        listenToPublicEvents();
    }

    private void connectToSocket() {
        try {
            IO.Options opts;
            opts = new IO.Options();
            //optional parameter for authentication
            opts.query = "token=" + token;
            opts.forceNew = true;
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            socket = IO.socket(url, opts);
            socket.connect();

        } catch (URISyntaxException e) {

            throw new RuntimeException(e);
        }
    }

    private void listenToPublicEvents() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            if (onConnect != null)
                onConnect.onResult(true);
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            if (onConnect != null)
                onConnect.onResult(false);
        });
    }


    public void emit(String event, JSONObject data, Ack ack) {
        socket.emit(event, new JSONObject[]{data}, ack);
    }


    public void on(String event, Emitter.Listener em) {
        socket.on(event, em);
    }


    public static SocketManager getSocketManger() {
        if (socketManger == null) {
            socketManger = new SocketManager();
        }
        return socketManger;
    }


    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    public void onDestroy() {
        onConnect = null;
        socket.disconnect();
    }

    public interface Callback<T> {
        void onResult(T t);
    }

}