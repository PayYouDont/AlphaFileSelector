package com.payudn.selector.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.payudn.selector.R;

import lombok.Getter;

@Getter
public class ArrowLabelView extends LinearLayout{
    String labelText;
    String labelData;
    ImageView arrowImage;
    TextView labelTextView,labelDataView;
    ImageView arrorImageVilew;
    private Context mContext;
    private View mView;
    public ArrowLabelView(Context context) {
        this (context,null);
    }

    public ArrowLabelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ArrowLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super (context, attrs, defStyleAttr);
        initView (context,attrs);
    }

    public ArrowLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super (context, attrs, defStyleAttr, defStyleRes);
        initView (context,attrs);
    }

    //初始化UI，可根据业务需求设置默认值。
    private void initView(Context context,AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.arrow_label_view, this, true);
        labelTextView = mView.findViewById (R.id.label_text_view);
        labelDataView = mView.findViewById (R.id.label_data_view);
        arrorImageVilew = mView.findViewById (R.id.arrow_image);
        initAttr (mContext,attrs);
    }
    private void initAttr(Context context, @Nullable AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes (attrs, R.styleable.arrowLabel);
        setLabelText (typedArray.getString (R.styleable.arrowLabel_label_text));
        setLabelData (typedArray.getString (R.styleable.arrowLabel_label_data));
    }
    public void setLabelText(String text){
        labelText = text;
        labelTextView.setText (labelText);
    }
    public void setLabelData(String data){
        labelData = data;
        labelDataView.setText (labelData);
    }
    public void setLabelData(int data){
        labelData = String.valueOf (data);
        labelDataView.setText (labelData);
    }
}
