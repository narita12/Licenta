package com.example.bluetooth_test;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public final static String MODULE_MAC = "00:18:91:D7:9F:EC";
    public final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothAdapter bta;                 //bluetooth stuff
    BluetoothSocket mmSocket;             //bluetooth stuff
    BluetoothDevice mmDevice;             //bluetooth stuff
    Button sendMessage, clear;                   //UI stuff
    TextView MessageText, response;       //UI stuff
    TextView motor1, motor2, motor3, motor4, motor5, motor6;
    ConnectedThread btt = null;           //Our custom thread
    public Handler mHandler;              //this receives messages from thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("[BLUETOOTH]", "Creating listeners");

        sendMessage = (Button)   findViewById(R.id.send);
        clear = (Button)   findViewById(R.id.clear);
        MessageText = (TextView) findViewById(R.id.MessageText);
        response    = (TextView) findViewById(R.id.response);
        motor1    = (TextView) findViewById(R.id.motor1);
        motor2    = (TextView) findViewById(R.id.motor2);
        motor3    = (TextView) findViewById(R.id.motor3);
        motor4    = (TextView) findViewById(R.id.motor4);
        motor5    = (TextView) findViewById(R.id.motor5);
        motor6    = (TextView) findViewById(R.id.motor6);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.setText("");
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) {
                    String sendtxt = MessageText.getText().toString();

                    if(sendtxt.length() > 0) {
                        //convert numbers in individual bytes
                        String codified_message;
                        codified_message = "" +
                                (char)Integer.parseInt(sendtxt.substring( 0,  3 )) +
                                (char)Integer.parseInt(sendtxt.substring( 3,  6 )) +
                                (char)Integer.parseInt(sendtxt.substring( 6,  9 )) +
                                (char)Integer.parseInt(sendtxt.substring( 9,  12)) +
                                (char)Integer.parseInt(sendtxt.substring( 12, 15)) +
                                (char)Integer.parseInt(sendtxt.substring( 15, 18));
                        sendtxt = codified_message;
                        Toast.makeText(MainActivity.this, "Send converted message", Toast.LENGTH_LONG).show();
                    }else{
                        String codified_message;
                        codified_message = "" +
                                (char)Integer.parseInt(motor1.getText().toString()) +
                                (char)Integer.parseInt(motor2.getText().toString()) +
                                (char)Integer.parseInt(motor3.getText().toString()) +
                                (char)Integer.parseInt(motor4.getText().toString()) +
                                (char)Integer.parseInt(motor5.getText().toString()) +
                                (char)Integer.parseInt(motor6.getText().toString());
                        sendtxt = codified_message;
                        Toast.makeText(MainActivity.this, "Send fields", Toast.LENGTH_LONG).show();
                    }

                    btt.write(sendtxt.getBytes());
                    MessageText.setText("");

                    //disable the button and wait for 1 seconds to enable it again
                    sendMessage.setEnabled(false);
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
                                    sendMessage.setEnabled(true);
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                initiateBluetoothProcess();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            initiateBluetoothProcess();
        }
    }

    public void initiateBluetoothProcess(){
        if (bta.isEnabled()) {

            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
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
                    return;
                }
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if (msg.what == ConnectedThread.RESPONSE_MESSAGE) {
                        String txt = (String) msg.obj;
                        response.append("\n" + txt);
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
    }

}