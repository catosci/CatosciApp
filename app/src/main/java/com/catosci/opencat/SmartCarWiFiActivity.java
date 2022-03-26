package com.catosci.opencat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.longdo.mjpegviewer.MjpegView;
import io.github.controlwear.virtual.joystick.android.JoystickView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class SmartCarWiFiActivity extends AppCompatActivity {

    enum ActionSmartCar {
        ACTION_MOVE,
        ACTION_CAR_MODE,
        ACTION_BUZZER,
        MODE_NONE,
        MODE_GRAVITY,
        MODE_ULTRASONIC,
        MODE_ULTRASONIC_NOIR,
        MODE_TRACKING,
        MODE_FOLLOW
    }
    private String SEP = "@";
    ActionSmartCar currentMode = ActionSmartCar.MODE_NONE;

    enum DirectionSmartCar {
        FORWARD,
        RIGHT,
        LEFT,
        BACKWARD,
        STOP
    }
    private DirectionSmartCar lastDirection = DirectionSmartCar.STOP;  // Used to check whether send new direction movement command
    private DirectionSmartCar newDirection = DirectionSmartCar.STOP;  // Register new direction so send

    private int SPEED_SLOW = 40;
    private int SPEED_NORMAL= 70;
    private int SPEED_FAST = 90;
    private int currentSpeed=SPEED_NORMAL;


    private ToggleButton TogBtnUltra;
    private ToggleButton  TogBtnUltra_NoIR;
    private ToggleButton  TogBtnTracking;
    private ToggleButton  TogBtnFollow;

    private Button BtnSlow;
    private Button BtnNormal;
    private Button BtnFast;

    private TextView TxtSonar;
    private TextView TxtBattery;

    private MjpegView viewer;

    // Instruction map, values must be the same as in your OpenCat.h file!
    private HashMap<ActionSmartCar, String> instructionMap = new HashMap<ActionSmartCar, String>();

    private long directionPerSecondLimit = 100;  // Send one direction command each 0.5 seconds
    private String strReady="";
    private boolean isSmartCarReady = false;


    String remoteWIFICarIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_smartcar_wifi);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        remoteWIFICarIP = globalVariable.getRemoteIP();

        // Initialize instruction map
        instructionMap.put(ActionSmartCar.ACTION_MOVE, "A");
        instructionMap.put(ActionSmartCar.ACTION_CAR_MODE, "H");
        instructionMap.put(ActionSmartCar.ACTION_BUZZER, "A");
        instructionMap.put(ActionSmartCar.MODE_NONE, "0");
        instructionMap.put(ActionSmartCar.MODE_GRAVITY, "1");
        instructionMap.put(ActionSmartCar.MODE_ULTRASONIC, "2");
        instructionMap.put(ActionSmartCar.MODE_ULTRASONIC_NOIR, "3");
        instructionMap.put(ActionSmartCar.MODE_TRACKING, "4");
        instructionMap.put(ActionSmartCar.MODE_FOLLOW, "5");

        TogBtnUltra = findViewById(R.id.TogBtnUltra);
        TogBtnUltra_NoIR = findViewById(R.id.TogBtnUltraNOIR);
        TogBtnTracking = findViewById(R.id.TogBtnTrack);
        TogBtnFollow = findViewById(R.id.TogBtnFollow);
        BtnSlow = findViewById(R.id.btSlow);
        BtnNormal = findViewById(R.id.btNormal);
        BtnFast = findViewById(R.id.btFast);
        TxtSonar= findViewById(R.id.TxtSonar);
        TxtBattery= findViewById(R.id.TxtBattery);
        viewer = findViewById(R.id.mjpegview_ble);

        SetDefaultColorSpeedBtn();
        BtnNormal.setTextColor(getResources().getColor(R.color.colorButton2));

        TogBtnUltra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    TogBtnUltra_NoIR.setEnabled(false);
                    TogBtnTracking.setEnabled(false);
                    TogBtnFollow.setEnabled(false);

                    currentMode= ActionSmartCar.MODE_ULTRASONIC;
                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_ULTRASONIC)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                } else {
                    // The toggle is disabled
                    EnableActionTogBtn();
                    currentMode= ActionSmartCar.MODE_NONE;

                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_NONE)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        TogBtnUltra_NoIR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    TogBtnUltra.setEnabled(false);
                    TogBtnTracking.setEnabled(false);
                    TogBtnFollow.setEnabled(false);

                    currentMode= ActionSmartCar.MODE_ULTRASONIC_NOIR;
                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_ULTRASONIC_NOIR)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                } else {
                    // The toggle is disabled
                    EnableActionTogBtn();
                    currentMode= ActionSmartCar.MODE_NONE;

                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_NONE)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        TogBtnTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    TogBtnUltra.setEnabled(false);
                    TogBtnUltra_NoIR.setEnabled(false);
                    TogBtnFollow.setEnabled(false);

                    currentMode= ActionSmartCar.MODE_TRACKING;
                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_TRACKING)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                } else {
                    // The toggle is disabled
                    EnableActionTogBtn();
                    currentMode= ActionSmartCar.MODE_NONE;

                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_NONE)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        TogBtnFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    TogBtnUltra.setEnabled(false);
                    TogBtnUltra_NoIR.setEnabled(false);
                    TogBtnTracking.setEnabled(false);


                    currentMode= ActionSmartCar.MODE_FOLLOW;
                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_FOLLOW)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                } else {
                    // The toggle is disabled
                    EnableActionTogBtn();
                    currentMode= ActionSmartCar.MODE_NONE;

                    String text = instructionMap.get(ActionSmartCar.ACTION_CAR_MODE)+SEP;
                    text+=instructionMap.get(ActionSmartCar.MODE_NONE)+SEP;
                    sendURLMessage(text,remoteWIFICarIP);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        BtnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentGait = Command.CRAWL;
                currentSpeed=SPEED_SLOW;
                SetDefaultColorSpeedBtn();
                BtnSlow.setTextColor(getResources().getColor(R.color.colorButton2));
            }
        });

        BtnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSpeed=SPEED_NORMAL;
                SetDefaultColorSpeedBtn();
                BtnNormal.setTextColor(getResources().getColor(R.color.colorButton2));
            }
        });

        BtnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSpeed=SPEED_FAST;
                SetDefaultColorSpeedBtn();
                BtnFast.setTextColor(getResources().getColor(R.color.colorButton2));
            }
        });


        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView_Ble);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                //double angle = getAngle(xPercent, yPercent);
                newDirection = getNewDirection(angle);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendDirection();
                handler.postDelayed(this, directionPerSecondLimit);
            }
        },directionPerSecondLimit );

    }


    public void showCam() {
        // Set jpeg video url to viewer
        String mjpegSource = "http://" + remoteWIFICarIP + ":81/stream";
        Log.i("Execute command", mjpegSource);
        //viewer.setMode(MjpegView.MODE_FIT_HEIGHT);
        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
        viewer.setAdjustHeight(true);
        viewer.setSupportPinchZoomAndPan(false);
        viewer.setUrl(mjpegSource);
        viewer.startStream();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.smartcar_wifi, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        switch (item.getItemId()){
            case R.id.Conn:
                // Stop viewer when rotate display
//                viewer.stopStream();
                showCam();
                break;
            case R.id.Stop:
                // Stop viewer when rotate display
                viewer.stopStream();
                break;
            case R.id.Info:
                Intent intentInstructionsActivity = new Intent(SmartCarWiFiActivity.this, InstructionsActivity.class);
                startActivity(intentInstructionsActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void SetDefaultColorSpeedBtn(){
        BtnSlow.setTextColor(ContextCompat.getColor(this, R.color.white));
        BtnNormal.setTextColor(ContextCompat.getColor(this, R.color.white));
        BtnFast.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    public void EnableActionTogBtn(){
        TogBtnUltra.setEnabled(true);
        TogBtnUltra_NoIR.setEnabled(true);
        TogBtnTracking.setEnabled(true);
        TogBtnFollow.setEnabled(true);
    }


    private DirectionSmartCar getNewDirection(double angle) {
        DirectionSmartCar res = DirectionSmartCar.STOP;  // Neutral position
        if (angle >= 315 || angle <= 45) {
            if (angle < 0.01) {
                res = DirectionSmartCar.STOP;
            } else {
                res = DirectionSmartCar.RIGHT;
            }
        } else if (angle > 45 && angle <= 135) {
            res = DirectionSmartCar.FORWARD;
        } else if (angle > 135 && angle <= 225) {
            res = DirectionSmartCar.LEFT;
        } else if (angle > 225 && angle <= 315) {
            res = DirectionSmartCar.BACKWARD;
        }
        return res;
    }


    private void sendDirection() {

        //update rec value
/*        //update rec value
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String ReadMessage = globalVariable.getSensorMessage();
        String tmpRead;
        String tmpIRdx,tmpIRsx,tmpSonar;
        if ((ReadMessage.length() > 2) && (ReadMessage.charAt(0) == 'E')) {
            //E#50#1#0
            tmpRead = ReadMessage.substring(2);
            tmpSonar = tmpRead.substring(0,tmpRead.indexOf("#"));
            TxtSonar.setText(tmpSonar);
            if (Float.valueOf(tmpSonar) < 40 ){
                ImgSonar.setVisibility(View.VISIBLE);
            }else {
                ImgSonar.setVisibility(View.INVISIBLE);
            }

            tmpRead = tmpRead.substring(tmpRead.indexOf("#")+1);
            tmpIRdx = tmpRead.substring(0,tmpRead.indexOf("#"));
            if (Integer.valueOf(tmpIRdx)<1) {
                ImgIRdx.setVisibility(View.VISIBLE);
            }else{
                ImgIRdx.setVisibility(View.INVISIBLE);
            }

            tmpRead = tmpRead.substring(tmpRead.indexOf("#")+1);
            tmpIRsx = tmpRead.substring(0,tmpRead.indexOf("#"));
            if (Integer.valueOf(tmpIRsx)<1) {
                ImgIRsx.setVisibility(View.VISIBLE);
            }else{
                ImgIRsx.setVisibility(View.INVISIBLE);
            }
        }

        if ((ReadMessage.length() > 2) && (ReadMessage.charAt(0) == 'I')) {
            //I#5290#
            tmpRead = ReadMessage.substring(2);
            tmpRead= tmpRead.substring(0,tmpRead.indexOf("#"));
            float Battery =  Float.valueOf(tmpRead);
            Battery = Battery/1000;
            TxtBattery.setText(String.valueOf(Battery).substring(0,3)+"V");
        }*/

        String text;
        switch (currentMode) {
            case MODE_NONE:
                if (lastDirection != newDirection) {

                    text = instructionMap.get(ActionSmartCar.ACTION_MOVE);
                    switch (newDirection) {
                        case FORWARD:
                            text += SEP+Integer.toString(currentSpeed)+SEP+Integer.toString(currentSpeed)+SEP;
                            break;
                        case LEFT:
                            text += SEP+"0"+SEP+Integer.toString(currentSpeed)+SEP;
                            break;
                        case RIGHT:
                            text += SEP+Integer.toString(currentSpeed)+SEP+"0"+SEP;
                            break;
                        case BACKWARD:
                            text += SEP+Integer.toString(-currentSpeed)+SEP+Integer.toString(-currentSpeed)+SEP;
                            break;
                        case STOP:
                            text += SEP+"0"+SEP+"0"+SEP;
                            break;
                    }

                    sendURLMessage(text,remoteWIFICarIP);
                    lastDirection = newDirection;
                    Log.d("DEBUG", "Message sent: " + text);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (ConnectBleActivity.createConnectThread != null){
            ConnectBleActivity.createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void sendURLMessage2(String message, String remoteWIFICarIP) {
        String url = "http://" + remoteWIFICarIP + "/control?var=message&val=" + message;

        RequestQueue queue = Volley.newRequestQueue(this);

        Toast.makeText(getApplicationContext(),url,Toast.LENGTH_LONG).show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // enjoy your response
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // enjoy your error status
            }
        });

        queue.add(stringRequest);
    }


    public void sendURLMessage(String message, String remoteWIFICarIP) {
        String message_url = "http://" + remoteWIFICarIP + "/control?var=message&val=" + message;
        Toast.makeText(getApplicationContext(),message_url,Toast.LENGTH_LONG).show();
        try
        {

            URL url = new URL(message_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
