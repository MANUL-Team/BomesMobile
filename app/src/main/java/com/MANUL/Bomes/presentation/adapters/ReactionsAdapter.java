package com.MANUL.Bomes.presentation.adapters;

import android.app.Dialog;
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
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReactionsAdapter extends RecyclerView.Adapter<ReactionsViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<String> reactions;
    ChatActivity activity;
    long id;
    Dialog dialog;
    public ReactionsAdapter(Context context, ArrayList<String> reactions, ChatActivity activity, long id, Dialog dialog){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.reactions = reactions;
        this.activity = activity;
        this.id = id;
        this.dialog = dialog;
    }
    @NonNull
    @Override
    public ReactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reaction_item_send, parent, false);
        return new ReactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionsViewHolder holder, int position) {
        String link = reactions.get(position);
        Glide.with(context).load("https://bomes.ru/" + link).into(holder.icon);
        holder.reactionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.addReaction(link, id);
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reactions.size();
    }
}
class ReactionsViewHolder extends RecyclerView.ViewHolder{
    ImageView icon;
    CardView reactionCard;
    public ReactionsViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.reaction_send_image);
        reactionCard = itemView.findViewById(R.id.reaction_send_card);
    }
}
