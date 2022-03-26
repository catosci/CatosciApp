package com.catosci.opencat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class OpenCatSetupActivity extends AppCompatActivity {
    private String strReady="";
    private boolean isBittleReady = false;

    private Integer SelectedServo = 0;
    private int[] servoarray= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    private boolean isStartCalibration = false;
    private Button btStartCalibration;
    private Button bt0;
    private Button bt1;
    private Button bt2;
    private Button bt8;
    private Button bt9;
    private Button bt10;
    private Button bt11;
    private Button bt12;
    private Button bt13;
    private Button bt14;
    private Button bt15;
    private Button btPlus;
    private Button btMinus;
    private TextView TextViewAngolo;

    private TableLayout TabLayServo;
    private EditText editTextSelectServo;
    private LinearLayout LinLayAngolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencat_setup);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        // methods to display the icon in the ActionBar
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.white)));


        strReady = getIntent().getStringExtra("BittleReady");
        if (strReady.equals("ready")){isBittleReady=true;}

        btStartCalibration= findViewById(R.id.btStartCalibration);
        bt0= findViewById(R.id.bt0);
        bt1= findViewById(R.id.bt1);
        bt2= findViewById(R.id.bt2);
        bt8= findViewById(R.id.bt8);
        bt9= findViewById(R.id.bt9);
        bt10= findViewById(R.id.bt10);
        bt11= findViewById(R.id.bt11);
        bt12= findViewById(R.id.bt12);
        bt13= findViewById(R.id.bt13);
        bt14= findViewById(R.id.bt14);
        bt15= findViewById(R.id.bt15);
        btPlus= findViewById(R.id.btPlus);
        btMinus= findViewById(R.id.btMinus);
        TextViewAngolo = findViewById(R.id.textViewAngolo);
        TabLayServo = findViewById(R.id.TabLayServo);
        editTextSelectServo= findViewById(R.id.editTextSelectServo);
        LinLayAngolo = findViewById(R.id.LinLayAngolo);

        TabLayServo.setVisibility(View.INVISIBLE);
        editTextSelectServo.setVisibility(View.INVISIBLE);
        LinLayAngolo.setVisibility(View.INVISIBLE);

        btStartCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = "c";
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);

                    isStartCalibration=true;

                    SelectedServo = 0;
                    SetDefaultColorServo();
                    bt0.setTextColor(getResources().getColor(R.color.colorButton4));
                    TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));

                    TabLayServo.setVisibility(View.VISIBLE);
                    editTextSelectServo.setVisibility(View.VISIBLE);
                    LinLayAngolo.setVisibility(View.VISIBLE);
                }
            }
        });


        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                servoarray[SelectedServo] += 1;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
                if (isBittleReady) {
                    String text = "c" + String.valueOf(SelectedServo) + " " + String.valueOf(servoarray[SelectedServo]);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        btMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                servoarray[SelectedServo] -= 1;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
                if (isBittleReady) {
                    String text = "c" + String.valueOf(SelectedServo) + " " + String.valueOf(servoarray[SelectedServo]);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt0.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 0;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));

            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt1.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 1;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));

            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt2.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 2;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt8.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 8;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt9.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 9;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt10.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 10;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt11.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 11;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt12.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 12;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt13.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 13;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt14.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 14;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });
        bt15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDefaultColorServo();
                bt15.setTextColor(getResources().getColor(R.color.colorButton4));
                SelectedServo = 15;
                TextViewAngolo.setText(String.valueOf(servoarray[SelectedServo]));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.opencat_setup, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        switch (item.getItemId()){
            case R.id.Save:
                if (isBittleReady) {
                    String text = "s";
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                    Toast.makeText(this, "Calibration Saved", Toast.LENGTH_SHORT).show();

                    isStartCalibration=false;

                    TabLayServo.setVisibility(View.INVISIBLE);
                    editTextSelectServo.setVisibility(View.INVISIBLE);
                    LinLayAngolo.setVisibility(View.INVISIBLE);
                }
                break;
            case android.R.id.home:
                if (isStartCalibration) {
                    //Put up the Yes/No message box
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder
                            .setTitle("Calibration")
                            .setMessage("Do you want to abort Calibration?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Yes button clicked, do something
                                    if (isBittleReady) {
                                        String text = "a";
                                        ConnectBleActivity.connectedThread.write(text);
                                        Log.d("DEBUG", "Message sent: " + text);
                                    }
                                    OpenCatSetupActivity.this.finish();
                                }

                            })

                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //No button clicked, do something
                                }
                            })
                            .show();
                }else {
                    this.finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    public void SetDefaultColorServo(){
        bt0.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt1.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt2.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt8.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt9.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt10.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt11.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt12.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt13.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt14.setTextColor(ContextCompat.getColor(this, R.color.white));
        bt15.setTextColor(ContextCompat.getColor(this, R.color.white));
    }
}