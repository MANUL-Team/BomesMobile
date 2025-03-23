package com.MANUL.Bomes.presentation.createChat

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.presentation.view.activities.ChatActivity
import com.MANUL.Bomes.domain.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.domain.SimpleObjects.UserData
import com.MANUL.Bomes.presentation.BaseRequestHandler

class CreatingChatRequestHandler(
    activity: FragmentActivity,
    val viewModel: CreatingChatViewModel,
    val pathImage: String
) : BaseRequestHandler(activity) {

    override fun responseReturnFriends(obj: UniversalJSONObject) {
        super.responseReturnFriends(obj)
        viewModel.userAddListCompletion()
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