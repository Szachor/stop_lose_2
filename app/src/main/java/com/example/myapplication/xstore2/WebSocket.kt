package com.example.myapplication.xstore2

//import com.example.myapplication.xstore2.model.Movie
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
        socketWriter = PrintStream(socketClient!!.getOutputStream())
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

    @Throws(IOException::class)
    open fun getNextMessage(): String? {
        val response = StringBuilder()

        do {
            // Sometimes server close the connection. socketClient!!.isOutputShutdown checks if the output is closed
            // Without calling isOutputShutdown the "socketReader.readLine()" method blocks thread (without possibility to close)
            // With isOutputShutdown the "socketReader.readLine()" method returns null if socket is closed
            // In both cases isOutputShutdown returns false
            var x1 = socketClient!!.isOutputShutdown
            val line = socketReader.readLine() ?: throw IOException("OutputShutdown Exception - probably webSocket closed by server")
            response.append(line)
        } while (line == "")
        return response.toString()
    }

    open val isConnected
        get() = !socketClient!!.isClosed

    @Throws(IOException::class)
    open fun disconnect() {
        socketClient!!.close()
    }
}