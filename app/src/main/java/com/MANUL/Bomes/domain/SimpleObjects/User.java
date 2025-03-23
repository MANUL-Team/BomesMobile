package com.MANUL.Bomes.domain.SimpleObjects;

public class User {
    public String username;
    public String avatar;
    public String description;
    public String identifier;
    public int friendsCount;
    public boolean isFriend;
    public String whichFriend;
    public User(String username, String avatar, String identifier, int friendsCount){
        this.username = username;
        this.avatar = avatar;
        this.identifier = identifier;
        this.friendsCount = friendsCount;
        this.description = "";
        this.isFriend = false;
        this.whichFriend = "";
    }
}
