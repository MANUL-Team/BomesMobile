package com.MANUL.Bomes.Utils

import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.Utils.RequestCreationFactory.Companion.create
import com.MANUL.Bomes.presentation.BaseRequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class BoMesWebSocketListener : WebSocketListener() {
    private val objectMapper by lazy{ ObjectMapper() }
    private var requestHandler: BaseRequestHandler? = null

    fun setRequestHandler(newRequestHandler: BaseRequestHandler?){
        requestHandler = newRequestHandler
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        val obj = RequestCreationFactory.create(RequestEvent.ConnectUser)
        webSocket.send(objectMapper.writeValueAsString(obj))

        val getFriends = RequestCreationFactory.create(RequestEvent.GetFriends)
        webSocket.send(objectMapper.writeValueAsString(getFriends))

        val loadMe = create(RequestEvent.GetUser)
        webSocket.send(objectMapper.writeValueAsString(loadMe))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val obj = objectMapper.readValue(
            text,
            UniversalJSONObject::class.java
        )

        requestHandler?.start(obj)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        BoMesWebSocket.reconnect()
    }

    fun getFriends(){    }

    companion object{
        private var webSocketListener: BoMesWebSocketListener? = null
        fun get(): BoMesWebSocketListener {
            if (webSocketListener == null) {
                webSocketListener = BoMesWebSocketListener()
            }
            return webSocketListener!!
        }
    }
}