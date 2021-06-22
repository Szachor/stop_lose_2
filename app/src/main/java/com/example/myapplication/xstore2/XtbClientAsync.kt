package com.example.myapplication.xstore2

import org.json.JSONException
import org.json.JSONObject
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
    internal val xtbClient: XtbClient
    internal val executor = Executors.newSingleThreadExecutor()

    constructor(login: String, password: String) {
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
                completableFuture.complete(false)
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

    fun getAllSymbolsAsync(): Future<JSONObject> {
        val completableFuture = CompletableFuture<JSONObject>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getAllSymbols())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun getSymbolAsync(symbol: String): Future<JSONObject> {
        val completableFuture = CompletableFuture<JSONObject>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getSymbol(symbol))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun getTickPricesAsync(symbol: String): Future<JSONObject> {
        val completableFuture = CompletableFuture<JSONObject>()
        executor.submit {
            try {
                completableFuture.complete(xtbClient.getTickPrices(symbol))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun getPingAsync(): Future<JSONObject> {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return completableFuture
    }

    fun subscribeGetTicketPrice(symbol: String): Future<Boolean> {
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