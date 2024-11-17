package com.MANUL.Bomes.Fragments;

import static com.MANUL.Bomes.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.Activities.ChatsActivity;
import com.MANUL.Bomes.Activities.MainActivity;
import com.MANUL.Bomes.Activities.SplashScreen;
import com.MANUL.Bomes.Adapters.ChatsAdapter;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.Chat;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.MANUL.Bomes.Utils.RequestCreationFactory;
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

    boolean pause = false;

    public ChatsFragment(){}

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
        Request request = NowRequest;

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(ws, t, response);
                WebSocketListener listener = this;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                webSocket = client.newWebSocket(request, listener);
                            }
                        }, 1000);
                    }
                });
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
                            if (obj.event.equals("WrongAuthInIdentifier")){
                                Toast.makeText(activity, "Данные авторизации устарели!", Toast.LENGTH_LONG).show();
                                UserData.avatar = null;
                                UserData.identifier = null;
                                UserData.email = null;
                                UserData.description = null;
                                UserData.username = null;
                                UserData.table_name = null;
                                UserData.chatId = null;
                                UserData.chatAvatar = null;
                                UserData.isLocalChat = 0;
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                activity.finish();
                                webSocket.close(1000, null);
                            }
                            if (obj.event.equals("ReturnUser")){
                                if (obj.user.identifier.equals(UserData.identifier)){
                                    UserData.username = obj.user.username;
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
                            if (e.getMessage() != null)
                                Log.e("JSON", e.getMessage());
                        }
                    }
                });
            }
            @Override
            public void onOpen(@NonNull WebSocket ws, @NonNull Response response) {
                super.onOpen(ws, response);
                try {
                    UniversalJSONObject obj = RequestCreationFactory.create(RequestCreationFactory.ConnectUser);
                    webSocket.send(objectMapper.writeValueAsString(obj));

                    UniversalJSONObject loadMe = RequestCreationFactory.create(RequestCreationFactory.GetUser);
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
                                    UniversalJSONObject setToken = RequestCreationFactory.create(RequestCreationFactory.SetToken, token);
                                    try {
                                        webSocket.send(objectMapper.writeValueAsString(setToken));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                }
                catch (JsonProcessingException e) {
                    if (e.getMessage() != null)
                        Log.e("JSON", e.getMessage());
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
            UniversalJSONObject loadChats = RequestCreationFactory.create(RequestCreationFactory.GetUserChats);
            webSocket.send(objectMapper.writeValueAsString(loadChats));
        }
        catch (JsonProcessingException e){
            if (e.getMessage() != null)
                Log.e("JSON", e.getMessage());
        }
    }
    @Override
    public void onResume() {
        pause = false;
        if (webSocket == null)
            connectToServer();
        if (UserData.identifier == null){
            Intent intent = new Intent(activity, SplashScreen.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
        }
        else {
            getUserChats();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause = true;
    }
    @Override
    public void onStop() {
        super.onStop();
        webSocket.close(1000, null);
        webSocket = null;
    }
}