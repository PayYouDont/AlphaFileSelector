package com.payudn.selector.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;
import com.payudn.selector.adapter.GridLayoutItemDecoration;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CardView<T> extends LinearLayout {
    @Getter
    private CardContentAdapter cardContentAdapter;
    @Getter
    private LinearLayout typeLayout;
    @Getter
    private View root,resources_type,resourcesTime,resourcesCount;
    @Setter
    protected CardContentAdapter.OnSelectItemListener onSelectItemListener;
    @Setter
    protected OnLongClickListener onLongClickListener;
    @Setter
    protected CardContentAdapter.OnSetContetnViewListener<T> onSetContetnViewListener;
    @Getter
    protected boolean eidtStatus = false;
    public CardView(Context context) {
        super (context);
    }
    protected View init(List<T> tList,int resource,List<Integer> resources,int spanCount){
        root = LayoutInflater.from(getContext ()).inflate(R.layout.layout_card, this, true);
        typeLayout = findViewById (R.id.card_resources_typeLayout);
        typeLayout.setOnClickListener (v -> {
            System.out.println ("进入type分类页面");
        });
        resources_type = findViewById (R.id.card_resources_type);
        resourcesTime = findViewById (R.id.card_resources_time);
        resourcesCount = findViewById (R.id.card_resources_count);
        cardContentAdapter = new CardContentAdapter (tList,resource,resources);
        if(onSelectItemListener!=null){
            cardContentAdapter.setOnSelectItemListener (onSelectItemListener);
        }
        if(onLongClickListener!=null){
            cardContentAdapter.setOnLongClickListener (onLongClickListener);
        }
        if(onSetContetnViewListener!=null){
            cardContentAdapter.setOnSetContetnViewListener (onSetContetnViewListener);
        }
        initGridLayout(spanCount);
        return root;
    }
    private void initGridLayout(int spanCount){
        RecyclerView recyclerView = findViewById (R.id.card_resources_countView);
        recyclerView.addItemDecoration (new GridLayoutItemDecoration (10,spanCount));
        GridLayoutManager gridLayoutManager = new GridLayoutManager (getContext (),spanCount,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager (gridLayoutManager);
        recyclerView.setAdapter (cardContentAdapter);
    }
    protected List<View> getAllChildViews(View view){
        List<View> allChildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                allChildren.add(viewChild);
                //再次 调用本身（递归）
                allChildren.addAll(getAllChildViews(viewChild));
            }
        }
        return allChildren;
    }
}
