package pl.edu.pw.elka.hexancientempires;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class ConnectionService extends Service {
    public static final UUID SERVICE_UUID = UUID.fromString("3d9ee4b4-f508-11e5-8a8f-b74a5cbde83f");

    private BluetoothSocket socket;
    private WeakReference<Listener> listener;
    private Handler handler;
    private BluetoothServerSocket serverSocket;
    private boolean isServer;

    private Listener getListener() {
        return listener != null? listener.get(): null;
    }

    private void post(Runnable action) {
        if(handler != null) {
            handler.post(action);
        }
    }

    public void setListener(Listener listener) {
        this.listener = new WeakReference<Listener>(listener);
        this.handler = new Handler();
    }

    public ConnectionService() {
    }

    public class Binder extends android.os.Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new Binder();
    }

    public void connect(BluetoothDevice device) {
        if(socket != null) {
            throw new IllegalStateException("Already connected");
        }

        stopListening();

        try {
            socket = device.createRfcommSocketToServiceRecord(SERVICE_UUID);
            Log.d("BT", "Connecting to " + device);
            socket.connect();
            Log.d("BT", "Connected");

            connected(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        if(socket == null) {
            throw new IllegalStateException();
        }

        try {
            Log.d("BT", "< " + data);
            socket.getOutputStream().write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if(socket == null) {
            throw new IllegalStateException();
        }

        try {
            Log.d("BT", "Disconnecting");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startInputThread() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("BT", "reading input");
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while((line = input.readLine()) != null) {
                        handleLineReceived(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    handleDisconnect();
                }
            }
        }.start();
    }

    private void handleDisconnect() {
        Log.d("BT", "Disconnected");
        socket = null;

        final Listener listener = getListener();
        if(listener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    listener.disconnected();
                }
            });
        }
    }

    private void handleLineReceived(final String line) {
        Log.d("BT", "> " + line);

        final Listener listener = getListener();
        if(listener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    listener.lineReceived(line);
                }
            });
        }
    }


    public void startListening() {
        new ListeningThread().start();
    }

    public void stopListening() {
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ListeningThread extends Thread {
        public void run() {
            try {
                serverSocket = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingRfcommWithServiceRecord("HexAncientEmpires", SERVICE_UUID);
                Log.d("BT", "Listening for connections");
                socket = serverSocket.accept();
                Log.d("BT", "Accepted connection");
                connected(true);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stopListening();
            }
        }
    }

    private void connected(boolean isServer) {
        stopListening();
        this.isServer = isServer;

        startInputThread();

        final Listener listener = getListener();
        if(listener != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    listener.connected();
                }
            });
        }
    }

    public interface Listener {
        public void connected();
        public void disconnected();
        public void lineReceived(String line);
    }

    public boolean isServer() {
        return isServer;
    }
}
