package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue



// TODO Apply requirements about request limits:
// Request should be sent in 200ms internals. This rule can be broken 5 times in row.
open class XtbClient {
    protected var stopListening = false

    // This WebSocket for main connection
    // It is used for the Request-Reply commands.
    internal var webSocket: WebSocket? = null

    // This WebSocket is used for streaming methods
    internal var streamingWebSocket: WebSocket? = null

    val subscriptionResponses = LinkedBlockingQueue<JSONObject>()

    val isConnected: Boolean
        get() = if (webSocket == null) {
            false
        } else webSocket!!.isConnected



    private var getSymbolResponseAdapter: JsonAdapter<GetSymbolResponse>
    private var getTickPricesResponseAdapter: JsonAdapter<GetTickPricesResponse>
    private var getAllSymbolsResponseAdapter: JsonAdapter<GetAllSymbolsResponse>

    init {
        val moshi: Moshi = Moshi.Builder().build()
        getSymbolResponseAdapter = moshi.adapter(GetSymbolResponse::class.java)
        getAllSymbolsResponseAdapter = moshi.adapter(GetAllSymbolsResponse::class.java)
        getTickPricesResponseAdapter = moshi.adapter(GetTickPricesResponse::class.java)

    }

    @Throws(JSONException::class)
    open fun connect(login: String, password: String, connectionType: ConnectionType): Boolean {
        if (isConnected) {
            return true
        }
        try {
            webSocket = XtbWebSocket.createWebSocket(connectionType, isStreamingWebSocket = false)
            streamingWebSocket = XtbWebSocket.createWebSocket(connectionType, isStreamingWebSocket = true)
            login(login, password)
            stopListening = false
        }
        catch (exception: IOException){
            exception.printStackTrace()
            return false
        }
        return true
    }

    fun disconnect(): Boolean {
        stopListening = true
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
    protected fun login(login: String, password: String) {
        val response = processMessage(XtbServiceBodyMessageBuilder.getLoginMessage(
            login,
            password
        ))
        streamSessionId = JSONObject(response).getString("streamSessionId")
    }

    @Throws(JSONException::class)
    private fun processMessage(message: String): String {
        webSocket!!.sendMessage(message)
        return getResponse(webSocket!!)
    }

    private fun getResponse(webSocket: WebSocket): String {
        val response: String
        try {
            response = webSocket.getNextMessage()!!
        } catch (exception: IOException) {
            // Disconnected webSocket when thread was waiting for message
            if (!stopListening) {
                exception.printStackTrace()
            }
            return "Error: Lost connection"
        }
        return response
    }

    fun runSubscriptionStreamingReader() {
        val runnable = Runnable {
            while (!stopListening && isConnected) {
                var response: JSONObject
                try {
                    val responseString = getResponse(streamingWebSocket!!)
                    response = JSONObject(responseString)
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
    fun getAllSymbols(): List<GetSymbolResponseReturnData> {
        val jsonMessage = processMessage(XtbServiceBodyMessageBuilder.getAllSymbolsMessage())
        return getAllSymbolsResponseAdapter.fromJson(jsonMessage)?.returnData!!
    }

    @Throws(JSONException::class)
    fun getPing(): JSONObject {
        val responseString = processMessage(XtbServiceBodyMessageBuilder.getPingJsonMessage())
        return JSONObject(responseString)
    }

    @Throws(JSONException::class)
    fun getProfitCalculation(
        closePrice: Float,
        cmd: Int,
        openPrice: Float,
        symbol: String?,
        volume: Float
    ): JSONObject {
        val responseString = processMessage(
            XtbServiceBodyMessageBuilder.getProfitCalculationMessage(
                closePrice,
                cmd,
                openPrice,
                symbol,
                volume
            )
        )
        return JSONObject(responseString)
    }

    @Throws(JSONException::class)
    fun getSymbol(symbol: String): GetSymbolResponseReturnData {
        val responseString = processMessage(XtbServiceBodyMessageBuilder.getSymbolMessage(symbol))
        val responseObject = getSymbolResponseAdapter.fromJson(responseString)
        if(responseObject!!.errorCode == "BE115") throw NotFoundSymbol()
        return responseObject.returnData!!
    }

    fun subscribeGetKeepAlive() {
        val streamingWebSocket = streamingWebSocket
        streamingWebSocket!!.sendMessage(
            XtbServiceBodyMessageBuilder.getKeepAliveMessage(
                streamSessionId!!
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

    fun getTickPrices(symbol: String): List<GetTickPricesReturnData> {
        val responseString =
            processMessage(XtbServiceBodyMessageBuilder.getTickPriceMessage(symbol))
        return getTickPricesResponseAdapter.fromJson(responseString)!!.returnData?.quotations!!
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
class NotFoundSymbol: Exception()