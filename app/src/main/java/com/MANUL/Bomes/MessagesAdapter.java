package com.MANUL.Bomes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<Message> messages;
    ObjectMapper objectMapper = new ObjectMapper();
    ChatActivity activity;
    public MessagesAdapter(Context context, ArrayList<Message> messages, ChatActivity activity){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.messages = messages;
        this.activity = activity;
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.message = message;
        message.holder = holder.messageCard;
        message.viewHolder = holder;

        if (message.reply.isEmpty()){
            holder.replyCard.setVisibility(View.GONE);
        }
        else{
            try {
                holder.replyCard.setVisibility(View.VISIBLE);
                UniversalJSONObject reply = objectMapper.readValue(message.reply, UniversalJSONObject.class);
                String replyText = reply.username + ": ";
                if (reply.dataType.equals("text")){
                    replyText += reply.value;
                }
                else{
                    replyText += "Вложение";
                }
                holder.replyText.setText(replyText);
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        holder.usernameMsg.setText(message.username);

        if (message.isRead == 1 || !message.sender.equals(UserData.identifier))
            holder.isReadMsg.setVisibility(View.GONE);
        else
            holder.isReadMsg.setVisibility(View.VISIBLE);

        holder.textValueMsg.setVisibility(View.GONE);
        holder.imageMsg.setVisibility(View.GONE);
        holder.stickerMsg.setVisibility(View.GONE);
        holder.videoCard.setVisibility(View.GONE);
        holder.audioPlayerCard.setVisibility(View.GONE);

        if (message.dataType.equals("text")){
            holder.textValueMsg.setVisibility(View.VISIBLE);
            holder.textValueMsg.setText(message.value);
        }
        else if (message.dataType.equals("image")){
            holder.imageCard.setVisibility(View.VISIBLE);
            holder.imageMsg.setVisibility(View.VISIBLE);
            Glide.with(context).load("https://bomes.ru/" + message.value).into(holder.imageMsg);
        }
        else if (message.dataType.equals("sticker")){
            holder.stickerMsg.setVisibility(View.VISIBLE);
            Glide.with(context).load("https://bomes.ru/" + message.value).into(holder.stickerMsg);
        }
        else if (message.dataType.equals("video")){
            holder.videoCard.setVisibility(View.VISIBLE);
            holder.videoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPlayer.Video_URL = message.value;
                    Intent intent = new Intent(context, VideoPlayer.class);
                    activity.startActivity(intent);
                }
            });
        }
        else if (message.dataType.equals("audio")){
            holder.audioPlayerCard.setVisibility(View.VISIBLE);
            MediaPlayer mediaPlayer = new MediaPlayer();
            Handler handler = new Handler();
            try {
                mediaPlayer.setDataSource("https://bomes.ru/" + message.value);
                mediaPlayer.prepare();

                holder.audioTimeText.setText(getAudioTime(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition()));

                holder.playAudioCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            Glide.with(context).load(R.drawable.white_stop_audio).into(holder.audio_display_controls);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer.isPlaying()){
                                        holder.audioTimeText.setText(getAudioTime(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition()));
                                        handler.postDelayed(this, 100);
                                    }
                                }
                            }, 100);
                        }
                        else{
                            mediaPlayer.pause();
                            Glide.with(context).load(R.drawable.white_play_audio).into(holder.audio_display_controls);
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Glide.with(context).load(R.drawable.white_play_audio).into(holder.audio_display_controls);
                    }
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        holder.timeMsg.setText(new SimpleDateFormat("HH:mm").format(message.time*1000));

        ConstraintSet set = new ConstraintSet();
        set.clone(holder.messageLayout);
        if (message.sender.equals(UserData.identifier)){
            set.clear(R.id.messageCard, ConstraintSet.LEFT);
            set.connect(R.id.messageCard, ConstraintSet.RIGHT, R.id.messageLayout, ConstraintSet.RIGHT, 16);
        }
        else{
            set.clear(R.id.messageCard, ConstraintSet.RIGHT);
            set.connect(R.id.messageCard, ConstraintSet.LEFT, R.id.messageLayout, ConstraintSet.LEFT, 16);
        }
        set.applyTo(holder.messageLayout);


        holder.messageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setContentView(R.layout.dialog);
                dialog.show();
                CardView replyCardDialog = dialog.findViewById(R.id.replyCardDialog);
                replyCardDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startReplying(message);
                        dialog.dismiss();
                    }
                });

                CardView editCardDialog = dialog.findViewById(R.id.editCardDialog);
                if (message.sender.equals(UserData.identifier) && message.dataType.equals("text")) {
                    editCardDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startEditing(message);
                            dialog.dismiss();
                        }
                    });
                }
                else{
                    editCardDialog.setVisibility(View.GONE);
                }

                CardView deleteCardDialog = dialog.findViewById(R.id.deleteCardDialog);
                if (message.sender.equals(UserData.identifier)) {
                    deleteCardDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.deleteMessage(message);
                            dialog.dismiss();
                        }
                    });
                }
                else{
                    deleteCardDialog.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    private String getAudioTime(int duration, int currentTime){
        int durationSecs = duration / 1000;
        int mins = durationSecs / 60;
        int secs = durationSecs - mins * 60;
        String minsStr = String.valueOf(mins);
        if (minsStr.length() < 2)
            minsStr = "0" + minsStr;
        String secsStr = String.valueOf(secs);
        if (secsStr.length() < 2)
            secsStr = "0" + secsStr;
        String durationStr = minsStr + ":" + secsStr;

        int currentTimeSecs = currentTime / 1000;
        mins = currentTimeSecs / 60;
        secs = currentTimeSecs - mins * 60;
        minsStr = String.valueOf(mins);
        if (minsStr.length() < 2)
            minsStr = "0" + minsStr;
        secsStr = String.valueOf(secs);
        if (secsStr.length() < 2)
            secsStr = "0" + secsStr;
        String currentTimeStr = minsStr + ":" + secsStr;
        return currentTimeStr + "/" + durationStr;
    }
}
class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView usernameMsg, textValueMsg, timeMsg, replyText, audioTimeText;
    ConstraintLayout messageLayout;
    ImageView imageMsg, stickerMsg, isReadMsg, audio_display_controls;
    CardView messageCard, replyCard, imageCard, videoCard, audioPlayerCard, playAudioCard;
    Message message;
    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        usernameMsg = itemView.findViewById(R.id.usernameMsg);
        textValueMsg = itemView.findViewById(R.id.textValueMsg);
        timeMsg = itemView.findViewById(R.id.timeMsg);
        messageLayout = itemView.findViewById(R.id.messageLayout);
        imageMsg = itemView.findViewById(R.id.imageMsg);
        stickerMsg = itemView.findViewById(R.id.stickerMsg);
        isReadMsg = itemView.findViewById(R.id.isReadMsg);
        messageCard = itemView.findViewById(R.id.messageCard);
        replyCard = itemView.findViewById(R.id.replyCard);
        replyText = itemView.findViewById(R.id.replyText);
        imageCard = itemView.findViewById(R.id.imageCard);
        videoCard = itemView.findViewById(R.id.videoCard);
        audioPlayerCard = itemView.findViewById(R.id.audioPlayerCard);
        playAudioCard = itemView.findViewById(R.id.playAudioCard);
        audioTimeText = itemView.findViewById(R.id.audioTimeText);
        audio_display_controls = itemView.findViewById(R.id.audio_display_controls);
    }
}
