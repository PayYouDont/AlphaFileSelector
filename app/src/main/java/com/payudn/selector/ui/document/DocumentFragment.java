package com.payudn.selector.ui.document;

import android.view.View;

import androidx.lifecycle.ViewModelProviders;

import com.payudn.selector.R;
import com.payudn.selector.annotation.RootView;
import com.payudn.selector.ui.BaseFragment;

public class DocumentFragment extends BaseFragment {
    private DocumentModel mViewModel;
    @RootView(R.layout.fragment_document)
    private View root;
    public static DocumentFragment newInstance() {
        return new DocumentFragment ();
    }
    @Override
    protected void onCreateView() {
        mViewModel = ViewModelProviders.of (this).get (DocumentModel.class);
        mViewModel.setContext (getContext ());
        mViewModel.getFiles ().observe (this,fileBeans -> initRecyclerView (R.id.file_recycler,fileBeans,null));
    }
}
