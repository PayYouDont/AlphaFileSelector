package com.payudn.selector.ui.document;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.payudn.selector.entity.FileBean;
import com.payudn.selector.util.FileUtil;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class DocumentModel extends ViewModel {
    @Setter
    private Context context;
    @Getter
    private MutableLiveData<List<FileBean>> fileData;
    public DocumentModel() {
        fileData = new MutableLiveData<> ();
    }
    public MutableLiveData<List<FileBean>> getFiles() {
        if(context!=null){
            List<FileBean> fileList = FileUtil.getFilesByType (context,FileUtil.TYPE_DOC);
            fileData.setValue (fileList);
        }
        return fileData;
    }
}
