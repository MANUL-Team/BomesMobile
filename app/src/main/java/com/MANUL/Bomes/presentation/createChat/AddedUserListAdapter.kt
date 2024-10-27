package com.MANUL.Bomes.presentation.createChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.Fragments.CreatingChatFragment
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.databinding.AddedUserItemBinding

class AddedUserListAdapter(
    val userAddedList: MutableList<CreatingChatUser>,
    val creatingChatFragment: CreatingChatFragment
) : RecyclerView.Adapter<AddedUserListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AddedUserItemBinding.bind(itemView)
        fun bind(user: CreatingChatUser, creatingChatFragment: CreatingChatFragment) = with(binding){
            creatingChatFragment.addedUserViewHolderBind(binding, user)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.added_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userAddedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userAddedList[position], creatingChatFragment)
    }

}
