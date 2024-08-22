package com.MANUL.Bomes.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.SimpleObjects.Chat;
import com.MANUL.Bomes.Activities.ChatsActivity;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<Chat> chats;
    ChatsActivity activity;
    public ChatsAdapter(Context context, ArrayList<Chat> chats, ChatsActivity activity){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.chats = chats;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Chat chat = chats.get(position);
        chat.avatar = chat.avatar.replace("\\", "/");
        holder.chatName.setText(chat.chatName);
        holder.lastMessage.setText(chat.lastMessage);
        String avatar = chat.avatar;
        if (!avatar.isEmpty())
            Glide.with(context).load("https://bomes.ru/" + chat.avatar).into(holder.avatar);
        else
            Glide.with(context).load("https://bomes.ru/media/icon.png").into(holder.avatar);
        if (chats.get(position).notRead > 0){
            holder.notReadCard.setVisibility(View.VISIBLE);
            holder.notRead.setText(String.valueOf(chat.notRead));
        }
        else
            holder.notReadCard.setVisibility(View.GONE);

        if (chat.isLocalChat == 1) {
            holder.lastOnlineText.setVisibility(View.VISIBLE);
            if (chat.lastOnline != 0) {
                holder.lastOnlineText.setText(setLastOnlineText(chat.lastOnline));
                holder.lastOnlineText.setTextColor(activity.getResources().getColor(R.color.gray));
            } else {
                holder.lastOnlineText.setText("Онлайн");
                holder.lastOnlineText.setTextColor(activity.getResources().getColor(R.color.green));
            }
        }
        else{
            holder.lastOnlineText.setVisibility(View.GONE);
        }

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData.table_name = chat.table_name;
                UserData.chatId = chat.chatId;
                UserData.isLocalChat = chat.isLocalChat;
                UserData.chatAvatar = chat.avatar;
                UserData.chatName = chat.chatName;
                activity.openChat();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    private String setLastOnlineText(long lastOnline){
        Date onlineDateAsDate = new Date(lastOnline * 1000);
        Date nowDateAsDate = new Date(System.currentTimeMillis());
        String onlineDate = new SimpleDateFormat("dd.MM.YYYY").format(onlineDateAsDate);
        String nowDate = new SimpleDateFormat("dd.MM.YYYY").format(nowDateAsDate);
        String time = new SimpleDateFormat("HH:mm").format(onlineDateAsDate);

        if (onlineDate.equals(nowDate)){
            return "Был(а) в сети: " + time;
        }
        else if (onlineDateAsDate.getYear() == nowDateAsDate.getYear() && onlineDateAsDate.getMonth() == nowDateAsDate.getMonth()
                && onlineDateAsDate.getDate() == nowDateAsDate.getDate() - 1){
            return "Был(а) в сети: вчера в " + time;
        }
        else{
            return "Был(а) в сети: " + new SimpleDateFormat("dd.MM.YYYY").format(onlineDateAsDate) + " в " + time;
        }
    }
}
class ChatViewHolder extends RecyclerView.ViewHolder{
    TextView chatName, lastMessage, notRead, lastOnlineText;
    ImageView avatar;
    CardView notReadCard;
    CardView mainCard;
    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        chatName = itemView.findViewById(R.id.chatName);
        lastMessage = itemView.findViewById(R.id.lastMessage);
        avatar = itemView.findViewById(R.id.chatAvatar);
        notReadCard = itemView.findViewById(R.id.notReadCard);
        notRead = itemView.findViewById(R.id.notRead);
        mainCard = itemView.findViewById(R.id.mainCard);
        lastOnlineText = itemView.findViewById(R.id.lastOnlineText);
    }
}
