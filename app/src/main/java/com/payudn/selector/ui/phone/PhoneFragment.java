package com.payudn.selector.ui.phone;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;
import com.payudn.selector.adapter.CardViewHolder;
import com.payudn.selector.adapter.GridLayoutItemDecoration;
import com.payudn.selector.entity.FileBean;
import com.payudn.selector.ui.ArrowLabelView;
import com.payudn.selector.util.DateUtil;
import com.payudn.selector.util.FileUtil;
import com.payudn.selector.util.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class PhoneFragment extends Fragment {

    private PhoneModel phoneModel;
    private View root;
    private ArrowLabelView filePath;
    @Getter
    private int parentId = 0;
    private RecyclerView recyclerView;
    private CardContentAdapter<FileBean> cardContentAdapter;
    private LinearLayout titleLayout;
    private ImageView viewModelImage;
    @Getter
    private boolean eidtStatus = false;
    public static PhoneFragment newInstance() {
        return new PhoneFragment ();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.phone_fragment, container, false);
        titleLayout = root.findViewById (R.id.title_layout);
        filePath = root.findViewById (R.id.root_path);
        filePath.setLabelText ("内部存储设备");
        filePath.setLabelData (0);
        filePath.setOnClickListener (v -> {
            Integer parentId = Integer.valueOf (filePath.getLabelData ());
            refreshAdapterByParentId (parentId);
        });
        viewModelImage = root.findViewById (R.id.view_model_image);
        viewModelImage.setOnClickListener (v -> {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager ();
            if (layoutManager instanceof GridLayoutManager){
                cardContentAdapter.setOnSetContetnViewListener ((holder, fileBean) ->setLinearViewHolder (holder,fileBean));
                recyclerView.setLayoutManager (new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false));
                viewModelImage.setImageResource (R.drawable.ic_view_model_grid);
            }else{
                cardContentAdapter.setOnSetContetnViewListener ((holder, fileBean) ->setGridViewHolder(holder,fileBean));
                recyclerView.setLayoutManager (new GridLayoutManager (getContext (),4,GridLayoutManager.VERTICAL,false));
                viewModelImage.setImageResource (R.drawable.ic_view_model_line);
                viewModelImage.refreshDrawableState ();
            }
            recyclerView.getAdapter ().notifyDataSetChanged ();
        });
        phoneModel = ViewModelProviders.of (this).get (PhoneModel.class);
        phoneModel.setContext (getContext ());
        List<Integer> resources = new ArrayList<> ();
        resources.add (R.id.file_type_image);
        resources.add (R.id.file_name);
        resources.add (R.id.file_info);
        resources.add (R.id.arrow_image);
        resources.add (R.id.image_check_btn);
        phoneModel.getFiles (parentId,null).observe (this,fileBeans -> {
            cardContentAdapter = new CardContentAdapter(fileBeans,R.layout.file_item,resources);
            cardContentAdapter.setOnSetContetnViewListener ((holder,fileBean) -> setLinearViewHolder (holder,fileBean));
            cardContentAdapter.setOnLongClickListener (v -> {
                eidtStatus = true;
                List<View> childViews = ViewUtil.getAllChildViews (root);
                childViews.forEach (view -> {
                    if(view instanceof CheckBox){
                        view.setVisibility (View.VISIBLE);
                    }else if(view.getId ()==R.id.arrow_image){
                        view.setVisibility (View.GONE);
                    }
                });
                return true;
            });
            recyclerView = root.findViewById (R.id.file_recycler);
            recyclerView.setLayoutManager (new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false));
            recyclerView.setAdapter (cardContentAdapter);
        });
        return root;
    }
    private void setGridViewHolder(CardViewHolder holder, FileBean fileBean){
        File file = new File (fileBean.getPath ());
        ((LinearLayout)holder.itemView.findViewById (R.id.file_item_layout)).setOrientation (LinearLayout.VERTICAL);
        holder.views.forEach (view -> {
            switch (view.getId ()){
                case R.id.file_type_image:
                    ImageView imageView = (ImageView)view;
                    setFileTypeImage (file,imageView,holder,fileBean,150,150);
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
                default:
                    break;
            }
        });
    }
    /**
    * @Author peiyongdong
    * @Description ( 设置CardViewHolder )
    * @Date 23:37 2019/10/21
    * @Param [holder, fileBean]
    * @return void
    **/
    private void setLinearViewHolder(CardViewHolder holder, FileBean fileBean){
        File file = new File (fileBean.getPath ());
        ((LinearLayout)holder.itemView.findViewById (R.id.file_item_layout)).setOrientation (LinearLayout.HORIZONTAL);
        holder.views.forEach (view -> {
            switch (view.getId ()){
                case R.id.file_type_image:
                    ImageView imageView = (ImageView)view;
                    setFileTypeImage (file,imageView,holder,fileBean,100,100);
                    break;
                case R.id.file_name:
                    LinearLayout linearLayout = (LinearLayout)view.getParent ();
                    ViewGroup.LayoutParams params = linearLayout.getLayoutParams ();
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    linearLayout.setLayoutParams (params);
                    ((TextView)view).setText (file.getName ());
                    TextView textView = (TextView)view;
                    textView.setText (file.getName ());
                    textView.setEllipsize (TextUtils.TruncateAt.END);
                    break;
                case R.id.file_info:
                    view.setVisibility (View.VISIBLE);
                    if(file.isDirectory ()){
                        int count = file.listFiles ().length;
                        if(fileBean.getParentId ()!=null){
                            ((TextView)view).setText (count+"项 | "+ DateUtil.formatDate (fileBean.getUpdateTime (),"yyyy/MM/dd"));
                        }
                    }else{
                        ((TextView)view).setText (fileBean.getSize ()+"KB | "+ DateUtil.formatDate (fileBean.getUpdateTime (),"yyyy/MM/dd"));
                    }
                    break;
                case R.id.arrow_image:
                    if(file.isDirectory ()){
                        view.setVisibility (View.VISIBLE);
                    }else {
                        view.setVisibility (View.GONE);
                    }
                    break;
                case R.id.image_check_btn:
                    CheckBox checkBox = (CheckBox) view;
                    checkBox.setOnCheckedChangeListener ((buttonView, isChecked) -> {
                        if(isChecked){
                            Toast.makeText (getContext (),"当前已选择："+file.getName (),Toast.LENGTH_SHORT).show ();
                        }
                    });
                default:
                        break;
            }
        });
    }
    private void setFileTypeImage(File file,ImageView imageView,CardViewHolder holder,FileBean fileBean,int outWidth,int outHeight){
        if(file.isDirectory ()){
            holder.itemView.setOnClickListener (v -> {
                parentId = fileBean.getId ();
                ArrowLabelView arrowLabelView = new ArrowLabelView (getContext ());
                arrowLabelView.setLabelText (file.getName ());
                arrowLabelView.setLabelData (fileBean.getId ());
                arrowLabelView.setOnClickListener (view -> {
                    Integer parentId = Integer.valueOf (arrowLabelView.getLabelData ());
                    refreshAdapterByParentId (parentId);
                });
                titleLayout.addView (arrowLabelView);
                List<FileBean> fileBeanList = FileUtil.getFileByParentId (getContext (),parentId,null);
                cardContentAdapter.getTList ().clear ();
                cardContentAdapter.getTList ().addAll (fileBeanList);
                cardContentAdapter.notifyDataSetChanged ();
            });
            imageView.setImageResource (R.drawable.ic_file_file);
        }else{
            Bitmap bitmap = FileUtil.parseToBitmap (root.getContext (),file.getPath (),outWidth,outHeight,5,1);
            imageView.setImageBitmap (bitmap);
            holder.itemView.setOnClickListener (null);
        }
    }
    public void refreshAdapterByParentId(int parentId){
        if(parentId>=0){
            for(int i=titleLayout.getChildCount ()-1;i>0;i--){
                ArrowLabelView arrowLabelView = (ArrowLabelView)titleLayout.getChildAt (i);
                if(Integer.valueOf (arrowLabelView.getLabelData ())==parentId){
                    break;
                }
                titleLayout.removeViewAt (i);
            }
            this.parentId = parentId;
            List<FileBean> fileBeanList = FileUtil.getFileByParentId (getContext (),parentId,null);
            cardContentAdapter.getTList ().clear ();
            cardContentAdapter.getTList ().addAll (fileBeanList);
            cardContentAdapter.notifyDataSetChanged ();
        }
    }
    public void backByEditStatus(){
        if(isEidtStatus ()){
            List<View> childViews = ViewUtil.getAllChildViews (root);
            childViews.forEach (view -> {
                if(view instanceof CheckBox){
                    view.setVisibility (View.GONE);
                }else if(view.getId ()==R.id.arrow_image){
                    view.setVisibility (View.VISIBLE);
                }
            });
            eidtStatus = false;
        }
    }
}
