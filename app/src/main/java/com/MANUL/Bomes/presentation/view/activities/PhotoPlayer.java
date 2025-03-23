package com.MANUL.Bomes.presentation.view.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.MANUL.Bomes.R;
import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import java.util.UUID;

public class PhotoPlayer extends AppCompatActivity {

    public static String PHOTO_URL;
    TouchImageView scaling_image;

    ConstraintLayout photo_settings, main_view;
    CardView download_btn;

    Animation alpha_in, alpha_out;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_player);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scaling_image = findViewById(R.id.scaling_image);
        Glide.with(this).load("https://bomes.ru/" + PHOTO_URL).into(scaling_image);


        main_view = findViewById(R.id.main_view);
        photo_settings = findViewById(R.id.photo_settings);
        download_btn = findViewById(R.id.download_btn);
        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        scaling_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo_settings.getVisibility() == View.VISIBLE){
                    photo_settings.startAnimation(alpha_out);
                    photo_settings.setVisibility(View.GONE);
                }
                else{
                    photo_settings.setVisibility(View.VISIBLE);
                    photo_settings.startAnimation(alpha_in);
                }
            }
        });

        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://bomes.ru/" + PHOTO_URL;
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String fileName = UUID.randomUUID().toString();
                String[] fileData = PHOTO_URL.split("\\.");
                String title = "Bomes/" + fileName + "." + fileData[fileData.length-1];
                request.setDescription("Downloading...");
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
                DownloadManager downloadManager = (DownloadManager) PhotoPlayer.this.getSystemService(DOWNLOAD_SERVICE);
                long downloadID = downloadManager.enqueue(request);
                BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (downloadID == id) {
                            Toast.makeText(PhotoPlayer.this, "Загрузка завершена!", Toast.LENGTH_SHORT).show();
                            unregisterReceiver(this);
                        }
                    }
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
                }
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.activity_switch_reverse_first);
    }
}