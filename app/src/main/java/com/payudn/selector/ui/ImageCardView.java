package com.payudn.selector.ui;

import android.content.Context;
import android.view.View;

import com.payudn.selector.entity.ImageBean;

import java.util.List;

public class ImageCardView extends CardView<ImageBean>{
    private List<ImageBean> imageBeanList;
    private List<View> views;
    private int resource;

    public ImageCardView(Context context, List<ImageBean> imageBeanList, List<View> views, int resource) {
        super (context);
        this.imageBeanList = imageBeanList;
        this.views = views;
        this.resource = resource;

    }
    @Override
    void setViewData() {
        
    }
    public View create(){
       return super.create (imageBeanList,resource,views);
    }
}
