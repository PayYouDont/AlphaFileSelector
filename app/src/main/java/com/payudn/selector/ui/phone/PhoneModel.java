package com.payudn.selector.ui.phone;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payudn.selector.util.FileUtil;

import java.io.File;
import java.util.List;

import lombok.Setter;

public class PhoneModel extends ViewModel {
    @Setter
    private Context context;
    private MutableLiveData<List<File>> fileData;
    public PhoneModel() {
        fileData = new MutableLiveData<> ();
    }
    public MutableLiveData<List<File>> getFiles(int parent, FileUtil.OnFileListener onFileListener) {
        if(context!=null){
            List<File> fileList = FileUtil.getFileByParentId (context,parent,onFileListener);
            fileData.setValue (fileList);
        }
        return fileData;
    }
}
