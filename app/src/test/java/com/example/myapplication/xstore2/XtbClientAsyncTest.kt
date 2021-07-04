package com.example.myapplication.xstore2

import junit.framework.TestCase
import org.json.JSONException
import org.junit.Assert.assertThrows
import org.junit.internal.Throwables
import java.util.concurrent.*

open class XtbClientAsyncTest : TestCase() {
    private var xtbService: XtbClientAsync? = null

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        //TODO Login and Password should be moved to another space
        xtbService = XtbClientAsync("12366113", "xoh17653")
        xtbService!!.connectAsync().get()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    public override fun tearDown() {
        xtbService!!.disconnectAsync().get()
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetAllSymbolsAsync() {
        val response = xtbService!!.getAllSymbolsAsync()[20, TimeUnit.SECONDS]

        val testedValue = response.size

        assert(testedValue > 10)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetSymbolAsync() {
        val symbol = "USDPLN"

        val response = xtbService!!.getSymbolAsync("USDPLN")[3, TimeUnit.SECONDS]
        val returnedSymbol = response.symbol

        assertEquals(returnedSymbol, symbol)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetSymbolAsyncNonExistingSymbol() {
        assertThrows(NotFoundSymbol::class.java){
            val result = xtbService!!.getSymbolAsync("XXX").exceptionally { e ->
                throw e
            }
            try {
                result[3, TimeUnit.SECONDS]
            }catch (e : ExecutionException){
                throw e.cause!!
            }
        }
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
            xtbService!!.getProfitCalculationAsync(
                1.2f,
                1,
                1.0f,
                requestedSymbol,
                10.0f
            )[1, TimeUnit.SECONDS]
        val result = response.getJSONObject("returnData")
        val profit = result.getDouble("profit")

        assertTrue(profit < 0)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetProfitCalculationAsyncProfitZero() {
        val requestedSymbol = "EURUSD"

        val response =
            xtbService!!.getProfitCalculationAsync(
                1.0f,
                1,
                1.0f,
                requestedSymbol,
                10.0f
            )[1, TimeUnit.SECONDS]
        val result = response.getJSONObject("returnData")
        val profit = result.getDouble("profit")

        assertEquals(0.0, profit)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetTicketPriceStarted() {
        val isStarted = xtbService!!.subscribeGetTicketPrice("EURUSD")[1, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testGetTickPriceResponse() {
        val requestedSymbol = "EURPLN"

        val response =
            xtbService!!.getTickPricesAsync(requestedSymbol)[3, TimeUnit.SECONDS]
        val receivedSymbol: String = response[0].symbol

        assertTrue(receivedSymbol == requestedSymbol)
    }

    // TODO this test should be rewritten
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
        val isStarted = xtbService!!.subscribeGetKeepAlive()[20, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    // TODO this test should be rewritten
    fun testGetAllSymbolsAndSubscribe10FirstAndWaitFor10Responses() {
        val allSymbols = xtbService!!.getAllSymbolsAsync().get()

        var i = 0
        while (i < 25) {
            val symbol = allSymbols!![i++].symbol
            xtbService!!.subscribeGetTicketPrice(symbol)
        }

        val queue = xtbService!!.subscriptionResponsesQueue

        val executor = Executors.newSingleThreadExecutor()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            var j = 0
            while (j++ < 10) {
                queue.take()
            }
            completableFuture.complete(true)
        }
        val isThreadReturningResponses = completableFuture[50, TimeUnit.SECONDS]
        assertTrue(isThreadReturningResponses)
    }

    // TODO this test should be rewritten
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