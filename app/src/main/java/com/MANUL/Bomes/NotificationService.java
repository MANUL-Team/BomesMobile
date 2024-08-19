package com.MANUL.Bomes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class NotificationService extends FirebaseMessagingService {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    String token;
    @WorkerThread
    public void onNewToken(@NonNull String token){
        this.token = token;
        connectToServer();
    }

    private void connectToServer(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("wss://bomes.ru:8000").build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull WebSocket ws, @androidx.annotation.NonNull Throwable t, @Nullable Response response) {
                super.onFailure(ws, t, response);
                Log.e("Fail", t.getMessage());
            }
            @Override
            public void onMessage(@androidx.annotation.NonNull WebSocket ws, @androidx.annotation.NonNull String text) {
                super.onMessage(ws, text);
                try {
                    UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onOpen(@androidx.annotation.NonNull WebSocket ws, @androidx.annotation.NonNull Response response) {
                super.onOpen(ws, response);
                try {
                    UniversalJSONObject obj = new UniversalJSONObject();
                    obj.event = "setIdentifier";
                    obj.identifier = UserData.identifier;
                    webSocket.send(objectMapper.writeValueAsString(obj));

                    UniversalJSONObject setToken = new UniversalJSONObject();
                    setToken.identifier = UserData.identifier;
                    setToken.token = token;
                    setToken.event = "SetToken";
                    webSocket.send(objectMapper.writeValueAsString(setToken));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
