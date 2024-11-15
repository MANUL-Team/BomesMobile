package com.MANUL.Bomes.presentation.friends

import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.Utils.RequestCreationFactory
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class FriendsWebSocketListener(
    private val viewModel: FriendsViewModel,
    private val messageListener: (UniversalJSONObject) -> Unit
) : WebSocketListener() {
    private val objectMapper by lazy{ ObjectMapper()}

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        val obj = RequestCreationFactory.create("setIdentifier")
        webSocket.send(objectMapper.writeValueAsString(obj))

        val getFriends = RequestCreationFactory.create("GetFriends")
        webSocket.send(objectMapper.writeValueAsString(getFriends))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val obj = objectMapper.readValue(
            text,
            UniversalJSONObject::class.java
        )

        if (obj.event == "WrongAuthInIdentifier") {
            viewModel.responseWrongAuthInIdentifier()
            webSocket.close(1000, null)
        }

        messageListener.invoke(obj)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }
}