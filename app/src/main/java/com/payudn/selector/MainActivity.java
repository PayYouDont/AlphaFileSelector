package com.payudn.selector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.payudn.selector.ui.CollectFragment;
import com.payudn.selector.ui.DocumentFragment;
import com.payudn.selector.ui.MoreFragment;
import com.payudn.selector.ui.MusicFragment;
import com.payudn.selector.ui.phone.PhoneFragment;
import com.payudn.selector.ui.PictureFragment;
import com.payudn.selector.ui.RecentFragment;
import com.payudn.selector.ui.VideoFragment;

import java.util.ArrayList;
import java.util.List;

import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private PermissionHelper permissionHelper;
    private TabLayout mTabLayout;
    public static SearchView searchView;
    ViewPager viewPager;
    private String [] tabTexts = {"手机","最近","文档","视频","图片","音乐","收藏","更多"};
    private List<Fragment> fragmentList = new ArrayList<> ();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        searchView = findViewById (R.id.query_view);
        searchView.setOnClickListener (v -> {
            System.out.println ("搜索操作");
        });
        mTabLayout = findViewById(R.id.tab_layout);
        for(int i=0;i<tabTexts.length;i++){
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(tabTexts[i]);
            mTabLayout.addTab(tab);
        }
        fragmentList.add (PhoneFragment.newInstance ());
        fragmentList.add (RecentFragment.newInstance ());
        fragmentList.add (DocumentFragment.newInstance ());
        fragmentList.add (VideoFragment.newInstance ());
        fragmentList.add (PictureFragment.newInstance ());
        fragmentList.add (MusicFragment.newInstance ());
        fragmentList.add (CollectFragment.newInstance ());
        fragmentList.add (MoreFragment.newInstance ());
        viewPager = findViewById (R.id.view_pager);
        viewPager.setAdapter (new FragmentPagerAdapter (getSupportFragmentManager (),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get (position);
            }

            @Override
            public int getCount() {
                return fragmentList.size ();
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return tabTexts[position];
            }
        });
        viewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager (viewPager);
        mTabLayout.getTabAt (0).select ();
        mTabLayout.addOnTabSelectedListener (this);
        permissionHelper = new PermissionHelper(this);
        getPermission();
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
        // Manifest.permission.READ_MEDIA_IMAGES,
        //                Manifest.permission.READ_MEDIA_VIDEO,
        //                Manifest.permission.READ_MEDIA_AUDIO,
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
