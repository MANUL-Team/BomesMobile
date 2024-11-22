package com.MANUL.Bomes.presentation.friends

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.SimpleObjects.UserDataKt
import com.MANUL.Bomes.databinding.FragmentFriendsBinding

class FriendsViewModel(
    inflater: LayoutInflater,
    private val activity: FragmentActivity?
) : ViewModel() {
    private val _binding = FragmentFriendsBinding.inflate(inflater)

    init {
        _binding.apply {
            friendsList.adapter = FriendsListAdapter(UserDataKt.users, activity)
            friendsList.layoutManager = LinearLayoutManager(activity)
        }
    }

    val binding by lazy { _binding }

    fun adapterUpdate() {
        binding.friendsList.adapter?.notifyDataSetChanged()
    }

}