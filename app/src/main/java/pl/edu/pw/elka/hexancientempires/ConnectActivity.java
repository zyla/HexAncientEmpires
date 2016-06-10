package pl.edu.pw.elka.hexancientempires;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity implements ConnectionService.Listener {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    private List<BluetoothDevice> devices = new ArrayList<>();
    private DevicesAdapter listAdapter;
    private ConnectionService connectionService;

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
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(connectionService != null) {
            connectionService.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            bluetoothEnabled();
        }
    }

    private void startGameWithDevice(final BluetoothDevice device) {
        bluetoothAdapter.cancelDiscovery();
        setProgressBarIndeterminateVisibility(false);

        final ProgressDialog dialog = ProgressDialog.show(this, "Connecting", "Connecting to " + device.getName(), true, false);

        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... args) {
                try {
                    connectionService.connect(device);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }

            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if(!result) {
                    new AlertDialog.Builder(ConnectActivity.this)
                        .setTitle("Connection error")
                        .setMessage("Error connecting to " + device.getName())
                        .setPositiveButton("OK", null)
                        .create().show();
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            bluetoothEnabled();
        }
    }

    private void bluetoothEnabled() {
        Intent intent = new Intent(getApplicationContext(), ConnectionService.class);
        boolean bound = bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("ConnectActivity", "Service connected");

                connectionService = ((ConnectionService.Binder) binder).getService();
                connectionService.setListener(ConnectActivity.this);
                connectionService.startListening();

                findDevices();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("ConnectActivity", "Service disconnected");
            }
        }, BIND_AUTO_CREATE);

        Log.d("ConnectActivity", "Binding service success=" + bound);
    }

    private void findDevices() {
        devices.clear();
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

    // ConnectionService listener
    public void connected() {
        startActivity(new Intent(getApplicationContext(), GameActivity.class));
    }

    public void disconnected() {}
    public void lineReceived(String line) {}
}
