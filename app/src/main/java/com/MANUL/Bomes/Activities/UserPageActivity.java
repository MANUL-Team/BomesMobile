package com.MANUL.Bomes.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.User;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class UserPageActivity extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;


    public static User openedUser;
    ImageView avatar;
    TextView username, description, addFriendText;
    CardView openChat, addFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        init();
        setValues();
        connectToServer();
    }
    private void init(){
        avatar = findViewById(R.id.userpage_avatar);
        username = findViewById(R.id.userpage_username);
        description = findViewById(R.id.userpage_description);
        openChat = findViewById(R.id.openChatBtn_userpage);
        addFriend = findViewById(R.id.addFriend_userpage);
        addFriendText = findViewById(R.id.addFriendText_userpage);
    }
    private void setValues(){
        if (openedUser.avatar.isEmpty())
            Glide.with(this).load("https://bomes.ru/media/icon.png").into(avatar);
        else
            Glide.with(this).load("https://bomes.ru/" + openedUser.avatar).into(avatar);
        username.setText(openedUser.username);
        if (!openedUser.description.isEmpty())
            description.setText(openedUser.description);
        else
            description.setText("Описания пока нет...");

        if (openedUser.identifier.equals(UserData.identifier)){
            addFriend.setVisibility(View.GONE);
        }
        else{
            if (openedUser.isFriend){
                addFriendText.setText("Удалить из друзей");
                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFriendMethod();
                    }
                });
            }
            else{
                addFriendText.setText("Добавить в друзья");
                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addFriendMethod();
                    }
                });
            }
        }

        openChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> usersIdentifiers = new ArrayList<>();
                usersIdentifiers.add(UserData.identifier);
                usersIdentifiers.add(openedUser.identifier);
                Collections.sort(usersIdentifiers);
                String table_name = usersIdentifiers.get(0) + "-" + usersIdentifiers.get(1);
                table_name = md5(table_name);
                String[] addingUsers = new String[2];
                addingUsers[0] = UserData.identifier;
                addingUsers[1] = openedUser.identifier;

                UniversalJSONObject createChat = new UniversalJSONObject();
                createChat.table_name = table_name;
                createChat.usersToAdd = addingUsers;
                createChat.chat_name = openedUser.identifier;
                createChat.isLocalChat = 1;
                createChat.event = "CreateChat";
                try {
                    webSocket.send(objectMapper.writeValueAsString(createChat));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void connectToServer(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("wss://bomes.ru:8000").build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(ws, t, response);
                Log.e("Fail", t.getMessage());
            }
            @Override
            public void onMessage(@NonNull WebSocket ws, @NonNull String text) {
                super.onMessage(ws, text);
                runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals("ReturnUser")){
                                if (obj.user.identifier.equals(UserData.identifier)){
                                    UserData.username = obj.user.username;
                                    UserData.password = obj.user.password;
                                    UserData.email = obj.user.email;
                                    UserData.description = obj.user.description;
                                    UserData.avatar = obj.user.avatar;
                                }
                                if (obj.user.identifier.equals(openedUser.identifier)){
                                    openedUser.description = obj.user.description;
                                    openedUser.isFriend = obj.user.isFriend;
                                    setValues();
                                }
                            }
                            else if (obj.event.equals("ChatCreated")){
                                UserData.table_name = obj.table_name;
                                UserData.chatId = obj.chat_name;
                                UserData.isLocalChat = 1;
                                UserData.chatAvatar = openedUser.avatar;
                                UserData.chatName = openedUser.username;
                                Intent intent = new Intent(UserPageActivity.this, ChatActivity.class);
                                startActivity(intent);
                                finish();
                                webSocket.close(1000, null);
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            @Override
            public void onOpen(@NonNull WebSocket ws, @NonNull Response response) {
                super.onOpen(ws, response);
                try {
                    UniversalJSONObject obj = new UniversalJSONObject();
                    obj.event = "setIdentifier";
                    obj.identifier = UserData.identifier;
                    webSocket.send(objectMapper.writeValueAsString(obj));

                    UniversalJSONObject loadMe = new UniversalJSONObject();
                    loadMe.event = "GetUser";
                    loadMe.identifier = UserData.identifier;
                    loadMe.friendId = UserData.identifier;
                    webSocket.send(objectMapper.writeValueAsString(loadMe));

                    UniversalJSONObject loadOther = new UniversalJSONObject();
                    loadOther.event = "GetUser";
                    loadOther.identifier = UserData.identifier;
                    loadOther.friendId = openedUser.identifier;
                    webSocket.send(objectMapper.writeValueAsString(loadOther));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void addFriendMethod(){
        UniversalJSONObject addFriendObj = new UniversalJSONObject();
        addFriendObj.identifier = UserData.identifier;
        addFriendObj.friendId = openedUser.identifier;
        addFriendObj.event = "AddFriend";
        try {
            webSocket.send(objectMapper.writeValueAsString(addFriendObj));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        addFriendText.setText("Удалить из друзей");
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriendMethod();
            }
        });
    }
    private void removeFriendMethod(){
        UniversalJSONObject removeFriendObj = new UniversalJSONObject();
        removeFriendObj.identifier = UserData.identifier;
        removeFriendObj.friendId = openedUser.identifier;
        removeFriendObj.event = "RemoveFriend";
        try {
            webSocket.send(objectMapper.writeValueAsString(removeFriendObj));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        addFriendText.setText("Добавить в друзья");
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendMethod();
            }
        });
    }
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}