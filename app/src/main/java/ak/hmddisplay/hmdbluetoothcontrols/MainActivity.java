package ak.hmddisplay.hmdbluetoothcontrols;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ak.hmddisplay.bluetoothconnect.BluetoothController;

public class MainActivity extends Activity {

    TextView textView;
    Button button;

    private BroadcastReceiver receiver;
    BluetoothController btController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);


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

    public void sendMsg(View view) {
        btController.write("Bier");
    }

    public void setText(String text) {
        textView.setText(text);
    }

}
