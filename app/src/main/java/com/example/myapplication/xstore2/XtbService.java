package com.example.myapplication.xstore2;

/*
I/System.out: xapia.x-station.eu DEMO_A DEMO A 5124 5125
I/System.out: xapib.x-station.eu DEMO_B DEMO B 5124 5125

I/System.out: xapia.x-station.eu REAL_A REAL A 5112 5113
I/System.out: xapib.x-station.eu REAL_B REAL B 5112 5113

I/System.out: xapia.x-station.eu UAT_A UAT A 5116 5117
I/System.out: xapib.x-station.eu UAT_B UAT B 5116 5117
 */

import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class XtbService implements ExchangeService {
    Socket socket;

    public XtbService(){
        login();
        listenToPublicEvents();
    }

    public void login() {
        try {
            IO.Options opts;
            opts = new IO.Options();
            opts.query = "";
            opts.forceNew = true;
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            socket = IO.socket("https://ws.xtb.com/demo:5124", opts);
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void listenToPublicEvents() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("socket.on(Socket.EVENT_CONNECT");
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            System.out.println("socket.on(Socket.EVENT_DISCONNECT");
        });
        socket.emit("login","{\n" +
                "\t\"command\" : \"login\",\n" +
                "\t\"arguments\" : {\n" +
                "\t\t\"userId\" : \"12263751\",\n" +
                "\t\t\"password\": \"xoh26561\"\n" +
                "\t}\n" +
                "}", )
    }
}
