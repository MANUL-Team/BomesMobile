package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.databinding.AddUserItemBinding
import com.MANUL.Bomes.databinding.FragmentCreatingChatBinding
import com.MANUL.Bomes.presentation.createChat.AddUserListAdapter
import com.MANUL.Bomes.presentation.createChat.AddedUserListAdapter
import com.MANUL.Bomes.presentation.createChat.CreatingChatWebSocketListener
import com.empire_mammoth.vk_client.presentation.account.CreatingChatViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class CreatingChatFragment : Fragment() {

    private lateinit var binding: FragmentCreatingChatBinding

    private lateinit var webSocketListener: CreatingChatWebSocketListener
    private lateinit var viewModel: CreatingChatViewModel
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    val checkedList: MutableList<Boolean> = mutableListOf()

    private val userAddList = mutableListOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18"
    )
    private val userAddedList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 0..<userAddList.size) checkedList.add(false)

        viewModel = ViewModelProvider(this)[CreatingChatViewModel::class.java]
        webSocketListener = CreatingChatWebSocketListener(viewModel)
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatingChatBinding.inflate(inflater)

        binding.apply {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            addedUserList.layoutManager = layoutManager
            addedUserList.adapter = AddedUserListAdapter(userAddedList)

            addUserList.adapter =
                AddUserListAdapter(userAddList, this@CreatingChatFragment)
            addUserList.layoutManager = LinearLayoutManager(activity)
        }

        return binding.root
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

    fun viewHolderBind(addUserItemBinding: AddUserItemBinding, position: Int) =
        with(addUserItemBinding) {
            addUserText.text = userAddList[position]
            addUserCheckBox.isChecked = checkedList[position]
            addUserCardview.setOnClickListener {
                addUserCheckBox.isChecked = !addUserCheckBox.isChecked
                checkedList[position] = false
                if (addUserCheckBox.isChecked) {
                    userAddedList.add(userAddList[position])
                    checkedList[position] = true
                } else {
                    userAddedList.remove(userAddList[position])
                    checkedList[position] = false
                }
                binding.addedUserList.adapter?.notifyDataSetChanged()
            }
        }
}