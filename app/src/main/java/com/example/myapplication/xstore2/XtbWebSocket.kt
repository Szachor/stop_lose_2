package com.example.myapplication.xstore2
import java.util.concurrent.locks.ReentrantLock

internal class XtbWebSocket(isStreamingWebSocket: Boolean) : WebSocket(
    _webSocketEndpoint,
    if (isStreamingWebSocket) _webSocketStreamingPort else _webSocketPort
) {
    companion object {
        /*
I/System.out: xapia.x-station.eu DEMO_A DEMO A 5124 5125
I/System.out: xapib.x-station.eu DEMO_B DEMO B 5124 5125
wss://ws.xtb.com/demo

I/System.out: xapia.x-station.eu REAL_A REAL A 5112 5113
I/System.out: xapib.x-station.eu REAL_B REAL B 5112 5113

I/System.out: xapia.x-station.eu UAT_A UAT A 5116 5117
I/System.out: xapib.x-station.eu UAT_B UAT B 5116 5117
 */
        private const val _webSocketPort = 5124
        private const val _webSocketStreamingPort = 5125
        private const val _webSocketEndpoint = "xapia.x-station.eu"
    }

    private var lastSentMessageTime:Long = 0

    // 200 ms
    private val timeInternalBetweenRequest = 200

    // Requirement: 200 ms per request
    // Request should be sent in 200ms internals. This rule can be broken 5 times in row.
    var lock: ReentrantLock = ReentrantLock()
    override fun sendMessage(message: String?){
        lock.lock()
        if(System.currentTimeMillis() - lastSentMessageTime < timeInternalBetweenRequest){
            Thread.sleep(200)
        }
        super.sendMessage(message)
        lastSentMessageTime = System.currentTimeMillis()
        lock.unlock()
    }
}