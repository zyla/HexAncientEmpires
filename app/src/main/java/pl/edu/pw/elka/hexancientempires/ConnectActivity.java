package pl.edu.pw.elka.hexancientempires;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    private List<BluetoothDevice> devices = new ArrayList<>();
    private DevicesAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_connect);

        ListView listView = (ListView) findViewById(R.id.devices);
        listAdapter = new DevicesAdapter();
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = devices.get(position);
                startGameWithDevice(device);
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            findDevices();
        }
    }

    private void startGameWithDevice(final BluetoothDevice device) {
        bluetoothAdapter.cancelDiscovery();
        setProgressBarIndeterminateVisibility(false);

        Intent intent = new Intent(getApplicationContext(), ConnectionService.class);

        boolean bound = bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("ConnectActivity", "Service connected");

                ConnectionService connection = ((ConnectionService.Binder) binder).getService();
                connection.connect(device);

                startActivity(new Intent(getApplicationContext(), GameActivity.class));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("ConnectActivity", "Service disconnected");
            }
        }, BIND_AUTO_CREATE);

        Log.d("ConnectActivity", "Binding service success=" + bound);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            findDevices();
        }
    }

    private void findDevices() {
        setProgressBarIndeterminateVisibility(true);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("HexAncientEmpires", "Found device: " + device + " name=" + device.getName());
                    listAdapter.add(device);
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        bluetoothAdapter.startDiscovery();

        listAdapter.addAll(bluetoothAdapter.getBondedDevices());
    }

    // TODO make it cleaner?
    private class DevicesAdapter extends ArrayAdapter<BluetoothDevice> {
        public DevicesAdapter() {
            super(ConnectActivity.this, R.layout.item_device, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            BluetoothDevice device = devices.get(position);
            view.setText(device.getName() == null? device.getAddress() : device.getName());
            return view;
        }
    }
}
