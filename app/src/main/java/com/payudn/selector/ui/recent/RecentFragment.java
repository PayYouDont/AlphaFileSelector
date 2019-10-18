package com.payudn.selector.ui.recent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.payudn.selector.R;

public class RecentFragment extends Fragment {

    private RecentViewModel mViewModel;

    public static RecentFragment newInstance() {
        return new RecentFragment ();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate (R.layout.recent_fragment, container, false);
    }

}
