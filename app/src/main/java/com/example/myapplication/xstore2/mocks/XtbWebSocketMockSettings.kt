package com.example.myapplication.xstore2.mocks

import android.os.SystemClock
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashMap

internal class XtbWebSocketMockSettings() {
    private var symbolsBehaviours: MutableMap<String, SymbolBehaviour> = HashMap()

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
}