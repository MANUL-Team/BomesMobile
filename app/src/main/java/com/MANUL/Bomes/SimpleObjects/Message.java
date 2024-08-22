package com.MANUL.Bomes.SimpleObjects;

import android.view.View;

import com.MANUL.Bomes.Adapters.MessageViewHolder;

public class Message {
    public String username;
    public String dataType;
    public String value;
    public String reply;
    public int isRead;
    public long time;
    public long id;
    public String sender;
    public View holder;
    public MessageViewHolder viewHolder;
    public Message(String username, String dataType, String value, String reply, int isRead, long time, long id, String sender){
        this.username = username;
        this.dataType = dataType;
        this.value = value;
        this.reply = reply;
        this.isRead = isRead;
        this.time = time;
        this.id = id;
        this.sender = sender;
    }
}
