package com.payudn.selector.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardViewHolder extends RecyclerView.ViewHolder{
    public View consumeTypeView;
    public List<View> views;
    public CardViewHolder(@NonNull View itemView,List<View> views) {
        super (itemView);
        consumeTypeView = itemView;
        this.views = views;
    }
}
