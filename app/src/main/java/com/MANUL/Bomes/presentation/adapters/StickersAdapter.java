package com.MANUL.Bomes.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.presentation.view.activities.ChatActivity;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.domain.SimpleObjects.Sticker;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StickersAdapter extends RecyclerView.Adapter<StickerHolder> {
    ArrayList<Sticker> stickers;
    Context context;
    LayoutInflater inflater;
    ChatActivity chat;

    public StickersAdapter(Context context, ArrayList<Sticker> stickers, ChatActivity chat){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.stickers = stickers;
        this.chat = chat;
    }
    @NonNull
    @Override
    public StickerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.sticker_item, parent, false);
        return new StickerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerHolder holder, @SuppressLint("RecyclerView") int position) {
        Sticker sticker = stickers.get(position);
        Glide.with(context).load("https://bomes.ru/" + sticker.link).into(holder.stickerImage);
        holder.stickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat.closeStickers();
                chat.sendSticker(sticker.link);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }
}
class StickerHolder extends RecyclerView.ViewHolder {
    ImageView stickerImage;
    CardView stickerBtn;
    public StickerHolder(@NonNull View itemView) {
        super(itemView);
        stickerImage = itemView.findViewById(R.id.stickerImage);
        stickerBtn = itemView.findViewById(R.id.stickerBtn);
    }
}
