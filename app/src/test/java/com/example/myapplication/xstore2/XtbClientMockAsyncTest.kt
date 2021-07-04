package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.mocks.XtbMockClientAsync
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
        val response = xtbService!!.getAllSymbolsAsync()[10, TimeUnit.SECONDS]
        val numberOfReturnedSymbols = response.size
        assertEquals(3, numberOfReturnedSymbols)
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
            xtbService!!.getSymbolAsync("USDPLN")[10, TimeUnit.SECONDS]
        val returnedSymbol = response.symbol
        assertEquals(returnedSymbol, symbol)
    }

    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        JSONException::class,
        TimeoutException::class
    )
    fun testGetPingAsync() {
        val status = xtbService!!.getPingAsync()[10, TimeUnit.SECONDS].getBoolean("status")
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
        assertEquals(-200000.0, profit)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testSubscribeGetTicketPriceStarted() {
        (xtbService as XtbMockClientAsync).generateDefaultSymbolBehaviour(
            "PLNUSD",
            cycleTimeInSeconds = 10,
            numberOfUpdatesInOneCycle = 100
        )
        val isStarted = xtbService!!.subscribeGetTicketPrice("PLNUSD")[10, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testSubscribeGetTicketPriceReturnedTwoResponses() {
        (xtbService as XtbMockClientAsync).generateDefaultSymbolBehaviour(
            "PLNUSD",
            cycleTimeInSeconds = 10,
            numberOfUpdatesInOneCycle = 3
        )
        xtbService!!.subscribeGetTicketPrice("PLNUSD")[10, TimeUnit.SECONDS]
        val queue = xtbService!!.subscriptionResponsesQueue
        val executor = Executors.newSingleThreadExecutor()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            var i = 0
            while (i < 4) {
                try {
                    val response = queue.take()
                    if (response.getJSONObject("returnData").getString("symbol") != "PLNUSD") {
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
        val isThreadReturningResponses = completableFuture[1088, TimeUnit.SECONDS]
        assertTrue(isThreadReturningResponses)
    }
}