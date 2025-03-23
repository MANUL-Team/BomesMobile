package com.MANUL.Bomes.presentation.view.activities;

import static com.MANUL.Bomes.domain.Utils.ServerUtilsKt.NowRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.MANUL.Bomes.R;
import com.MANUL.Bomes.domain.SimpleObjects.Sticker;
import com.MANUL.Bomes.domain.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.domain.Utils.RequestCreationFactory;
import com.MANUL.Bomes.domain.Utils.RequestEvent;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SelectStickerForHints extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;


    LayoutInflater inflater;
    FlexboxLayout stickers_flexbox;
    ArrayList<Sticker> stickers = new ArrayList<>();
    ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sticker_for_hints);
        init();
        connectToServer();
    }
    private void init(){
        inflater = LayoutInflater.from(this);
        stickers_flexbox = findViewById(R.id.stickers_flexbox);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    @SuppressLint({"CommitPrefEdits", "NotifyDataSetChanged", "MissingInflatedId"})
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals(RequestEvent.ReturnStickers)){
                                for (int i = 0; i < obj.stickers.length; i++) {
                                    String sticker = obj.stickers[i];
                                    int stickerId = i;
                                    stickers.add(new Sticker(sticker));
                                    View view = inflater.inflate(R.layout.sticker_for_hint_item, null, false);
                                    Glide.with(SelectStickerForHints.this).load("https://bomes.ru/" + sticker).into((ImageView) view.findViewById(R.id.sticker_for_hint_img));
                                    view.findViewById(R.id.sticker_card).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CustomizeStickerHints.stickerLink = sticker;
                                            CustomizeStickerHints.stickerId = stickerId;
                                            Intent intent = new Intent(SelectStickerForHints.this, CustomizeStickerHints.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.activity_switch_animation_first, R.anim.nothing);
                                        }
                                    });
                                    stickers_flexbox.addView(view);
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
                try {
                    UniversalJSONObject getStickers = RequestCreationFactory.create(RequestEvent.GetStickers);
                    webSocket.send(objectMapper.writeValueAsString(getStickers));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onClosed(@NonNull WebSocket ws, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                Handler handler = new Handler();
                WebSocketListener listener = this;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SelectStickerForHints.this, "Reconnection", Toast.LENGTH_SHORT).show();
                        webSocket = client.newWebSocket(request, listener);
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) webSocket.close(1000, null);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.activity_switch_reverse_first);
    }
}