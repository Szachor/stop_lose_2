package com.example.myapplication.xstore2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;


/*
 *
 * "customTag": "my_login_command_id"
 * At most 50 simultaneous connections from the same client address are allowed (an attempt to obtain the 51st connection returns the error EX008). If you need this rule can be lenified please contact the xStore Support Team.
 * Every new connection that fails to deliver data within one second from when it is established may be forced to close with no notification.
 * Each command invocation should not contain more than 1kB of data.
 * User should send requests in 200 ms intervals. This rule can be broken, but if it happens 6 times in a row the connection is dropped.
 *
 * */

/*interface MyInterface {
    Boolean doSomething() throws JSONException, IOException;
}*/

public class XtbServiceAsync {
    private final XtbService xtbService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService streamingExecutor = Executors.newFixedThreadPool(6);


    public XtbServiceAsync(String login, String password) {
        this.xtbService = new XtbService(login, password);
    }

    protected XtbServiceAsync(XtbService xtbService) {
        this.xtbService = xtbService;
    }

    public Future<Boolean> connectAsync() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        //MyInterface myInterface = xtbService::connect;

        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.connect());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    public Future<Boolean> disconnectAsync() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            completableFuture.complete(xtbService.disconnect());
        });

        return completableFuture;
    }

    /*
    public Future<JSONObject> runAsyncTask(){
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.getAllSymbols());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }*/

    public Future<JSONObject> getAllSymbolsAsync() {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.getAllSymbols());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    public Future<JSONObject> getSymbolAsync(String symbol) {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.getSymbol(symbol));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    public Future<JSONObject> getPingAsync() {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.getPing());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return completableFuture;
    }

    public Future<JSONObject> getProfitCalculationAsync(float closePrice, int cmd, float openPrice, String symbol, float volume) {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.getProfitCalculation(closePrice, cmd, openPrice, symbol, volume));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return completableFuture;
    }

    public Future<Boolean> subscribeGetTicketPrice(String symbol) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        streamingExecutor.submit(() -> {
            if (xtbService.isConnected()) {

            }
            xtbService.subscribeGetTicketPrices(symbol);
            xtbService.runSubscriptionStreamingReader();
            completableFuture.complete(true);
        });
        return completableFuture;
    }

    public Future<Boolean> subscribeGetKeepAlive() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        streamingExecutor.submit(() -> {
            xtbService.subscribeGetKeepAlive();
            xtbService.runSubscriptionStreamingReader();
            completableFuture.complete(true);
        });
        return completableFuture;
    }

    public LinkedBlockingQueue<JSONObject> getSubscriptionResponsesQueue() {
        return xtbService.getSubscriptionResponses();
    }

    public Future<Boolean> isConnected() {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            completableFuture.complete(xtbService.isConnected());
        });
        return completableFuture;
    }
}


class XtbService {
    final private String login;
    final private String password;

    private boolean _stopListening = false;

    // This WebSocket for main connection
    // It is used for the Request-Reply commands.
    protected WebSocket _webSocket;

    //
    protected WebSocket _streamingWebSocket;
    protected static String streamSessionId;
    private final LinkedBlockingQueue<JSONObject> subscriptionResponses = new LinkedBlockingQueue<>();

    public boolean isConnected() {
        if (getWebSocket() == null) {
            return false;
        }
        return getWebSocket().isConnected();
    }

    public XtbService(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public LinkedBlockingQueue<JSONObject> getSubscriptionResponses() {
        return subscriptionResponses;
    }

    private WebSocket getWebSocket() {
        return _webSocket;
    }


    private WebSocket getStreamingWebSocket() {
        return _streamingWebSocket;
    }

    public Boolean connect() throws JSONException {
        if (isConnected()) {
            return true;
        }

        _webSocket = new XtbWebSocket(false);
        _streamingWebSocket = new XtbWebSocket(true);

        // Login should be moved from this place somewhere else...
        this.login();
        _stopListening = false;
        return true;
    }

    public Boolean disconnect() {
        _stopListening = true;
        try {
            _webSocket.disconnect();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            _streamingWebSocket.disconnect();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }

    protected void login() throws JSONException {
        JSONObject response = processMessage(XtbServiceBodyMessageBuilder.getLoginMessage(this.login, this.password));
        streamSessionId = response.getString("streamSessionId");
    }

    private JSONObject processMessage(String message) throws JSONException {
        getWebSocket().sendMessage(message);
        JSONObject response;
        response = getResponse(getWebSocket());
        return response;
    }

    private JSONObject getResponse(WebSocket webSocket) throws JSONException {
        JSONObject response;
        try {
            response = webSocket.getNextMessage();
        } catch (JSONException exception) {
            response = new JSONObject();
            response.put("Error", "Couldn't parse response to the JSON format");
            response.put("Response", response);
            exception.printStackTrace();
        } catch (IOException exception) {
            // Thread was waiting for message, but Connection was lost.
            if(!_stopListening){
                exception.printStackTrace();
            }
            response = new JSONObject();
            response.put("Error", "Lost connection");
        }
        return new JSONObject(response.toString());
    }

    public void runSubscriptionStreamingReader() {
        Runnable runnable = () -> {
            while (!_stopListening && isConnected()) {
                JSONObject response;
                try {
                    response = getResponse(getStreamingWebSocket());
                    if (!response.has("Error")) {
                        subscriptionResponses.put(new JSONObject(response.toString()));
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }catch (InterruptedException e){
                    if(!_stopListening){
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    protected JSONObject getAllSymbols() throws JSONException {
        return processMessage(XtbServiceBodyMessageBuilder.getAllSymbolsMessage());
    }

    protected JSONObject getPing() throws JSONException {
        return processMessage(XtbServiceBodyMessageBuilder.getPingJsonMessage());
    }

    protected JSONObject getProfitCalculation(float closePrice, int cmd, float openPrice, String symbol, float volume) throws JSONException {
        return processMessage(XtbServiceBodyMessageBuilder.getProfitCalculationMessage(closePrice, cmd, openPrice, symbol, volume));
    }

    protected JSONObject getSymbol(String symbol) throws JSONException {
        return processMessage(XtbServiceBodyMessageBuilder.getSymbolMessage(symbol));
    }

    protected void subscribeGetKeepAlive() {
        WebSocket streamingWebSocket = getStreamingWebSocket();
        streamingWebSocket.sendMessage(XtbServiceBodyMessageBuilder.getKeepAliveMessage(streamSessionId));
    }

    public void subscribeGetTicketPrices(String symbol) {
        WebSocket streamingWebSocket = getStreamingWebSocket();
        streamingWebSocket.sendMessage(XtbServiceBodyMessageBuilder.getTickPricesMessage(streamSessionId, symbol));
    }
}

class XtbServiceBodyMessageBuilder {
    static final private String loginJson = "{" +
            "\"command\" : \"login\"," +
            "\"arguments\" : {" +
            "\"userId\" : \"%s\"," +
            "\"password\": \"%s\"" +
            "}" +
            "}";

    static final private String getAllSymbolsJson = "{" +
            "\"command\": \"getAllSymbols\"" +
            "}";

    static final private String getKeepAliveJson = "{" +
            "\"command\" : \"getKeepAlive\"," +
            "\"streamSessionId\" : \"%s\"" +
            "}";

    static final private String getTickPricesJson = "{" +
            "\"command\" : \"getTickPrices\"," +
            "\"streamSessionId\" : \"%s\"," +
            "\"symbol\": \"%s\"" +
            "}";

    static final private String pingJson = "{" +
            "\"command\": \"ping\"" +
            "}";

    static final private String getSymbolJson = "{" +
            "\"command\": \"getSymbol\"," +
            "\"arguments\": {" +
            "\"symbol\": \"%s\"" +
            "}" +
            "}";

    static final private String getProfitCalculationJson = "{" +
            "\"command\": \"getProfitCalculation\"," +
            "\"arguments\": {" +
            "\"closePrice\": %s," +
            "\"cmd\": %s," +
            "\"openPrice\": %s," +
            "\"symbol\": \"%s\"," +
            "\"volume\": %s" +
            "}" +
            "}";

    public static String getLoginMessage(String userId, String password) {
        return format(loginJson, userId, password);
    }

    public static String getAllSymbolsMessage() {
        return format(getAllSymbolsJson);
    }

    public static String getKeepAliveMessage(String streamSessionId) {
        return format(getKeepAliveJson, streamSessionId);
    }

    public static String getTickPricesMessage(String streamSessionId, String symbol) {
        return format(getTickPricesJson, streamSessionId, symbol);
    }

    public static String getPingJsonMessage() {
        return format(pingJson);
    }

    public static String getSymbolMessage(String symbol) {
        return format(getSymbolJson, symbol);
    }

    public static String getProfitCalculationMessage(float closePrice, int cmd, float openPrice, String symbol, float volume) {
        return format(getProfitCalculationJson, closePrice, cmd, openPrice, symbol, volume);
    }

    private static String format(String message, Object... parameters) {
        return String.format(message, parameters);
    }
}