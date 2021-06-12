package com.example.myapplication

import com.example.myapplication.xstore2.XtbServiceAsync
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


class XtbServiceTest {
    var login = "12263751"
    var password = "xoh26561"

    @Before
    fun setup_connection() {
        val xtbService = XtbServiceAsync("12263751", "xoh26561")
        xtbService.connectAsync()
        val isConnected = xtbService.isConnected
    }

    @After
    fun end_disconnection() {
        val xtbService = XtbServiceAsync("12263751", "xoh26561")
        xtbService.disconnectAsync()
    }

    @Test
    fun xxx_test(){
        val xtbService = XtbServiceAsync("12263751", "xoh26561")
        xtbService.connectAsync()
        val isConnected = xtbService.isConnected
        assertEquals(true, isConnected.get())
    }
}