package com.MANUL.Bomes.Utils

import okhttp3.Request

private val BomesRequest = Request.Builder().url("wss://bomes.ru:8000").build()
private val TestServerRequest = Request.Builder().url("ws://192.168.31.24:8000").build()

@JvmField
val NowRequest = TestServerRequest


object RequestEvent{
    const val ConnectUser = "ConnectUser"
    const val GetUser = "GetUser"
    const val GetFriends = "GetFriends"
    const val GetUsers = "GetUsers"
    const val SetChat = "SetChat"
    const val GetStickers = "GetStickers"
    const val GetReactions = "GetReactions"
    const val GetChatUsers = "GetChatUsers"
    const val SendRegCode = "SendRegCode"
    const val GetCurrentAndroidVersion = "GetCurrentAndroidVersion"
    const val AddFriend = "AddFriend"
    const val RemoveFriend = "RemoveFriend"
    const val GetUserChats = "GetUserChats"
    const val GetPartner = "GetPartner"
    const val SetToken = "SetToken"
    const val IsUserOnline = "IsUserOnline"
    const val ReadMessage = "ReadMessage"
    const val Typing = "Typing"
    const val GetChatMessages = "GetChatMessages"
    const val ConfirmingEmail = "ConfirmingEmail"
    const val UpdateValue = "UpdateValue"
    const val EditMessage = "EditMessage"
    const val DeleteMessage = "DeleteMessage"
    const val AddReaction = "AddReaction"
    const val RemoveReaction = "RemoveReaction"
    const val SendMessage = "SendMessage"
    const val Login = "Login"
    const val CheckPrefsIdentifier = "CheckPrefsIdentifier"
    const val UpdateUserData = "UpdateUserData"
    const val CreateChat = "CreateChat"

    const val WrongAuthInIdentifier = "WrongAuthInIdentifier"
    const val ReturnFriends = "ReturnFriends"
    const val ChatCreated = "ChatCreated"
    const val ReturnUser = "ReturnUser"
    const val ReturnUserChats = "ReturnUserChats"
    const val Notification = "Notification"
    const val MatDetected = "MatDetected"
    const val WithoutMats = "WithoutMats"
    const val ReturnUsers = "ReturnUsers"
    const val ReturnOnline = "ReturnOnline"
    const val ReturnChatMessages = "ReturnChatMessages"
    const val MessageIsRead = "MessageIsRead"
    const val ReturnStickers = "ReturnStickers"
    const val ReturnReactions = "ReturnReactions"
    const val ReturnChatUsers = "ReturnChatUsers"
    const val EditMessageForUsers = "EditMessageForUsers"
    const val DeleteMessageForUsers = "DeleteMessageForUsers"
    const val ReactionForUsers = "ReactionForUsers"
    const val WrongCode = "WrongCode"
    const val RightCode = "RightCode"
    const val TruePassword = "TruePassword"
    const val WrongPassword = "WrongPassword"
    const val ReturnCurrentAndroidVersion = "ReturnCurrentAndroidVersion"
}
