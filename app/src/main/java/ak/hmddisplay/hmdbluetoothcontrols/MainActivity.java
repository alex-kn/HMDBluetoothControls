package ak.hmddisplay.hmdbluetoothcontrols;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ak.hmddisplay.bluetoothconnect.BluetoothController;

public class MainActivity extends Activity {

    TextView connectedTextView;

    private BroadcastReceiver receiver;
    BluetoothController btController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectedTextView = findViewById(R.id.connectedTextView);
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
                setText(text);
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

    @SuppressLint("SetTextI18n")
    public void startAsClient(View view) {
        connectedTextView.setText("connecting ...");
        btController.startAsClient();
    }

    public void switchRepresentation(View view) {
        btController.write("0");
    }

    public void setText(String text) {
        connectedTextView.setText(text);
    }

}
