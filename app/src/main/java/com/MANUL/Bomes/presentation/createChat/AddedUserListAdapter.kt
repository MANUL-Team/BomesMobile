package com.MANUL.Bomes.presentation.createChat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.databinding.AddedUserItemBinding

class AddedUserListAdapter : RecyclerView.Adapter<AddedUserListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AddedUserItemBinding.bind(itemView)
        fun bind(position: Int) = with(binding){
            Log.e("AddUserListAdapter", position.toString())
            addedUserText.text = "Egor"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.added_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
