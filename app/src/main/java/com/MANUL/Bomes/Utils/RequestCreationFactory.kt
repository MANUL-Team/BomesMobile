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
                "setIdentifier" -> factory.setIdentifier()
                "GetUser" -> factory.getUser()
                "GetFriends" -> factory.getFriends()
                "GetUsers" -> factory.getUsers()
                "setChat" -> factory.setChat()
                "GetStickers" -> factory.getStickers()
                "GetReactions" -> factory.getReactions()
                "GetChatUsers" -> factory.getChatUsers()
                "GetPartner" -> factory.getPartner()
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
                "ConfirmingEmail"-> factory.confirmingEmail(argument)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(event: String, argument: String, id: Long): UniversalJSONObject? {
            return when (event) {
                "EditMessage"-> factory.editMessage(argument, id)
                "DeleteMessage"-> factory.deleteMessage(id)
                "AddReaction"-> factory.addReaction(argument, id)
                "RemoveReaction"-> factory.removeReaction(id)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(
            event: String,
            type: String,
            messageText: String,
            replyingMessage: Message?
        ): UniversalJSONObject? {
            return when (event) {
                "message" -> factory.message(type,messageText, replyingMessage)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }
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
        msg.id = id
        msg.chat = UserData.table_name
        msg.event = "DeleteMessage"
        return msg
    }

    private fun editMessage(argument: String, id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
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

    private fun message(type: String,messageText: String, replyingMessage: Message?): UniversalJSONObject {
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
        msg.event = "message"

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
        setChat.event = "setChat"
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

    private fun setIdentifier(): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.event = "setIdentifier"
        obj.identifier = UserData.identifier
        obj.password = UserData.password
        return obj
    }


}