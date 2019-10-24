package com.payudn.selector.ui.phone;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardViewHolder;
import com.payudn.selector.adapter.GridLayoutItemDecoration;
import com.payudn.selector.annotation.RootView;
import com.payudn.selector.annotation.ViewById;
import com.payudn.selector.entity.FileBean;
import com.payudn.selector.ui.ArrowLabelView;
import com.payudn.selector.ui.BaseFragment;
import com.payudn.selector.util.ViewUtil;

import java.io.File;
import java.util.List;

import lombok.Getter;

@Getter
public class PhoneFragment extends BaseFragment {
    private PhoneModel phoneModel;
    @RootView(R.layout.phone_fragment)
    private View root;
    @ViewById(R.id.view_model_image)
    private ImageView viewModelImage;
    @ViewById(R.id.title_layout)
    private LinearLayout titleLayout;
    @ViewById(R.id.root_path)
    private ArrowLabelView filePath;
    //线性布局
    public static final int LAYOUT_LINEAR = 0;
    //网格布局
    public static final int LAYOUT_GRID = 1;
    private int layoutType = 0;
    public static PhoneFragment newInstance() {
        return new PhoneFragment ();
    }
    @Override
    protected void onCreateView() {
        parentId = 0;
        filePath.setLabelText ("内部存储设备");
        filePath.setLabelData (0);
        filePath.setOnClickListener (v -> refreshAdapterByParentId (Integer.valueOf (filePath.getLabelData ()),titleLayout));
        viewModelImage.setOnClickListener (v -> changeLayout ());
        phoneModel = ViewModelProviders.of (this).get (PhoneModel.class);
        phoneModel.setContext (getContext ());
        phoneModel.getFiles (parentId,null).observe (this,fileBeans -> initRecyclerView (R.id.file_recycler,fileBeans,titleLayout));
    }
    /**
     * @Author peiyongdong
     * @Description ( 设置网格布局 )
     * @Date 10:10 2019/10/23
     * @Param [holder, fileBean]
     * @return void
     **/
    private void setGridViewHolder(CardViewHolder holder, FileBean fileBean){
        File file = new File (fileBean.getPath ());
        ((LinearLayout)holder.itemView.findViewById (R.id.file_item_layout)).setOrientation (LinearLayout.VERTICAL);
        holder.views.forEach (view -> {
            switch (view.getId ()){
                case R.id.file_type_image:
                    ImageView imageView = (ImageView)view;
                    setFileTypeImage (file,imageView,holder,fileBean,150,150,titleLayout);
                    break;
                case R.id.file_name:
                    LinearLayout linearLayout = (LinearLayout)view.getParent ();
                    ViewGroup.LayoutParams params = linearLayout.getLayoutParams ();
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    linearLayout.setLayoutParams (params);
                    TextView textView = (TextView)view;
                    textView.setText (file.getName ());
                    textView.setMaxWidth (GridLayoutItemDecoration.dip2px (getContext (),50));
                    textView.setEllipsize (TextUtils.TruncateAt.END);
                    textView.setSingleLine (true);
                    textView.setTextSize (16);
                    break;
                case R.id.file_info:
                case R.id.arrow_image:
                    view.setVisibility (View.GONE);
                    break;
                case R.id.image_check_btn:
                    RelativeLayout layout = (RelativeLayout)view.getParent ();
                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams ();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = GridLayoutItemDecoration.dip2px (getContext (),65);
                    layout.setLayoutParams (layoutParams);
                    break;
            }
        });
    }
    /**
     * @return void
     * @Author peiyongdong
     * @Description (更改布局)
     * @Date 10:39 2019/10/23
     * @Param []
     **/
    private void changeLayout(){
        changeEditStatus ();
        if (recyclerView.getLayoutManager () instanceof GridLayoutManager){
            layoutType = LAYOUT_LINEAR;
            cardContentAdapter.setOnSetContetnViewListener ((holder, fileBean) ->setLinearViewHolder (holder,fileBean,titleLayout));
            recyclerView.removeItemDecorationAt (0);
            recyclerView.setLayoutManager (new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false));
            viewModelImage.setImageResource (R.drawable.ic_view_model_grid);
        }else{
            layoutType = LAYOUT_GRID;
            cardContentAdapter.setOnSetContetnViewListener ((holder, fileBean) ->setGridViewHolder(holder,fileBean));
            recyclerView.addItemDecoration (new GridLayoutItemDecoration (getContext (),4,60));
            recyclerView.setLayoutManager (new GridLayoutManager (getContext (),4,GridLayoutManager.VERTICAL,false));
            viewModelImage.setImageResource (R.drawable.ic_view_model_line);
        }
        recyclerView.getAdapter ().notifyDataSetChanged ();
    }
    /**
    * @Author peiyongdong
    * @Description ( 取消编辑状态 )
    * @Date 10:09 2019/10/23
    * @Param []
    * @return void
    **/
    public void changeEditStatus(){
        if(isEidtStatus ()){
            List<View> childViews = ViewUtil.getAllChildViews (root);
            childViews.forEach (view -> {
                if(view instanceof CheckBox){
                    CheckBox checkBox = (CheckBox)view;
                    checkBox.setChecked (false);
                    checkBox.setVisibility (View.GONE);
                    ((RelativeLayout)checkBox.getParent ()).setVisibility (View.GONE);
                }else if(layoutType==LAYOUT_LINEAR&&view.getId ()==R.id.arrow_image){
                    view.setVisibility (View.VISIBLE);
                }
            });
            eidtStatus = false;
        }
    }
}
