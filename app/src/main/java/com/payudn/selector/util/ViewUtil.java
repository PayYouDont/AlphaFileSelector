package com.payudn.selector.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewUtil {
    public static List<View> getAllChildViews(View view){
        List<View> allChildren = new ArrayList<> ();
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
