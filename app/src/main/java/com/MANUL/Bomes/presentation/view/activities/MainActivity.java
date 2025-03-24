package com.MANUL.Bomes.presentation.view.activities;

import static com.MANUL.Bomes.domain.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.data.model.UniversalJSONObject;
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

public class MainActivity extends AppCompatActivity {
    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    SharedPreferences prefs;
    EditText emailField, passwordField;
    CardView loginBtn;
    TextView registerBtnLogin;
    String identifier;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);

        identifier = prefs.getString("identifier", "none");
        password = prefs.getString("password", "none");

        connectToServer();
        init();

    }
    private void init(){
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtnLogin = findViewById(R.id.registerBtnLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocket != null){
                    try {
                        String plaintext = passwordField.getText().toString();
                        String passwordHash = UserPageActivity.md5(plaintext);

                        UniversalJSONObject sendObj = RequestCreationFactory.create(RequestEvent.Login,emailField.getText().toString(), passwordHash,null);

                        String sendData = objectMapper.writeValueAsString(sendObj);

                        webSocket.send(sendData);

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
        registerBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                webSocket.close(1000, null);
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

                            if (obj.event.equals(RequestEvent.TruePassword)){
                                UserData.email = obj.email;
                                UserData.identifier = obj.identifier;
                                UserData.password = obj.password;
                                UserData.username = obj.username;
                                UserData.avatar = obj.avatar;
                                UserData.description = obj.description;

                                prefs.edit().putString("identifier", obj.identifier).apply();
                                prefs.edit().putString("password", obj.password).apply();

                                Intent intent = new Intent(MainActivity.this, ChatsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                                webSocket.close(1000, null);
                            }
                            else if (obj.event.equals(RequestEvent.WrongPassword)){
                                Toast.makeText(MainActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                            }
                            else if(obj.event.equals("UserNotFound")){
                                Toast.makeText(MainActivity.this, "Пользователь не найден!", Toast.LENGTH_SHORT).show();
                            }
                            else if (obj.event.equals(RequestEvent.ReturnUser)){
                                if (obj.user.identifier.equals(identifier)){
                                    UserData.username = obj.user.username;
                                    UserData.identifier = obj.user.identifier;
                                    UserData.email = obj.user.email;
                                    UserData.description = obj.user.description;
                                    UserData.avatar = obj.user.avatar;

                                    Intent intent = new Intent(MainActivity.this, ChatsActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                    webSocket.close(1000, null);
                                }
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
                if (!identifier.equals("none")){
                    try {
                        UniversalJSONObject loadMe = RequestCreationFactory.create(RequestEvent.CheckPrefsIdentifier, identifier, password, null);
                        webSocket.send(objectMapper.writeValueAsString(loadMe));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}