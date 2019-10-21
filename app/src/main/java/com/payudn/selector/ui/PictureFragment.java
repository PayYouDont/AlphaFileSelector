package com.payudn.selector.ui;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;
import com.payudn.selector.entity.MediaBean;

import java.util.ArrayList;
import java.util.List;

public class PictureFragment extends Fragment {
    private View root;
    private DataViewModel mViewModel;
    private CardContentAdapter<ImageCardView> cardContentAdapter;
    public static PictureFragment newInstance() {
        return new PictureFragment ();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.picture_fragment, container, false);
        mViewModel = ViewModelProviders.of (this).get (DataViewModel.class);
        mViewModel.setContext (getContext ());
        Handler handler = new Handler (msg -> {
            if(msg.what==0){
                MediaBean mediaBean =  mViewModel.getImageBeanList ().get (mViewModel.getImageBeanList ().size ()-1);
                if(cardContentAdapter!=null){
                    List<ImageCardView> imageCardViews = cardContentAdapter.getTList ();
                    CardContentAdapter<MediaBean> adapter = imageCardViews.get (imageCardViews.size ()-1).getCardContentAdapter ();
                    adapter.getTList ().add (mediaBean);
                    adapter.notifyDataSetChanged ();
                }
            }
            return true;
        });
        mViewModel.setHandler (handler);
        initData();
        return root;
    }
    private void initData(){
        mViewModel.getMediaBeanData ().observe (this,viewDataMap -> {
            List<ImageCardView> imageCardViews = new ArrayList<> ();
            viewDataMap.forEach ((date, integer) -> {
                List<MediaBean> mediaBeans = new ArrayList<> ();
                for(int i=0;i<integer;i++){
                    MediaBean mediaBean = new MediaBean (MediaBean.Type.Image,"",0,"test");
                    mediaBeans.add (mediaBean);
                }
                ImageCardView imageCard = new ImageCardView (getContext (),mediaBeans);
                imageCardViews.add (imageCard);
            });
            cardContentAdapter = new CardContentAdapter (imageCardViews,R.layout.layout_card,CardView.getCardResources ());
            cardContentAdapter.setOnSetContetnViewListener ((holder, imageCardView) -> holder.views.forEach (view -> {
                if(view.getId ()==R.id.card_resources_recyclerView){
                    RecyclerView recyclerView = (RecyclerView)view;
                    imageCardView.setRoot (holder.consumeTypeView);
                    imageCardView.setRecyclerView (recyclerView);
                    imageCardView.create ();
                }
            }));
            RecyclerView recyclerView = root.findViewById (R.id.picture_recycler);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager (linearLayoutManager);
            recyclerView.setAdapter (cardContentAdapter);
        });
    }
}
