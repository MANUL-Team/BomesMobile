package com.MANUL.Bomes.Utils

import android.util.Log
import com.MANUL.Bomes.Activities.UserPageActivity
import com.MANUL.Bomes.SimpleObjects.ConfirmationUser
import com.MANUL.Bomes.SimpleObjects.Message
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.fasterxml.jackson.databind.ObjectMapper

class RequestCreationFactory {
    companion object {
        private val factory: RequestCreationFactory by lazy {
            RequestCreationFactory()
        }

        @JvmStatic
        fun create(event: String): UniversalJSONObject? {
            return when (event) {
                RequestEvent.ConnectUser -> factory.connectUser()
                RequestEvent.GetUser -> factory.getUser()
                RequestEvent.GetFriends -> factory.getFriends()
                RequestEvent.GetUsers -> factory.getUsers()
                RequestEvent.OpenChat -> factory.openChat()
                RequestEvent.CloseChat -> factory.closeChat()
                RequestEvent.GetStickers -> factory.getStickers()
                RequestEvent.GetReactions -> factory.getReactions()
                RequestEvent.GetChatUsers -> factory.getChatUsers()
                RequestEvent.SendRegCode -> factory.sendRegCode()
                RequestEvent.GetCurrentAndroidVersion -> factory.getCurrentAndroidVersion()
                RequestEvent.AddFriend-> factory.addFriend()
                RequestEvent.RemoveFriend-> factory.removeFriend()
                RequestEvent.GetUserChats-> factory.getUserChats()
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        fun create(event: String, argument: String): UniversalJSONObject? {
            return when (event) {
                RequestEvent.GetPartner -> factory.getPartner(argument)
                RequestEvent.SetToken -> factory.setToken(argument)
                RequestEvent.IsUserOnline -> factory.isUserOnline(argument)
                RequestEvent.ReadMessage -> factory.readMessage(argument)
                RequestEvent.Typing -> factory.typing(argument)
                RequestEvent.GetChatMessages -> factory.getChatMessages(argument)
                RequestEvent.ConfirmingEmail -> factory.confirmingEmail(argument)
                RequestEvent.UpdateValue-> factory.updateValue(argument)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        fun create(event: String, argument1: Long, argument2: String): UniversalJSONObject? {
            return when (event) {
                RequestEvent.GetUsers -> factory.getUsers(argument1, argument2)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        fun create(event: String, argument: String, id: Long): UniversalJSONObject? {
            return when (event) {
                RequestEvent.EditMessage -> factory.editMessage(argument, id)
                RequestEvent.DeleteMessage -> factory.deleteMessage(id)
                RequestEvent.AddReaction -> factory.addReaction(argument, id)
                RequestEvent.RemoveReaction -> factory.removeReaction(id)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        fun create(
            event: String,
            argument1: String,
            argument2: String,
            replyingMessage: Message?
        ): UniversalJSONObject? {
            return when (event) {
                RequestEvent.SendMessage -> factory.sendMessage(argument1, argument2, replyingMessage)
                RequestEvent.Login -> factory.login(argument1, argument2)
                RequestEvent.CheckPrefsIdentifier -> factory.checkPrefsIdentifier(argument1, argument2)
                RequestEvent.UpdateUserData-> factory.updateUserData(argument1, argument2)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        fun create(
            event: String,
            tableName: String,
            usersToAdd: Array<String?>,
            chatName: String,
            isLocalChat: Int,
            pathImage: String
        ): UniversalJSONObject? {
            return when (event) {
                RequestEvent.CreateChat -> factory.createChat(
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

    private fun updateValue(argument: String): UniversalJSONObject {
        val updAvatar = UniversalJSONObject()
        updAvatar.table = "users"
        updAvatar.column = "identifier"
        updAvatar.where = UserData.identifier
        updAvatar.variable = "avatar"
        updAvatar.value = argument
        updAvatar.event = RequestEvent.UpdateValue
        return updAvatar
    }

    private fun updateUserData(name: String, description: String): UniversalJSONObject {
        val saveData = UniversalJSONObject()
        saveData.name = name
        saveData.description = description
        saveData.identifier = UserData.identifier
        saveData.event = RequestEvent.UpdateUserData
        return saveData
    }

    private fun getUserChats(): UniversalJSONObject {
        val loadChats = UniversalJSONObject()
        loadChats.event = RequestEvent.GetUserChats
        loadChats.identifier = UserData.identifier
        loadChats.password = UserData.password
        return loadChats
    }

    private fun removeFriend(): UniversalJSONObject {
        val removeFriendObj = UniversalJSONObject()
        removeFriendObj.identifier = UserPageActivity.openedUser.identifier
        removeFriendObj.request_identifier = UserData.identifier
        removeFriendObj.request_password = UserData.password
        removeFriendObj.event = RequestEvent.RemoveFriend
        return removeFriendObj
    }

    private fun addFriend(): UniversalJSONObject {
        val addFriendObj = UniversalJSONObject()
        addFriendObj.identifier = UserPageActivity.openedUser.identifier
        addFriendObj.request_identifier = UserData.identifier
        addFriendObj.request_password = UserData.password
        addFriendObj.event = RequestEvent.AddFriend
        return addFriendObj
    }

    private fun createChat(
        tableName: String,
        usersToAdd: Array<String?>,
        chatName: String,
        isLocalChat: Int,
        pathImage: String
    ): UniversalJSONObject {
        val createChat = UniversalJSONObject()
        createChat.table_name = tableName
        createChat.event = RequestEvent.CreateChat
        createChat.usersToAdd = usersToAdd
        createChat.chat_name = chatName
        createChat.isLocalChat = isLocalChat
        if (isLocalChat != 1) {
            createChat.avatar = pathImage
            createChat.owner = UserData.identifier
        }
        return createChat
    }

    private fun getCurrentAndroidVersion(): UniversalJSONObject {
        val loadVersion = UniversalJSONObject()
        loadVersion.event = RequestEvent.GetCurrentAndroidVersion
        return loadVersion
    }

    private fun sendRegCode(): UniversalJSONObject {
        val regUser = UniversalJSONObject()
        regUser.email = ConfirmationUser.email
        regUser.password = ConfirmationUser.password
        regUser.username = ConfirmationUser.username
        regUser.event = RequestEvent.SendRegCode
        return regUser
    }

    private fun checkPrefsIdentifier(identifier: String, password: String): UniversalJSONObject {
        val loadMe = UniversalJSONObject()
        loadMe.event = RequestEvent.GetUser
        loadMe.identifier = identifier
        loadMe.request_identifier = identifier
        loadMe.request_password = password
        return loadMe
    }

    private fun login(email: String, password: String): UniversalJSONObject {
        val sendObj = UniversalJSONObject()
        sendObj.event = RequestEvent.Login
        sendObj.email = email
        sendObj.password = password
        return sendObj

    }

    private fun confirmingEmail(argument: String): UniversalJSONObject {
        val confirmingEmail = UniversalJSONObject()
        confirmingEmail.email = ConfirmationUser.email
        confirmingEmail.code = argument.toInt()
        confirmingEmail.event = RequestEvent.ConfirmingEmail
        return confirmingEmail
    }

    private fun removeReaction(id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.msgId = id
        msg.sender = UserData.identifier
        msg.chat = UserData.table_name
        msg.event = RequestEvent.RemoveReaction
        return msg
    }

    private fun addReaction(argument: String, id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.msgId = id
        msg.type = argument
        msg.sender = UserData.identifier
        msg.chat = UserData.table_name
        msg.event = RequestEvent.AddReaction
        return msg
    }

    private fun deleteMessage(id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.identifier = UserData.identifier
        msg.password = UserData.password
        msg.id = id
        msg.chat = UserData.table_name
        msg.event = RequestEvent.DeleteMessage
        return msg
    }

    private fun editMessage(argument: String, id: Long): UniversalJSONObject {
        val msg = UniversalJSONObject()
        msg.identifier = UserData.identifier
        msg.password = UserData.password
        msg.value = argument
        msg.id = id
        msg.chat = UserData.table_name
        msg.event = RequestEvent.EditMessage
        return msg
    }

    private fun getChatMessages(argument: String): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.table_name = UserData.table_name
        obj.identifier = UserData.identifier
        obj.password = UserData.password
        obj.loadedMessages = argument.toLong()
        obj.event = RequestEvent.GetChatMessages
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
        msg.event = RequestEvent.SendMessage

        return msg
    }

    private fun typing(argument: String): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.chat = UserData.table_name
        obj.username = UserData.username
        obj.identifier = UserData.identifier
        obj.typingType = argument
        obj.event = RequestEvent.Typing
        return obj
    }

    private fun readMessage(argument: String): UniversalJSONObject {
        val readMsg = UniversalJSONObject()
        readMsg.chat = UserData.table_name
        readMsg.id = argument.toLong()
        readMsg.event = RequestEvent.ReadMessage
        return readMsg
    }

    private fun isUserOnline(argument: String): UniversalJSONObject {
        val isUserOnline = UniversalJSONObject()
        isUserOnline.event = RequestEvent.IsUserOnline
        isUserOnline.identifier = argument
        return isUserOnline
    }

    private fun getPartner(argument: String): UniversalJSONObject {
        val loadOther = UniversalJSONObject()
        loadOther.event = RequestEvent.GetUser
        loadOther.identifier = argument
        loadOther.request_identifier = UserData.identifier
        loadOther.request_password = UserData.password
        return loadOther
    }

    private fun getChatUsers(): UniversalJSONObject {
        val getChatUsers = UniversalJSONObject()
        getChatUsers.event = RequestEvent.GetChatUsers
        getChatUsers.table_name = UserData.table_name
        getChatUsers.identifier = UserData.identifier
        getChatUsers.password = UserData.password
        return getChatUsers
    }

    private fun getReactions(): UniversalJSONObject {
        val getReactions = UniversalJSONObject()
        getReactions.event = RequestEvent.GetReactions
        return getReactions
    }

    private fun getStickers(): UniversalJSONObject {
        val getStickers = UniversalJSONObject()
        getStickers.event = RequestEvent.GetStickers
        return getStickers
    }

    private fun openChat(): UniversalJSONObject {
        val setChat = UniversalJSONObject()
        setChat.event = RequestEvent.OpenChat
        setChat.chat_name = UserData.table_name
        return setChat
    }
    private fun closeChat(): UniversalJSONObject {
        val setChat = UniversalJSONObject()
        setChat.event = RequestEvent.CloseChat
        return setChat
    }

    private fun getUsers(): UniversalJSONObject {
        val getUsers = UniversalJSONObject()
        getUsers.event = RequestEvent.GetUsers
        getUsers.identifier = UserData.identifier
        getUsers.password = UserData.password
        return getUsers
    }

    private fun getUsers(skip: Long, search: String): UniversalJSONObject{
        val getUsers = UniversalJSONObject()
        getUsers.event = RequestEvent.GetUsers
        getUsers.identifier = UserData.identifier
        getUsers.password = UserData.password
        getUsers.skip = skip
        getUsers.search = search
        return getUsers
    }

    private fun getFriends(): UniversalJSONObject {
        val getFriends = UniversalJSONObject()
        getFriends.event = RequestEvent.GetFriends
        getFriends.identifier = UserData.identifier
        getFriends.request_identifier = UserData.identifier
        getFriends.request_password = UserData.password
        return getFriends
    }

    private fun setToken(token: String): UniversalJSONObject {
        val setToken = UniversalJSONObject()
        setToken.identifier = UserData.identifier
        setToken.password = UserData.password
        setToken.token = token
        setToken.event = RequestEvent.SetToken
        return setToken
    }

    private fun getUser(): UniversalJSONObject {
        val loadMe = UniversalJSONObject()
        loadMe.event = RequestEvent.GetUser
        loadMe.identifier = UserData.identifier
        loadMe.request_identifier = UserData.identifier
        loadMe.request_password = UserData.password
        return loadMe
    }

    private fun connectUser(): UniversalJSONObject {
        val obj = UniversalJSONObject()
        obj.event = RequestEvent.ConnectUser
        obj.identifier = UserData.identifier
        obj.password = UserData.password
        return obj
    }


}