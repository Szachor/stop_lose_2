package com.example.myapplication.xstore2.mocks

import com.example.myapplication.xstore2.WebSocket
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue


internal class XtbWebSocketMock :
    WebSocket() {

    private var xtbWebSocketMockSettings= XtbMockServer

    private val responses = LinkedBlockingQueue<String>()

    override fun sendMessage(message: String?) {
        try {
            val jsonMessage = JSONObject(message.toString())
            when (val command = jsonMessage.getString("command")) {
                "login" -> {
                    // TODO Should be moved to other responses
                    val loginResponse =
                        """{"status": true, "streamSessionId": "8469308861804289383"}"""
                    responses.put(loginResponse)
                }
                "getTickPrices" -> {
                    val symbol = jsonMessage.getString("symbol")
                    val symbolBehaviour = xtbWebSocketMockSettings.getSymbolBehaviour(symbol)
                    val smw = StreamingMockWorker(
                        command = command,
                        responses = responses,
                        symbol = symbol,
                        symbolBehaviour = symbolBehaviour
                    )
                    smw.start()
                    println("XtbWebSocketMock::getTickPrices Started")
                }
                "getSymbol" -> {
                    val symbol = jsonMessage.getJSONObject("arguments").getString("symbol")
                    val getSymbolResponse = XtbMockResponses.getSymbolMockResponse(symbol)
                    putMessageToQueue(getSymbolResponse)
                }
                "getAllSymbols" -> {
                    val getSymbolResponse = XtbMockResponses.getAllSymbolsMockResponse()
                    putMessageToQueue(getSymbolResponse)
                }
                "getProfitCalculation" -> {
                    val getProfitCalculationResponse = XtbMockResponses.getProfitCalculationMockResponse()
                    putMessageToQueue(getProfitCalculationResponse)
                }
                "ping" -> {
                    val pingResponse = """{"status": true}"""
                    putMessageToQueue(pingResponse)
                }
                else -> throw IllegalStateException("Unexpected value: $command")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun putMessageToQueue(json: String){
        responses.put(json)
    }

    override fun getNextMessage(): String {
        return responses.take()
    }

    override var isConnected = true

    override fun disconnect() {
        isConnected = false
    }
}

