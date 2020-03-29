package com.example.test_bluetooth_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {


    Button connect, list_btn,ON,OFF;
    ListView lv;
    BluetoothDevice[] btArray;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    int A_R_E=0;

    private OutputStream os;
    private InputStream is;
    private BluetoothSocket socket;

    private static final String APP_NAME = "BTLED";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connect = (Button) findViewById(R.id.connect);
        list_btn = (Button) findViewById(R.id.list_btn);
        ON = (Button) findViewById(R.id.on);
        OFF = (Button) findViewById(R.id.off);
        lv = (ListView) findViewById(R.id.lv);
        final BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();

        if (blueAdapter == null) {
            finish();
        }
        if (!blueAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!blueAdapter.isDiscovering()){
                    Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent,A_R_E);
                }
            }
        });


        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt = blueAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    lv.setAdapter(arrayAdapter);
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = (BluetoothDevice) btArray[i];
                //uuids = device.getUuids();
                try {
                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char[] test = {0x01, 0x99, 0x02, 0x00, 0x99};
                write(test);
            }
        });
        OFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char[] test1 = {0x01, 0x99, 0x02, 0x01, 0x99};
                write(test1);
            }
        });

    }

    private void write(char[] ch) {

        try {

            for (int k = 0; k < ch.length; k++) {
                new DataOutputStream(socket.getOutputStream()).writeByte(ch[k]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true){
            try {
                bytes = socket.getInputStream().read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}