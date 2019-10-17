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
    private List<Integer> resources;
    @Setter
    private OnSelectItemListener onSelectItemListener;
    @Setter
    private View.OnLongClickListener onLongClickListener;
    @Setter
    private OnSetContetnViewListener<T> onSetContetnViewListener;
    CardViewHolder holder;

    public CardContentAdapter(List<T> tList, int resource, List<Integer> resources) {
        this.tList = tList;
        this.resource = resource;
        this.resources = resources;
    }

    public CardContentAdapter(List<T> tList, int resource, List<Integer> resources, OnSelectItemListener onSelectItemListener, View.OnLongClickListener onLongClickListener, OnSetContetnViewListener<T> onSetContetnViewListener) {
        this.tList = tList;
        this.resource = resource;
        this.resources = resources;
        this.onSelectItemListener = onSelectItemListener;
        this.onLongClickListener = onLongClickListener;
        this.onSetContetnViewListener = onSetContetnViewListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        root = LayoutInflater.from (parent.getContext ()).inflate (resource,parent,false);
        holder = new CardViewHolder (root,resources);
        holder.consumeTypeView.setOnClickListener (view -> {
            if(onSelectItemListener!=null){
                onSelectItemListener.onSelect (holder.getAdapterPosition ());
            }
        });
        if(onLongClickListener!=null){
            holder.consumeTypeView.setOnLongClickListener (onLongClickListener);
        }
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
    public interface OnSetContetnViewListener<T>{
        void setViews(List<View> views,T t);
    }
    public interface OnSelectItemListener{
        void onSelect(int position);
    }
}
