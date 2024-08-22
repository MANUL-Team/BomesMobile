package com.MANUL.Bomes.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.SimpleObjects.Message;
import com.MANUL.Bomes.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView usernameMsg, textValueMsg, timeMsg, replyText, audioTimeText;
    ConstraintLayout messageLayout;
    ImageView imageMsg, stickerMsg, isReadMsg, audio_display_controls;
    CardView messageCard, replyCard, imageCard, videoCard, audioPlayerCard, playAudioCard;
    public Message message;

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
