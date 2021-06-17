package com.example.myapplication.xstore2

import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocketFactory

internal open class WebSocket {
    private var webSocketEndpoint: String? = null
    private var webSocketPort = 0

    constructor(webSocketEndpoint: String?, webSocketPort: Int) {
        this.webSocketEndpoint = webSocketEndpoint
        this.webSocketPort = webSocketPort
        initSocketWriter()
    }

    protected constructor()

    private var socketClient: Socket? = null
    private var socketWriter: PrintStream? = null
    private fun initSocketWriter() {
        val socketAddress = InetSocketAddress(webSocketEndpoint, webSocketPort)
        val socketFactory = SSLSocketFactory.getDefault()
        try {
            socketClient = socketFactory.createSocket(socketAddress.address, socketAddress.port)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        try {
            socketWriter = PrintStream(socketClient!!.getOutputStream())
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    private var _socketReader: BufferedReader? = null
    private val socketReader: BufferedReader
        get() {
            if (_socketReader == null) {
                var streamingReadStream: InputStream? = null
                try {
                    streamingReadStream = socketClient!!.getInputStream()
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }
                _socketReader = BufferedReader(InputStreamReader(streamingReadStream))
            }
            return _socketReader!!
        }

    open fun sendMessage(message: String?) {
        socketWriter!!.print(message)
    }

    @get:Throws(JSONException::class, IOException::class)
    open val nextMessage: JSONObject?
        get() {
            var line: String
            val response = StringBuilder()
            line = socketReader.readLine()
            do {
                response.append(line)
                line = socketReader.readLine()
            } while (line != "")
            return JSONObject(response.toString())
        }
    open val isConnected
        get() = !socketClient!!.isClosed

    @Throws(IOException::class)
    open fun disconnect() {
        socketClient!!.close()
    }
}