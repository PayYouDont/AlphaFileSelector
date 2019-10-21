package com.payudn.selector.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payudn.selector.entity.FileBean;
import com.payudn.selector.util.DateUtil;
import com.payudn.selector.util.MediaLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class DataViewModel extends ViewModel {
    private MutableLiveData<Map<Date,Integer>> vieWData;
    @Getter
    public List<FileBean> imageBeanList = new ArrayList<> ();
    @Setter
    private Context context;
    @Setter
    Handler handler;
    private Date startDate;
    private List<Date> dateList;
    public DataViewModel() {
        vieWData = new MutableLiveData<> ();
        try {
            startDate = new SimpleDateFormat ("yyyy-MM-dd").parse ("2015-01-01");
            dateList = DateUtil.getDays (startDate);
        }catch (Exception e){
            Log.e (getClass ().getName (), "DataViewModel: 解析日期错误", e);
        }
    }
    public LiveData< Map<Date,Integer>> getMediaBeanData() {
        if(context!=null){
            MediaLoader loader = new MediaLoader (context);
            Map<Date,Integer> map = loader.getPhotoCountOrderByDay (dateList);
            loader.setOnLoadDataListener (mediaBean -> refreshData(mediaBean));
            //loader.getAllPhotoInfo ();
            vieWData.setValue (map);
        }
        return vieWData;
    }
    private void refreshData(FileBean mediaBean){
        imageBeanList.add (mediaBean);
        Message message = new Message ();
        message.what = 1;
        if(handler!=null){
            handler.handleMessage (message);
        }
    }
}
