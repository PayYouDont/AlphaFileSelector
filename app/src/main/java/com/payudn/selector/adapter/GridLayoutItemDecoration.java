package com.payudn.selector.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int count;
    private Paint paint;
    public GridLayoutItemDecoration(int space,int count) {
        this.space = space;
        this.count = count;
        paint = new Paint();
        paint.setAlpha(200); // 设置alpha不透明度,范围为0~255
        paint.setColor(Color.parseColor("#7C7C7C"));
        paint.setStrokeWidth(1);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw (c, parent, state);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            //——
            //上
            c.drawLine (childView.getX ()-1,childView.getY ()-1,childView.getX ()+childView.getWidth ()+1,childView.getY ()-1,paint);
            //|左
            c.drawLine (childView.getX ()-1,childView.getY ()-1,childView.getX ()-1,childView.getY ()+childView.getHeight ()+1,paint);
            //右|
            c.drawLine (childView.getX ()+childView.getWidth ()+1,childView.getY ()-1,childView.getX ()+childView.getWidth ()+1,childView.getY ()+childView.getHeight ()+1,paint);
            //下
            //——
            c.drawLine (childView.getX ()-1,childView.getY ()+childView.getHeight ()+1,childView.getX ()+childView.getWidth ()-1,childView.getY ()+childView.getHeight ()+1,paint);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver (c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = space;
        outRect.bottom = space;
        if(parent.getChildLayoutPosition (view)<count){//第一排
            outRect.top = 1;
        }
        if(parent.getChildLayoutPosition (view)%count==0){
            outRect.left = 1;
        }else if((parent.getChildLayoutPosition (view)+1)%count==0){
            outRect.right = 2;
        }
    }
}
