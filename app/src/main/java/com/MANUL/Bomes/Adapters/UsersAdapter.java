package com.MANUL.Bomes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.Activities.UserPageActivity;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private ArrayList<User> users;
    private Context context;
    private LayoutInflater inflater;
    public UsersAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.username);
        if (user.avatar.isEmpty())
            Glide.with(context).load("https://bomes.ru/media/icon.png").into(holder.avatar);
        else
            Glide.with(context).load("https://bomes.ru/" + user.avatar).into(holder.avatar);

        holder.friendsCount.setText("Друзей: " + user.friendsCount);

        holder.user_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPageActivity.openedUser = user;
                Intent intent = new Intent(context, UserPageActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
class UserViewHolder extends RecyclerView.ViewHolder{
    TextView username, friendsCount;
    ImageView avatar;
    CardView user_card;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username_user_text);
        avatar = itemView.findViewById(R.id.avatar_user_image);
        friendsCount = itemView.findViewById(R.id.friendsCount_user_text);
        user_card = itemView.findViewById(R.id.user_card);
    }
}
