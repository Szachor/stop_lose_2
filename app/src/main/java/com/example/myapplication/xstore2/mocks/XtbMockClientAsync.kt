package com.example.myapplication.xstore2.mocks

import com.example.myapplication.xstore2.XtbClient
import com.example.myapplication.xstore2.XtbClientAsync
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

internal class XtbMockClientAsync : XtbClientAsync("Mock login", "Mock password") {

    override fun connectAsync(): Future<Boolean> {
        xtbClient = XtbMockClient()
        val completableFuture = CompletableFuture<Boolean>()
        //MyInterface myInterface = xtbService::connect;
        executor.submit {
            try {
                completableFuture.complete(xtbClient.connect())
            } catch (e: Exception) {
                e.printStackTrace()
                completableFuture.complete(false)
            }
        }
        return completableFuture
    }

    fun generateDefaultSymbolBehaviour(symbol: String = "EURPLN", cycleTimeInSeconds: Int = 10, numberOfUpdatesInOneCycle: Int = 100) {
        val startTick = TickPrice(10.1, 100, 9.9, 100)
        setStartSymbolBehaviour(symbol, tickPrice = startTick)

        var nextTick = TickPrice(20.0, 1000, 20.0, 1000)
        addNextSymbolsBehaviour(
            symbol,
            tickPrice = nextTick,
            cycleTimeInSeconds = cycleTimeInSeconds,
            numberOfUpdatesInOneCycle = numberOfUpdatesInOneCycle
        )

        nextTick = TickPrice(50.0, 1000, 50.0, 1000)
        addNextSymbolsBehaviour(
            symbol,
            tickPrice = nextTick,
            cycleTimeInSeconds = cycleTimeInSeconds,
            numberOfUpdatesInOneCycle = numberOfUpdatesInOneCycle
        )
    }

    fun setStartSymbolBehaviour(
        symbol: String,
        tickPrice: TickPrice
    ): Future<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            (xtbClient as XtbMockClient).setStartSymbolBehaviour(symbol, tickPrice)
            completableFuture.complete(true)
        }
        return completableFuture
    }

    fun addNextSymbolsBehaviour(
        symbol: String,
        tickPrice: TickPrice,
        cycleTimeInSeconds: Int,
        numberOfUpdatesInOneCycle: Int
    ): Future<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            (xtbClient as XtbMockClient).addNextSymbolsBehaviour(
                symbol,
                tickPrice,
                cycleTimeInSeconds,
                numberOfUpdatesInOneCycle
            )
            completableFuture.complete(true)
        }
        return completableFuture
    }
}

internal class XtbMockClient : XtbClient("Mock login", "Mock password") {
    private val streamingWebSocketMock: XtbWebSocketMock
        get() {
            return streamingWebSocket as XtbWebSocketMock
        }

    override fun connect(): Boolean {
        if (isConnected) {
            return true
        }
        webSocket = XtbWebSocketMock()
        streamingWebSocket = XtbWebSocketMock()

        // TODO Login should be moved from this place somewhere else...
        login()
        // TODO Check _stopListening, rewrite this a little bit
        stopListening = false
        return true
    }

    fun addNextSymbolsBehaviour(
        symbol: String,
        tickPrice: TickPrice,
        cycleTimeInSeconds: Int,
        numberOfUpdatesInOneCycle: Int
    ) {
        this.streamingWebSocketMock.xtbWebSocketMockSettings.addNextSymbolsBehaviour(
            symbol,
            tickPrice,
            cycleTimeInSeconds,
            numberOfUpdatesInOneCycle
        )
    }

    fun setStartSymbolBehaviour(
        symbol: String,
        tickPrice: TickPrice
    ) {
        this.streamingWebSocketMock.xtbWebSocketMockSettings.setStartSymbolBehaviour(
            symbol,
            tickPrice
        )
    }
}