package com.MANUL.Bomes.presentation.createChat

import android.util.Log
import com.MANUL.Bomes.Activities.UserPageActivity
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.SimpleObjects.UserData.table_name
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
    private var objectMapper: ObjectMapper = ObjectMapper()

    private var pathImage: String? = null

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

    fun processingPhotoUploadRequest(response: retrofit2.Response<ResponseBody>) {
        val reply = response.body()!!.string()
        val obj: UniversalJSONObject = objectMapper.readValue<UniversalJSONObject>(
            reply,
            UniversalJSONObject::class.java
        )
        pathImage = obj.filePath
        viewModel.insertingImage(obj)
    }

    fun requestCreateChatForm(addedUserList: MutableList<CreatingChatUser>): String {
        val addingUsers: Array<String?> = arrayOfNulls<String>(addedUserList.size+1)
        var tableName = Calendar.getInstance().time.toString()
        for (i in 0 until addedUserList.size) {
            tableName += "-" + addedUserList[i].user.identifier
            addingUsers[i] = (addedUserList[i].user.identifier)
        }
        addingUsers[addedUserList.size] = UserData.identifier
        tableName = UserPageActivity.md5(tableName)
        //Log.e("requestCreateChatForm", tableName)

        val creatingChat = UniversalJSONObject()
        creatingChat.event = "CreateChat"
        creatingChat.table_name = tableName
        creatingChat.usersToAdd = addingUsers
        //Log.e("requestCreateChatForm", creatingChat.usersToAdd.toString())
        creatingChat.chat_name = viewModel.binding.createChatEditText.text.toString()
        //Log.e("requestCreateChatForm", creatingChat.chat_name)
        creatingChat.isLocalChat = 0
        creatingChat.avatar = pathImage
        creatingChat.owner = UserData.identifier

        return objectMapper.writeValueAsString(creatingChat)
    }
}