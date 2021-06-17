package com.example.myapplication.xstore2

import org.json.JSONException

internal class XtbMockClientAsync : XtbClientAsync(XtbMockClient())
internal class XtbMockClient : XtbClient("Mock login", "Mock password") {
    @Throws(JSONException::class)
    override fun connect(): Boolean {
        if (isConnected) {
            return true
        }
        webSocket = XtbWebSocketMock(false)
        streamingWebSocket = XtbWebSocketMock(true)

        // Login should be moved from this place somewhere else...
        login()
        return true
    }
}