package com.MANUL.Bomes.presentation.friends

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.MANUL.Bomes.Activities.UserPageActivity
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.databinding.FriendsItemBinding

class FriendsListAdapter(
    val userAddList: MutableList<User>,
    val activity: FragmentActivity?,
) :
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = FriendsItemBinding.bind(itemView)
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
            }
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
        holder.bind(userAddList[position], activity)
    }

}
