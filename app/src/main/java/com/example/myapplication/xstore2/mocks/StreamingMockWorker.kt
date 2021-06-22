package com.example.myapplication.xstore2.mocks

import android.os.SystemClock
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

internal class StreamingMockWorker(
    val command: String,
    private val symbolBehaviour: SymbolBehaviour,
    val symbol: String,
    private val responses: LinkedBlockingQueue<String>,
) : Thread() {
    init {
    }

    override fun run() {
        val startTickPrice = getStartTickPrice()
        val tickPriceStepsQueue: Queue<TickPriceStep> = LinkedList(symbolBehaviour.tickPriceSteps)
        sendMessage(symbol, startTickPrice)

        var currentStep = 0
        val tickPriceStep = tickPriceStepsQueue.poll()


        var previousTickPrice = startTickPrice
        while (tickPriceStep != null) {
            val nextTickPrice = tickPriceStep.tickPrice

            val numberOfSteps = tickPriceStep.numberOfUpdatesInOneCycle
            val cycleTimeInSeconds = tickPriceStep.cycleTimeInSeconds
            val timeOfOneStep = 1.0 * cycleTimeInSeconds / numberOfSteps

            while (currentStep++ < numberOfSteps) {
                SystemClock.sleep(timeOfOneStep.toLong())
                val currentTickPrice =
                    getNextTickPrice(previousTickPrice, nextTickPrice, currentStep, numberOfSteps)
                sendMessage(symbol, currentTickPrice)
            }
            previousTickPrice = nextTickPrice
        }
    }

    private fun sendMessage(symbol: String, tickPrice: TickPrice) {
        val getTickPricesResponseJson = toTickPricesResponse(symbol, tickPrice).getStringResponse()
        responses.put(getTickPricesResponseJson)
    }

    private fun getStartTickPrice(): TickPrice {
        return symbolBehaviour.startTickPrice
    }

    private fun getNextTickPrice(
        firstTickPrice: TickPrice,
        secondTickPrice: TickPrice,
        stepNumber: Int,
        numberOfSteps: Int
    ): TickPrice {
        val x = 1.0 * stepNumber / numberOfSteps
        return TickPrice(firstTickPrice, secondTickPrice, x)
    }

    private fun toTickPricesResponse(symbol: String, tickPrice: TickPrice): GetTickPricesResponse {
        return GetTickPricesResponse(symbol = symbol, ask = tickPrice.ask, askVolume = tickPrice.askVolume,
        bid = tickPrice.bid, bidVolume = tickPrice.bidVolume)
    }
}