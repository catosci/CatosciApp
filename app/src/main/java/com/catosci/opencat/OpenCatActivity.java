package com.catosci.opencat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
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
import android.widget.Toast;

import java.util.HashMap;

/**
 * Author EnriqueMoran on 11/03/2021.
 * https://github.com/EnriqueMoran
 */

public class OpenCatActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    enum Command {
        REST,
        FORWARD,
        GYRO,
        LED,
        LEFT,
        BALANCE,
        RIGHT,
        SHUTDOWN,
        BACKWARD,
        CALIBRATION,
        STEP,
        CRAWL,
        WALK,
        TROT,
        LOOKUP,
        BUTTUP,
        RUN,
        BOUND,
        GREETING,
        PUSHUP,
        PEE,
        STRETCH,
        SIT,
        ZERO
    }

    private Button restButton;
    private Button gyroButton;
    private Button ledButton;
    private Button stepButton;
    private Button crawlButton;
    private Button walkButton;
    private Button trotButton;
    private Button standButton;
    //carlo private Button modeButton;
    //carlo private Button runButton;
    private Button sitButton;
    private Button boundButton;
    private Button stretchButton;
    private Button zeroButton;
    private Button peeButton;
    private Button pushupButton;
    private Button greetButton;
    private Button BottomUpButton;


    // Instruction map, values must be the same as in your OpenCat.h file!
    private HashMap<Command, String> instructionMap = new HashMap<Command, String>();

    private Command lastDirection;  // Used to check whether send new direction movement command
    private Command newDirection;  // Register new direction so send
    private Command currentGait;
    private long directionPerSecondLimit = 500;  // Send one direction command each 0.5 seconds
    private String strReady="";
    private boolean isBittleReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_opencat);

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
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));

        strReady = getIntent().getStringExtra("BittleReady");
        if (strReady.equals("ready")){isBittleReady=true;}


        // Initialize instruction map
        //instructionMap.put(Command.REST, "d");
        instructionMap.put(Command.REST, "A#100#100#");
        Toast.makeText(this, "A#100#100#", Toast.LENGTH_SHORT).show();

        instructionMap.put(Command.GYRO, "g");
        instructionMap.put(Command.LED, "e");
        instructionMap.put(Command.SHUTDOWN, "z");
        instructionMap.put(Command.CALIBRATION, "c");

        instructionMap.put(Command.FORWARD, "F");
        instructionMap.put(Command.LEFT, "L");
        instructionMap.put(Command.BALANCE, "kbalance");
        instructionMap.put(Command.RIGHT, "R");
        instructionMap.put(Command.BACKWARD, "B");

        instructionMap.put(Command.STEP, "kvt");
        instructionMap.put(Command.CRAWL, "kcr");
        instructionMap.put(Command.WALK, "kwk");
        instructionMap.put(Command.TROT, "ktr");

        instructionMap.put(Command.LOOKUP, "klu");
        instructionMap.put(Command.BUTTUP, "kbuttUp");
        instructionMap.put(Command.STRETCH, "kstr");
        instructionMap.put(Command.SIT, "ksit");
        instructionMap.put(Command.ZERO, "kzero");

        instructionMap.put(Command.RUN, "krn");
        instructionMap.put(Command.BOUND, "kbd");
        instructionMap.put(Command.GREETING, "khi");
        instructionMap.put(Command.PUSHUP, "kpu");
        instructionMap.put(Command.PEE, "kpee");


        lastDirection = Command.BALANCE;
        newDirection = Command.BALANCE;
        currentGait = Command.WALK;

        restButton = findViewById(R.id.btRest);
        gyroButton = findViewById(R.id.btGyro);
        ledButton = findViewById(R.id.btLed);
        stepButton = findViewById(R.id.btvuoto2);
        crawlButton = findViewById(R.id.btSlow2);
        walkButton = findViewById(R.id.btNormal2);
        trotButton = findViewById(R.id.btFast2);
        standButton = findViewById(R.id.BtnUltraNoIR);
        //carlo modeButton = findViewById(R.id.btMode);
        //carlo runButton = findViewById(R.id.btRun);
        sitButton = findViewById(R.id.BtnUltra);
        boundButton = findViewById(R.id.btBound);
        stretchButton = findViewById(R.id.btStretch);
        zeroButton = findViewById(R.id.btZero);
        peeButton = findViewById(R.id.btPee);
        pushupButton = findViewById(R.id.btPushUp);
        greetButton = findViewById(R.id.btvuoto1);
        BottomUpButton= findViewById(R.id.btBotUp);

        SetDefaultColorMovBtn();
        walkButton.setTextColor(getResources().getColor(R.color.colorButton2));

        Context context = this;

        // Set Buttons functionality
//        btDisconnect.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (isBittleReady) {
//                    try {
//                        String text = instructionMap.get(Command.REST);
//                        myConnectionBt.write(text);
//                        isBittleReady = false;
//                        bittleConnectionStatus = "";
//                        btSocket.close();
//                    } catch (IOException e) {
//                        Toast.makeText(getBaseContext(), "Error disconnecting Bluetooth", Toast.LENGTH_SHORT).show();
//                    }
//                    finish();
//                }
//            }
//        });

        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.REST);
                    //ConnectActivity.connectedThread.write(text);
                    ConnectBleActivity.connectedThread.writeLF(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        gyroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.GYRO);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        ledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.LED);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        stepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.STEP);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);

                }
            }
        });

        crawlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGait = Command.CRAWL;

                SetDefaultColorMovBtn();
                crawlButton.setTextColor(getResources().getColor(R.color.colorButton2));
            }
        });

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGait = Command.WALK;

                SetDefaultColorMovBtn();
                walkButton.setTextColor(getResources().getColor(R.color.colorButton2));
            }
        });

        trotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentGait = Command.TROT;

                SetDefaultColorMovBtn();
                trotButton.setTextColor(getResources().getColor(R.color.colorButton2));
            }
        });

        standButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.BALANCE);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        BottomUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.BUTTUP);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });


        // carlo
//        modeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isBittleReady) {
//                    // TODO
//                }
//            }
//        });


        //carlo
//        runButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isBittleReady) {
//                    String text = instructionMap.get(Command.RUN);
//                    myConnectionBt.write(text);
//                    Log.d("DEBUG", "Message sent: " + text);
//                }
//            }
//        });

        sitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.SIT);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        boundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.BOUND);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        stretchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.STRETCH);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.ZERO);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        peeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.PEE);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        pushupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.PUSHUP);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

        greetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBittleReady) {
                    String text = instructionMap.get(Command.GREETING);
                    ConnectBleActivity.connectedThread.write(text);
                    Log.d("DEBUG", "Message sent: " + text);
                }
            }
        });

//        // Set Bluetooth message receiver
//        bluetoothIn = new Handler() {
//            public void handleMessage(android.os.Message msg) {
//                if (msg.what == handlerState) {
//                    String received = msg.obj.toString();
//                    Log.d("DEBUG", "Message received: " + received);
//                    if (!isBittleReady) {
//                        bittleConnectionStatus = bittleConnectionStatus + received;
//                        boolean isFound = bittleConnectionStatus.indexOf("Finished!") != -1 ? true : false; //true
//                        if (bittleConnectionStatus.length() > 10 && isFound) {
//                            isBittleReady = true;
//                            bittleConnectionStatus = "";
//                            progressBar.setVisibility(View.GONE);
//                            connectingText.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            }
//        };
//
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        verifyBTStatus();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendDirection();
                handler.postDelayed(this, directionPerSecondLimit);
            }
        },directionPerSecondLimit );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.opencat, menu);
        return super.onCreateOptionsMenu(menu);
    }

        // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        switch (item.getItemId()){
            case R.id.Setup:
                Intent intentSetupActivity = new Intent(OpenCatActivity.this, OpenCatSetupActivity.class);
                String strReady = "";
                if (isBittleReady) { strReady = "ready"; }
                intentSetupActivity.putExtra("BittleReady", strReady);
                startActivity(intentSetupActivity);
                break;

            case R.id.Info:
                Intent intentInstructionsActivity = new Intent(OpenCatActivity.this, InstructionsActivity.class);
                startActivity(intentInstructionsActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void SetDefaultColorMovBtn(){
        crawlButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        walkButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        trotButton.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

        @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        switch (id) {
            case R.id.joystickMove:
                double angle = getAngle(xPercent, yPercent);
                newDirection = getNewDirection(angle);
                // Log.d("Left Joystick", "X percent: " + xPercent + " Y percent: " +
                //         yPercent + " Angle: " + angle + " Direction: " + newDirection.toString());
                break;
        }
    }

    private void sendDirection() {
        String text;
        if (isBittleReady) {
            if (lastDirection != newDirection) {
                if (newDirection == Command.BACKWARD) {
                    text = "kbk";
                }
                else if (newDirection != Command.BALANCE) {
                    text = instructionMap.get(currentGait) + instructionMap.get(newDirection);
                } else {
                    text = instructionMap.get(newDirection);
                }
                ConnectBleActivity.connectedThread.write(text);
                lastDirection = newDirection;
                Log.d("DEBUG", "Message sent: " + text);
            }
        }
    }

    private double getAngle(float xPercent, float yPercent) {  // Used 0.2 instead to give a margin
        double angle_deg = 0;
        if (xPercent > 0 && yPercent > 0 && (xPercent > 0.2 || yPercent > 0.2)) {  // First quadrant
            double angle_rad = Math.atan2(xPercent, yPercent);
            angle_deg = angle_rad * 180 / Math.PI;
        } else if (xPercent > 0 && yPercent < 0 && (xPercent > 0.2 || yPercent < -0.2)) {  // Second quadrant
            double angle_rad = Math.atan2(xPercent, yPercent);
            angle_deg = angle_rad * 180 / Math.PI;
        } else if (xPercent < 0 && yPercent < 0 && (xPercent < -0.2 || yPercent < -0.2)) {  // Third quadrant
            double angle_rad = Math.atan2(xPercent, yPercent);
            angle_deg = angle_rad * 180 / Math.PI;
            angle_deg = 360 + angle_deg;
        } else if (xPercent < 0 && yPercent > 0 && (xPercent < -0.2 || yPercent > 0.2)) {  // Fourth quadrant
            double angle_rad = Math.atan2(xPercent, yPercent);
            angle_deg = angle_rad * 180 / Math.PI;
            angle_deg = 360 + angle_deg;
        }
        return angle_deg;
    }

    private Command getNewDirection(double angle) {
        Command res = Command.BALANCE;  // Neutral position
        if (angle >= 315 || angle <= 45) {
            if (angle < 0.01) {
                res = Command.BALANCE;
            } else {
                res = Command.FORWARD;
            }
        } else if (angle > 45 && angle <= 135) {
            res = Command.RIGHT;
        } else if (angle > 135 && angle <= 225) {
            res = Command.BACKWARD;
        } else if (angle > 225 && angle <= 315) {
            res = Command.LEFT;
        }
        return res;
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
}
