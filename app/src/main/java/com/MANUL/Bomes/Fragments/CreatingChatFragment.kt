package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.MANUL.Bomes.R
import com.MANUL.Bomes.databinding.FragmentCreatingChatBinding
import com.MANUL.Bomes.presentation.createChat.CreatingChatWebSocketListener
import com.empire_mammoth.vk_client.presentation.account.CreatingChatViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class CreatingChatFragment : Fragment() {

    private lateinit var binding: FragmentCreatingChatBinding

    private lateinit var webSocketListener: CreatingChatWebSocketListener
    private lateinit var viewModel: CreatingChatViewModel
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[CreatingChatViewModel::class.java]
        webSocketListener = CreatingChatWebSocketListener(viewModel)
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creating_chat, container, false)
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