package com.MANUL.Bomes.presentation.createChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UserDataKt
import com.MANUL.Bomes.databinding.AddUserItemBinding

class AddUserListAdapter(
    val userAddList: MutableList<CreatingChatUser>,
    val creatingChatViewModel: CreatingChatViewModel
) :
    RecyclerView.Adapter<AddUserListAdapter.ViewHolder>() {

        init {
            for (i in 0..<UserDataKt.users.size) userAddList.add(
                CreatingChatUser(
                    UserDataKt.users[i],
                    false
                )
            )
        }

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = AddUserItemBinding.bind(itemView)
        fun bind(position: CreatingChatUser, creatingChatViewModel: CreatingChatViewModel) {
            creatingChatViewModel.addUserViewHolderBind(binding, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userAddList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userAddList[position], creatingChatViewModel)
    }

}
