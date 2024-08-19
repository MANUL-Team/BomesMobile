package com.MANUL.Bomes;

public class UniversalJSONObject {
    public String event;
    public String message;
    public String date;
    public String email;
    public String password;
    public String identifier;
    public String username;
    public String avatar;
    public String description;
    public long lastOnline;
    public String lastMessage;
    public long lastUpdate;
    public String dataType;
    public int isLocalChat;
    public UniversalJSONObject[] chats;
    public String chat_name;
    public UniversalJSONObject user;
    public long id;
    public String table_name;
    public long notRead;
    public String user_identifier;
    public String chat;
    public boolean isOnline;
    public long loadedMessages;
    public UniversalJSONObject[] messages;
    public String value;
    public String reply;
    public String reaction;
    public int isRead;
    public long time;
    public String sender;
    public String chatName;
    public String[] stickers;
    public String token;
    public String friendId;
    public boolean isFriend;
    public String typingType;
    public UniversalJSONObject[] members;
    public String[] tokens;
    public String[] friends;
    public long messageId;
    public String fileName;
    public String filePath;



    public long getLastUpdate(){
        return lastUpdate;
    }

}
