package com.example.myapplication.xstore2.mocks

import java.util.*


class TickPrice(
    var ask: Double,
    var askVolume: Int,
    var bid: Double,
    var bidVolume: Int
) {
    constructor(startTickPrice: TickPrice, nextTickPrice: TickPrice, percentage: Double) : this(
        getValue(startTickPrice.ask, nextTickPrice.ask, percentage),
        getValue(startTickPrice.askVolume, nextTickPrice.askVolume, percentage),
        getValue(startTickPrice.bid, nextTickPrice.bid, percentage),
        getValue(startTickPrice.bidVolume, nextTickPrice.bidVolume, percentage)
    )

    companion object {
        fun getValue(v1: Double, v2: Double, percentage: Double): Double {
            return v1 + (v2 - v1) * percentage
        }

        fun getValue(v1: Int, v2: Int, percentage: Double): Int {
            return (v1 + (v2 - v1) * percentage).toInt()
        }
    }
}

class SymbolBehaviour(
    var startTickPrice: TickPrice
) {
    var tickPriceSteps: LinkedList<TickPriceStep> = LinkedList<TickPriceStep>()
}

class TickPriceStep(
    var tickPrice: TickPrice,
    var cycleTimeInSeconds: Int,
    var numberOfUpdatesInOneCycle: Int
)
