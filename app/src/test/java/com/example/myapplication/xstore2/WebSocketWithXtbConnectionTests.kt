package com.example.myapplication.xstore2

import junit.framework.TestCase
import org.junit.Assert.assertThrows
import java.io.IOException
import java.util.concurrent.ExecutionException

class WebSocketWithXtbConnectionTests : TestCase() {
    private var webSocket: WebSocket? = null

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        webSocket = XtbWebSocket(false)

    }

    @Throws(ExecutionException::class, InterruptedException::class)
    public override fun tearDown() {
    }

    fun testSendBadJsonStructure() {
        val loginJsonCommand =
            """{"command" : "login","arguments" : {"userId" : "XXX","password": "YYY"}}}"""

        webSocket?.sendMessage(loginJsonCommand)
        assertThrows(IOException::class.java) {
            webSocket?.getNextMessage()
        }
    }
}

