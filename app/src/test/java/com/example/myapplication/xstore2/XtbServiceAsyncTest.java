package com.example.myapplication.xstore2;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class XtbServiceAsyncTest extends TestCase {

    XtbServiceAsync xtbService;
    public void setUp() throws Exception {
        super.setUp();
        xtbService = new XtbServiceAsync("12263751", "xoh26561");
        xtbService.connectAsync();
    }

    public void tearDown() {
        xtbService.disconnectAsync();
    }

    public void testGetAllSymbolsAsync() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = xtbService.getAllSymbolsAsync().get();
        JSONArray tested_value = response.getJSONArray("returnData");
        int tested_value_2 = tested_value.length();
        String tested_value_3 = ((JSONObject)tested_value.get(0)).get("symbol").toString();
        assert(tested_value_2 > 10);
    }

    public void testGetSymbolAsync() throws ExecutionException, InterruptedException, JSONException {
        String symbol = "USDPLN";
        JSONObject response = xtbService.getSymbolAsync("USDPLN").get().getJSONObject("returnData");
        String returnedSymbol = response.get("symbol").toString();
        assertEquals(returnedSymbol, symbol);
    }

    public void testGetSymbolAsyncNonExistingSymbol() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = xtbService.getSymbolAsync("XXX").get();
        String errorCode = response.getString("errorCode");
        String errorDescription = response.getString("errorDescr");
        String status = response.getString("status");

        assertEquals(errorCode, "");
        assertEquals(status, "false");
    }

    public void testGetPingAsync() throws ExecutionException, InterruptedException {
        JSONObject response = xtbService.getPingAsync().get();
    }

    public void testGetProfitCalculationAsync() throws ExecutionException, InterruptedException {
        JSONObject response = xtbService.getProfitCalculationAsync(1.0f, 1, 1.0f, "PLNUSD", 10.0f).get();
    }

    public void testSubscribeGetTicketPrice() throws ExecutionException, InterruptedException {
        Boolean isStarted = xtbService.subscribeGetTicketPrice("PLNUSD").get();
    }

    public void testSubscribeGetKeepAlive() throws ExecutionException, InterruptedException {
        Boolean isStarted = xtbService.subscribeGetKeepAlive().get();
    }

    public void testGetSubscriptionResponsesQueue() throws ExecutionException, InterruptedException {
    }

    public void testIsConnected() throws ExecutionException, InterruptedException {
    }
}