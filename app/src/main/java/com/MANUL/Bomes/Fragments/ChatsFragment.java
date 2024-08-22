package com.MANUL.Bomes.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.SimpleObjects.Chat;
import com.MANUL.Bomes.Activities.ChatsActivity;
import com.MANUL.Bomes.Adapters.ChatsAdapter;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatsFragment extends Fragment {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    ChatsActivity activity;


    RecyclerView chatsRecycler;
    ChatsAdapter adapter;
    ArrayList<Chat> chats = new ArrayList<>();

    public ChatsFragment(ChatsActivity activity){
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        connectToServer();
    }

    public void init(View view){
        chatsRecycler = view.findViewById(R.id.chatsRecycler);
        adapter = new ChatsAdapter(activity, chats, activity);
        chatsRecycler.setLayoutManager(new LinearLayoutManager(activity));
        chatsRecycler.setAdapter(adapter);
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
                activity.runOnUiThread(new Runnable() {
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
    private void reverseArray(UniversalJSONObject[] arr) {
        UniversalJSONObject temp;
        for (int i = 0; i < arr.length / 2; i++) {
            temp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = temp;
        }
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
    @Override
    public void onResume() {
        getUserChats();
        super.onResume();
    }
}