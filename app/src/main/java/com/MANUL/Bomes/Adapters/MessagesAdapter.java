package com.MANUL.Bomes.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.MANUL.Bomes.Activities.ChatActivity;
import com.MANUL.Bomes.Activities.PhotoPlayer;
import com.MANUL.Bomes.Activities.VideoPlayer;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.Message;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<Message> messages;
    ObjectMapper objectMapper = new ObjectMapper();
    ChatActivity activity;
    Animation alpha_in, alpha_out, click_down, click_up;
    private final float MAX_DIST = 150;
    public MessagesAdapter(Context context, ArrayList<Message> messages, ChatActivity activity){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.messages = messages;
        this.activity = activity;
        alpha_in = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(context, R.anim.alpha_out);
        click_down = AnimationUtils.loadAnimation(context, R.anim.click_down);
        click_up = AnimationUtils.loadAnimation(context, R.anim.click_up);
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.reactionsLayout.removeAllViews();
        ArrayList<String> wasTypes = new ArrayList<>();
        String myType = "";
        for (int i = 0; i < message.reactions.length; i++) {
            if (message.reactions[i].sender.equals(UserData.identifier))
                myType = message.reactions[i].type;
        }
        for (int i = 0; i < message.reactions.length; i++) {
            if (!wasTypes.contains(message.reactions[i].type)) {
                View view = inflater.inflate(R.layout.reaction_item, null);
                ImageView imageView = view.findViewById(R.id.reaction_image);
                TextView textView = view.findViewById(R.id.reaction_count_text);
                CardView cardView = view.findViewById(R.id.reaction_card);
                final boolean isMyType = myType.equals(message.reactions[i].type);
                final String reactionType = message.reactions[i].type;
                if (isMyType)
                    cardView.setCardBackgroundColor(R.color.myReaction);
                Glide.with(context).load("https://bomes.ru/" + message.reactions[i].type).into(imageView);
                textView.setText(String.valueOf(getReactionTypeCount(message.reactions[i].type, message.reactions)));
                holder.reactionsLayout.addView(view);
                wasTypes.add(message.reactions[i].type);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMyType){
                            activity.removeReaction(message.id);
                        }
                        else{
                            activity.addReaction(reactionType, message.id);
                        }
                    }
                });
            }
        }
        holder.message = message;
        message.holder = holder.messageCard;
        message.viewHolder = holder;
        holder.startPosX = holder.moveCard.getX();

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

                holder.replyCard.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                holder.replyCard.startAnimation(click_down);
                                break;
                            case MotionEvent.ACTION_UP:
                                holder.replyCard.startAnimation(click_up);
                                activity.scrollToMessage(reply.id);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                holder.replyCard.startAnimation(click_up);
                                break;
                        }
                        return true;
                    }
                });
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

        switch (message.dataType) {
            case "text":
                holder.textValueMsg.setVisibility(View.VISIBLE);
                holder.textValueMsg.setText(message.value);
                break;
            case "image":
                holder.imageCard.setVisibility(View.VISIBLE);
                holder.imageMsg.setVisibility(View.VISIBLE);
                Glide.with(context).load("https://bomes.ru/" + message.value).into(holder.imageMsg);

                holder.imageCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PhotoPlayer.PHOTO_URL = message.value;
                        Intent intent = new Intent(context, PhotoPlayer.class);
                        activity.startActivity(intent);
                    }
                });
                break;
            case "sticker":
                holder.stickerMsg.setVisibility(View.VISIBLE);
                Glide.with(context).load("https://bomes.ru/" + message.value).into(holder.stickerMsg);
                break;
            case "video":
                holder.videoCard.setVisibility(View.VISIBLE);
                holder.videoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoPlayer.Video_URL = message.value;
                        Intent intent = new Intent(context, VideoPlayer.class);
                        activity.startActivity(intent);
                    }
                });
                break;
            case "audio":
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
                            if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.start();
                                Picasso.with(context).load(R.drawable.white_stop_audio).into(holder.audio_display_controls);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mediaPlayer.isPlaying()) {
                                            holder.audioTimeText.setText(getAudioTime(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition()));
                                            handler.postDelayed(this, 100);
                                        }
                                    }
                                }, 100);
                            } else {
                                mediaPlayer.pause();
                                Picasso.with(context).load(R.drawable.white_play_audio).into(holder.audio_display_controls);
                            }
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Picasso.with(context).load(R.drawable.white_play_audio).into(holder.audio_display_controls);
                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        holder.timeMsg.setText(new SimpleDateFormat("HH:mm").format(message.time*1000));

        ConstraintSet set = new ConstraintSet();
        set.clone(holder.messageInnerLayout);
        if (message.sender.equals(UserData.identifier)){
            holder.isMyMessage = true;
            set.clear(R.id.messageCard, ConstraintSet.LEFT);
            set.connect(R.id.messageCard, ConstraintSet.RIGHT, R.id.messageInnerLayout, ConstraintSet.RIGHT);
        }
        else{
            holder.isMyMessage = false;
            set.clear(R.id.messageCard, ConstraintSet.RIGHT);
            set.connect(R.id.messageCard, ConstraintSet.LEFT, R.id.messageInnerLayout, ConstraintSet.LEFT);
        }
        set.applyTo(holder.messageInnerLayout);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            private void moveBack(){
                Handler handler = new Handler();
                holder.replyBack = true;
                if (holder.moveCard.getX() < holder.startPosX) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            float newPos = holder.moveCard.getX() + 8;
                            if (newPos <= holder.startPosX) {
                                holder.moveCard.setX(newPos);
                                handler.postDelayed(this, 1);
                            } else {
                                holder.moveCard.setX(holder.startPosX);
                                holder.replyBack = false;
                                holder.drag = false;
                            }
                        }
                    }, 1);
                }
                else{
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            float newPos = holder.moveCard.getX() - 8;
                            if (newPos >= holder.startPosX) {
                                holder.moveCard.setX(newPos);
                                handler.postDelayed(this, 1);
                            } else {
                                holder.moveCard.setX(holder.startPosX);
                                holder.replyBack = false;
                                holder.drag = false;
                            }
                        }
                    }, 1);
                }
            }
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float evX = event.getX();
                float evY = event.getY();

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (!holder.drag) {
                            holder.drag = true;
                            holder.dragX = evX;
                            holder.dragY = evY;
                            holder.isScroll = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (holder.drag){
                            holder.diffX = evX - holder.dragX;
                            float diffY = evY - holder.dragY;
                            if (Math.abs(holder.diffX) <= MAX_DIST && Math.abs(diffY) < 25){
                                holder.moveCard.setX(holder.startPosX + holder.diffX);
                                if (Math.abs(holder.diffX) > 30)
                                    holder.move = true;
                            }
                            else if (Math.abs(holder.diffX) > MAX_DIST){
                                if (!holder.replyBack) {
                                    activity.startReplying(message);
                                    Vibrator vibration = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibration.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        vibration.vibrate(25);
                                    }
                                }
                                moveBack();
                            }
                            else if (Math.abs(diffY) >= 30){
                                moveBack();
                                holder.move = true;
                            }
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        holder.move = false;
                    case MotionEvent.ACTION_CANCEL:
                        if (Math.abs(evX - holder.dragX) < 20){
                            moveBack();
                            holder.drag = false;
                        }
                        else {
                            moveBack();
                        }
                        if (event.getAction() == MotionEvent.ACTION_CANCEL){
                            holder.isScroll = true;
                        }
                        return false;
                }
                return false;
            }
        };
        holder.touchEventer.setOnTouchListener(touchListener);
        holder.touchEventer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!holder.move && !holder.isScroll) {
                    createDialog(message);
                }
                return false;
            }
        });
        holder.touchEventer.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                activity.addReaction(activity.reactions.get(0), message.id);
            }
        }));
    }
    public int getReactionTypeCount(String type, UniversalJSONObject[] reactions){
        int count = 0;
        for (int i = 0; i < reactions.length; i++) {
            if (reactions[i].type.equals(type)) count++;
        }
        return count;
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
    public void createDialog(Message message){
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog);
        dialog.show();

        RecyclerView reactionsRecycler = dialog.findViewById(R.id.reactionsRecycler);
        ReactionsAdapter adapter = new ReactionsAdapter(context, activity.reactions, activity, message.id, dialog);
        reactionsRecycler.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        reactionsRecycler.setAdapter(adapter);

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
}
