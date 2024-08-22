package com.MANUL.Bomes.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.ConfirmationUser;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class CodeConfirmActivity extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;


    EditText codeEditText;
    CardView confirmCodeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_confirm);
        init();
        connectToServer();
    }
    private void init(){
        codeEditText = findViewById(R.id.codeEditText);
        confirmCodeBtn = findViewById(R.id.confirmCodeBtn);

        confirmCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code = Integer.parseInt(codeEditText.getText().toString());

                UniversalJSONObject confirmingEmail = new UniversalJSONObject();
                confirmingEmail.email = ConfirmationUser.email;
                confirmingEmail.code = code;
                confirmingEmail.event = "ConfirmingEmail";

                try {
                    webSocket.send(objectMapper.writeValueAsString(confirmingEmail));
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
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals("WrongCode")){
                                Toast.makeText(CodeConfirmActivity.this, "Неправильный код!", Toast.LENGTH_SHORT).show();
                            }
                            else if (obj.event.equals("RightCode")){
                                UserData.identifier = obj.userIndentifier;
                                UserData.email = obj.email;
                                UserData.password = obj.password;
                                UserData.username = obj.username;
                                UserData.avatar = "";
                                UserData.description = "";

                                Intent intent = new Intent(CodeConfirmActivity.this, ChatsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
            }
        });
    }
}