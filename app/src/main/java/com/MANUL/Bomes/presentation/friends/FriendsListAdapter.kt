package com.MANUL.Bomes.presentation.friends

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.presentation.view.activities.UserPageActivity
import com.MANUL.Bomes.R
import com.MANUL.Bomes.domain.SimpleObjects.User
import com.MANUL.Bomes.databinding.ItemFriendsBinding
import com.bumptech.glide.Glide

class FriendsListAdapter(
    val userAddList: MutableList<User>,
    val activity: FragmentActivity?,
) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemFriendsBinding.bind(itemView)
        fun bind(user: User, activity: FragmentActivity?) {
            binding.apply {
                addUserText.text = user.username
                buttonAddFriends.visibility = View.GONE
                friendsCardview.setOnClickListener {
                    UserPageActivity.openedUser = user
                    val intent = Intent(
                        activity,
                        UserPageActivity::class.java
                    )
                    activity?.startActivity(intent)
                }
                if (user.avatar.isEmpty()) activity?.let {
                    Glide.with(it).load("https://bomes.ru/media/icon.png")
                        .into(friendsImage)
                }
                else activity?.let {
                    Glide.with(it).load("https://bomes.ru/" + user.avatar).into(friendsImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userAddList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userAddList[position], activity)
    }

}
