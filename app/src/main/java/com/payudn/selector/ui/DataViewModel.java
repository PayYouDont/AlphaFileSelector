package com.payudn.selector.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payudn.selector.entity.MediaBean;
import com.payudn.selector.util.MediaLoader;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class DataViewModel extends ViewModel {
    private MutableLiveData<List<MediaBean>> vieWData;
    @Getter
    public List<MediaBean> imageBeanList = new ArrayList<> ();
    @Setter
    private Context context;
    @Setter
    Handler handler;
    public DataViewModel() {
        vieWData = new MutableLiveData<> ();
    }
    public LiveData<List<MediaBean>> getMediaBeanData() {
        if(context!=null){
            MediaLoader loader = new MediaLoader (context);
            loader.setOnLoadDataListener (mediaBean -> refreshData(mediaBean));
            loader.getAllPhotoInfo ();
            vieWData.setValue (imageBeanList);
        }
        return vieWData;
    }
    private void refreshData(MediaBean mediaBean){
        imageBeanList.add (mediaBean);
        Message message = new Message ();
        message.what = 1;
        if(handler!=null){
            handler.handleMessage (message);
        }
    }
}
