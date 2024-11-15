package com.MANUL.Bomes.presentation.friends

import android.R
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.Activities.MainActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.databinding.FragmentFriendsBinding

class FriendsViewModel(
    inflater: LayoutInflater,
    private val activity: FragmentActivity?
) : ViewModel() {
    private val _binding = FragmentFriendsBinding.inflate(inflater)

    private var users: MutableList<User> = mutableListOf()


    init {
        _binding.apply {
            friendsList.adapter = FriendsListAdapter(users)
            friendsList.layoutManager = LinearLayoutManager(activity)
        }
    }

    val binding = _binding

    fun responseReturnFriends(obj: UniversalJSONObject) {
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
    }

    fun responseWrongAuthInIdentifier() {
        Toast.makeText(activity, "Данные авторизации устарели!", Toast.LENGTH_LONG).show()
        UserData.avatar = null
        UserData.identifier = null
        UserData.email = null
        UserData.description = null
        UserData.username = null
        UserData.table_name = null
        UserData.chatId = null
        UserData.chatAvatar = null
        UserData.isLocalChat = 0
        val intent = Intent(activity, MainActivity::class.java)
        activity?.startActivity(intent)
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        activity?.finish()
    }


}