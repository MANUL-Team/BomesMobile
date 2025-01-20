package com.MANUL.Bomes.presentation.createChat

import android.content.res.Configuration
import android.os.Build
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.OVERRIDE_TRANSITION_CLOSE
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserDataKt
import com.MANUL.Bomes.databinding.ItemAddUserBinding
import com.MANUL.Bomes.databinding.ItemAddedUserBinding
import com.MANUL.Bomes.databinding.ActivityCreatingChatBinding
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CreatingChatViewModel(
    inflater: LayoutInflater,
    private val activity: FragmentActivity,
) : ViewModel() {
    private var _binding = ActivityCreatingChatBinding.inflate(inflater)
    private val userAddList: MutableList<CreatingChatUser> = mutableListOf()
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
            changeConfigurations()
            activity.addOnConfigurationChangedListener{
                changeConfigurations()
            }

            backBtn.setOnClickListener {
                activity.apply{
                    finish()
                }
            }
        }
    }

    fun changeConfigurations() = with(_binding){
        if(activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            addUserList.layoutManager = LinearLayoutManager(activity)
        else
            addUserList.layoutManager = GridLayoutManager(activity, 2)
    }

    val binding by lazy { _binding }

    fun getUserAddedListForCreateChat(): MutableList<CreatingChatUser>? {
        if (binding.createChatEditText.text.isEmpty())
            Toast.makeText(
                activity,
                "Название чата не может быть пустым!",
                Toast.LENGTH_LONG
            ).show()
        else if (userAddedList.size < 2)
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

    fun addUserViewHolderBind(addUserItemBinding: ItemAddUserBinding, user: CreatingChatUser) =
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
        addedUserItemBinding: ItemAddedUserBinding,
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

    fun userAddListCompletion(){
        userAddList.clear()
        for (i in 0..<UserDataKt.users.size) userAddList.add(
            CreatingChatUser(
                UserDataKt.users[i],
                false
            )
        )
    }
}