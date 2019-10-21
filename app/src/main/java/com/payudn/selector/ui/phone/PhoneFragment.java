package com.payudn.selector.ui.phone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payudn.selector.R;
import com.payudn.selector.adapter.CardContentAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhoneFragment extends Fragment {

    private PhoneModel phoneModel;
    private View root;
    public static PhoneFragment newInstance() {
        return new PhoneFragment ();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.phone_fragment, container, false);
        phoneModel = ViewModelProviders.of (this).get (PhoneModel.class);
        phoneModel.setContext (getContext ());
        List<Integer> resources = new ArrayList<> ();
        resources.add (R.id.file_type_image);
        resources.add (R.id.file_name);
        resources.add (R.id.file_info);
        phoneModel.getFiles (0,null).observe (this,files -> {
            CardContentAdapter<File> cardContentAdapter = new CardContentAdapter(files,R.layout.file_item,resources);
            cardContentAdapter.setOnSetContetnViewListener ((holder, file) -> {
                holder.views.forEach (view -> {
                    if(view.getId () == R.id.file_type_image){
                        if(file.isDirectory ()){

                        }
                    }else if(view.getId () == R.id.file_name){
                        ((TextView)view).setText (file.getName ());
                    }else if(view.getId ()==R.id.file_info){
                        ((TextView)view).setText (file.getName ());
                    }
                });
            });
            RecyclerView recyclerView = root.findViewById (R.id.file_recycler);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager (getContext (),LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager (linearLayoutManager);
            recyclerView.setAdapter (cardContentAdapter);
        });
        return root;
    }
}
