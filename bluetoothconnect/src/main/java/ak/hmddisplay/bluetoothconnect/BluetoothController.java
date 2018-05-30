package ak.hmddisplay.bluetoothconnect;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothController {

    private static final String TAG = "BT_CTRL";

    private Activity activity;
    private UUID HMD_UUID = UUID.fromString("5d01218c-641e-11e8-adc0-fa7ae01bbebc");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private DataOutputStream dataOut;

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isServer = true;


    public BluetoothController(Activity activity) {


        this.activity = activity;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 0);
        }

        //noinspection ConstantConditions
        if (isServer) {
            connectAsServer();
        } else {
            connectAsClient();
        }

    }

    private void connectAsServer() {
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Server", HMD_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        bluetoothServerSocket = tmp;

        new Thread(new Runnable() {
            @Override
            public void run() {

                BluetoothSocket socket = null;
                while (true) {
                    try {
                        socket = bluetoothServerSocket.accept();
                    } catch (IOException e) {
                        Log.e(TAG, "Could not close the connect socket", e);
                    }

                    if (socket != null) {
                        manageConnection(socket);
                        try {
                            bluetoothServerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }).start();
    }

    private void connectAsClient() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("Moto G (5)")) {
                bluetoothDevice = device;
            }
        }

        BluetoothSocket tmp = null;
        try {
            tmp = bluetoothDevice.createRfcommSocketToServiceRecord(HMD_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothSocket = tmp;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    bluetoothSocket.connect();
                } catch (IOException e) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    return;
                }
                manageConnection(bluetoothSocket);
            }
        };
    }

    public void write(String text){
        try {
            dataOut.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageConnection(BluetoothSocket socket) {
        InputStream tmpIn;
        OutputStream tmpOut;
        byte[] buffer = new byte[256];
        int bytes;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            return;
        }

        DataInputStream dataIn = new DataInputStream(tmpIn);
        dataOut = new DataOutputStream(tmpOut);

        try {
            while (dataIn.available() > 0) {
                bytes = dataIn.read(buffer);
                String msg = new String(buffer, 0, bytes);
                Log.i(TAG, "SUCCESS");
                Log.i(TAG, msg);

                Intent intent = new Intent("android.intent.action.MAIN").putExtra("TEXT", msg);
                activity.sendBroadcast(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
