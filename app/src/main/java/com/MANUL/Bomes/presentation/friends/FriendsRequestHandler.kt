package com.MANUL.Bomes.presentation.friends

import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.presentation.BaseRequestHandler

class FriendsRequestHandler(
    activity: FragmentActivity,
    val viewModel: FriendsViewModel
) : BaseRequestHandler(activity) {

    override fun responseReturnFriends(obj: UniversalJSONObject) {
        super.responseReturnFriends(obj)
        viewModel.binding.friendsList.adapter?.notifyDataSetChanged()
    }
}