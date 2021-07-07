package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.mocks.XtbMockServer
import junit.framework.TestCase
import org.json.JSONException
import java.util.concurrent.*

open class XtbClientMockAsyncTest : TestCase() {
    private lateinit var xtbClientAsync: XtbClientAsync

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        xtbClientAsync = XtbClientAsync()
        xtbClientAsync.connectAsync("", "", ConnectionType.MOCK)[5, TimeUnit.SECONDS]
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    public override fun tearDown() {
        XtbMockServer.cleanSymbolBehaviours()
        xtbClientAsync.disconnectAsync()[10, TimeUnit.SECONDS]
    }

    @Throws(
        ExecutionException::class,
        InterruptedException::class,
        JSONException::class,
        TimeoutException::class
    )
    fun testGetAllSymbolsAsync() {
        val response = xtbClientAsync.getAllSymbolsAsync()[10, TimeUnit.SECONDS]
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
            xtbClientAsync.getSymbolAsync("USDPLN")[10, TimeUnit.SECONDS]
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
        val status = xtbClientAsync.getPingAsync()[10, TimeUnit.SECONDS].getBoolean("status")
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
        val response = xtbClientAsync.getProfitCalculationAsync(
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
        XtbMockServer.generateDefaultSymbolBehaviour(
            "PLNUSD",
            cycleTimeInSeconds = 10,
            numberOfUpdatesInOneCycle = 100
        )
        val isStarted = xtbClientAsync.subscribeGetTicketPrice("PLNUSD")[10, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetTicketPriceReturnedResponse() {
        XtbMockServer.generateDefaultSymbolBehaviour(
            "PLNUSD",
            cycleTimeInSeconds = 10,
            numberOfUpdatesInOneCycle = 3
        )

        val requestedSymbol = "PLNUSD"
        xtbClientAsync.subscribeGetTicketPrice(requestedSymbol)[3, TimeUnit.SECONDS]
        val queue = xtbClientAsync.responsesQueueForTicketPrices
        val numberOfMinimalStreamResponses = 2
        var numberOfSecondsFromStartListening = 0

        while(queue.size < numberOfMinimalStreamResponses && numberOfSecondsFromStartListening++ < 20){
            Thread.sleep(1000)
        }

        assertTrue(queue.size>=numberOfMinimalStreamResponses)
        assertTrue(queue.take().symbol == requestedSymbol)
    }
}