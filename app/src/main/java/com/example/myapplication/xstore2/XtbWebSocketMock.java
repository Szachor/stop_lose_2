package com.example.myapplication.xstore2;

import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;

/*class XtbWebSocketMockSettings {

}*/


class XtbWebSocketMock extends WebSocket {
    boolean isConnected = true;
    boolean isStreamingWebSocket;
    //private XtbWebSocketMockSettings webSocketMockSettings;

    private final LinkedBlockingQueue<JSONObject> responses = new LinkedBlockingQueue<>();

    public XtbWebSocketMock(Boolean isStreamingWebSocket) {
        super();
        this.isStreamingWebSocket = isStreamingWebSocket;
    }

    @Override
    public void sendMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String command = jsonMessage.getString("command");
            switch (command) {
                case "login":
                    String loginResponse = "{\n" +
                            "\t\"status\": true,\n" +
                            "\t\"streamSessionId\": \"8469308861804289383\"\n" +
                            "}";
                    JSONObject loginResponseJson = new JSONObject(loginResponse);
                    responses.put(loginResponseJson);
                    break;
                case "getTickPrices":
                    Runnable runnable = () -> {
                        for(int i = 0;i<10;i++){
                            String getTickPricesResponse = "{\n" +
                                    "\t\"ask\": 4000.0,\n" +
                                    "\t\"askVolume\": 15000,\n" +
                                    "\t\"bid\": 4000.0,\n" +
                                    "\t\"bidVolume\": 16000,\n" +
                                    "\t\"high\": 4000.0,\n" +
                                    "\t\"level\": 0,\n" +
                                    "\t\"low\": 3500.0,\n" +
                                    "\t\"quoteId\": 0,\n" +
                                    "\t\"spreadRaw\": 0.000003,\n" +
                                    "\t\"spreadTable\": 0.00042,\n" +
                                    "\t\"symbol\": \"KOMB.CZ\",\n" +
                                    "\t\"timestamp\": 1272529161605\n" +
                                    "}";
                            SystemClock.sleep(2000);
                            try {
                                JSONObject getTickPricesResponseJson = new JSONObject(getTickPricesResponse);
                                responses.put(wrapResponse(command, getTickPricesResponseJson));
                            } catch (JSONException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new Thread(runnable).start();
                    break;

                case "getSymbol":
                    String getSymbolResponse = "{\n" +
                            "\t\"ask\": 4000.0,\n" +
                            "\t\"bid\": 4000.0,\n" +
                            "\t\"categoryName\": \"Forex\",\n" +
                            "\t\"contractSize\": 100000,\n" +
                            "\t\"currency\": \"USD\",\n" +
                            "\t\"currencyPair\": true,\n" +
                            "\t\"currencyProfit\": \"SEK\",\n" +
                            "\t\"description\": \"USD/PLN\",\n" +
                            "\t\"expiration\": null,\n" +
                            "\t\"groupName\": \"Minor\",\n" +
                            "\t\"high\": 4000.0,\n" +
                            "\t\"initialMargin\": 0,\n" +
                            "\t\"instantMaxVolume\": 0,\n" +
                            "\t\"leverage\": 1.5,\n" +
                            "\t\"longOnly\": false,\n" +
                            "\t\"lotMax\": 10.0,\n" +
                            "\t\"lotMin\": 0.1,\n" +
                            "\t\"lotStep\": 0.1,\n" +
                            "\t\"low\": 3500.0,\n" +
                            "\t\"marginHedged\": 0,\n" +
                            "\t\"marginHedgedStrong\": false,\n" +
                            "\t\"marginMaintenance\": null,\n" +
                            "\t\"marginMode\": 101,\n" +
                            "\t\"percentage\": 100.0,\n" +
                            "\t\"precision\": 2,\n" +
                            "\t\"profitMode\": 5,\n" +
                            "\t\"quoteId\": 1,\n" +
                            "\t\"shortSelling\": true,\n" +
                            "\t\"spreadRaw\": 0.000003,\n" +
                            "\t\"spreadTable\": 0.00042,\n" +
                            "\t\"starting\": null,\n" +
                            "\t\"stepRuleId\": 1,\n" +
                            "\t\"stopsLevel\": 0,\n" +
                            "\t\"swap_rollover3days\": 0,\n" +
                            "\t\"swapEnable\": true,\n" +
                            "\t\"swapLong\": -2.55929,\n" +
                            "\t\"swapShort\": 0.131,\n" +
                            "\t\"swapType\": 0,\n" +
                            "\t\"symbol\": \"USDPLN\",\n" +
                            "\t\"tickSize\": 1.0,\n" +
                            "\t\"tickValue\": 1.0,\n" +
                            "\t\"time\": 1272446136891,\n" +
                            "\t\"timeString\": \"Thu May 23 12:23:44 EDT 2013\",\n" +
                            "\t\"trailingEnabled\": true,\n" +
                            "\t\"type\": 21\n" +
                            "}";
                    JSONObject getSymbolResponseJson = new JSONObject(getSymbolResponse);
                    responses.put(wrapResponse(command, getSymbolResponseJson));
                    break;
                case "getAllSymbols":
                    String getAllSymbolsResponse = "[{\n" +
                            "\t\"ask\": 4000.0,\n" +
                            "\t\"bid\": 4000.0,\n" +
                            "\t\"categoryName\": \"Forex\",\n" +
                            "\t\"contractSize\": 100000,\n" +
                            "\t\"currency\": \"USD\",\n" +
                            "\t\"currencyPair\": true,\n" +
                            "\t\"currencyProfit\": \"SEK\",\n" +
                            "\t\"description\": \"USD/PLN\",\n" +
                            "\t\"expiration\": null,\n" +
                            "\t\"groupName\": \"Minor\",\n" +
                            "\t\"high\": 4000.0,\n" +
                            "\t\"initialMargin\": 0,\n" +
                            "\t\"instantMaxVolume\": 0,\n" +
                            "\t\"leverage\": 1.5,\n" +
                            "\t\"longOnly\": false,\n" +
                            "\t\"lotMax\": 10.0,\n" +
                            "\t\"lotMin\": 0.1,\n" +
                            "\t\"lotStep\": 0.1,\n" +
                            "\t\"low\": 3500.0,\n" +
                            "\t\"marginHedged\": 0,\n" +
                            "\t\"marginHedgedStrong\": false,\n" +
                            "\t\"marginMaintenance\": null,\n" +
                            "\t\"marginMode\": 101,\n" +
                            "\t\"percentage\": 100.0,\n" +
                            "\t\"precision\": 2,\n" +
                            "\t\"profitMode\": 5,\n" +
                            "\t\"quoteId\": 1,\n" +
                            "\t\"shortSelling\": true,\n" +
                            "\t\"spreadRaw\": 0.000003,\n" +
                            "\t\"spreadTable\": 0.00042,\n" +
                            "\t\"starting\": null,\n" +
                            "\t\"stepRuleId\": 1,\n" +
                            "\t\"stopsLevel\": 0,\n" +
                            "\t\"swap_rollover3days\": 0,\n" +
                            "\t\"swapEnable\": true,\n" +
                            "\t\"swapLong\": -2.55929,\n" +
                            "\t\"swapShort\": 0.131,\n" +
                            "\t\"swapType\": 0,\n" +
                            "\t\"symbol\": \"USDPLN\",\n" +
                            "\t\"tickSize\": 1.0,\n" +
                            "\t\"tickValue\": 1.0,\n" +
                            "\t\"time\": 1272446136891,\n" +
                            "\t\"timeString\": \"Thu May 23 12:23:44 EDT 2013\",\n" +
                            "\t\"trailingEnabled\": true,\n" +
                            "\t\"type\": 21\n" +
                            "}]";
                    JSONArray getAllSymbolsResponseJson = new JSONArray(getAllSymbolsResponse);
                    responses.put(wrapResponse(command, getAllSymbolsResponseJson));
                    break;
                case "getProfitCalculation":
                    String getProfitCalculationResponse = "{\n" +
                            "\t\"order\": 7497776,\n" +
                            "\t\"order2\": 7497777,\n" +
                            "\t\"position\": 7497776,\n" +
                            "\t\"profit\": 7076.52\n" +
                            "}";
                    JSONObject getProfitCalculationResponseJson = new JSONObject(getProfitCalculationResponse);
                    responses.put(wrapResponse(command, getProfitCalculationResponseJson));
                    break;
                case "ping":
                    String pingResponse = "{\n" +
                            "\t\"status\": true\t\n" +
                            "}";
                    JSONObject pingResponseJson = new JSONObject(pingResponse);
                    responses.put(pingResponseJson);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private JSONObject wrapResponse(String commandName, JSONObject data) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("command", commandName);
        response.put("returnData", data);
        return response;
    }

    private JSONObject wrapResponse(String commandName, JSONArray data) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("command", commandName);
        response.put("returnData", data);
        return response;
    }

    @Override
    public JSONObject getNextMessage() {
        try {
            return responses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void disconnect() {
        isConnected = false;
    }

    /*public void setWebSocketMockSettings(XtbWebSocketMockSettings webSocketMockSettings) {
        this.webSocketMockSettings = webSocketMockSettings;
    }*/
}

