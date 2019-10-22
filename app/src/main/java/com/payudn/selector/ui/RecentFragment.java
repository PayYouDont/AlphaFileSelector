package com.payudn.selector.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.payudn.selector.R;
import com.payudn.selector.ui.picture.DataViewModel;
import com.payudn.selector.util.FileUtil;

public class RecentFragment extends Fragment {

    private DataViewModel mViewModel;
    private View root;
    public static RecentFragment newInstance() {
        return new RecentFragment ();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.recent_fragment, container, false);
        mViewModel = ViewModelProviders.of (this).get (DataViewModel.class);
        mViewModel.setContext (getContext ());
        FileUtil.getRootFile (getContext (),cursor -> {
            //System.out.println (cursor.getString (cursor.getColumnIndex (MediaStore.Files.FileColumns.DATA)));
        });
        return root;
    }

}
