package com.payudn.selector.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.payudn.selector.R;
import com.payudn.selector.adapter.GridLayoutItemDecoration;
import com.payudn.selector.entity.FileBean;
import com.payudn.selector.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ImageCardView extends CardView<FileBean>{
    @Getter
    private List<FileBean> imageBeanList;
    private List<Integer> resources;
    private int resource;
    private int coverMargin;
    public ImageCardView(Context context, List<FileBean> imageBeanList) {
        super (context);
        this.imageBeanList = imageBeanList;
    }
    public View create(){
        resource = R.layout.image_card_item;
        resources = new ArrayList<> ();
        resources.add (R.id.image_content);
        resources.add (R.id.image_check_btn);
        setSpace (10);
        coverMargin = GridLayoutItemDecoration.dip2px (getContext (),space);
        setOnLongClickListener (view -> {
            eidtStatus = true;
            setEidt();
            return true;
        });
        setOnSetContetnViewListener ((viewHolder, imageBean) -> viewHolder.views.forEach (v -> {
                if(v.getId () == R.id.image_content){
                    ImageView imageView = (ImageView )v;
                    Point p = new Point ();
                    root.getDisplay ().getSize (p);
                    int width = p.x/4;
                    Bitmap bitmap = FileUtil.parseToBitmap (root.getContext (),imageBean.getPath (),width,width,25,1);
                    imageView.setImageBitmap (bitmap);
                }else if(v.getId () == R.id.image_check_btn){
                    CheckBox checkBox = (CheckBox)v;
                    checkBox.setOnCheckedChangeListener ((compoundButton, b) -> {
                        if(b){
                            System.out.println ("执行删除图片操作");
                        }
                    });
                }
            })
        );
        return init (imageBeanList,resource,resources,4);
    }
    public void setEidt(){
        if(isEidtStatus ()){
            List<View> childViews = getAllChildViews (root);
            childViews.forEach (v -> {
                if(v instanceof CheckBox){
                    CheckBox box = (CheckBox) v;
                    setCheckBoxStatus (box);
                }
            });
        }
    }
    private void setCheckBoxStatus(CheckBox checkBox){
        checkBox.setVisibility (VISIBLE);
        checkBox.setOnCheckedChangeListener ((buttonView, isChecked) -> {
            RelativeLayout relativeLayout = null;
            if(buttonView.getParent () instanceof RelativeLayout){
                relativeLayout = (RelativeLayout)buttonView.getParent ();
            }
            if(isChecked){
                if(relativeLayout!=null){
                    relativeLayout.setBackgroundColor (Color.parseColor ("#57000000"));
                }
            }else{
                relativeLayout.setBackgroundColor (Color.parseColor ("#00000000"));
            }
        });
        if(checkBox.getParent () instanceof RelativeLayout){
            RelativeLayout relativeLayout = (RelativeLayout)checkBox.getParent ();
            relativeLayout.setOnClickListener (v-> checkBox.performClick ());
            MarginLayoutParams params = (MarginLayoutParams) relativeLayout.getLayoutParams ();
            Point imageSize = new Point ();
            ImageView imageView = getCardContentAdapter ().getRoot ().findViewById (R.id.image_content);
            if(imageView!=null){
                imageSize.x = imageView.getWidth ();
                imageSize.y = imageView.getHeight ()- 2*coverMargin;
                params.width = imageSize.x;
                params.height = imageSize.y;
                params.topMargin = coverMargin;
                relativeLayout.setLayoutParams (params);
            }
        }
    }
}
