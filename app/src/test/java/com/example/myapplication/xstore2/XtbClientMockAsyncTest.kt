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

public class XtbClientMockAsyncTest extends TestCase {

    protected XtbClientAsync xtbService;

    public void setUp() throws Exception {
        super.setUp();
        xtbService = new XtbMockClientAsync();
        xtbService.connectAsync().get(10, TimeUnit.SECONDS);
    }

    public void tearDown() throws ExecutionException, InterruptedException, TimeoutException {
        xtbService.disconnectAsync().get(10, TimeUnit.SECONDS);
    }

    public void testGetAllSymbolsAsync() throws ExecutionException, InterruptedException, JSONException, TimeoutException {
        JSONObject response = xtbService.getAllSymbolsAsync().get(10, TimeUnit.SECONDS);
        JSONArray tested_value = response.getJSONArray("returnData");
        int tested_value_2 = tested_value.length();
        assert (tested_value_2 == 1);
    }

    public void testGetSymbolAsync() throws ExecutionException, InterruptedException, JSONException, TimeoutException {
        String symbol = "USDPLN";
        JSONObject response = xtbService.getSymbolAsync("USDPLN").get(10, TimeUnit.SECONDS).getJSONObject("returnData");
        String returnedSymbol = response.get("symbol").toString();
        assertEquals(returnedSymbol, symbol);
    }

    public void testGetPingAsync() throws ExecutionException, InterruptedException, JSONException, TimeoutException {
        boolean status = xtbService.getPingAsync().get(10, TimeUnit.SECONDS).getBoolean("status");
        assertTrue(status);
    }

    public void testGetProfitCalculationAsyncProfitZero() throws ExecutionException, InterruptedException, JSONException, TimeoutException {
        String requestedSymbol = "EURUSD";
        JSONObject response = xtbService.getProfitCalculationAsync(1.0f, 1, 1.0f, requestedSymbol, 10.0f).get(10, TimeUnit.SECONDS);
        JSONObject result = response.getJSONObject("returnData");
        double profit = result.getDouble("profit");
        assertEquals(7076.52, profit);
    }

    public void testSubscribeGetTicketPriceStarted() throws ExecutionException, InterruptedException, TimeoutException {
        boolean isStarted = xtbService.subscribeGetTicketPrice("PLNUSD").get(10, TimeUnit.SECONDS);
        assertTrue(isStarted);
    }

    public void testSubscribeGetTicketPriceReturnedTwoResponses() throws ExecutionException, InterruptedException, TimeoutException {
        xtbService.subscribeGetTicketPrice("USDPLN").get(10, TimeUnit.SECONDS);
        LinkedBlockingQueue<JSONObject> queue = xtbService.getSubscriptionResponsesQueue();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            int i = 0;
            while(i<2) {
                try {
                    JSONObject response = queue.take();
                    if (!response.getString("command").equals("getTickPrices")){
                        throw new Exception("Response seems not be OK. Expected command = getTickPrices in response, but could not find it.\n" +
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
