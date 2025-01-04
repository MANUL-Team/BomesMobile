package com.MANUL.Bomes.Fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.Utils.BoMesWebSocketListener
import com.MANUL.Bomes.Utils.NowRequest
import com.MANUL.Bomes.Utils.RequestCreationFactory
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.friends.FriendsRequestHandler
import com.MANUL.Bomes.presentation.friends.FriendsViewModel
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.WebSocket

class FriendsActivity : AppCompatActivity(R.layout.activity_friends) {

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
        viewModel = FriendsViewModel(layoutInflater, this)
        setContentView(viewModel.binding.root)

        requestHandler = FriendsRequestHandler(this, viewModel)
        webSocketListener = BoMesWebSocketListener(requestHandler)
        webSocket = okHttpClient.newWebSocket(NowRequest, webSocketListener)
    }

    override fun onResume() {
        super.onResume()

        val objectMapper = ObjectMapper()
        val obj = RequestCreationFactory.create(RequestEvent.GetFriends)
        webSocket?.send(objectMapper.writeValueAsString(obj))

        viewModel.adapterUpdate()
    }
}