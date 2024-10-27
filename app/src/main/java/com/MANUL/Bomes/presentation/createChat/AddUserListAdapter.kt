package com.MANUL.Bomes.presentation.createChat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.databinding.AddUserItemBinding

class AddUserListAdapter(
    val userAddList: MutableList<String>,
    val userAddedList: MutableList<String>,
    val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
) : RecyclerView.Adapter<AddUserListAdapter.ViewHolder>() {

    val checkedList: MutableList<Boolean> = mutableListOf()

    init {
        for (i in 0..<userAddList.size) checkedList.add(false)
    }

    class ViewHolder(
        itemView: View,
        val userAddedList: MutableList<String>,
        val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?,
        val userAddList: MutableList<String>,
        val checkedList: MutableList<Boolean>
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = AddUserItemBinding.bind(itemView)
        fun bind(position: Int) = with(binding) {
            addUserText.text = userAddList[position]
            addUserCheckBox.isChecked = checkedList[position]
            addUserCardview.setOnClickListener {
                addUserCheckBox.isChecked = !addUserCheckBox.isChecked
                checkedList[position] = false
                if (addUserCheckBox.isChecked) {
                    userAddedList.add(userAddList[position])
                    checkedList[position] = true
                } else {
                    userAddedList.remove(userAddList[position])
                    checkedList[position] = false
                }
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_user_item, parent, false)
        return ViewHolder(view, userAddedList, adapter, userAddList, checkedList)
    }

    override fun getItemCount(): Int {
        return userAddList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
