package com.payudn.selector.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CardViewHolder extends RecyclerView.ViewHolder{
    public View consumeTypeView;
    public List<View> views;
    public CardViewHolder(@NonNull View itemView, List<Integer> resources) {
        super (itemView);
        this.consumeTypeView = itemView;
        views = new ArrayList<> ();
        resources.forEach (integer -> {
            View view = itemView.findViewById (integer);
            views.add (view);
        });
    }

}
