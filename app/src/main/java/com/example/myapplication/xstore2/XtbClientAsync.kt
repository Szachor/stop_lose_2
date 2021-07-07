package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.mocks.TickPrice
import com.example.myapplication.xstore2.model.GetSymbolResponseReturnData
import com.example.myapplication.xstore2.model.GetTickPricesReturnData
import org.json.JSONObject
import java.util.concurrent.*

/*
 *
 * "customTag": "my_login_command_id"
 * At most 50 simultaneous connections from the same client address are allowed (an attempt to obtain the 51st connection returns the error EX008). If you need this rule can be lenified please contact the xStore Support Team.
 * Every new connection that fails to deliver data within one second from when it is established may be forced to close with no notification.
 * Each command invocation should not contain more than 1kB of data.
 * */
/*interface MyInterface {
    Boolean doSomething() throws JSONException, IOException;
}*/

open class XtbClientAsync {
    private lateinit var xtbClient: XtbClient
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun throwErrorIfNotConnected(){
        if(!xtbClient.isConnected){
            throw ClientNotConnected("The client service need to be connected to run this method")
        }
    }

    open fun connectAsync(login: String, password: String, connectionType: ConnectionType): Future<Boolean> {
        xtbClient = XtbClient()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.connect(login, password, connectionType = connectionType))
            } catch (e: Exception) {
                e.printStackTrace()
                completableFuture.complete(false)
            }
        }
        return completableFuture
    }

    fun disconnectAsync(): Future<Boolean> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit { completableFuture.complete(xtbClient.disconnect()) }
        return completableFuture
    }

    fun getAllSymbolsAsync(): Future<List<GetSymbolResponseReturnData>> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<List<GetSymbolResponseReturnData>>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getAllSymbols())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun getSymbolAsync(symbol: String): CompletableFuture<GetSymbolResponseReturnData> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<GetSymbolResponseReturnData>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getSymbol(symbol))
            } catch(e: NotFoundSymbol){
                completableFuture.completeExceptionally(e)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun getTickPricesAsync(symbol: String): Future<List<GetTickPricesReturnData>> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<List<GetTickPricesReturnData>>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getTickPrices(symbol))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    // #TODO Change JSONObject to structured response
    fun getPingAsync(): Future<JSONObject> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<JSONObject>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getPing())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    // #TODO Change JSONObject to structured response
    fun getProfitCalculationAsync(
        closePrice: Float,
        cmd: Int,
        openPrice: Float,
        symbol: String?,
        volume: Float
    ): Future<JSONObject> {
        throwErrorIfNotConnected()
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun subscribeGetTicketPrice(symbol: String): Future<Boolean> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            if (xtbClient.isConnected) {
                try {
                    xtbClient.subscribeGetTicketPrices(symbol)
                    completableFuture.complete(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                completableFuture.complete(false)
            }
        }
        return completableFuture
    }

    fun subscribeGetKeepAlive(): Future<Boolean> {
        throwErrorIfNotConnected()
        val completableFuture = CompletableFuture<Boolean>()
        executor.submit {
            xtbClient.subscribeGetKeepAlive()
            completableFuture.complete(true)
        }
        return completableFuture
    }

    val responsesQueueForTicketPrices: LinkedBlockingQueue<GetTickPricesReturnData>
        get() {
            throwErrorIfNotConnected()
            xtbClient.runSubscriptionStreamingReader()
            return xtbClient.subscriptionTickPricesResponses
        }

    val responsesQueueForKeepAlive: LinkedBlockingQueue<Long>
        get() {
            throwErrorIfNotConnected()
            xtbClient.runSubscriptionStreamingReader()
            return xtbClient.subscriptionKeepAliveResponses
        }


    /* To get responses from subscription use responsesQueueForTicketPrices or responsesQueueForKeepAlive.
    This queue contains only not recognised responses.
    * */
    val subscriptionResponsesQueue: LinkedBlockingQueue<JSONObject>
        get() {
            throwErrorIfNotConnected()
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

class ClientNotConnected(s: String) : Exception(s)
