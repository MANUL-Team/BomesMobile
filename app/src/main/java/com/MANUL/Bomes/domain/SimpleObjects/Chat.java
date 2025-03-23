package com.MANUL.Bomes.domain.SimpleObjects;

public class Chat {
    public String chatName;
    public long lastUpdate;
    public String lastMessage;
    public String avatar;
    public long notRead;
    public String table_name;
    public String chatId;
    public long lastOnline;
    public int isLocalChat;
    public Chat(){}
    public Chat(String chatName, long lastUpdate, String lastMessage, String avatar, long notRead, String table_name, String chatId, long lastOnline, int isLocalChat){
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.lastUpdate = lastUpdate;
        this.avatar = avatar;
        this.notRead = notRead;
        this.table_name = table_name;
        this.chatId = chatId;
        this.lastOnline = lastOnline;
        this.isLocalChat = isLocalChat;
    }
}
