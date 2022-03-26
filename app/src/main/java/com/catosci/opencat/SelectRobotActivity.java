package com.catosci.opencat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SelectRobotActivity extends AppCompatActivity {
    private ImageButton imgBtnOpenCatBle;
    private ImageButton imgBtnSmartCarBle;
    private ImageButton imgBtnSmartCarWifi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_select_robot);
        // calling this activity's function to
        // use ActionBar utility methods
        ActionBar actionBar = getSupportActionBar();
        // providing title for the ActionBar
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));
        // providing subtitle for the ActionBar
        //actionBar.setSubtitle("   Design a custom Action Bar");
        // adding icon in the ActionBar
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);


        imgBtnOpenCatBle= findViewById(R.id.imgBtnOpenCatBle);
        imgBtnSmartCarBle= findViewById(R.id.imgBtnSmartCarBle);
        imgBtnSmartCarWifi= findViewById(R.id.imgBtnSmartCarWifi);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        imgBtnOpenCatBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Play with OpenCatBle",Toast.LENGTH_LONG).show();// display the toast on home button click
                globalVariable.setRobotType(RobotTypes.OPENCAT_BLE);
                Intent ConnectBleActivity = new Intent(SelectRobotActivity.this, ConnectBleActivity.class);
                startActivity(ConnectBleActivity);
            }
        });

        imgBtnSmartCarBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Play with SmartCarBle",Toast.LENGTH_LONG).show();// display the toast on home button click
                globalVariable.setRobotType(RobotTypes.SMARTCAR_BLE);
                Intent ConnectBleActivity = new Intent(SelectRobotActivity.this, ConnectBleActivity.class);
                startActivity(ConnectBleActivity);
            }
        });

        imgBtnSmartCarWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Play with SmartCarWifi",Toast.LENGTH_LONG).show();// display the toast on home button click
                globalVariable.setRobotType(RobotTypes.SMARTCAR_WIFI);
                Intent ConnectWiFiActivity = new Intent(SelectRobotActivity.this, SmartCarWiFiActivity.class);
                startActivity(ConnectWiFiActivity);
            }
        });

    }
}


