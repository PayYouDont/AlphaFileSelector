package com.payudn.selector.ui.phone;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;
import com.payudn.selector.adapter.CardViewHolder;
import com.payudn.selector.adapter.GridLayoutItemDecoration;
import com.payudn.selector.entity.FileBean;
import com.payudn.selector.util.DateUtil;
import com.payudn.selector.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhoneFragment extends Fragment {

    private PhoneModel phoneModel;
    private View root;
    private TextView filePath;
    private int parentId = 0;
    private RecyclerView recyclerView;
    private CardContentAdapter<FileBean> cardContentAdapter;
    private LinearLayout titleLayout;
    public static PhoneFragment newInstance() {
        return new PhoneFragment ();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.phone_fragment, container, false);
        filePath = root.findViewById (R.id.file_path);
        titleLayout = root.findViewById (R.id.title_layout);
        phoneModel = ViewModelProviders.of (this).get (PhoneModel.class);
        phoneModel.setContext (getContext ());
        List<Integer> resources = new ArrayList<> ();
        resources.add (R.id.file_type_image);
        resources.add (R.id.file_name);
        resources.add (R.id.file_info);
        resources.add (R.id.arrow_image);
        phoneModel.getFiles (parentId,null).observe (this,fileBeans -> {
            cardContentAdapter = new CardContentAdapter(fileBeans,R.layout.file_item,resources);
            cardContentAdapter.setOnSetContetnViewListener ((holder,fileBean) -> setViewHolder (holder,fileBean));
            recyclerView = root.findViewById (R.id.file_recycler);
            recyclerView.setLayoutManager (new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false));
            recyclerView.setAdapter (cardContentAdapter);
        });
        return root;
    }
    /**
    * @Author peiyongdong
    * @Description ( 设置CardViewHolder )
    * @Date 23:37 2019/10/21
    * @Param [holder, fileBean]
    * @return void
    **/
    private void setViewHolder(CardViewHolder holder, FileBean fileBean){
        File file = new File (fileBean.getPath ());
        holder.views.forEach (view -> {
            if(view.getId () == R.id.file_type_image){
                ImageView imageView = (ImageView)view;
                if(file.isDirectory ()){
                    holder.itemView.setOnClickListener (v -> {
                        parentId = fileBean.getId ();
                        TextView textView = new TextView (getContext ());
                        textView.setText (file.getName ());
                        titleLayout.addView (textView);
                        ImageView image = new ImageView (getContext ());
                        image.setImageResource (R.drawable.ic_arrow);
                        int width = GridLayoutItemDecoration.dip2px (getContext (),20);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);//两个400分别为添加图片的大小
                        image.setLayoutParams(params);
                        titleLayout.addView (image);
                        List<FileBean> fileBeanList = FileUtil.getFileByParentId (getContext (),parentId,null);
                        cardContentAdapter.getTList ().clear ();
                        cardContentAdapter.getTList ().addAll (fileBeanList);
                        cardContentAdapter.notifyDataSetChanged ();
                    });
                    imageView.setImageResource (R.drawable.ic_file_file);
                }else{
                    Bitmap bitmap = FileUtil.parseToBitmap (root.getContext (),file.getPath (),100,100,5,1);
                    imageView.setImageBitmap (bitmap);
                    holder.itemView.setOnClickListener (null);
                }
            }else if(view.getId () == R.id.file_name){
                ((TextView)view).setText (file.getName ());
            }else if(view.getId ()==R.id.file_info){
                if(file.isDirectory ()){
                    int count = file.listFiles ().length;
                    if(fileBean.getParentId ()!=null){
                        ((TextView)view).setText (count+"项 | "+ DateUtil.formatDate (fileBean.getCreateTime (),"yyyy/MM/dd"));
                    }
                }else{
                    ((TextView)view).setText (fileBean.getSize ()+"KB | "+ DateUtil.formatDate (fileBean.getCreateTime (),"yyyy/MM/dd"));
                }
            }else if(view.getId ()==R.id.arrow_image){
                if(file.isDirectory ()){
                    view.setVisibility (View.VISIBLE);
                }else {
                    view.setVisibility (View.GONE);
                }
            }
        });
    }
    public void refreshAdapterByParentId(){
        if(parentId>0){
            titleLayout.removeViewAt (titleLayout.getChildCount ()-1);
            titleLayout.removeViewAt (titleLayout.getChildCount ()-1);
            parentId = FileUtil.findById (getContext (),parentId).getParentId ();
            List<FileBean> fileBeanList = FileUtil.getFileByParentId (getContext (),parentId,null);
            cardContentAdapter.getTList ().clear ();
            cardContentAdapter.getTList ().addAll (fileBeanList);
            cardContentAdapter.notifyDataSetChanged ();
        }
    }
}
