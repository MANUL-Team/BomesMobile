package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.MANUL.Bomes.R
import com.MANUL.Bomes.Utils.NowRequest
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.friends.FriendsViewModel
import com.MANUL.Bomes.presentation.friends.FriendsWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private lateinit var viewModel: FriendsViewModel

    private lateinit var webSocketListener: FriendsWebSocketListener
    private val okHttpClient by lazy {
        OkHttpClient()
    }
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

        webSocketListener = FriendsWebSocketListener(viewModel) { obj ->
            activity?.runOnUiThread {
                run {
                    if (obj.event == RequestEvent.ReturnFriends) {
                        viewModel.responseReturnFriends(obj)
                    }
                }
            }
        }
        webSocket = okHttpClient.newWebSocket(NowRequest, webSocketListener)

        return viewModel.binding.root
    }
}