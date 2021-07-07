package com.example.myapplication.xstore2

import junit.framework.TestCase
import org.json.JSONException
import org.junit.Assert.assertThrows
import java.lang.Thread.sleep
import java.util.concurrent.*

open class XtbClientAsyncTest : TestCase() {
    private lateinit var xtbService: XtbClientAsync
    //TODO Login and Password should be moved to another space
    private val login: String = "12366113"
    private val password: String = "xoh17653"

    private val shortTaskTimeout: Long = 1
    private val mediumTaskTimeout: Long = 7
    private val longTaskTimeout: Long = 30


    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        xtbService = XtbClientAsync()
        xtbService!!.connectAsync(login, password, connectionType = ConnectionType.TEST)[5, TimeUnit.SECONDS]
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    public override fun tearDown() {
        xtbService!!.disconnectAsync()[shortTaskTimeout, TimeUnit.SECONDS]
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetAllSymbolsAsync() {
        val response = xtbService!!.getAllSymbolsAsync()[longTaskTimeout, TimeUnit.SECONDS]

        val testedValue = response.size

        assert(testedValue > 10)
    }

    @Throws(ExecutionException::class, InterruptedException::class, JSONException::class)
    fun testGetSymbolAsync() {
        val symbol = "USDPLN"

        val response = xtbService!!.getSymbolAsync("USDPLN")[mediumTaskTimeout, TimeUnit.SECONDS]
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
        val status = xtbService!!.getPingAsync()[shortTaskTimeout, TimeUnit.SECONDS].getBoolean("status")
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

    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetTicketPriceReturnedResponse() {
        val requestedSymbol = "EURPLN"
        xtbService!!.subscribeGetTicketPrice(requestedSymbol)[shortTaskTimeout, TimeUnit.SECONDS]
        val queue = xtbService.responsesQueueForTicketPrices

        val numberOfMinimalStreamResponses = 2

        var numberOfSecondsFromStartListening = 0
        while(queue.size < numberOfMinimalStreamResponses && numberOfSecondsFromStartListening++ < longTaskTimeout){
            sleep(1000)
        }

        assertTrue(queue.size>=numberOfMinimalStreamResponses)
        assertTrue(queue.take().symbol == requestedSymbol)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun testSubscribeGetKeepAliveStarted() {
        val isStarted = xtbService.subscribeGetKeepAlive()[5, TimeUnit.SECONDS]
        assertTrue(isStarted)
    }

    @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
    fun testSubscribeGetKeepAliveReturnedTwoResponses() {
        xtbService.subscribeGetKeepAlive()[shortTaskTimeout, TimeUnit.SECONDS]
        val queue = xtbService.responsesQueueForKeepAlive

        val numberOfMinimalStreamResponses = 2

        var numberOfSecondsFromStartListening = 0
        while(queue.size < numberOfMinimalStreamResponses && numberOfSecondsFromStartListening++ < mediumTaskTimeout){
            sleep(1000)
        }

        assertTrue(queue.size>=numberOfMinimalStreamResponses)
    }
}