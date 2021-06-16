package com.example.myapplication.xstore2;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class XtbClientAsyncTest extends TestCase {

    protected XtbClientAsync xtbService;

    public void setUp() throws Exception {
        super.setUp();
        xtbService = new XtbClientAsync("12263751", "xoh26561");
        xtbService.connectAsync().get();
    }

    public void tearDown() throws ExecutionException, InterruptedException {
        xtbService.disconnectAsync().get();
    }

    public void testGetAllSymbolsAsync() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = xtbService.getAllSymbolsAsync().get();
        JSONArray tested_value = response.getJSONArray("returnData");
        int tested_value_2 = tested_value.length();
        assert (tested_value_2 > 10);
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
        boolean status = response.getBoolean("status");

        assertEquals("BE115", errorCode);
        assertFalse(status);
    }

    public void testGetPingAsync() throws ExecutionException, InterruptedException, JSONException {
        boolean status = xtbService.getPingAsync().get().getBoolean("status");
        assertTrue(status);
    }

    public void testGetProfitCalculationAsyncProfitBellowZero() throws ExecutionException, InterruptedException, JSONException {
        String requestedSymbol = "EURUSD";
        JSONObject response = xtbService.getProfitCalculationAsync(1.2f, 1, 1.0f, requestedSymbol, 10.0f).get();
        JSONObject result = response.getJSONObject("returnData");
        double profit = result.getDouble("profit");
        assertTrue(profit < 0);
    }

    public void testGetProfitCalculationAsyncProfitZero() throws ExecutionException, InterruptedException, JSONException {
        String requestedSymbol = "EURUSD";
        JSONObject response = xtbService.getProfitCalculationAsync(1.0f, 1, 1.0f, requestedSymbol, 10.0f).get();
        JSONObject result = response.getJSONObject("returnData");
        double profit = result.getDouble("profit");
        assertEquals(0.0, profit);
    }

    public void testSubscribeGetTicketPriceStarted() throws ExecutionException, InterruptedException {
        boolean isStarted = xtbService.subscribeGetTicketPrice("PLNUSD").get();
        assertTrue(isStarted);
    }

    public void testSubscribeGetKeepAliveStarted() throws ExecutionException, InterruptedException {
        boolean isStarted = xtbService.subscribeGetKeepAlive().get();
        assertTrue(isStarted);
    }

    public void testSubscribeGetKeepAliveReturnedTwoResponses() throws ExecutionException, InterruptedException, TimeoutException {
        xtbService.subscribeGetKeepAlive().get();
        LinkedBlockingQueue<JSONObject> queue = xtbService.getSubscriptionResponsesQueue();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            int i = 0;
            while(i<2) {
                try {
                    JSONObject response = queue.take();
                    if (!response.getString("command").equals("keepAlive")){
                        throw new Exception("Response seems not be OK. Expected command = keepAlive in response, but could not find it.\n" +
                                "Response: "+ response.toString());
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            completableFuture.complete(true);
        });

        boolean isThreadReturningResponses = completableFuture.get(10, TimeUnit.SECONDS);
        assertTrue(isThreadReturningResponses);
    }
}
