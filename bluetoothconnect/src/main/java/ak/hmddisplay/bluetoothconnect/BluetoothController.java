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

/**
 * UnityProject calls connectAsServer() from Unity
 * RemoteControlDevice calls connectAsClient() from MainActivity
 */
public class BluetoothController {

    private static final String TAG = "BT_CTRL";

    private Activity activity;
    private UUID HMD_UUID = UUID.fromString("5d01218c-641e-11e8-adc0-fa7ae01bbebc");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private DataOutputStream dataOut;
    private OnMessageReceivedListener onMessageReceivedListener;


    public BluetoothController(Activity activity) {


        this.activity = activity;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 0);
        }


    }

    public void addBluetoothListener(OnMessageReceivedListener onMessageReceivedListener) {
        this.onMessageReceivedListener = onMessageReceivedListener;
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
                        bluetoothSocket = socket;
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
            if (device.getName().equals("Moto G (5)") || device.getName().equals("Pixel 2")) {
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

        new Thread(new Runnable() {
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
                Intent intent = new Intent("android.intent.action.MAIN").putExtra("TEXT", "connected");
                activity.sendBroadcast(intent);
                manageConnection(bluetoothSocket);
            }
        }).start();
    }

    public void write(String text) {
        try {
            if (dataOut != null) {
                dataOut.write(text.getBytes());
            } else {
                Log.e(TAG, "Data Out is not available");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageConnection(BluetoothSocket socket) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        byte[] buffer = new byte[256];
        int bytes;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }

        DataInputStream dataIn = new DataInputStream(tmpIn);
        dataOut = new DataOutputStream(tmpOut);

        try {
            while (true) {
                if (dataIn.available() > 0) {
                    bytes = dataIn.read(buffer);
                    String msg = new String(buffer, 0, bytes);
                    msg = msg.substring(msg.length() - 1);
                    onMessageReceivedListener.onMessageReceived(msg);

                    Log.i(TAG, msg);


                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startAsServer() {
        connectAsServer();
    }

    public void startAsClient() {
        connectAsClient();
    }
}
