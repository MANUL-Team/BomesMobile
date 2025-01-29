package com.MANUL.Bomes.Activities;

import static com.MANUL.Bomes.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.Adapters.FindFriendsAdapter;
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

public class FindFriendsActivity extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;

    CardView backBtn, search_btn;
    RecyclerView searched_users_recycler;
    LinearLayoutManager layoutManager;
    FindFriendsAdapter adapter;
    EditText search_edit_text;

    String nowSearching = "";
    boolean nowLoading = false;

    private ArrayList<User> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        init();
        connectToServer();
    }

    private void init(){
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searched_users_recycler = findViewById(R.id.searched_users_recycler);
        adapter = new FindFriendsAdapter(FindFriendsActivity.this, FindFriendsActivity.this, users);
        layoutManager = new LinearLayoutManager(this);
        searched_users_recycler.setLayoutManager(layoutManager);
        searched_users_recycler.setAdapter(adapter);

        search_btn = findViewById(R.id.search_btn);
        search_edit_text = findViewById(R.id.search_edit_text);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowLoading) return;

                nowLoading = true;
                users.clear();
                adapter.notifyDataSetChanged();
                nowSearching = search_edit_text.getText().toString();
                UniversalJSONObject getUsers = RequestCreationFactory.create(
                        RequestEvent.GetUsers, 0, nowSearching);
                try {
                    webSocket.send(objectMapper.writeValueAsString(getUsers));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        searched_users_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (users.size() % 20 != 0){
                    return;
                }
                int id_last = layoutManager.findLastVisibleItemPosition();
                if (id_last >= users.size() - 5 && !nowLoading){
                    nowLoading = true;
                    UniversalJSONObject getUsers = RequestCreationFactory.create(
                            RequestEvent.GetUsers, users.size(), nowSearching);
                    try {
                        webSocket.send(objectMapper.writeValueAsString(getUsers));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void connectToServer(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = NowRequest;

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(ws, t, response);
                WebSocketListener listener = this;
                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals(RequestEvent.WrongAuthInIdentifier)){
                                Toast.makeText(FindFriendsActivity.this, "Данные авторизации устарели!", Toast.LENGTH_LONG).show();
                                UserData.avatar = null;
                                UserData.identifier = null;
                                UserData.email = null;
                                UserData.description = null;
                                UserData.username = null;
                                UserData.table_name = null;
                                UserData.chatId = null;
                                UserData.chatAvatar = null;
                                UserData.isLocalChat = 0;
                                Intent intent = new Intent(FindFriendsActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
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
                                nowLoading = false;
                                if (obj.searchingName != null && !obj.searchingName.equals(nowSearching)) {
                                    users.clear();
                                }
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
    protected void onDestroy() {
        super.onDestroy();
        webSocket.close(1000, null);
        webSocket = null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.activity_switch_reverse_first);
    }
}