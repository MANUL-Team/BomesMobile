package com.MANUL.Bomes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatsActivity extends AppCompatActivity {
    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;

    Toolbar mainToolbar;
    AccountHeader header;
    Drawer drawer;

    RecyclerView chatsRecycler;
    ChatsAdapter adapter;
    ArrayList<Chat> chats = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        askNotificationPermission();
        connectToServer();
        init();
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
                            }
                            else if (obj.event.equals("ReturnUserChats")){
                                chats.clear();
                                Arrays.sort(obj.chats, Comparator.comparing(UniversalJSONObject::getLastUpdate));
                                reverseArray(obj.chats);
                                for (UniversalJSONObject chat : obj.chats) {
                                    if (chat.isLocalChat == 1) {
                                        if (chat.user_identifier.equals(UserData.identifier))
                                            chat.chat_name = "Избранное";
                                        chats.add(new Chat(chat.chat_name, chat.lastUpdate, chat.lastMessage, chat.avatar, chat.notRead, chat.table_name, chat.user_identifier, chat.lastOnline, chat.isLocalChat));
                                    }
                                    else{
                                        chats.add(new Chat(chat.chat_name, chat.lastUpdate, chat.lastMessage, chat.avatar, chat.notRead, chat.table_name, chat.user_identifier, 0, chat.isLocalChat));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                            else if (obj.event.equals("Notification")){
                                getUserChats();
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

                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("Fail", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }

                                    // Get new FCM registration token
                                    String token = task.getResult();
                                    UniversalJSONObject setToken = new UniversalJSONObject();
                                    setToken.identifier = UserData.identifier;
                                    setToken.token = token;
                                    setToken.event = "SetToken";
                                    try {
                                        webSocket.send(objectMapper.writeValueAsString(setToken));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void init(){
        chatsRecycler = findViewById(R.id.chatsRecycler);
        adapter = new ChatsAdapter(this, chats, this);
        chatsRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatsRecycler.setAdapter(adapter);

        mainToolbar = findViewById(R.id.mainToolbar);

        setSupportActionBar(mainToolbar);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(ChatsActivity.this).load(uri).placeholder(placeholder).into(imageView);
            }
        });

        setHeader();
        setDrawer();
    }
    private void setDrawer(){
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mainToolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Чаты")
                                .withSelectable(true)
                                .withIcon(R.drawable.chats),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Все пользователи")
                                .withSelectable(true)
                                .withIcon(R.drawable.people),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Создать чат")
                                .withSelectable(true)
                                .withIcon(R.drawable.new_chat),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Настройки")
                                .withSelectable(true)
                                .withIcon(R.drawable.human)
                )
                .build();
    }

    private void setHeader(){
        Log.e("Data", UserData.username + " " + UserData.email + " " + UserData.avatar);
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withName(UserData.username)
                .withEmail(UserData.email);
        if (UserData.avatar.isEmpty())
            profileDrawerItem.withIcon(R.drawable.icon);
        else
            profileDrawerItem.withIcon("https://bomes.ru/" + UserData.avatar);
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.gradient)
                .addProfiles(
                        profileDrawerItem
                ).build();
    }
    private void getUserChats(){
        try {
            UniversalJSONObject loadChats = new UniversalJSONObject();
            loadChats.event = "GetUserChats";
            loadChats.identifier = UserData.identifier;
            webSocket.send(objectMapper.writeValueAsString(loadChats));
        }
        catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
    private void reverseArray(UniversalJSONObject[] arr) {
        UniversalJSONObject temp;
        for (int i = 0; i < arr.length / 2; i++) {
            temp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = temp;
        }
    }
    @Override
    protected void onResume() {
        getUserChats();
        super.onResume();
    }
    public void openChat(){
        Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
        startActivity(intent);
    }
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

}