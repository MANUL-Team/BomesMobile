package com.MANUL.Bomes.Activities;

import static com.MANUL.Bomes.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.MANUL.Bomes.Utils.RequestCreationFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    SharedPreferences prefs;
    String identifier;
    String password;
    Handler handler = new Handler();

    Animation splash_in;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        splash_in = AnimationUtils.loadAnimation(this, R.anim.splash_in);

        findViewById(R.id.splash_icon).startAnimation(splash_in);

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        identifier = prefs.getString("identifier", "none");
        password = prefs.getString("password", "none");

        handler.postDelayed(this::connectToServer, 500);
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
                            if (obj.event.equals("ReturnUser")){
                                if (obj.user.identifier.equals(identifier)){
                                    UserData.username = obj.user.username;
                                    UserData.identifier = obj.user.identifier;
                                    UserData.password = password;
                                    UserData.email = obj.user.email;
                                    UserData.description = obj.user.description;
                                    UserData.avatar = obj.user.avatar;

                                    Intent intent = new Intent(SplashScreen.this, ChatsActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                    webSocket.close(1000, null);
                                }
                            }
                            if (obj.event.equals("ReturnCurrentAndroidVersion")){
                                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                int versionCode = packageInfo.versionCode;
                                if (!obj.version.equals(String.valueOf(versionCode))){
                                    Intent intent = new Intent(SplashScreen.this, UpgradeBomesActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                    webSocket.close(1000, null);
                                }
                                else{
                                    if (identifier.equals("none")){
                                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                    else {
                                        UniversalJSONObject loadMe = RequestCreationFactory.create("checkPrefsIdentifier", identifier, password, null);
                                        webSocket.send(objectMapper.writeValueAsString(loadMe));
                                    }
                                }
                            }
                        } catch (JsonProcessingException | PackageManager.NameNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
                try {
                    UniversalJSONObject loadVersion = RequestCreationFactory.create("GetCurrentAndroidVersion");
                    webSocket.send(objectMapper.writeValueAsString(loadVersion));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}