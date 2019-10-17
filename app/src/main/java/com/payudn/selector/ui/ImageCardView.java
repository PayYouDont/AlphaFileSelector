package com.payudn.selector.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.payudn.selector.R;
import com.payudn.selector.entity.MediaBean;
import com.payudn.selector.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class ImageCardView extends CardView<MediaBean>{
    private List<MediaBean> imageBeanList;
    private List<Integer> resources;
    private int resource;
    public ImageCardView(Context context, List<MediaBean> imageBeanList) {
        super (context);
        this.imageBeanList = imageBeanList;

    }
    public View create(){
        resource = R.layout.image_card_item;
        resources = new ArrayList<> ();
        resources.add (R.id.image_content);
        resources.add (R.id.image_check_btn);
        setOnLongClickListener (view -> {
            eidtStatus = true;
            setEidt();
            return true;
        });
        setOnSetContetnViewListener ((views, imageBean) -> views.forEach (v -> {
                if(v.getId () == R.id.image_content){
                    ImageView imageView = (ImageView )v;
                    imageView.setImageBitmap (FileUtil.parseToBitmap (imageBean,320,320));
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
            List<View> childViews = getAllChildViews (this);
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
            ((RelativeLayout)checkBox.getParent ()).setOnClickListener (v-> checkBox.performClick ());
        }
    }
}
