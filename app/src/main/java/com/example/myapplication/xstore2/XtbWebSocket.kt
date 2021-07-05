package com.example.myapplication.xstore2

import com.example.myapplication.xstore2.mocks.XtbWebSocketMock
import java.util.concurrent.locks.ReentrantLock

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