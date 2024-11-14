package com.MANUL.Bomes.presentation.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.databinding.AddUserItemBinding
import com.MANUL.Bomes.databinding.AddedUserItemBinding

class FriendsListAdapter(
    val userAddList: MutableList<Int>,
) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = AddUserItemBinding.bind(itemView)
        fun bind(position: Int) {
            binding.addUserText.text = position.toString()
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
        holder.bind(userAddList[position])
    }

}
