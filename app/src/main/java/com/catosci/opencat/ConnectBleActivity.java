package com.catosci.opencat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;




public class ConnectBleActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private boolean isRobotReady = false;
    private RobotTypes RobotType;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private ProgressBar progressBar;
    private TextView connectingText;
    private Button btConnect;
    private Button BtnOpenInstruction;

    private  String SensorMessage="";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_connect_ble);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimaryDark)));
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);

        // Calling Application class (see application tag in AndroidManifest.xml)
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        RobotType=globalVariable.getRobotType();

        progressBar = findViewById(R.id.idProgressBar);
        progressBar.setVisibility(View.GONE);
        connectingText = findViewById(R.id.idConnectingBluetooth);
        btConnect = findViewById(R.id.btConnect);
        BtnOpenInstruction= findViewById(R.id.BtnOpenInstruction);

        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
        // Get the device address to make BT Connection
        deviceAddress = getIntent().getStringExtra("deviceAddress");
        // Show progree and connection status
        connectingText.setText("Connecting to " + deviceName + "...");
        progressBar.setVisibility(View.VISIBLE);
        btConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
        createConnectThread.start();
    }

        /*
        Second most important piece of Code. GUI Handler
         */
    handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case CONNECTING_STATUS:
                    switch(msg.arg1){
                        case 1:
                            connectingText.setText("Connected to " + deviceName);
                            progressBar.setVisibility(View.GONE);
                            btConnect.setEnabled(true);
                            btConnect.setText(R.string.btn_play);
                            isRobotReady = true;
                            //buttonToggle.setEnabled(true);
                            break;
                        case -1:
                            connectingText.setText("Device fails to connect");
                            progressBar.setVisibility(View.GONE);
                            btConnect.setEnabled(true);
                            break;
                    }
                    break;

                case MESSAGE_READ:
                    String arduinoMsg = msg.obj.toString(); // Read message from Arduino

                    SensorMessage=arduinoMsg;
                    //Set name and email in global/application context
                    globalVariable.setSensorMessage(SensorMessage);

//                    String strValue;
//                    strValue = arduinoMsg.substring(2);
//                    strValue = strValue.substring(0,strValue.indexOf("#"));
//                    if (arduinoMsg.charAt(0)=='E'){
//                        SonarMessage=strValue;
//                    }


//                    switch (arduinoMsg.charAt(0)){
//                        case 'E':
//                            SonarMessage=strValue;
//                            break;
//                        case 'M':
//                            IRdxMessage=strValue;
//                            break;
//                        case 'N':
//                            IRsxMessage=strValue;
//                            break;
//                    }

                    break;
            }
        }
    };

        // Select Bluetooth Device
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRobotReady) {
                    switch (RobotType){
                        case OPENCAT_BLE:
                            Intent OpenCatActivity = new Intent(ConnectBleActivity.this, OpenCatActivity.class);
                            OpenCatActivity.putExtra("BittleReady", "ready");
                            startActivity(OpenCatActivity);
                            break;
                        case SMARTCAR_BLE:
                            Intent intentSmartCarActivity = new Intent(ConnectBleActivity.this, SmartCarBleActivity.class);
                            intentSmartCarActivity.putExtra("SmartCarReady", "ready");
//                            intentSmartCarActivity.putExtra("SonarMessage", SonarMessage);
//                            intentSmartCarActivity.putExtra("IRdxMessage", IRdxMessage);
//                            intentSmartCarActivity.putExtra("IRsxMessage", IRsxMessage);
                            startActivity(intentSmartCarActivity);
                            break;
                    }

                }
                else {
                    Intent intent = new Intent(ConnectBleActivity.this, SelectBleDeviceActivity.class);
                    startActivity(intent);
                }

            }
        });

        BtnOpenInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConnectBleActivity.this, InstructionsActivity.class);
                startActivity(intent);

            }
        });



    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        public void writeLF(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            byte[] output = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, output, 0, bytes.length);
            //output[bytes.length] = (byte) 13;
            output[bytes.length] = (byte) 10;
            try {
                mmOutStream.write(output);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */

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

