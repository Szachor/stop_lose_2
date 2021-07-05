package com.example.myapplication

import com.example.myapplication.xstore2.ConnectionType
import com.example.myapplication.xstore2.XtbClientAsync
import junit.framework.Assert.assertEquals
import org.junit.Test

class XtbClientSetupTest(
    private val login: String = "12366113",
    private val password: String = "xoh17653"
) {

    @Test
    fun connection_test() {
        val xtbService =
            XtbClientAsync()
        xtbService.connectAsync(login, password, connectionType = ConnectionType.TEST)
        val isConnected = xtbService.isConnected
        assertEquals(true, isConnected.get())
    }

    @Test
    fun disconnection_test() {
        val xtbService =
            XtbClientAsync()
        xtbService.connectAsync(login, password, connectionType = ConnectionType.TEST)
        var isConnected = xtbService.isConnected
        assertEquals(true, isConnected.get())
        xtbService.disconnectAsync()
        isConnected = xtbService.isConnected
        assertEquals(false, isConnected.get())
    }
}