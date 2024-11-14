package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.MANUL.Bomes.R
import com.MANUL.Bomes.databinding.FragmentFriendsBinding
import com.MANUL.Bomes.presentation.createChat.CreatingChatViewModel
import com.MANUL.Bomes.presentation.createChat.CreatingChatWebSocketListener
import com.MANUL.Bomes.presentation.friends.FriendsViewModel
import okhttp3.OkHttpClient
import okhttp3.WebSocket

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private lateinit var viewModel: FriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FriendsViewModel(inflater, activity)

        return viewModel.binding.root
    }
}