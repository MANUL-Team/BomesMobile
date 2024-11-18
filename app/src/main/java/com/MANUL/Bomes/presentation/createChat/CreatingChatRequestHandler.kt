package com.MANUL.Bomes.presentation.createChat

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.Activities.ChatActivity
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.presentation.BaseRequestHandler

class CreatingChatRequestHandler(
    activity: FragmentActivity,
    val viewModel: CreatingChatViewModel,
    val userAddList: MutableList<CreatingChatUser>,
    val pathImage: String
) : BaseRequestHandler(activity) {

    override fun responseReturnFriends(obj: UniversalJSONObject) {
        var users: MutableList<User> = mutableListOf()
        for (jsonObject in obj.users) {
            val user = User(
                jsonObject.username,
                jsonObject.avatar,
                jsonObject.identifier,
                jsonObject.friendsCount
            )

            users.add(user)
        }

        for (i in 0..<users.size) userAddList.add(
            CreatingChatUser(
                users[i],
                false
            )
        )

        viewModel.binding.addUserList.adapter?.notifyDataSetChanged()
    }

    override fun chatCreated(obj: UniversalJSONObject) {
        UserData.table_name = obj.table_name
        UserData.chatId = obj.chat_name
        UserData.isLocalChat = 0
        UserData.chatAvatar = pathImage
        UserData.chatName = obj.chat_name
        val intent = Intent(
            activity,
            ChatActivity::class.java
        )
        activity.startActivity(intent)
        activity.finish()
    }
}