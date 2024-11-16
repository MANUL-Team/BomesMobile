package com.MANUL.Bomes.presentation.createChat

import android.util.Log
import com.MANUL.Bomes.Activities.UserPageActivity
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.Utils.RequestCreationFactory
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Calendar


class CreatingChatWebSocketListener(
    private val viewModel: CreatingChatViewModel,
    private val messageListener: (UniversalJSONObject) -> Unit
) : WebSocketListener() {
    private val objectMapper by lazy{ ObjectMapper()}

    private var _pathImage: String = ""
    public val pathImage by lazy { _pathImage }

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

    fun processingPhotoUploadRequest(response: retrofit2.Response<ResponseBody>) {
        val reply = response.body()!!.string()
        val obj: UniversalJSONObject = objectMapper.readValue<UniversalJSONObject>(
            reply,
            UniversalJSONObject::class.java
        )
        _pathImage = obj.filePath
        viewModel.insertingImage(obj)
    }

    fun responseChatCreated(obj: UniversalJSONObject) {
        UserData.table_name = obj.table_name
        UserData.chatId = obj.chat_name
        UserData.isLocalChat = 0
        UserData.chatAvatar = _pathImage
        UserData.chatName = obj.chat_name
    }
}