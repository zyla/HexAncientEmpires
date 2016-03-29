package pl.edu.pw.elka.hexancientempires;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectionService extends Service {
    public static final UUID SERVICE_UUID = UUID.fromString("3d9ee4b4-f508-11e5-8a8f-b74a5cbde83f");

    public ConnectionService() {
    }

    @Override
    public IBinder onBind(final Intent intent) {
        new Thread() {
            public void run() {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(SERVICE_UUID);
                    socket.connect();
                    Log.d("BT", "Connected");
                    OutputStream output = socket.getOutputStream();
                    output.write("Siema\n".getBytes());
                    output.flush();
                    Log.d("BT", "Sent data");
                    socket.close();
                    Log.d("BT", "Finished");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return new Binder();
    }
}
