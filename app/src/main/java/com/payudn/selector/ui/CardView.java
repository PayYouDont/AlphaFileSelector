package com.payudn.selector.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;
import com.payudn.selector.adapter.GridLayoutItemDecoration;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class CardView<T> extends LinearLayout {
    protected CardContentAdapter<T> cardContentAdapter;
    protected RecyclerView recyclerView;
    protected LinearLayout typeLayout;
    protected View root,resources_type,resourcesTime,resourcesCount;
    protected CardContentAdapter.OnSelectItemListener onSelectItemListener;
    protected OnLongClickListener onLongClickListener;
    protected CardContentAdapter.OnSetContetnViewListener<T> onSetContetnViewListener;
    protected boolean eidtStatus = false;
    protected int space;
    public CardView(Context context) {
        super (context);
    }
    public View init(List<T> tList,int resource,List<Integer> resources,int spanCount){
        if(root==null){
            root = LayoutInflater.from(getContext ()).inflate(R.layout.layout_card, this, true);
        }
        typeLayout = root.findViewById (R.id.card_resources_typeLayout);
        resources_type = root.findViewById (R.id.card_resources_type);
        resourcesTime = root.findViewById (R.id.card_resources_time);
        resourcesCount = root.findViewById (R.id.card_resources_count);
        //初始化内容Adapter
        if(cardContentAdapter==null){
            cardContentAdapter = new CardContentAdapter (tList,resource,resources);
        }
        //初始化内容recyclerView
        if(recyclerView==null){
            recyclerView = root.findViewById (R.id.card_resources_recyclerView);
        }
        if(typeLayout!=null){
            typeLayout.setOnClickListener (v -> {
                System.out.println ("进入type分类页面");
            });
        }
        //选中时Listener
        if(onSelectItemListener!=null){
            cardContentAdapter.setOnSelectItemListener (onSelectItemListener);
        }
        //长按时Listener
        if(onLongClickListener!=null){
            cardContentAdapter.setOnLongClickListener (onLongClickListener);
        }
        //内容设置Listener
        if(onSetContetnViewListener!=null){
            cardContentAdapter.setOnSetContetnViewListener (onSetContetnViewListener);
        }
        recyclerView.addItemDecoration (new GridLayoutItemDecoration (root.getContext (),spanCount,space));
        GridLayoutManager gridLayoutManager = new GridLayoutManager (root.getContext (),spanCount,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager (gridLayoutManager);
        recyclerView.setAdapter (cardContentAdapter);
        return root;
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
    public static List<Integer> getCardResources(){
        List<Integer> list = new ArrayList<> ();
        list.add (R.id.card_resources_type);
        list.add (R.id.card_resources_time);
        list.add (R.id.card_resources_count);
        list.add (R.id.card_resources_recyclerView);
        return list;
    }

}
