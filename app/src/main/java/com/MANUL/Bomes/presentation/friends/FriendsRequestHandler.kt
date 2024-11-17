package com.MANUL.Bomes.presentation.friends

import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.presentation.BaseRequestHandler

class FriendsRequestHandler(activity: FragmentActivity,val viewModel: FriendsViewModel, val users: MutableList<User>) : BaseRequestHandler(activity) {

    override fun responseReturnFriends(obj: UniversalJSONObject) {
        users.clear()
        for (jsonObject in obj.users) {
            val user = User(
                jsonObject.username,
                jsonObject.avatar,
                jsonObject.identifier,
                jsonObject.friendsCount
            )

            users.add(user)
        }
        viewModel.binding.friendsList.adapter?.notifyDataSetChanged()
    }
}