package com.MANUL.Bomes.Utils

import okhttp3.OkHttpClient
import okhttp3.WebSocket

object BoMesWebSocket {
    private var webSocket: WebSocket? = null
    fun get(): WebSocket {
        if (webSocket == null) {
            val okHttpClient = OkHttpClient()
            webSocket = okHttpClient.newWebSocket(NowRequest, BoMesWebSocketListener.get())
        }
        return webSocket!!
    }
    fun reconnect(){
        val client: OkHttpClient = OkHttpClient.Builder().build()
        webSocket = client.newWebSocket(NowRequest, BoMesWebSocketListener.get())
    }
    fun close(){
        webSocket?.close(1000, null)
        webSocket = null
    }
}