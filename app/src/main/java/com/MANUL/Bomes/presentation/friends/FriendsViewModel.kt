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
import com.MANUL.Bomes.databinding.AddUserItemBinding
import com.MANUL.Bomes.databinding.AddedUserItemBinding
import com.MANUL.Bomes.databinding.FragmentCreatingChatBinding
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class FriendsViewModel(
    inflater: LayoutInflater,
    private val activity: FragmentActivity?
) : ViewModel() {
    private lateinit var _binding: FragmentCreatingChatBinding

    init {
        _binding = FragmentCreatingChatBinding.inflate(inflater)
    }

    val binding = _binding
}