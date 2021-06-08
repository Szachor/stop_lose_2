package com.example.myapplication.xstore2;/*
I/System.out: xapia.x-station.eu DEMO_A DEMO A 5124 5125
I/System.out: xapib.x-station.eu DEMO_B DEMO B 5124 5125
wss://ws.xtb.com/demo

I/System.out: xapia.x-station.eu REAL_A REAL A 5112 5113
I/System.out: xapib.x-station.eu REAL_B REAL B 5112 5113

I/System.out: xapia.x-station.eu UAT_A UAT A 5116 5117
I/System.out: xapib.x-station.eu UAT_B UAT B 5116 5117
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import kotlin.NotImplementedError;

public class XtbAsyncService {
    private final XtbService xtbService;

    public XtbAsyncService(String login, String password) {
        this.xtbService = new XtbService(login, password);
    }

    private boolean isWorking = false;

    private void AssertWorking() {
        assert !isWorking;
        isWorking = true;
    }

    private void StopWorking() {
        isWorking = false;
    }

    public Future<Boolean> connectAsync() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            completableFuture.complete(xtbService.connect());
        });

        return completableFuture;
    }

    public Future<Boolean> disconnectAsync() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            completableFuture.complete(xtbService.disconnect());
        });

        return completableFuture;
    }

    public Future<JSONObject> getAllSymbolsAsync() {
        AssertWorking();

        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            completableFuture.complete(xtbService.getAllSymbols());
        });

        StopWorking();
        return completableFuture;
    }

    public Future<JSONObject> getSymbolAsync(String symbol) {
        AssertWorking();

        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            completableFuture.complete(xtbService.getSymbol(symbol));
        });

        StopWorking();
        return completableFuture;
    }


    public Future<JSONObject> getProfitCalculationAsync(float closePrice, int cmd, float openPrice, String symbol, float volume) {
        AssertWorking();

        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            completableFuture.complete(xtbService.getProfitCalculation(closePrice, cmd, openPrice, symbol, volume));
        });

        StopWorking();
        return completableFuture;
    }

    public Future<Boolean> subscribeGetTicketPrice(String symbol) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            xtbService.subscribeGetTicketPrices(symbol);
            xtbService.runSubscriptionStreamingReader();
            completableFuture.complete(true);
        });
        return completableFuture;
    }

    public Future<Boolean> subscribeGetKeepAlive() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            xtbService.getKeepAlive();
            xtbService.runSubscriptionStreamingReader();
            completableFuture.complete(true);
        });
        return completableFuture;
    }

    public LinkedBlockingQueue<JSONObject> getSubscriptionResponses() {
        return xtbService.getSubscriptionResponses();
    }
}


class WebSocket {
    private final int _webSocketPort = 5124;
    private final int _webSocketStreamingPort = 5125;
    private final String _webSocketEndpoint = "xapia.x-station.eu";

    private final int webSocketPort;

    public WebSocket(int webSocketPort) {
        this.webSocketPort = webSocketPort;
    }

    private Socket socketClient;

    private InetSocketAddress _socketAddress;

    private InetSocketAddress getInetSocketAddress() {
        if (_socketAddress == null) {
            _socketAddress = new InetSocketAddress(_webSocketEndpoint, webSocketPort);
        }
        return _socketAddress;
    }

    private PrintStream _apiSocketWriter;

    private PrintStream getSocketWriter() {
        if (_apiSocketWriter == null) {
            try {
                initSocketWriter();
            } catch (Exception ex) {
                throw new NotImplementedError();
            }
        }
        return _apiSocketWriter;
    }

    private void initSocketWriter() throws IOException {
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        InetSocketAddress socketAddress = getInetSocketAddress();
        socketClient = socketFactory.createSocket(socketAddress.getAddress(), socketAddress.getPort());
        _apiSocketWriter = new PrintStream(socketClient.getOutputStream());
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

class XtbService {
    protected static String streamSessionId;


    final private String login;
    final private String password;

    private LinkedBlockingQueue<JSONObject> subscriptionResponses = new LinkedBlockingQueue<>();

    public LinkedBlockingQueue<JSONObject> getSubscriptionResponses() {
        return subscriptionResponses;
    }

    public void setSubscriptionResponses(LinkedBlockingQueue<JSONObject> subscriptionResponses) {
        this.subscriptionResponses = subscriptionResponses;
    }

    private boolean isLogged() {
        return _webSocket != null;
    }

    private WebSocket _webSocket;

    private WebSocket getWebSocket() {
        if (_webSocket == null) {
            _webSocket = new WebSocket(5124);
        }
        return _webSocket;
    }

    private WebSocket _streamingWebSocket;

    private WebSocket getStreamingWebSocket() {
        if (_streamingWebSocket == null) {
            _streamingWebSocket = new WebSocket(5125);
        }
        return _streamingWebSocket;
    }

    private void login() {
        WebSocket webSocket = getWebSocket();
        JSONObject response = processMessage(loginJson, (Object) login, (Object) password);
        try {
            streamSessionId = response.getString("streamSessionId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject processMessage(String message, Object... parameters) {
        String messageWitParameters = String.format(message, parameters);
        return processMessage(messageWitParameters);
    }

    private JSONObject processMessage(String message) {
        if (!isLogged()) {
            login();
        }
        String line = "";
        JSONObject response = null;

        getWebSocket().sendMessage(message);
//        while (!line.equals("")) {
//        }
        try {
            line = getWebSocket().getSocketReader().readLine();
            response = new JSONObject(line);
            //System.out.println(response);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        while (!line.equals("")) {
            try {
                line = getWebSocket().getSocketReader().readLine();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return response;
    }

    private String getResponse(WebSocket webSocket) {
        String line = null;
        try {
            line = webSocket.getSocketReader().readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return line;
    }

    protected void getKeepAlive() {
        WebSocket streamingWebSocket = getStreamingWebSocket();
        String getKeepAliveWithSessionIdJson = String.format(getKeepAliveJson, streamSessionId);
        streamingWebSocket.sendMessage(getKeepAliveWithSessionIdJson);
    }

    public void subscribeGetTicketPrices(String symbol) {
        WebSocket streamingWebSocket = getStreamingWebSocket();
        String getTickPricesToSend = String.format(getTickPrices, streamSessionId, symbol);
        streamingWebSocket.sendMessage(getTickPricesToSend);
    }

    boolean isStreamingReaderRunning = false;

    public void runSubscriptionStreamingReader() {
        if (!isStreamingReaderRunning) {
            isStreamingReaderRunning = true;

            Runnable runnable = () -> {
                String line;
                StringBuilder response;

                while (isStreamingReaderRunning) {
                    line = "Start";
                    response = new StringBuilder();
                    while (!line.equals("")) {
                        try {
                            line = getStreamingWebSocket().getSocketReader().readLine();
                            response.append(line);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                    try {
                        subscriptionResponses.put(new JSONObject(response.toString()));

                        String threadResponse = "<---------------------------------------------------------------------->\n" +
                                "<---------------------------Thread logs----------------------------------->\n" +
                                "This is response from Thread\n" + response.toString() +
                                "\n<---------------------------Thread logs-------------------------------->\n" +
                                "<---------------------------------------------------------------------->\n";
                        System.out.println(threadResponse);
                    } catch (InterruptedException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(runnable).start();
        }
    }

    public void stopStreamingReader() {
        isStreamingReaderRunning = false;
    }

    public XtbService(String login, String password) {
        this.login = login;
        this.password = password;
    }

    protected JSONObject getProfitCalculation(float closePrice, int cmd, float openPrice, String symbol, float volume) {
        String message = XtbServiceMethodsContent.getGetProfitCalculationMessage(closePrice, cmd, openPrice, symbol, volume);
        return processMessage(getProfitCalculationJson, (Object) closePrice, (Object) cmd, (Object) openPrice, (Object) symbol, (Object) volume);
    }

    protected JSONObject getSymbol(String symbol) {
        return processMessage(getSymbolJson, (Object) symbol);
    }

    protected JSONObject getAllSymbols() {
        return processMessage(getAllSymbolsJson);
    }

    public Boolean connect() {
        _webSocket = new WebSocket(5124);
        _streamingWebSocket = new WebSocket(5125);

    }
}

class XtbServiceMethodsContent {
    static final private String loginJson = "{\n" +
            "\t\"command\" : \"login\",\n" +
            "\t\"arguments\" : {\n" +
            "\t\t\"userId\" : \"%s\",\n" +
            "\t\t\"password\": \"%s\"\n" +
            "\t}\n" +
            "}";

    static final private String getAllSymbolsJson = "{\n" +
            "\t\"command\": \"getAllSymbols\"\n" +
            "}";

    static final private String getKeepAliveJson = "{\n" +
            "\t\"command\" : \"getKeepAlive\",\n" +
            "\t\"streamSessionId\" : \"%s\"\n" +
            "}\n";

    static final private String getTickPricesJson = "{\n" +
            "\t\"command\" : \"getTickPrices\",\n" +
            "\t\"streamSessionId\" : \"%s\",\n" +
            "\t\"symbol\": \"%s\"\n" +
            "}";

    static final private String pingJson = "{\n" +
            "\t\"command\": \"ping\"\n" +
            "}";

    static final private String getSymbolJson = "{\n" +
            "\t\"command\": \"getSymbol\",\n" +
            "\t\"arguments\": {\n" +
            "\t\t\"symbol\": \"%s\"\n" +
            "\t}\n" +
            "}";


    static final private String getProfitCalculationJson = "{\n" +
            "\t\"command\": \"getProfitCalculation\",\n" +
            "\t\"arguments\": {\n" +
            "\t\t\"closePrice\": %s,\n" +
            "\t\t\"cmd\": %s,\n" +
            "\t\t\"openPrice\": %s,\n" +
            "\t\t\"symbol\": \"%s\",\n" +
            "\t\t\"volume\": %s\n" +
            "\t}\n" +
            "}";


    public static String getLoginMessage() {
        return loginJson;
    }

    public static String getGetAllSymbolsMessage() {
        return getAllSymbolsJson;
    }

    public static String getGetKeepAliveMessage() {
        return getKeepAliveJson;
    }

    public static String getGetTickPricesMessage() {
        return getTickPricesJson;
    }

    public static String getPingJsonMessage() {
        return pingJson;
    }

    public static String getGetSymbolnMessage() {
        return getSymbolJson;
    }

    public static String getGetProfitCalculationMessage(float closePrice, int cmd, float openPrice, String symbol, float volume) {
        String message = format(getProfitCalculationJson, ());
        return getProfitCalculationJson;
    }



    private static class XTBMessageBuilder {
        static final private String baseMessage = "{\n" +
                "\t\"command\": \"getSymbol\",\n" +
                "\t\"arguments\": {\n" +
                "\t\t\"symbol\": \"%s\"\n" +
                "\t}\n" +
                "}";
        JSONObject messageJson;

        public XTBMessageBuilder(String command, String sessionId, Object... args) throws JSONException {
            this(command, args);


            if (args.length > 0) {

            }
        }

        public XTBMessageBuilder(String command, Object... args) throws JSONException {
            this(command);
            if (args.length > 0) {
                messageJson.put("arguments", "%s");
                for (Object arg : args) {

                }
            }
        }

        public XTBMessageBuilder(String command) throws JSONException {
            messageJson = new JSONObject();
            messageJson.put("command", "%s");

        }

    }

    private static String format(message, parameters) {

    }
}