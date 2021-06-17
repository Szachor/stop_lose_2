package com.example.myapplication.xstore2

import junit.framework.TestCase
import org.json.JSONException
import java.util.concurrent.*

open class XtbClientMockAsyncTest : TestCase() {
    private var xtbService: XtbClientAsync? = null
    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        xtbService = XtbMockClientAsync()
        (xtbService as XtbMockClientAsync).connectAsync()[10, TimeUnit.SECONDS]
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    public override fun tearDown() {
        xtbService!!.disconnectAsync()[10, TimeUnit.SECONDS]
    }

    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        JSONException::class,
        TimeoutException::class
    )
    fun testGetAllSymbolsAsync() {
        val response = xtbService!!.allSymbolsAsync[10, TimeUnit.SECONDS]
        val testedValue = response.getJSONArray("returnData")
        val testedValue2 = testedValue.length()
        assert(testedValue2 == 1)
    }

    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        JSONException::class,
        TimeoutException::class
    )
    fun testGetSymbolAsync() {
        val symbol = "USDPLN"
        val response =
            xtbService!!.getSymbolAsync("USDPLN")[10, TimeUnit.SECONDS].getJSONObject("returnData")
        val returnedSymbol = response["symbol"].toString()
        assertEquals(returnedSymbol, symbol)
    }

    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        JSONException::class,
        TimeoutException::class
    )
    fun testGetPingAsync() {
        val status = xtbService!!.pingAsync[10, TimeUnit.SECONDS].getBoolean("status")
        assertTrue(status)
    }

    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        JSONException::class,
        TimeoutException::class
    )
    fun testGetProfitCalculationAsyncProfitZero() {
        val requestedSymbol = "EURUSD"
        val response = xtbService!!.getProfitCalculationAsync(
            1.0f,
            1,
            1.0f,
            requestedSymbol,
            10.0f
        )[10, TimeUnit.SECONDS]
        val result = response.getJSONObject("returnData")
        val profit = result.getDouble("profit")
        assertEquals(7076.52, profit)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testSubscribeGetTicketPriceStarted() {
        val isStarted = xtbService!!.subscribeGetTicketPrice("PLNUSD")[10, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testSubscribeGetTicketPriceReturnedTwoResponses() {
        xtbService!!.subscribeGetTicketPrice("USDPLN")[10, TimeUnit.SECONDS]
        val queue = xtbService!!.subscriptionResponsesQueue
        val executor = Executors.newSingleThreadExecutor()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            var i = 0
            while (i < 2) {
                try {
                    val response = queue.take()
                    if (response.getString("command") != "getTickPrices") {
                        throw Exception(
                            """
    Response seems not be OK. Expected command = getTickPrices in response, but could not find it.
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