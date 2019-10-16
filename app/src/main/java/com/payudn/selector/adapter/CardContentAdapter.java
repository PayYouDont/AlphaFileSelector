package com.payudn.selector.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CardContentAdapter<T> extends RecyclerView.Adapter{
    private List<T> tList;
    private int resource;
    @Getter
    private View root;
    @Setter
    private List<View> views;
    @Setter
    private OnSelectItemListener onSelectItemListener;
    @Setter
    private OnSetContetnViewListener<T> onSetContetnViewListener;
    CardViewHolder holder;

    public CardContentAdapter(List<T> tList, int resource, List<View> views) {
        this.tList = tList;
        this.resource = resource;
        this.views = views;
    }

    public CardContentAdapter(List<T> tList, int resource, List<View> views, OnSelectItemListener onSelectItemListener, OnSetContetnViewListener<T> onSetContetnViewListener) {
        this.tList = tList;
        this.resource = resource;
        this.views = views;
        this.onSelectItemListener = onSelectItemListener;
        this.onSetContetnViewListener = onSetContetnViewListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from (parent.getContext ()).inflate (resource,parent,false);
        holder = new CardViewHolder (root,views);
        holder.consumeTypeView.setOnClickListener (v -> {
            if(onSelectItemListener!=null){
                onSelectItemListener.onSelect (holder.getAdapterPosition ());
            }

        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T t = tList.get (position);
        CardViewHolder viewHolder = (CardViewHolder) holder;
        if(onSetContetnViewListener!=null){
            onSetContetnViewListener.setViews (viewHolder.views,t);
        }
    }

    @Override
    public int getItemCount() {
        return tList.size ();
    }

    public interface OnSelectItemListener{
        void onSelect(int position);
    }
    public interface OnSetContetnViewListener<T>{
        void setViews(List<View> views,T t);
    }
}
