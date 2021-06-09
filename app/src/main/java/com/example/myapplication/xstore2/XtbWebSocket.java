package com.example.myapplication.xstore2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

class XtbWebSocket extends WebSocket {
/*
I/System.out: xapia.x-station.eu DEMO_A DEMO A 5124 5125
I/System.out: xapib.x-station.eu DEMO_B DEMO B 5124 5125
wss://ws.xtb.com/demo

I/System.out: xapia.x-station.eu REAL_A REAL A 5112 5113
I/System.out: xapib.x-station.eu REAL_B REAL B 5112 5113

I/System.out: xapia.x-station.eu UAT_A UAT A 5116 5117
I/System.out: xapib.x-station.eu UAT_B UAT B 5116 5117
 */

    private static final int _webSocketPort = 5124;
    private static final int _webSocketStreamingPort = 5125;
    private static final String _webSocketEndpoint = "xapia.x-station.eu";

    public XtbWebSocket(Boolean isStreamingWebSocket) {
        super(_webSocketEndpoint, isStreamingWebSocket ? _webSocketStreamingPort : _webSocketPort);
    }
}

class WebSocket {

    public WebSocket(String webSocketEndpoint, int webSocketPort) {
        _socketAddress = new InetSocketAddress(webSocketEndpoint, webSocketPort);
        initSocketWriter();
    }

    private Socket socketClient;

    private final InetSocketAddress _socketAddress;

    private InetSocketAddress getInetSocketAddress() {
        return _socketAddress;
    }

    private PrintStream _apiSocketWriter;

    private PrintStream getSocketWriter() {
        return _apiSocketWriter;
    }

    private void initSocketWriter() {
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

    public BufferedReader getSocketReader() {
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
}