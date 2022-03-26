package com.catosci.opencat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

/**
 * Author EnriqueMoran on 11/03/2021.
 * https://github.com/EnriqueMoran
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(SplashActivity.this, TitleActivity.class);
                //Intent intent = new Intent(SplashActivity.this, ConnectBleActivity.class);
                Intent intent = new Intent(SplashActivity.this, SelectRobotActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }
}