package com.MANUL.Bomes.Activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.MANUL.Bomes.R
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

        webSocketListener = BoMesWebSocketListener()
        webSocket = okHttpClient.newWebSocket(NowRequest, webSocketListener)
        requestHandler = FriendsRequestHandler(this, webSocket!!, viewModel)
        webSocketListener.setRequestHandler(requestHandler)
    }

    override fun onResume() {
        super.onResume()

        val objectMapper = ObjectMapper()
        val obj = RequestCreationFactory.create(RequestEvent.GetFriends)
        webSocket?.send(objectMapper.writeValueAsString(obj))

        viewModel.adapterUpdate()
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.nothing,
                R.anim.activity_switch_reverse_first
            )
        } else
            overridePendingTransition(R.anim.nothing, R.anim.activity_switch_reverse_first)
    }
}