package com.example.myapplication.xstore2;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

class WebSocket {

    String webSocketEndpoint;
    int webSocketPort;

    public WebSocket(String webSocketEndpoint, int webSocketPort) {
        this.webSocketEndpoint = webSocketEndpoint;
        this.webSocketPort = webSocketPort;

        initSocketWriter();
    }

    protected WebSocket() {
    }

    private Socket socketClient;

    private InetSocketAddress _socketAddress;

    private InetSocketAddress getInetSocketAddress() {
        return _socketAddress;
    }

    private PrintStream _apiSocketWriter;

    private PrintStream getSocketWriter() {
        return _apiSocketWriter;
    }

    private void initSocketWriter() {
        _socketAddress = new InetSocketAddress(webSocketEndpoint, webSocketPort);
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        InetSocketAddress socketAddress = getInetSocketAddress();
        try {
            socketClient = socketFactory.createSocket(socketAddress.getAddress(), socketAddress.getPort());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            _apiSocketWriter = new PrintStream(socketClient.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private BufferedReader _socketReader;

    private BufferedReader getSocketReader() {
        if (_socketReader == null) {
            InputStream streamingReadStream = null;
            try {
                streamingReadStream = socketClient.getInputStream();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            _socketReader = new BufferedReader(new InputStreamReader(streamingReadStream));
        }
        return _socketReader;
    }

    public void sendMessage(String message) {
        getSocketWriter().print(message);
    }

    public JSONObject getNextMessage() throws JSONException, IOException {
        String line;
        StringBuilder response = new StringBuilder();
        line = getSocketReader().readLine();
        do {
            response.append(line);
            line = getSocketReader().readLine();
        } while (!line.equals(""));
        return new JSONObject(response.toString());
    }

    public boolean isConnected() {
        return !socketClient.isClosed();
    }

    public void disconnect() throws IOException {
        socketClient.close();
    }
}