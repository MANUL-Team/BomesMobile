package com.MANUL.Bomes.presentation.createChat

import android.R
import android.content.Intent
import android.widget.Toast
import com.MANUL.Bomes.Activities.MainActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class CreatingChatWebSocketListener(
    private val viewModel: CreatingChatViewModel,
    private val messageListener: (UniversalJSONObject) -> Unit
) : WebSocketListener() {

    var objectMapper: ObjectMapper = ObjectMapper()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        val obj = UniversalJSONObject()
        obj.event = "setIdentifier"
        obj.identifier = UserData.identifier
        obj.password = UserData.password
        webSocket.send(objectMapper.writeValueAsString(obj))

        val getFriends = UniversalJSONObject()
        getFriends.event = "GetFriends"
        getFriends.identifier = UserData.identifier
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