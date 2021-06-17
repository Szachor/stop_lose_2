package com.example.myapplication.xstore2

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
}