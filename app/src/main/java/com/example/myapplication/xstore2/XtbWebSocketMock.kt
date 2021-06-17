package com.example.myapplication.xstore2

import android.os.SystemClock
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue

/*class XtbWebSocketMockSettings {

}*/
internal class XtbWebSocketMock(var isStreamingWebSocket: Boolean) : WebSocket() {

    //private XtbWebSocketMockSettings webSocketMockSettings;
    private val responses = LinkedBlockingQueue<JSONObject>()
    override fun sendMessage(message: String?) {
        try {
            val jsonMessage = JSONObject(message.toString())
            when (val command = jsonMessage.getString("command")) {
                "login" -> {
                    val loginResponse = """{
	"status": true,
	"streamSessionId": "8469308861804289383"
}"""
                    val loginResponseJson = JSONObject(loginResponse)
                    responses.put(loginResponseJson)
                }
                "getTickPrices" -> {
                    val runnable = Runnable {
                        var i = 0
                        while (i < 10) {
                            val getTickPricesResponse = """{
	"ask": 4000.0,
	"askVolume": 15000,
	"bid": 4000.0,
	"bidVolume": 16000,
	"high": 4000.0,
	"level": 0,
	"low": 3500.0,
	"quoteId": 0,
	"spreadRaw": 0.000003,
	"spreadTable": 0.00042,
	"symbol": "KOMB.CZ",
	"timestamp": 1272529161605
}"""
                            SystemClock.sleep(2000)
                            try {
                                val getTickPricesResponseJson = JSONObject(getTickPricesResponse)
                                responses.put(wrapResponse(command, getTickPricesResponseJson))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            i++
                        }
                    }
                    Thread(runnable).start()
                }
                "getSymbol" -> {
                    val getSymbolResponse = """{
	"ask": 4000.0,
	"bid": 4000.0,
	"categoryName": "Forex",
	"contractSize": 100000,
	"currency": "USD",
	"currencyPair": true,
	"currencyProfit": "SEK",
	"description": "USD/PLN",
	"expiration": null,
	"groupName": "Minor",
	"high": 4000.0,
	"initialMargin": 0,
	"instantMaxVolume": 0,
	"leverage": 1.5,
	"longOnly": false,
	"lotMax": 10.0,
	"lotMin": 0.1,
	"lotStep": 0.1,
	"low": 3500.0,
	"marginHedged": 0,
	"marginHedgedStrong": false,
	"marginMaintenance": null,
	"marginMode": 101,
	"percentage": 100.0,
	"precision": 2,
	"profitMode": 5,
	"quoteId": 1,
	"shortSelling": true,
	"spreadRaw": 0.000003,
	"spreadTable": 0.00042,
	"starting": null,
	"stepRuleId": 1,
	"stopsLevel": 0,
	"swap_rollover3days": 0,
	"swapEnable": true,
	"swapLong": -2.55929,
	"swapShort": 0.131,
	"swapType": 0,
	"symbol": "USDPLN",
	"tickSize": 1.0,
	"tickValue": 1.0,
	"time": 1272446136891,
	"timeString": "Thu May 23 12:23:44 EDT 2013",
	"trailingEnabled": true,
	"type": 21
}"""
                    val getSymbolResponseJson = JSONObject(getSymbolResponse)
                    responses.put(wrapResponse(command, getSymbolResponseJson))
                }
                "getAllSymbols" -> {
                    val getAllSymbolsResponse = """[{
	"ask": 4000.0,
	"bid": 4000.0,
	"categoryName": "Forex",
	"contractSize": 100000,
	"currency": "USD",
	"currencyPair": true,
	"currencyProfit": "SEK",
	"description": "USD/PLN",
	"expiration": null,
	"groupName": "Minor",
	"high": 4000.0,
	"initialMargin": 0,
	"instantMaxVolume": 0,
	"leverage": 1.5,
	"longOnly": false,
	"lotMax": 10.0,
	"lotMin": 0.1,
	"lotStep": 0.1,
	"low": 3500.0,
	"marginHedged": 0,
	"marginHedgedStrong": false,
	"marginMaintenance": null,
	"marginMode": 101,
	"percentage": 100.0,
	"precision": 2,
	"profitMode": 5,
	"quoteId": 1,
	"shortSelling": true,
	"spreadRaw": 0.000003,
	"spreadTable": 0.00042,
	"starting": null,
	"stepRuleId": 1,
	"stopsLevel": 0,
	"swap_rollover3days": 0,
	"swapEnable": true,
	"swapLong": -2.55929,
	"swapShort": 0.131,
	"swapType": 0,
	"symbol": "USDPLN",
	"tickSize": 1.0,
	"tickValue": 1.0,
	"time": 1272446136891,
	"timeString": "Thu May 23 12:23:44 EDT 2013",
	"trailingEnabled": true,
	"type": 21
}]"""
                    val getAllSymbolsResponseJson = JSONArray(getAllSymbolsResponse)
                    responses.put(wrapResponse(command, getAllSymbolsResponseJson))
                }
                "getProfitCalculation" -> {
                    val getProfitCalculationResponse = """{
	"order": 7497776,
	"order2": 7497777,
	"position": 7497776,
	"profit": 7076.52
}"""
                    val getProfitCalculationResponseJson = JSONObject(getProfitCalculationResponse)
                    responses.put(wrapResponse(command, getProfitCalculationResponseJson))
                }
                "ping" -> {
                    val pingResponse = """{
	"status": true	
}"""
                    val pingResponseJson = JSONObject(pingResponse)
                    responses.put(pingResponseJson)
                }
                else -> throw IllegalStateException("Unexpected value: $command")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Throws(JSONException::class)
    private fun wrapResponse(commandName: String, data: JSONObject): JSONObject {
        val response = JSONObject()
        response.put("command", commandName)
        response.put("returnData", data)
        return response
    }

    @Throws(JSONException::class)
    private fun wrapResponse(commandName: String, data: JSONArray): JSONObject {
        val response = JSONObject()
        response.put("command", commandName)
        response.put("returnData", data)
        return response
    }

    @get:Throws(JSONException::class, IOException::class)
    override val nextMessage: JSONObject?
        get() {
            try {
                return responses.take()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return JSONObject()
        }


    override var isConnected = true

    override fun disconnect() {
        isConnected = false
    } /*public void setWebSocketMockSettings(XtbWebSocketMockSettings webSocketMockSettings) {
        this.webSocketMockSettings = webSocketMockSettings;
    }*/
}