package com.example.myapplication.xstore2.mocks

import com.example.myapplication.xstore2.model.GetTickPricesReturnData
import com.example.myapplication.xstore2.model.GetTickPricesStreamingResponse
import com.example.myapplication.xstore2.model.XtbMoshiModelsMapper
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

// TODO should be probably moved to XtbMockServer
internal class StreamingMockWorker(
    val command: String,
    private val symbolBehaviour: SymbolBehaviour,
    val symbol: String,
    private val responses: LinkedBlockingQueue<String>,
) : Thread() {

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
                // TODO sleep seems working for java, but systemClock.sleep for android - should be confirmed
                sleep(timeOfOneStep.toLong() * 1000)
                //SystemClock.sleep(timeOfOneStep.toLong())
                val currentTickPrice =
                    getNextTickPrice(previousTickPrice, nextTickPrice, currentStep, numberOfSteps)
                sendMessage(symbol, currentTickPrice)
            }
            previousTickPrice = nextTickPrice
        }
    }

    private fun sendMessage(symbol: String, tickPrice: TickPrice) {
        val getTickPricesResponseJson = XtbMoshiModelsMapper.TickPricesStreamingResponseJsonAdapter.toJson(toTickPricesResponse(symbol, tickPrice))
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

    private fun toTickPricesResponse(symbol: String, tickPrice: TickPrice): GetTickPricesStreamingResponse {
        val tickPriceReturnData = GetTickPricesReturnData(symbol = symbol, ask = tickPrice.ask, askVolume = tickPrice.askVolume,
            bid = tickPrice.bid, bidVolume = tickPrice.bidVolume, high = tickPrice.ask, level = 1, low = tickPrice.bid, quoteId = 1, spreadRaw = 1.1, spreadTable = 1.1)
        return GetTickPricesStreamingResponse(command="tickPrices", data=tickPriceReturnData)
    }
}