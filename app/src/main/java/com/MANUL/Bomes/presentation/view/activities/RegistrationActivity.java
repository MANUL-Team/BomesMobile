package com.MANUL.Bomes.presentation.view.activities;

import static com.MANUL.Bomes.domain.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.domain.SimpleObjects.ConfirmationUser;
import com.MANUL.Bomes.data.model.UniversalJSONObject;
import com.MANUL.Bomes.domain.Utils.RequestCreationFactory;
import com.MANUL.Bomes.domain.Utils.RequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RegistrationActivity extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;


    TextView loginBtnRegister;
    EditText usernameRegField, emailRegField, passwordRegField, passwordRegConfirmField;
    CardView registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        connectToServer();
    }
    private void init(){
        loginBtnRegister = findViewById(R.id.loginBtnRegister);
        usernameRegField = findViewById(R.id.usernameRegField);
        emailRegField = findViewById(R.id.emailRegField);
        passwordRegField = findViewById(R.id.passwordRegField);
        passwordRegConfirmField = findViewById(R.id.passwordRegConfirmField);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailRegField.getText().toString();
                String password = passwordRegField.getText().toString();
                String passwordConfirm = passwordRegConfirmField.getText().toString();
                String username = usernameRegField.getText().toString();

                if (email.isEmpty() || password.isEmpty() || username.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Поля не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(passwordConfirm)){
                    Toast.makeText(RegistrationActivity.this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.length() < 3){
                    Toast.makeText(RegistrationActivity.this, "Слишком короткое имя пользователя!", Toast.LENGTH_SHORT).show();
                    return;
                }

                password = UserPageActivity.md5(password);
                ConfirmationUser.email = email;
                ConfirmationUser.password = password;
                ConfirmationUser.username = username;

                UniversalJSONObject regUser = RequestCreationFactory.create(RequestEvent.RegisterUser);
                try {
                    webSocket.send(objectMapper.writeValueAsString(regUser));
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
                            if (obj.event.equals(RequestEvent.EmailAlreadyUsed))
                                Toast.makeText(RegistrationActivity.this, "Пользователь с такой почтой уже зарегистрирован!", Toast.LENGTH_LONG).show();
                            else if (obj.event.equals(RequestEvent.GreatEmail)){
                                Intent intent = new Intent(RegistrationActivity.this, CodeConfirmActivity.class);
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
            @Override
            public void onClosed(@NonNull WebSocket ws, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                Handler handler = new Handler();
                WebSocketListener listener = this;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrationActivity.this, "Reconnection", Toast.LENGTH_SHORT).show();
                        webSocket = client.newWebSocket(request, listener);
                    }
                }, 1000);
            }
        });
    }
}