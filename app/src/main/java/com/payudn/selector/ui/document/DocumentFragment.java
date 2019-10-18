package com.payudn.selector.ui.document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.payudn.selector.R;
import com.payudn.selector.ui.recent.RecentFragment;
import com.payudn.selector.ui.recent.RecentViewModel;

public class DocumentFragment extends Fragment {
    private DocumentViewModel mViewModel;

    public static RecentFragment newInstance() {
        return new RecentFragment ();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_document, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        mViewModel = ViewModelProviders.of (this).get (DocumentViewModel.class);
        // TODO: Use the ViewModel
    }
}
