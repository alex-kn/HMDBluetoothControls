package ak.hmddisplay.hmdbluetoothcontrols;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ak.hmddisplay.bluetoothconnect.BluetoothController;

public class MainActivity extends Activity {

    Button connectButton;

    private BroadcastReceiver receiver;
    BluetoothController btController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = findViewById(R.id.connectButton);
        btController = new BluetoothController(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String text = intent.getStringExtra("TEXT");
                setButtonText(text);
            }
        };
        this.registerReceiver(receiver, intentFilter);
    }

    public void sendOne(View view) {
        btController.write("1");
    }

    public void sendTwo(View view) {
        btController.write("2");
    }

    public void sendThree(View view) {
        btController.write("3");
    }

    public void sendFour(View view) {
        btController.write("4");
    }

    public void sendFive(View view) {
        btController.write("5");
    }

    @SuppressLint("SetTextI18n")
    public void startAsClient(View view) {
        connectButton.setText("connecting ...");
        connectButton.setEnabled(false);
        btController.startAsClient();
    }

    public void switchRepresentation(View view) {
        btController.write("0");
    }

    public void setButtonText(String text) {
        connectButton.setText(text);

    }
}
