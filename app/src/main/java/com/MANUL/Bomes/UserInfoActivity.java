package com.MANUL.Bomes;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class UserInfoActivity extends AppCompatActivity {
    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    TextView identifierText, usernameText, descriptionText;
    ImageView avatarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        connectToServer();
        init();
        loadUserData();
    }
    private void init(){
        identifierText = findViewById(R.id.identifierText);
        usernameText = findViewById(R.id.usernameText);
        descriptionText = findViewById(R.id.descriptionText);
        avatarImage = findViewById(R.id.avatarImage);
    }
    private void loadUserData(){
        identifierText.setText(UserData.identifier);
        usernameText.setText(UserData.username);
        descriptionText.setText(UserData.description);
        Glide.with(UserInfoActivity.this).load("https://bomes.ru/" + UserData.avatar).into(avatarImage);
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
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            @Override
            public void onOpen(@NonNull WebSocket ws, @NonNull Response response) {
                super.onOpen(ws, response);
                UniversalJSONObject obj = new UniversalJSONObject();
                obj.event = "setIdentifier";
                obj.identifier = UserData.identifier;
                try {
                    webSocket.send(objectMapper.writeValueAsString(obj));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}