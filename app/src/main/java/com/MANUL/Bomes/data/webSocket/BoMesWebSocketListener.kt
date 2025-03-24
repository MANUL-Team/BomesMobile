package com.MANUL.Bomes.data.webSocket

import com.MANUL.Bomes.domain.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.domain.Utils.RequestCreationFactory
import com.MANUL.Bomes.domain.Utils.RequestEvent
import com.MANUL.Bomes.presentation.BaseRequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
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