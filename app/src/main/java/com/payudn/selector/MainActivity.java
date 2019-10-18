package com.payudn.selector;

import android.Manifest;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private PermissionHelper permissionHelper;
    private TabLayout mTabLayout;
    private String [] tabTexts = {"手机","最近","视频","图片","音乐","收藏","更多"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        mTabLayout = findViewById(R.id.tab_layout);
        for(int i=0;i<tabTexts.length;i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTexts[i]));
        }
        mTabLayout.getTabAt (1).select ();
        mTabLayout.addOnTabSelectedListener (this);
        permissionHelper = new PermissionHelper(this);
        getPermission();
        /*Button button = findViewById (R.id.select_btn);
        List<MediaBean> imageBeanList = MediaLoader.getMediaBeans (this);
        button.setOnClickListener (view -> {
            ImageCardView imageCardView = new ImageCardView (this,imageBeanList);
            imageCardView.setOnSelectItemListener (position -> {
                System.out.println ("选择了这image"+imageBeanList.get (position));
            });
            setContentView (imageCardView.create ());
        });*/
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loadFragment (tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    private void loadFragment(TabLayout.Tab tab){
        switch (tab.getPosition ()){
            case 0:

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
    }
    private void getPermission() {
        permissionHelper.check(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MEDIA_CONTENT_CONTROL
        ).onSuccess(() -> {})
                .onDenied(() -> Toast.makeText (this,"权限被拒绝！将无法获取到WiFi信息!",Toast.LENGTH_SHORT).show ())
                .onNeverAskAgain(() -> Toast.makeText (this,"权限被拒绝！将无法获取到WiFi信息,下次不会再询问了！",Toast.LENGTH_SHORT).show ()).run();
    }
}
