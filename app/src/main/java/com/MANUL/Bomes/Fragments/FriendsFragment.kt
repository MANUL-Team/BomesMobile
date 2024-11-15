package com.MANUL.Bomes.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.MANUL.Bomes.Activities.ChatActivity
import com.MANUL.Bomes.R
import com.MANUL.Bomes.databinding.FragmentFriendsBinding
import com.MANUL.Bomes.presentation.createChat.CreatingChatViewModel
import com.MANUL.Bomes.presentation.createChat.CreatingChatWebSocketListener
import com.MANUL.Bomes.presentation.friends.FriendsViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private lateinit var viewModel: FriendsViewModel

    private lateinit var webSocketListener: CreatingChatWebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FriendsViewModel(inflater, activity)

        webSocketListener = CreatingChatWebSocketListener(viewModel as CreatingChatViewModel) { obj ->
            activity?.runOnUiThread {
                run {
                    if (obj.event == "ReturnFriends") {
                        viewModel.responseReturnFriends(obj)
                    }
                }
            }
        }
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        return viewModel.binding.root
    }

    private fun createRequest(): Request {
        val webSocketUrl = "wss://bomes.ru:8000"
        return Request.Builder()
            .url(webSocketUrl)
            .build()
    }
}