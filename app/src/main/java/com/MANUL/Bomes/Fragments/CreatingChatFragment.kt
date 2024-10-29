package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.MANUL.Bomes.presentation.createChat.CreatingChatViewModel
import com.MANUL.Bomes.presentation.createChat.CreatingChatWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class CreatingChatFragment : Fragment() {

    private lateinit var webSocketListener: CreatingChatWebSocketListener
    private lateinit var viewModel: CreatingChatViewModel
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel = ViewModelProvider(this)[CreatingChatViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = CreatingChatViewModel(inflater, activity)

        webSocketListener = CreatingChatWebSocketListener(viewModel){obj ->
            activity?.runOnUiThread{
                run {
                    if (obj.event == "ReturnFriends") {
                        viewModel.responseReturnFriends(obj)
                    }
                    else if (obj.event == "ChatCreated") {
                        Log.e("obj.event", "ChatCreated")

                    }
                }
            }
        }
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        return viewModel.binding!!.root
    }


    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, null)
    }

    private fun createRequest(): Request {
        val webSocketUrl = "wss://bomes.ru:8000"
        return Request.Builder()
            .url(webSocketUrl)
            .build()
    }
}