package com.MANUL.Bomes.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.MANUL.Bomes.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomizeStickerHints extends AppCompatActivity {

    public static String stickerLink;
    public static int stickerId;

    ImageView sticker_img;
    LinearLayout hints_layout;

    ArrayList<String> hints = new ArrayList<>();
    LayoutInflater inflater;

    CardView add_hint_btn;
    EditText hint_edit_text;
    ImageView back_btn;

    SharedPreferences preferences;
    String data;
    public static String separator = "/////";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_sticker_hints);
        init();
        loadHints();
    }
    private void init(){
        inflater = LayoutInflater.from(this);

        preferences = getSharedPreferences("StickersHints", MODE_PRIVATE);
        data = preferences.getString(String.valueOf(stickerId), "");
        String[] values = data.split(separator);
        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty())
                hints.add(values[i]);
        }

        sticker_img = findViewById(R.id.sticker_img);
        Glide.with(this).load("https://bomes.ru/" + stickerLink).into(sticker_img);
        hints_layout = findViewById(R.id.hints_layout);
        hint_edit_text = findViewById(R.id.hint_edit_text);
        add_hint_btn = findViewById(R.id.add_hint_btn);
        add_hint_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = hint_edit_text.getText().toString();
                if (!value.isEmpty() && !hints.contains(value)){
                    addHint(value);
                }
            }
        });
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadHints(){
        for (int i = 0; i < hints.size(); i++) {
            String value = hints.get(i);
            View view = inflater.inflate(R.layout.sticker_hint_item, null, false);
            ((TextView) view.findViewById(R.id.hint_value_text)).setText(value);
            view.findViewById(R.id.remove_hint_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hints.remove(value);
                    hints_layout.removeView(view);
                    saveData();
                }
            });
            hints_layout.addView(view);
        }
    }
    private void addHint(String hint){
        hints.add(hint);
        View view = inflater.inflate(R.layout.sticker_hint_item, null, false);
        ((TextView) view.findViewById(R.id.hint_value_text)).setText(hint);
        view.findViewById(R.id.remove_hint_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hints.remove(hint);
                hints_layout.removeView(view);
                saveData();
            }
        });
        hints_layout.addView(view);

        saveData();
    }
    private String constructData(){
        if (!hints.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < hints.size() - 1; i++) {
                result.append(hints.get(i)).append(separator);
            }
            result.append(hints.get(hints.size() - 1));
            return result.toString();
        }
        else{
            return "";
        }
    }
    private void saveData(){
        preferences.edit().putString(String.valueOf(stickerId), constructData()).apply();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.activity_switch_reverse_first);
    }
}