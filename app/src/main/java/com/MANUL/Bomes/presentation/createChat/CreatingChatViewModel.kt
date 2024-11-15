package com.MANUL.Bomes.presentation.createChat

import android.R
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.Activities.MainActivity
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.databinding.AddUserItemBinding
import com.MANUL.Bomes.databinding.AddedUserItemBinding
import com.MANUL.Bomes.databinding.FragmentCreatingChatBinding
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CreatingChatViewModel(
    inflater: LayoutInflater,
    private val activity: FragmentActivity?
) : ViewModel() {
    private var _binding = FragmentCreatingChatBinding.inflate(inflater)
    private val userAddList: MutableList<CreatingChatUser> = mutableListOf()
    private var users: MutableList<User> = mutableListOf()
    private val userAddedList: MutableList<CreatingChatUser> = mutableListOf()

    init {
        _binding.apply {
            val layoutManager = FlexboxLayoutManager(activity)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            addedUserList.layoutManager = layoutManager
            addedUserList.adapter = AddedUserListAdapter(userAddedList, this@CreatingChatViewModel)

            addUserList.adapter =
                AddUserListAdapter(userAddList, this@CreatingChatViewModel)
            addUserList.layoutManager = LinearLayoutManager(activity)
        }
    }

    val binding by lazy { _binding }

    fun getUserAddedListForCreateChat(): MutableList<CreatingChatUser>? {
        if (binding.createChatEditText.text.isEmpty())
            Toast.makeText(
                activity,
                "Название чата не может быть пустым!",
                Toast.LENGTH_LONG
            ).show()
        else if (userAddedList.size < 3)
            Toast.makeText(
                activity,
                "В чате должно быть по крайней мере 3 пользователя!",
                Toast.LENGTH_LONG
            ).show()
        else
            return userAddedList
        return null
    }

    fun insertingImage(obj: UniversalJSONObject) {
        Glide.with(activity!!).load("https://bomes.ru/" + obj.filePath)
            .into(binding.createChatAvatar)
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

            if (user.user.avatar.isEmpty()) activity?.let {
                Glide.with(it).load("https://bomes.ru/media/icon.png")
                    .into(addUserImage)
            }
            else activity?.let {
                Glide.with(it).load("https://bomes.ru/" + user.user.avatar).into(addUserImage)
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

            if (user.user.avatar.isEmpty()) activity?.let {
                Glide.with(it).load("https://bomes.ru/media/icon.png")
                    .into(addedUserImage)
            }
            else activity?.let {
                Glide.with(it).load("https://bomes.ru/" + user.user.avatar).into(addedUserImage)
            }

        }

    fun responseWrongAuthInIdentifier() {
        Toast.makeText(activity, "Данные авторизации устарели!", Toast.LENGTH_LONG).show()
        UserData.avatar = null
        UserData.identifier = null
        UserData.email = null
        UserData.description = null
        UserData.username = null
        UserData.table_name = null
        UserData.chatId = null
        UserData.chatAvatar = null
        UserData.isLocalChat = 0
        val intent = Intent(activity, MainActivity::class.java)
        activity?.startActivity(intent)
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        activity?.finish()
    }

    fun responseReturnFriends(obj: UniversalJSONObject) {
        users.clear()

        for (jsonObject in obj.users) {
            val user = User(
                jsonObject.username,
                jsonObject.avatar,
                jsonObject.identifier,
                jsonObject.friendsCount
            )
            users.add(user)
        }

        for (i in 0..<users.size) userAddList.add(
            CreatingChatUser(
                users[i],
                false
            )
        )

        binding.addUserList.adapter?.notifyDataSetChanged()
    }

}