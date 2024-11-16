package com.MANUL.Bomes.Utils

import android.util.Log
import com.MANUL.Bomes.SimpleObjects.ConfirmationUser
import com.MANUL.Bomes.SimpleObjects.Message
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.fasterxml.jackson.databind.ObjectMapper

class RequestCreationFactory() {
    companion object {
        private val factory: RequestCreationFactory by lazy {
            RequestCreationFactory()
        }

        @JvmStatic
        public fun create(event: String): UniversalJSONObject? {
            return when (event) {
                "setIdentifier" -> factory.connectUser()
                "GetUser" -> factory.getUser()
                "GetFriends" -> factory.getFriends()
                "GetUsers" -> factory.getUsers()
                "setChat" -> factory.setChat()
                "GetStickers" -> factory.getStickers()
                "GetReactions" -> factory.getReactions()
                "GetChatUsers" -> factory.getChatUsers()
                "GetPartner" -> factory.getPartner()
                "SendRegCode" -> factory.sendRegCode()
                "GetCurrentAndroidVersion" -> factory.getCurrentAndroidVersion()
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(event: String, argument: String): UniversalJSONObject? {
            return when (event) {
                "SetToken" -> factory.setToken(argument)
                "IsUserOnline" -> factory.isUserOnline(argument)
                "ReadMessage" -> factory.readMessage(argument)
                "Typing" -> factory.typing(argument)
                "GetChatMessages" -> factory.getChatMessages(argument)
                "ConfirmingEmail" -> factory.confirmingEmail(argument)
                "checkPrefsIdentifier" -> factory.checkPrefsIdentifier(argument)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(event: String, argument: String, id: Long): UniversalJSONObject? {
            return when (event) {
                "EditMessage" -> factory.editMessage(argument, id)
                "DeleteMessage" -> factory.deleteMessage(id)
                "AddReaction" -> factory.addReaction(argument, id)
                "RemoveReaction" -> factory.removeReaction(id)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(
            event: String,
            argument1: String,
            argument2: String,
            replyingMessage: Message?
        ): UniversalJSONObject? {
            return when (event) {
                "message" -> factory.sendMessage(argument1, argument2, replyingMessage)
                "login" -> factory.login(argument1, argument2)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(
            event: String,
            tableName: String,
            usersToAdd: Array<String?>,
            chatName: String,
            isLocalChat: Int,
            pathImage: String
        ): UniversalJSONObject? {
            return when (event) {
                "CreateChat" -> factory.createChat(
                    tableName,
                    usersToAdd,
                    chatName,
                    isLocalChat,
                    pathImage
                )

                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }
    }

    private fun createChat(
        tableName: String,
        usersToAdd: Array<String?>,
        chatName: String,
        isLocalChat: Int,
        pathImage: String
    ): UniversalJSONObject {
        val creatingChat = UniversalJSONObject()
        creatingChat.event = "CreateChat"
        creatingChat.table_name = tableName
        creatingChat.usersToAdd = usersToAdd
        creatingChat.chat_name = chatName
        creatingChat.isLocalChat = isLocalChat
        creatingChat.avatar = pathImage
        creatingChat.owner = UserData.identifier
        return creatingChat
    }

    private fun getCurrentAndroidVersion(): UniversalJSONObject {
        val loadVersion = UniversalJSONObject()
        loadVersion.event = "GetCurrentAndroidVersion"
        return loadVersion
    }

    private fun sendRegCode(): UniversalJSONObject {
        val regUser = UniversalJSONObject()
        regUser.email = ConfirmationUser.email
        regUser.password = ConfirmationUser.password
        regUser.username = ConfirmationUser.username
        regUser.event = "SendRegCode"
        return regUser
    }

    private fun checkPrefsIdentifier(identifier: String): UniversalJSONObject? {
        val loadMe = UniversalJSONObject()
        loadMe.event = "GetUser"
        loadMe.identifier = identifier
        loadMe.friendId = identifier
        return loadMe
    }

    private fun login(email: String, password: String): UniversalJSONObject {
        val sendObj = UniversalJSONObject()
        sendObj.event = "Login"
        sendObj.email = email
        sendObj.password = password
        return sendObj

    }

    private fun confirmingEmail(argument: String): UniversalJSONObject {
        val confirmingEmail = UniversalJSONObject()
        confirmingEmail.email = ConfirmationUser.email
        confirmingEmail.code = argument.toInt()
        confirmingEmail.event = "ConfirmingEmail"
        return confirmingEmail
    }

    private fun removeReaction(id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.msgId = id
        msg.sender = UserData.identifier
        msg.chat = UserData.table_name
        msg.event = "RemoveReaction"
        return msg
    }

    private fun addReaction(argument: String, id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.msgId = id
        msg.type = argument
        msg.sender = UserData.identifier
        msg.chat = UserData.table_name
        msg.event = "AddReaction"
        return msg
    }

    private fun deleteMessage(id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.identifier = UserData.identifier
        msg.password = UserData.password
        msg.id = id
        msg.chat = UserData.table_name
        msg.event = "DeleteMessage"
        return msg
    }

    private fun editMessage(argument: String, id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.identifier = UserData.identifier
        msg.password = UserData.password
        msg.value = argument
        msg.id = id
        msg.chat = UserData.table_name
        msg.event = "EditMessage"
        return msg
    }

    private fun getChatMessages(argument: String): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.table_name = UserData.table_name
        obj.identifier = UserData.identifier
        obj.loadedMessages = argument.toLong()
        obj.event = "GetChatMessages"
        return obj
    }

    private fun sendMessage(
        type: String,
        messageText: String,
        replyingMessage: Message?
    ): UniversalJSONObject {
        val objectMapper = ObjectMapper()
        val msg = UniversalJSONObject()
        msg.sender = UserData.identifier
        msg.dataType = type
        msg.value = messageText
        if (replyingMessage == null) msg.reply = ""
        else {
            val replyMsg = UniversalJSONObject()
            replyMsg.sender = replyingMessage.sender
            replyMsg.value = replyingMessage.value
            replyMsg.dataType = replyingMessage.dataType
            replyMsg.chat = UserData.table_name
            replyMsg.username = replyingMessage.username
            replyMsg.isRead = replyingMessage.isRead
            replyMsg.id = replyingMessage.id
            msg.reply = objectMapper.writeValueAsString(replyMsg)
        }
        msg.chat = UserData.table_name
        msg.username = UserData.username
        msg.isRead = 0
        msg.event = "SendMessage"

        return msg
    }

    private fun typing(argument: String): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.chat = UserData.table_name
        obj.username = UserData.username
        obj.identifier = UserData.identifier
        obj.typingType = argument
        obj.event = "Typing"
        return obj
    }

    private fun readMessage(argument: String): UniversalJSONObject {
        val readMsg = UniversalJSONObject()
        readMsg.chat = UserData.table_name
        readMsg.id = argument.toLong()
        readMsg.event = "ReadMessage"
        return readMsg
    }

    private fun isUserOnline(argument: String): UniversalJSONObject {
        val isUserOnline = UniversalJSONObject()
        isUserOnline.event = "IsUserOnline"
        isUserOnline.identifier = argument
        return isUserOnline
    }

    private fun getPartner(): UniversalJSONObject {
        val loadOther = UniversalJSONObject()
        loadOther.event = "GetUser"
        loadOther.identifier = UserData.identifier
        loadOther.friendId = UserData.chatId
        return loadOther
    }

    private fun getChatUsers(): UniversalJSONObject {
        val getChatUsers = UniversalJSONObject()
        getChatUsers.event = "GetChatUsers"
        getChatUsers.table_name = UserData.table_name
        getChatUsers.identifier = UserData.identifier
        return getChatUsers
    }

    private fun getReactions(): UniversalJSONObject {
        val getReactions = UniversalJSONObject()
        getReactions.event = "GetReactions"
        return getReactions
    }

    private fun getStickers(): UniversalJSONObject {
        val getStickers = UniversalJSONObject()
        getStickers.event = "GetStickers"
        return getStickers
    }

    private fun setChat(): UniversalJSONObject {
        val setChat = UniversalJSONObject()
        setChat.event = "SetChat"
        setChat.chatName = UserData.table_name
        return setChat
    }

    private fun getUsers(): UniversalJSONObject {
        val getUsers = UniversalJSONObject()
        getUsers.event = "GetUsers"
        return getUsers
    }

    private fun getFriends(): UniversalJSONObject {
        val getFriends = UniversalJSONObject()
        getFriends.event = "GetFriends"
        getFriends.identifier = UserData.identifier
        return getFriends
    }

    private fun setToken(token: String): UniversalJSONObject {
        val setToken = UniversalJSONObject()
        setToken.identifier = UserData.identifier
        setToken.password = UserData.password
        setToken.token = token
        setToken.event = "SetToken"
        return setToken
    }

    private fun getUser(): UniversalJSONObject {
        val loadMe = UniversalJSONObject()
        loadMe.event = "GetUser"
        loadMe.identifier = UserData.identifier
        loadMe.friendId = UserData.identifier
        return loadMe
    }

    private fun connectUser(): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.event = "ConnectUser"
        obj.identifier = UserData.identifier
        obj.password = UserData.password
        return obj
    }


}