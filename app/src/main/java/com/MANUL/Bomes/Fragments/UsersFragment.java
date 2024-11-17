package com.MANUL.Bomes.Fragments;

import static com.MANUL.Bomes.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.MANUL.Bomes.Adapters.UsersAdapter;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.User;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.MANUL.Bomes.Utils.RequestCreationFactory;
import com.MANUL.Bomes.Utils.RequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class UsersFragment extends Fragment {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    ChatsActivity activity;


    RecyclerView users_recycler;
    UsersAdapter adapter;
    ArrayList<User> users = new ArrayList<>();

    public UsersFragment(){}

    public UsersFragment(ChatsActivity activity){
        this.activity = activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        connectToServer();
    }
    private void init(View view){
        users = new ArrayList<>();
        users_recycler = view.findViewById(R.id.users_recycler);
        adapter = new UsersAdapter(activity, users);
        users_recycler.setLayoutManager(new LinearLayoutManager(activity));
        users_recycler.setAdapter(adapter);
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
                            if (obj.event.equals(RequestEvent.WrongAuthInIdentifier)){
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
                            if (obj.event.equals(RequestEvent.ReturnUser)){
                                if (obj.user.identifier.equals(UserData.identifier)){
                                    UserData.username = obj.user.username;
                                    UserData.email = obj.user.email;
                                    UserData.description = obj.user.description;
                                    UserData.avatar = obj.user.avatar;
                                }
                            }
                            else if(obj.event.equals(RequestEvent.ReturnUsers)){
                                users.clear();
                                adapter.notifyDataSetChanged();
                                for (UniversalJSONObject jsonObject:obj.users) {
                                    User user = new User(jsonObject.username, jsonObject.avatar, jsonObject.identifier, jsonObject.friendsCount);
                                    users.add(user);
                                    adapter.notifyItemInserted(users.size()-1);
                                }
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
                    UniversalJSONObject obj = RequestCreationFactory.create(RequestEvent.ConnectUser);
                    webSocket.send(objectMapper.writeValueAsString(obj));

                    UniversalJSONObject loadMe = RequestCreationFactory.create(RequestEvent.GetUser);
                    webSocket.send(objectMapper.writeValueAsString(loadMe));

                    UniversalJSONObject getUsers = RequestCreationFactory.create(RequestEvent.GetUsers);
                    webSocket.send(objectMapper.writeValueAsString(getUsers));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webSocket == null)
            connectToServer();
    }

    @Override
    public void onStop() {
        super.onStop();
        webSocket.close(1000, null);
        webSocket = null;
    }
}