package com.MANUL.Bomes.presentation.createChat

import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.Utils.RequestCreationFactory
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.friends.FriendsRequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class CreatingChatWebSocketListener(
    private val requestHandler: CreatingChatRequestHandler,
) : WebSocketListener() {
    private val objectMapper by lazy{ ObjectMapper()}

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        val obj = RequestCreationFactory.create(RequestEvent.ConnectUser)
        webSocket.send(objectMapper.writeValueAsString(obj))

        val getFriends = RequestCreationFactory.create(RequestEvent.GetFriends)
        webSocket.send(objectMapper.writeValueAsString(getFriends))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val obj = objectMapper.readValue(
            text,
            UniversalJSONObject::class.java
        )

        requestHandler.start(obj)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }
}