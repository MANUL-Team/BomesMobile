package com.MANUL.Bomes.presentation.view.activities;

import static com.MANUL.Bomes.domain.Utils.ServerUtilsKt.NowRequest;

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

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.domain.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.domain.SimpleObjects.UserData;
import com.MANUL.Bomes.domain.Utils.RequestCreationFactory;
import com.MANUL.Bomes.domain.Utils.RequestEvent;
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

                UniversalJSONObject confirmingEmail = RequestCreationFactory.create(RequestEvent.ConfirmEmail, Integer.toString(code));
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
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals(RequestEvent.WrongCode)){
                                Toast.makeText(CodeConfirmActivity.this, "Неправильный код!", Toast.LENGTH_SHORT).show();
                            }
                            else if (obj.event.equals(RequestEvent.RightCode)){
                                UserData.identifier = obj.userIdentifier;
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