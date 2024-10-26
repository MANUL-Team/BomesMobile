package com.MANUL.Bomes.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.MANUL.Bomes.Activities.ChatsActivity;
import com.MANUL.Bomes.Activities.MainActivity;
import com.MANUL.Bomes.Activities.SelectStickerForHints;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UserData;

public class SettingsFragment extends Fragment {

    CardView stickers_hints_settings, exit_from_account_settings;
    ChatsActivity activity;

    public SettingsFragment(ChatsActivity activity){
        this.activity = activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        stickers_hints_settings = activity.findViewById(R.id.stickers_hints_settings);
        stickers_hints_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SelectStickerForHints.class);
                activity.startActivity(intent);
            }
        });
        exit_from_account_settings = activity.findViewById(R.id.exit_from_account_settings);
        exit_from_account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
                UserData.identifier = null;
                UserData.avatar = null;
                UserData.email = null;
                UserData.chatId = null;
                UserData.password = null;
                UserData.description = null;
                UserData.isLocalChat = 0;
                UserData.chatAvatar = null;
                UserData.table_name = null;
                UserData.chatName = null;
                prefs.edit().putString("identifier", "none").apply();
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                activity.finish();
            }
        });
    }
}