package com.MANUL.Bomes.presentation.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.databinding.FriendsItemBinding

class FriendsListAdapter(
    val userAddList: MutableList<User>,
) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = FriendsItemBinding.bind(itemView)
        fun bind(user: User) {
            binding.addUserText.text = user.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userAddList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userAddList[position])
    }

}
