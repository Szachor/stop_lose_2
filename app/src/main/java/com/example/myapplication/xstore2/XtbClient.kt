package com.example.myapplication.xstore2

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue

open class XtbClient(private val login: String?, private val password: String?) {
    private var _stopListening = false

    // This WebSocket for main connection
    // It is used for the Request-Reply commands.
    internal var webSocket: WebSocket? = null
    internal var streamingWebSocket: WebSocket? = null
    val subscriptionResponses = LinkedBlockingQueue<JSONObject>()
    val isConnected: Boolean
        get() = if (webSocket == null) {
            false
        } else webSocket!!.isConnected

    @Throws(JSONException::class)
    open fun connect(): Boolean {
        if (isConnected) {
            return true
        }
        webSocket = XtbWebSocket(false)
        streamingWebSocket = XtbWebSocket(true)

        login()
        _stopListening = false
        return true
    }

    fun disconnect(): Boolean {
        _stopListening = true
        try {
            webSocket!!.disconnect()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        try {
            streamingWebSocket!!.disconnect()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        return true
    }

    @Throws(JSONException::class)
    protected fun login() {
        val response = processMessage(XtbServiceBodyMessageBuilder.getLoginMessage(login, password))
        streamSessionId = response.getString("streamSessionId")
    }

    @Throws(JSONException::class)
    private fun processMessage(message: String): JSONObject {
        webSocket!!.sendMessage(message)
        return getResponse(webSocket!!)
    }

    @Throws(JSONException::class)
    private fun getResponse(webSocket: WebSocket): JSONObject {
        var response: JSONObject
        try {
            val responseString = webSocket.getNextMessage()!!
            response = JSONObject(responseString)
        } catch (exception: JSONException) {
            response = JSONObject()
            response.put("Error", "Couldn't parse response to the JSON format")
            response.put("Response", response)
            exception.printStackTrace()
        } catch (exception: IOException) {
            // Disconnected webSocket when thread was waiting for message
            if (!_stopListening) {
                exception.printStackTrace()
            }
            response = JSONObject()
            response.put("Error", "Lost connection")
        }
        return JSONObject(response.toString())
    }

    fun runSubscriptionStreamingReader() {
        val runnable = Runnable {
            while (!_stopListening && isConnected) {
                var response: JSONObject
                try {
                    response = getResponse(streamingWebSocket!!)
                    if (!response.has("Error")) {
                        subscriptionResponses.put(JSONObject(response.toString()))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        val t = Thread(runnable)
        t.start()
    }

    @Throws(JSONException::class)
    fun getAllSymbols(): JSONObject =
        processMessage(XtbServiceBodyMessageBuilder.getAllSymbolsMessage())

    @Throws(JSONException::class)
    fun getPing(): JSONObject = processMessage(XtbServiceBodyMessageBuilder.getPingJsonMessage())

    @Throws(JSONException::class)
    fun getProfitCalculation(
        closePrice: Float,
        cmd: Int,
        openPrice: Float,
        symbol: String?,
        volume: Float
    ): JSONObject {
        return processMessage(
            XtbServiceBodyMessageBuilder.getProfitCalculationMessage(
                closePrice,
                cmd,
                openPrice,
                symbol,
                volume
            )
        )
    }

    @Throws(JSONException::class)
    fun getSymbol(symbol: String?): JSONObject {
        return processMessage(XtbServiceBodyMessageBuilder.getSymbolMessage(symbol))
    }

    fun subscribeGetKeepAlive() {
        val streamingWebSocket = streamingWebSocket
        streamingWebSocket!!.sendMessage(
            XtbServiceBodyMessageBuilder.getKeepAliveMessage(
                streamSessionId
            )
        )
    }

    fun subscribeGetTicketPrices(symbol: String?) {
        val streamingWebSocket = streamingWebSocket
        streamingWebSocket!!.sendMessage(
            XtbServiceBodyMessageBuilder.getTickPricesStreamingMessage(
                streamSessionId, symbol
            )
        )
    }

    fun getTickPrices(symbol: String): JSONObject {
        return processMessage(XtbServiceBodyMessageBuilder.getTickPriceMessage(symbol))
    }

    companion object {
        protected var streamSessionId: String? = null
    }
}

internal object XtbServiceBodyMessageBuilder {
    fun getLoginMessage(userId: String?, password: String?): String {
        return """{"command" : "login","arguments" : {"userId" : "$userId","password": "$password"}}"""
    }

    fun getAllSymbolsMessage(): String = """{"command": "getAllSymbols"}"""

    fun getKeepAliveMessage(streamSessionId: String?): String {
        return """{"command" : "getKeepAlive","streamSessionId" : "$streamSessionId"}"""
    }

    fun getTickPricesStreamingMessage(streamSessionId: String?, symbol: String?): String {
        return """{"command" : "getTickPrices","streamSessionId" : "$streamSessionId","symbol": "$symbol"}"""
    }

    fun getPingJsonMessage(): String = """{"command": "ping"}"""

    fun getSymbolMessage(symbol: String?): String {
        return """{"command": "getSymbol","arguments": {"symbol": "$symbol"}}"""
    }

    // System.currentTimeMillis()-10000
    // At the beginning I was using timestamp: System.currentTimeMillis() -> but it was not working, XTB returned no data
    // I was trying also System.currentTimeMillis()-10000 -> in result sometimes I was receiving some data, sometimes no
    // ${System.currentTimeMillis()-1000000 seems that it almost always returns data in XTB working hours
    // For System.currentTimeMillis()-100000 returns the same response as for timestamp: 1 -> the same.
    fun getTickPriceMessage(symbol: String?): String {
        return """{"command": "getTickPrices","arguments": {"level": 0, "symbols": ["$symbol"], "timestamp": 1}}"""
    }

    fun getProfitCalculationMessage(
        closePrice: Float,
        cmd: Int,
        openPrice: Float,
        symbol: String?,
        volume: Float
    ): String {
        return """{"command": "getProfitCalculation","arguments": {"closePrice": $closePrice,"cmd": $cmd,"openPrice": $openPrice,"symbol": "$symbol","volume": $volume}}"""
    }
}