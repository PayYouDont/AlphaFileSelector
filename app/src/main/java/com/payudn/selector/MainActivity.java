package com.payudn.selector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.payudn.selector.ui.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        Button button = findViewById (R.id.select_btn);
        button.setOnClickListener (view -> {
           //setContentView ( new CardView (this).create ());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
    }
}
