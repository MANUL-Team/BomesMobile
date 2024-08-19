package com.MANUL.Bomes;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayer extends AppCompatActivity {
    public static String Video_URL;
    private VideoView video_player;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        video_player = findViewById(R.id.video_player);
        video_player.setVideoURI(Uri.parse("https://bomes.ru/" + Video_URL));

        MediaController mediaController = new MediaController(this);
        video_player.setMediaController(mediaController);
        mediaController.setAnchorView(video_player);

        video_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video_player.start();
            }
        });
    }
}