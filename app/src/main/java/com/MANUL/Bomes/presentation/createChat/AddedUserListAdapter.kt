package com.MANUL.Bomes.presentation.createChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.databinding.ItemAddedUserBinding

class AddedUserListAdapter(
    val userAddedList: MutableList<CreatingChatUser>,
    val creatingChatViewModel: CreatingChatViewModel
) : RecyclerView.Adapter<AddedUserListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemAddedUserBinding.bind(itemView)
        fun bind(user: CreatingChatUser, creatingChatViewModel: CreatingChatViewModel) = with(binding){
            creatingChatViewModel.addedUserViewHolderBind(binding, user)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_added_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userAddedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userAddedList[position], creatingChatViewModel)
    }

}
