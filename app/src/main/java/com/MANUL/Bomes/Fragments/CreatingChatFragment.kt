package com.MANUL.Bomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.databinding.AddUserItemBinding
import com.MANUL.Bomes.databinding.AddedUserItemBinding
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

    val userAddList: MutableList<CreatingChatUser> = mutableListOf()

    private val users = mutableListOf(
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
    private val userAddedList: MutableList<CreatingChatUser> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 0..<users.size) userAddList.add(
            CreatingChatUser(
                User(users[i], "", "", 0),
                false
            )
        )

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
            addedUserList.adapter = AddedUserListAdapter(userAddedList, this@CreatingChatFragment)

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

    fun addUserViewHolderBind(addUserItemBinding: AddUserItemBinding, user: CreatingChatUser) =
        with(addUserItemBinding) {
            addUserText.text = user.user.username
            addUserCheckBox.isChecked = user.checked
            addUserCardview.setOnClickListener {
                addUserCheckBox.isChecked = !addUserCheckBox.isChecked
                user.checked = false
                if (addUserCheckBox.isChecked) {
                    userAddedList.add(user)
                    user.checked = true
                } else {
                    userAddedList.remove(user)
                    user.checked = false
                }
                binding.addedUserList.adapter?.notifyDataSetChanged()
            }
        }

    fun addedUserViewHolderBind(
        addedUserItemBinding: AddedUserItemBinding,
        user: CreatingChatUser
    ) =
        with(addedUserItemBinding) {
            addedUserText.text = user.user.username
            addedUserCancel.setOnClickListener {
                userAddedList.remove(user)
                user.checked = false
                binding.addedUserList.adapter?.notifyDataSetChanged()
                binding.addUserList.adapter?.notifyDataSetChanged()
            }
        }
}