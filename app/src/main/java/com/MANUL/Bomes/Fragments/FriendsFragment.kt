package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.Utils.BoMesWebSocketListener
import com.MANUL.Bomes.Utils.NowRequest
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.friends.FriendsRequestHandler
import com.MANUL.Bomes.presentation.friends.FriendsViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private val users: MutableList<User> = mutableListOf()

    private lateinit var viewModel: FriendsViewModel

    private var webSocket: WebSocket? = null
    private lateinit var webSocketListener: BoMesWebSocketListener
    private val okHttpClient by lazy {
        OkHttpClient()
    }
    private lateinit var requestHandler: FriendsRequestHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FriendsViewModel(inflater, activity, users)

        requestHandler = FriendsRequestHandler(requireActivity(), viewModel,users)
        webSocketListener = BoMesWebSocketListener(requestHandler)
        webSocket = okHttpClient.newWebSocket(NowRequest, webSocketListener)

        return viewModel.binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.adapterUpdate()
    }
}