package com.payudn.selector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.payudn.selector.entity.MediaBean;
import com.payudn.selector.ui.ImageCardView;
import com.payudn.selector.util.MediaLoader;

import java.util.ArrayList;
import java.util.List;

import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity {
    private PermissionHelper permissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        permissionHelper = new PermissionHelper(this);
        getPermission();
        Button button = findViewById (R.id.select_btn);
        List<MediaBean> imageBeanList = MediaLoader.getMediaBeans (this);
        button.setOnClickListener (view -> {
            ImageCardView imageCardView = new ImageCardView (this,imageBeanList);
            imageCardView.setOnSelectItemListener (position -> {
                System.out.println ("选择了这image"+imageBeanList.get (position));
            });
            setContentView (imageCardView.create ());
        });
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
