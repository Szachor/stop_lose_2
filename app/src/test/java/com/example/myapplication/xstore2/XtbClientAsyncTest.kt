package com.example.myapplication.xstore2

import junit.framework.TestCase
import org.json.JSONException
import java.util.concurrent.*

open class XtbClientAsyncTest : TestCase() {
    private var xtbService: XtbClientAsync? = null
    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        xtbService = XtbClientAsync("12263751", "xoh26561")
        xtbService!!.connectAsync().get()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    public override fun tearDown() {
        xtbService!!.disconnectAsync().get()
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetAllSymbolsAsync() {
        val response = xtbService!!.getAllSymbolsAsync()[10, TimeUnit.SECONDS]
        val testedValue = response.getJSONArray("returnData")
        val testedValue2 = testedValue.length()
        assert(testedValue2 > 10)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetSymbolAsync() {
        val symbol = "USDPLN"
        val response = xtbService!!.getSymbolAsync("USDPLN").get().getJSONObject("returnData")
        val returnedSymbol = response["symbol"].toString()
        assertEquals(returnedSymbol, symbol)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetSymbolAsyncNonExistingSymbol() {
        val response = xtbService!!.getSymbolAsync("XXX").get()
        val errorCode = response.getString("errorCode")
        val status = response.getBoolean("status")
        assertEquals("BE115", errorCode)
        assertFalse(status)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetPingAsync() {
        val status = xtbService!!.getPingAsync().get().getBoolean("status")
        assertTrue(status)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetProfitCalculationAsyncProfitBellowZero() {
        val requestedSymbol = "EURUSD"
        val response =
            xtbService!!.getProfitCalculationAsync(1.2f, 1, 1.0f, requestedSymbol, 10.0f)[1, TimeUnit.SECONDS]
        val result = response.getJSONObject("returnData")
        val profit = result.getDouble("profit")
        assertTrue(profit < 0)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetProfitCalculationAsyncProfitZero() {
        val requestedSymbol = "EURUSD"
        val response =
            xtbService!!.getProfitCalculationAsync(1.0f, 1, 1.0f, requestedSymbol, 10.0f)[1, TimeUnit.SECONDS]
        val result = response.getJSONObject("returnData")
        val profit = result.getDouble("profit")
        assertEquals(0.0, profit)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetTicketPriceStarted() {
        val isStarted = xtbService!!.subscribeGetTicketPrice("EURUSD")[1, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    // This test need to be corrected
    // For some reason this test locks :/ timeout is not working after calling get method
    // it looks like waiting for response from socket is blocking test
    // Using another thread with executor is workaround for this problem
    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testGetTickPriceResponse() {
        val requestedSymbol = "EURPLN"
        val executor = Executors.newSingleThreadExecutor()
        val completableFuture = CompletableFuture<Boolean>()
        var getResponse = false
        executor.submit {
            val response =
                xtbService!!.getTickPricesAsync(requestedSymbol)[3, TimeUnit.SECONDS]
            getResponse = response.getBoolean("status")
            completableFuture.complete(true)
        }
        completableFuture[11, TimeUnit.SECONDS]
        assertTrue(getResponse)
    }

    // This test need to be corrected
    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetTicketPriceReturnedResponse() {
        val requestedSymbol = "EURPLN"
        xtbService!!.subscribeGetTicketPrice(requestedSymbol).get()
        val queue = xtbService!!.subscriptionResponsesQueue
        val executor = Executors.newSingleThreadExecutor()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            var i = 0
            while (i < 1) {
                try {
                    val response = queue.take()
                    if (response.getJSONObject("data").getString("symbol") != requestedSymbol) {
                        throw Exception(
                            """
    Response seems not be OK. Expected command = keepAlive in response, but could not find it.
    Response: $response
    """.trimIndent()
                        )
                    }
                    i++
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            completableFuture.complete(true)
        }
        val isThreadReturningResponses = completableFuture[20, TimeUnit.SECONDS]
        assertTrue(isThreadReturningResponses)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetKeepAliveStarted() {
        val isStarted = xtbService!!.subscribeGetKeepAlive().get()
        assertTrue(isStarted)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testSubscribeGetKeepAliveReturnedTwoResponses() {
        xtbService!!.subscribeGetKeepAlive().get()
        val queue = xtbService!!.subscriptionResponsesQueue
        val executor = Executors.newSingleThreadExecutor()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            var i = 0
            while (i < 2) {
                try {
                    val response = queue.take()
                    if (response.getString("command") != "keepAlive") {
                        throw Exception(
                            """
    Response seems not be OK. Expected command = keepAlive in response, but could not find it.
    Response: $response
    """.trimIndent()
                        )
                    }
                    i++
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            completableFuture.complete(true)
        }
        val isThreadReturningResponses = completableFuture[10, TimeUnit.SECONDS]
        assertTrue(isThreadReturningResponses)
    }
}