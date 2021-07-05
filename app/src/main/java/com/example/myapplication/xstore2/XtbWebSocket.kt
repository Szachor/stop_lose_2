package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.mocks.XtbWebSocketMock
import java.util.concurrent.locks.ReentrantLock


// TODO Create class for different services: Prod, Test, Mock

class WebSocketConnectionData constructor(
    login: String,
    password: String,
    webSocketPort: Int,
    webSocketStreamingPort: Int,
    webSocketEndpoint: String,
)

class XtbConnectionData private constructor() {

    /*
    // Demo connection
    xapia.x-station.eu DEMO_A DEMO A 5124 5125
    xapib.x-station.eu DEMO_B DEMO B 5124 5125
    wss://ws.xtb.com/demo

    // Prod connection
    I/System.out: xapia.x-station.eu REAL_A REAL A 5112 5113
    I/System.out: xapib.x-station.eu REAL_B REAL B 5112 5113

    // UAT connection
    I/System.out: xapia.x-station.eu UAT_A UAT A 5116 5117
    I/System.out: xapib.x-station.eu UAT_B UAT B 5116 5117
     */
    companion object {
        fun createConnectionData(
            login: String,
            password: String,
            connectionType: ConnectionType
        ): WebSocketConnectionData {
            val webSocketPort: Int
            val webSocketStreamingPort: Int
            val webSocketEndpoint: String
            when (connectionType) {
                ConnectionType.PROD -> {
                    webSocketPort = 5112
                    webSocketStreamingPort = 5113
                    webSocketEndpoint = "xapia.x-station.eu"
                }
                ConnectionType.TEST -> {
                    webSocketPort = 0
                    webSocketStreamingPort = 0
                    webSocketEndpoint = "MOCK"
                }
                ConnectionType.MOCK -> {
                    webSocketPort = 5124
                    webSocketStreamingPort = 5125
                    webSocketEndpoint = "xapia.x-station.eu"
                }
            }

            return WebSocketConnectionData(
                login = login,
                password = password,
                webSocketPort = webSocketPort,
                webSocketStreamingPort = webSocketStreamingPort,
                webSocketEndpoint = webSocketEndpoint
            )
        }

        private const val _webSocketPort = 5124
        private const val _webSocketStreamingPort = 5125
        private const val _webSocketEndpoint = "xapia.x-station.eu"
    }
}

enum class ConnectionType {
    PROD,
    TEST,
    MOCK
}


internal class XtbWebSocket private constructor(webSocketEndpoint: String, webSocketPort: Int) :
    WebSocket(
        webSocketEndpoint = webSocketEndpoint, webSocketPort = webSocketPort
    ) {

    private var lastSentMessageTime: Long = 0

    // 200 ms
    private val timeInternalBetweenRequest = 200

    // Requirement: 200 ms per request
    // Request should be sent in 200ms internals. This rule can be broken 5 times in row.
    private var lock: ReentrantLock = ReentrantLock()
    override fun sendMessage(message: String?) {
        lock.lock()
        if (System.currentTimeMillis() - lastSentMessageTime < timeInternalBetweenRequest) {
            Thread.sleep(200)
        }
        super.sendMessage(message)
        lastSentMessageTime = System.currentTimeMillis()
        lock.unlock()
    }

    companion object {
        fun createWebSocket(
            connectionType: ConnectionType,
            isStreamingWebSocket: Boolean
        ): WebSocket {
            val webSocketEndpoint = "xapia.x-station.eu"
            when (connectionType) {
                ConnectionType.PROD -> {
                    return XtbWebSocket(
                        webSocketPort = if(isStreamingWebSocket) 5113 else 5112,
                        webSocketEndpoint = webSocketEndpoint
                    )
                }
                ConnectionType.MOCK -> {
                    return XtbWebSocketMock()
                }
                ConnectionType.TEST -> {
                    return XtbWebSocket(
                        webSocketPort = if(isStreamingWebSocket) 5125 else 5124,
                    webSocketEndpoint = webSocketEndpoint
                    )
                }
            }
        }
    }
}