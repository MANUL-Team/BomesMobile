package com.MANUL.Bomes.presentation.friends

import android.util.Log
import com.MANUL.Bomes.Activities.UserPageActivity
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Calendar


class FriendsWebSocketListener(
    private val viewModel: FriendsViewModel,
    private val messageListener: (UniversalJSONObject) -> Unit
) : WebSocketListener() {
    private var objectMapper: ObjectMapper = ObjectMapper()

    private var pathImage: String = ""

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