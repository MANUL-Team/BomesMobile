package com.MANUL.Bomes.presentation.createChat

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.databinding.AddUserItemBinding
import com.MANUL.Bomes.databinding.AddedUserItemBinding
import com.MANUL.Bomes.databinding.FragmentCreatingChatBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CreatingChatViewModel(inflater: LayoutInflater, activity: FragmentActivity?) : ViewModel() {
    private lateinit var _binding: FragmentCreatingChatBinding
    public val binding = _binding

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

    init {
        for (i in 0..<users.size) userAddList.add(
            CreatingChatUser(
                User(users[i], "", "", 0),
                false
            )
        )

        _binding = FragmentCreatingChatBinding.inflate(inflater)

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