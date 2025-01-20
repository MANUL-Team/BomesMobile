package com.MANUL.Bomes.presentation.friends

import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.SimpleObjects.UserDataKt
import com.MANUL.Bomes.presentation.BaseRequestHandler
import okhttp3.WebSocket

class FriendsRequestHandler(
    activity: FragmentActivity,
    webSocket: WebSocket,
    val viewModel: FriendsViewModel
) : BaseRequestHandler(activity, webSocket) {

    override fun responseReturnFriends(obj: UniversalJSONObject) {
        UserDataKt.users.clear()
        for (jsonObject in obj.users) {
            val user = User(
                jsonObject.username,
                jsonObject.avatar,
                jsonObject.identifier,
                jsonObject.friendsCount
            )

            UserDataKt.users.add(user)
        }
        viewModel.binding.friendsList.adapter?.notifyDataSetChanged()
    }
}