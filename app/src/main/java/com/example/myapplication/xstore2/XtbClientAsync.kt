package com.example.myapplication.xstore2

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue

/*
 *
 * "customTag": "my_login_command_id"
 * At most 50 simultaneous connections from the same client address are allowed (an attempt to obtain the 51st connection returns the error EX008). If you need this rule can be lenified please contact the xStore Support Team.
 * Every new connection that fails to deliver data within one second from when it is established may be forced to close with no notification.
 * Each command invocation should not contain more than 1kB of data.
 * User should send requests in 200 ms intervals. This rule can be broken, but if it happens 6 times in a row the connection is dropped.
 *
 * */
/*interface MyInterface {
    Boolean doSomething() throws JSONException, IOException;
}*/
open class XtbClientAsync {
    private val xtbClient: XtbClient
    private val executor = Executors.newSingleThreadExecutor()

    constructor(login: String?, password: String?) {
        xtbClient = XtbClient(login, password)
    }

    protected constructor(xtbClient: XtbClient) {
        this.xtbClient = xtbClient
    }

    fun connectAsync(): Future<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()
        //MyInterface myInterface = xtbService::connect;
        executor.submit {
            try {
                completableFuture.complete(xtbClient.connect())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun disconnectAsync(): Future<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit { completableFuture.complete(xtbClient.disconnect()) }
        return completableFuture
    }

    /*
    public Future<JSONObject> runAsyncTask(){
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                completableFuture.complete(xtbService.getAllSymbols());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }*/
    val allSymbolsAsync: Future<JSONObject>
        get() {
            val completableFuture = CompletableFuture<JSONObject>()
            executor.submit {
                try {
                    completableFuture.complete(xtbClient.allSymbols)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return completableFuture
        }

    fun getSymbolAsync(symbol: String?): Future<JSONObject> {
        val completableFuture = CompletableFuture<JSONObject>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getSymbol(symbol))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    val pingAsync: Future<JSONObject>
        get() {
            val completableFuture = CompletableFuture<JSONObject>()
            executor.submit {
                try {
                    completableFuture.complete(xtbClient.ping)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return completableFuture
        }

    fun getProfitCalculationAsync(
        closePrice: Float,
        cmd: Int,
        openPrice: Float,
        symbol: String?,
        volume: Float
    ): Future<JSONObject> {
        val completableFuture = CompletableFuture<JSONObject>()
        executor.submit {
            try {
                completableFuture.complete(
                    xtbClient.getProfitCalculation(
                        closePrice,
                        cmd,
                        openPrice,
                        symbol,
                        volume
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun subscribeGetTicketPrice(symbol: String?): Future<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            if (xtbClient.isConnected) {
                xtbClient.subscribeGetTicketPrices(symbol)
                completableFuture.complete(true)
            } else {
                completableFuture.complete(false)
            }
        }
        return completableFuture
    }

    fun subscribeGetKeepAlive(): Future<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            xtbClient.subscribeGetKeepAlive()
            completableFuture.complete(true)
        }
        return completableFuture
    }

    val subscriptionResponsesQueue: LinkedBlockingQueue<JSONObject>
        get() {
            xtbClient.runSubscriptionStreamingReader()
            return xtbClient.subscriptionResponses
        }
    val isConnected: Future<Boolean>
        get() {
            val completableFuture = CompletableFuture<Boolean>()
            executor.submit { completableFuture.complete(xtbClient.isConnected) }
            return completableFuture
        }
}

open class XtbClient(private val login: String?, private val password: String?) {
    private var _stopListening = false

    // This WebSocket for main connection
    // It is used for the Request-Reply commands.
    internal var webSocket: WebSocket? = null

    //
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

        // Login should be moved from this place somewhere else...
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
            response = webSocket.nextMessage!!
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

    @get:Throws(JSONException::class)
    val allSymbols: JSONObject
        get() = processMessage(XtbServiceBodyMessageBuilder.allSymbolsMessage)

    @get:Throws(JSONException::class)
    val ping: JSONObject
        get() = processMessage(XtbServiceBodyMessageBuilder.pingJsonMessage)

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
            XtbServiceBodyMessageBuilder.getTickPricesMessage(
                streamSessionId, symbol
            )
        )
    }

    companion object {
        protected var streamSessionId: String? = null
    }
}

internal object XtbServiceBodyMessageBuilder {
    private const val loginJson = "{" +
            "\"command\" : \"login\"," +
            "\"arguments\" : {" +
            "\"userId\" : \"%s\"," +
            "\"password\": \"%s\"" +
            "}" +
            "}"
    private const val getAllSymbolsJson = "{" +
            "\"command\": \"getAllSymbols\"" +
            "}"
    private const val getKeepAliveJson = "{" +
            "\"command\" : \"getKeepAlive\"," +
            "\"streamSessionId\" : \"%s\"" +
            "}"
    private const val getTickPricesJson = "{" +
            "\"command\" : \"getTickPrices\"," +
            "\"streamSessionId\" : \"%s\"," +
            "\"symbol\": \"%s\"" +
            "}"
    private const val pingJson = "{" +
            "\"command\": \"ping\"" +
            "}"
    private const val getSymbolJson = "{" +
            "\"command\": \"getSymbol\"," +
            "\"arguments\": {" +
            "\"symbol\": \"%s\"" +
            "}" +
            "}"
    private const val getProfitCalculationJson = "{" +
            "\"command\": \"getProfitCalculation\"," +
            "\"arguments\": {" +
            "\"closePrice\": %s," +
            "\"cmd\": %s," +
            "\"openPrice\": %s," +
            "\"symbol\": \"%s\"," +
            "\"volume\": %s" +
            "}" +
            "}"

    fun getLoginMessage(userId: String?, password: String?): String {
        return format(loginJson, userId!!, password!!)
    }

    val allSymbolsMessage: String
        get() = format(getAllSymbolsJson)

    fun getKeepAliveMessage(streamSessionId: String?): String {
        return format(getKeepAliveJson, streamSessionId!!)
    }

    fun getTickPricesMessage(streamSessionId: String?, symbol: String?): String {
        return format(getTickPricesJson, streamSessionId!!, symbol!!)
    }

    val pingJsonMessage: String
        get() = format(pingJson)

    fun getSymbolMessage(symbol: String?): String {
        return format(getSymbolJson, symbol!!)
    }

    fun getProfitCalculationMessage(
        closePrice: Float,
        cmd: Int,
        openPrice: Float,
        symbol: String?,
        volume: Float
    ): String {
        return format(getProfitCalculationJson, closePrice, cmd, openPrice, symbol!!, volume)
    }

    private fun format(message: String, vararg parameters: Any): String {
        return String.format(message, *parameters)
    }
}