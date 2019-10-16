package com.payudn.selector.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class CardView<T> extends LinearLayout {
    @Getter
    private CardContentAdapter cardContentAdapter;
    @Getter
    private LinearLayout typeLayout;
    @Getter
    private View root,resources_type,resourcesTime,resourcesCount;
    @Setter
    private CardContentAdapter.OnSelectItemListener onSelectItemListener;
    @Setter
    private CardContentAdapter.OnSetContetnViewListener<T> onSetContetnViewListener;
    public CardView(Context context) {
        super (context);
    }
    protected View init(List<T> tList,int resource,List<View> views){
        root = LayoutInflater.from(getContext ()).inflate(R.layout.layout_card, this, true);
        typeLayout = findViewById (R.id.card_resources_typeLayout);
        typeLayout.setOnClickListener (v -> {
            System.out.println ("进入type分类页面");
        });
        resources_type = findViewById (R.id.card_resources_type);
        resourcesTime = findViewById (R.id.card_resources_time);
        resourcesCount = findViewById (R.id.card_resources_count);
        cardContentAdapter = new CardContentAdapter (tList,resource,views);
        cardContentAdapter.setOnSetContetnViewListener (onSetContetnViewListener);
        initGridLayout();
        setViewData();
        return root;
    }
    private void initGridLayout(){
        int width = getWidth ();
        int count = width/cardContentAdapter.getRoot ().getWidth ();
        RecyclerView recyclerView = findViewById (R.id.card_resources_countView);
        recyclerView.setLayoutManager (new GridLayoutManager (getContext (),count,GridLayoutManager.VERTICAL,false));
        recyclerView.setAdapter (cardContentAdapter);
        cardContentAdapter.setOnSelectItemListener (onSelectItemListener);
    }
    abstract void setViewData();
}
