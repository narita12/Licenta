package com.example.testinterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public final static String MODULE_MAC = "00:18:91:D7:9F:EC";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothAdapter bta;                 //bluetooth stuff
    BluetoothSocket mmSocket;             //bluetooth stuff
    BluetoothDevice mmDevice;             //bluetooth stuff
    ConnectedThread btt = null;           //Our custom thread
    public Handler mHandler;              //this receives messages from thread

    TextView messageWindow;
    TextView statusWindow;
    TextView pump1, pump2, pump3, pump4, pump5, pump6;

    Button sendButton;
    Button clearButton;

    CheckBox check_container;


    private int validate_pumps_values(TextView pump) {
        if (pump.getText().toString().isEmpty()) {
            pump.setError("Please add a value");
            return -1;
        }
        int IntPump = Integer.parseInt(pump.getText().toString());
        if (IntPump < 0 || IntPump > 250){
            pump.setError("Value between 0 and 255");
            return -1;
        }
        return IntPump;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            messageWindow = findViewById(R.id.text_scroll);
            statusWindow = findViewById(R.id.status_window);
            pump1 = findViewById(R.id.input_motor1);
            pump2 = findViewById(R.id.input_motor2);
            pump3 = findViewById(R.id.input_motor3);
            pump4 = findViewById(R.id.input_motor4);
            pump5 = findViewById(R.id.input_motor5);
            pump6 = findViewById(R.id.input_motor6);

            sendButton = findViewById(R.id.button_send);
            clearButton = findViewById(R.id.button_clear);

            check_container = findViewById(R.id.check_container);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageWindow.setText("");
            }
        });



        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if (mmSocket.isConnected() && btt != null) {
                    if(check_container.isChecked()) {
                        messageWindow.append("Codify command\n");
                        int IntPump1 = validate_pumps_values(pump1);
                        int IntPump2 = validate_pumps_values(pump2);
                        int IntPump3 = validate_pumps_values(pump3);
                        int IntPump4 = validate_pumps_values(pump4);
                        int IntPump5 = validate_pumps_values(pump5);
                        int IntPump6 = validate_pumps_values(pump6);

                        if(IntPump1<0 || IntPump2<0 || IntPump3<0 || IntPump4<0 || IntPump5<0 || IntPump6<0 ){
                            Toast.makeText(MainActivity.this, "Errors", Toast.LENGTH_LONG).show();
                        }
                        else {
                            String codified_message;
                            codified_message = "" +
                                    (char) IntPump1 +
                                    (char) IntPump2 +
                                    (char) IntPump3 +
                                    (char) IntPump4 +
                                    (char) IntPump5 +
                                    (char) IntPump6;

                            messageWindow.append("Sending command\n");
                            btt.write(codified_message.getBytes());

                            sendButton.setEnabled(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendButton.setEnabled(true);
                                        }
                                    });
                                }
                            }).start();
                        }
                    } else{
                        Toast.makeText(MainActivity.this, "Put a glass and check the CheckBox", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "No connection", Toast.LENGTH_LONG).show();

                }
            }
        });
        bta = BluetoothAdapter.getDefaultAdapter();
        //if bluetooth is not enabled then create Intent for user to turn it on
        if(bta == null){
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_LONG).show();
        }
        else {
            if (!bta.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            } else {
                switch(initiateBluetoothProcess()){
                    case 0 : statusWindow.setText(getResources().getString(R.string.error_code_0_status)); messageWindow.append(getResources().getString(R.string.error_code_0));break;
                    case 1: statusWindow.setText(getResources().getString(R.string.error_code_1_status)); messageWindow.append(getResources().getString(R.string.error_code_1));break;
                    case 2: statusWindow.setText(getResources().getString(R.string.error_code_2_status)); messageWindow.append(getResources().getString(R.string.error_code_2)); break;
                    default: statusWindow.setText(getResources().getString(R.string.error_code_unknown_status)); messageWindow.append(getResources().getString(R.string.error_code_unknown)); break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.bluetooth){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageWindow.append("Trying to connect...\n");
                            if(bta == null){
                                Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_LONG).show();
                                messageWindow.append("Bluetooth not available\n");
                            }
                            else {
                                if (!bta.isEnabled()) {
                                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
                                } else {
                                    switch (initiateBluetoothProcess()) {
                                        case 0:
                                            statusWindow.setText(getResources().getString(R.string.error_code_0_status));
                                            messageWindow.append(getResources().getString(R.string.error_code_0));
                                            break;
                                        case 1:
                                            statusWindow.setText(getResources().getString(R.string.error_code_1_status));
                                            messageWindow.append(getResources().getString(R.string.error_code_1));
                                            break;
                                        case 2:
                                            statusWindow.setText(getResources().getString(R.string.error_code_2_status));
                                            messageWindow.append(getResources().getString(R.string.error_code_2));
                                            break;
                                        default:
                                            statusWindow.setText(getResources().getString(R.string.error_code_unknown_status));
                                            messageWindow.append(getResources().getString(R.string.error_code_unknown));
                                            break;
                                    }
                                }
                            }
                        }
                    });
                }
            }).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override //when you return from another activity, this is the first function that it's called
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            initiateBluetoothProcess();
        }
    }

    public int initiateBluetoothProcess(){
        if (bta.isEnabled()) {
            if(btt!=null) {
                btt.cancel();
            }
            //attempt to connect to bluetooth module
            BluetoothSocket tmp;
            mmDevice = bta.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[BLUETOOTH]", "Connected to: " + mmDevice.getName());
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException c) {
                    return 2; //mmSocket close error
                }
                return 1; //Connection error
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if (msg.what == ConnectedThread.RESPONSE_MESSAGE) {
                        String txt = (String) msg.obj;
                        messageWindow.append(txt + "\n");
                    }
                }
            };
            Log.i("[BLUETOOTH]", "Creating and running Thread");
            btt = new ConnectedThread(mmSocket, mHandler);
            btt.start();
        } else {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 3);
        }
        return 0;
    }
}
