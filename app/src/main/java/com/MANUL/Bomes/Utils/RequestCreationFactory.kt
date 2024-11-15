package com.MANUL.Bomes.Utils

import android.util.Log
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData

class RequestCreationFactory() {
    companion object {
        private lateinit var factory: RequestCreationFactory

        @JvmStatic
        public fun create(event: String): UniversalJSONObject? {
            if (!this::factory.isInitialized) factory = RequestCreationFactory()

            return when (event) {
                "setIdentifier" -> factory.setIdentifier()
                "GetUser" -> factory.getUser()
                "GetFriends" -> factory.getFriends()
                "GetUsers" -> factory.getUsers()
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }

        @JvmStatic
        public fun create(event: String, token: String? = null): UniversalJSONObject? {
            if (!this::factory.isInitialized) factory = RequestCreationFactory()

            return when (event) {
                "SetToken" -> factory.setToken(token)
                else -> {
                    Log.e("RequestCreationFactory", "there is no event")
                    return null
                }
            }
        }
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

    private fun setToken(token: String?): UniversalJSONObject {
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