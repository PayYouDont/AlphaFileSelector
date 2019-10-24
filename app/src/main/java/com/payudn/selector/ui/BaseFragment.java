package com.payudn.selector.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;
import com.payudn.selector.adapter.CardViewHolder;
import com.payudn.selector.adapter.GridLayoutItemDecoration;
import com.payudn.selector.annotation.RootView;
import com.payudn.selector.annotation.ViewById;
import com.payudn.selector.entity.FileBean;
import com.payudn.selector.entity.MediaScanner;
import com.payudn.selector.util.DateUtil;
import com.payudn.selector.util.FileUtil;
import com.payudn.selector.util.ReflectUtil;
import com.payudn.selector.util.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseFragment extends Fragment {
    private View root;
    protected RecyclerView recyclerView;
    protected CardContentAdapter<FileBean> cardContentAdapter;
    protected boolean eidtStatus = false;
    protected List<Integer> holderViewIds;
    protected int parentId;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        this.savedInstanceState = savedInstanceState;
        holderViewIds = new ArrayList<> ();
        holderViewIds.add (R.id.file_type_image);
        holderViewIds.add (R.id.file_name);
        holderViewIds.add (R.id.file_info);
        holderViewIds.add (R.id.arrow_image);
        holderViewIds.add (R.id.image_check_btn);
        initRootView ();
        initViewByAnnotation();
        onCreateView();
        return root;
    }
    /**
    * @Author peiyongdong
    * @Description ( 抽象方法，初始化各个View )
    * @Date 17:56 2019/10/24
    * @Param []
    * @return void
    **/
    abstract protected void onCreateView();
    /**
    * @Author peiyongdong
    * @Description ( 初始化RecyclerView，应该在onCreateView()方法中调用)
    * @Date 17:56 2019/10/24
    * @Param [recyclerId, fileBeans, titleLayout]
    * @return void
    **/
    protected void initRecyclerView(int recyclerId,List<FileBean> fileBeans,LinearLayout titleLayout){
        recyclerView = root.findViewById (recyclerId);
        recyclerView.setLayoutManager (new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false));
        cardContentAdapter = new CardContentAdapter(fileBeans,R.layout.file_item,holderViewIds);
        cardContentAdapter.setOnSetContetnViewListener ((holder,fileBean) -> setLinearViewHolder (holder,fileBean,titleLayout));
        cardContentAdapter.setOnLongClickListener (v -> {
            eidtStatus = true;
            List<View> childViews = ViewUtil.getAllChildViews (root);
            childViews.forEach (view -> {
                if(view instanceof CheckBox){
                    view.setVisibility (View.VISIBLE);
                    RelativeLayout relativeLayout = (RelativeLayout)view.getParent ();
                    relativeLayout.setVisibility (View.VISIBLE);
                    relativeLayout.setOnClickListener (layout-> view.performClick ());
                }else if(view.getId ()==R.id.arrow_image){
                    view.setVisibility (View.GONE);
                }
            });
            return true;
        });
        recyclerView.setAdapter (cardContentAdapter);
    }
    /**
    * @Author peiyongdong
    * @Description ( 设置线性布局（默认） )
    * @Date 17:57 2019/10/24
    * @Param [holder, fileBean, titleLayout]
    * @return void
    **/
    protected void setLinearViewHolder(CardViewHolder holder, FileBean fileBean,LinearLayout titleLayout){
        ((LinearLayout)holder.itemView.findViewById (R.id.file_item_layout)).setOrientation (LinearLayout.HORIZONTAL);
        File file = new File (fileBean.getPath ());
        MediaScanner mediaScanner = new MediaScanner (getContext ());
        mediaScanner.scanFile (file,"image/jpeg");
        holder.views.forEach (view -> {
            switch (view.getId ()){
                case R.id.file_type_image:
                    ImageView imageView = (ImageView)view;
                    setFileTypeImage (file,imageView,holder,fileBean,100,100,titleLayout);
                    break;
                case R.id.file_name:
                    LinearLayout linearLayout = (LinearLayout)view.getParent ();
                    ViewGroup.LayoutParams params = linearLayout.getLayoutParams ();
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
                    int status = file.isDirectory ()?View.VISIBLE:View.GONE;
                    view.setVisibility (status);
                    break;
                case R.id.image_check_btn:
                    RelativeLayout layout = (RelativeLayout)view.getParent ();
                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams ();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = GridLayoutItemDecoration.dip2px (getContext (),40);
                    layout.setLayoutParams (layoutParams);
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
    /**
     * @Author peiyongdong
     * @Description ( 设置文件图片 )
     * @Date 10:10 2019/10/23
     * @Param [file, imageView, holder, fileBean, outWidth, outHeight]
     * @return void
     **/
    protected void setFileTypeImage(File file,ImageView imageView,CardViewHolder holder,FileBean fileBean,int outWidth,int outHeight,LinearLayout titleLayout){
        if(file.isDirectory ()){
            holder.itemView.setOnClickListener (v -> {
                if(titleLayout!=null){
                    ArrowLabelView arrowLabelView = new ArrowLabelView (getContext ());
                    arrowLabelView.setLabelText (file.getName ());
                    arrowLabelView.setLabelData (fileBean.getId ());
                    arrowLabelView.setOnClickListener (view -> {
                        Integer parentId = Integer.valueOf (arrowLabelView.getLabelData ());
                        refreshAdapterByParentId (parentId,titleLayout);
                    });
                    titleLayout.addView (arrowLabelView);
                    parentId = fileBean.getId ();
                    List<FileBean> fileBeanList = FileUtil.getFileByParentId (getContext (),parentId,null);
                    cardContentAdapter.getTList ().clear ();
                    cardContentAdapter.getTList ().addAll (fileBeanList);
                    cardContentAdapter.notifyDataSetChanged ();
                }
            });
            imageView.setImageResource (R.drawable.ic_file_file);
        }else{
            Bitmap bitmap = FileUtil.parseToBitmap (root.getContext (),file.getPath (),outWidth,outHeight,5,1);
            imageView.setImageBitmap (bitmap);
            holder.itemView.setOnClickListener (null);
        }
    }
    /**
     * @Author peiyongdong
     * @Description ( 根据parentId刷新数据 )
     * @Date 11:11 2019/10/23
     * @Param [parentId]
     * @return void
     **/
    public void refreshAdapterByParentId(int parentId,LinearLayout titleLayout){
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
    /**
    * @Author peiyongdong
    * @Description ( 初始化root )
    * @Date 17:58 2019/10/24
    * @Param []
    * @return void
    **/
    private void initRootView(){
        ReflectUtil.initFieldByAnnotation (getClass (), RootView.class,(annotation, field) -> {
            try {
                RootView rootView = (RootView)annotation;
                if(rootView.value ()==-1){
                    field.set (this,inflater.inflate (getId (getActivity (),field.getName ()), container, false));
                }else{
                    field.set (this,inflater.inflate (rootView.value (), container, false));
                }
                root = (View) field.get (this);
            }catch (IllegalAccessException e) {
                Log.e (getClass ().getName (), e.getMessage (),e);
            }
        });
    }
    /**
    * @Author peiyongdong
    * @Description ( 初始化view )
    * @Date 17:58 2019/10/24
    * @Param []
    * @return void
    **/
    private void initViewByAnnotation() {
        ReflectUtil.initFieldByAnnotation (getClass (),ViewById.class,(annotation, field) -> {
            ViewById viewById = (ViewById)annotation;
            try {
                if (viewById.value () == -1) {
                    field.set (this, root.findViewById (getId (getActivity (), field.getName ())));
                } else {
                    field.set (this, root.findViewById (viewById.value ()));
                }
            } catch (IllegalAccessException e) {
                Log.e (getClass ().getName (), e.getMessage (),e);
            }
        });
    }

    private int getId(Context context, String resName) {
        return context.getResources ().getIdentifier (resName, "id", context.getPackageName ());
    }

}
