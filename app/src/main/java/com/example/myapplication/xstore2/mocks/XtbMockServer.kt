package com.example.myapplication.xstore2.mocks

import kotlin.collections.HashMap

object XtbMockServer {
    private var symbolsBehaviours: MutableMap<String, SymbolBehaviour> = HashMap()

    fun cleanSymbolBehaviours(){
        symbolsBehaviours = HashMap()
    }

    fun getSymbolBehaviour(symbol: String): SymbolBehaviour {
        return symbolsBehaviours[symbol]!!
    }

    fun setStartSymbolBehaviour(symbol: String, tickPrice: TickPrice) {
        if (!symbolsBehaviours.containsKey(symbol)) {
            symbolsBehaviours[symbol] = SymbolBehaviour(startTickPrice = tickPrice)
        } else {
            symbolsBehaviours[symbol]?.startTickPrice = tickPrice
        }
    }

    fun addNextSymbolsBehaviour(
        symbol: String,
        tickPrice: TickPrice,
        cycleTimeInSeconds: Int,
        numberOfUpdatesInOneCycle: Int
    ) {
        symbolsBehaviours[symbol]?.tickPriceSteps?.add(
            TickPriceStep(
                tickPrice,
                cycleTimeInSeconds,
                numberOfUpdatesInOneCycle
            )
        )
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
}