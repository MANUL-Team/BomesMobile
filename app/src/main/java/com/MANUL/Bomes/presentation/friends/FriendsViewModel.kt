package com.MANUL.Bomes.presentation.friends

import android.R
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.Activities.MainActivity
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.databinding.AddedUserItemBinding
import com.MANUL.Bomes.databinding.FragmentFriendsBinding
import com.MANUL.Bomes.databinding.FriendsItemBinding
import com.bumptech.glide.Glide

class FriendsViewModel(
    inflater: LayoutInflater,
    private val activity: FragmentActivity?,
    users: MutableList<User>
) : ViewModel() {
    private val _binding = FragmentFriendsBinding.inflate(inflater)

    init {
        _binding.apply {
            friendsList.adapter = FriendsListAdapter(users, activity)
            friendsList.layoutManager = LinearLayoutManager(activity)
        }
    }

    val binding by lazy { _binding }

}