package pl.edu.pw.elka.hexancientempires;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class GameActivity extends AppCompatActivity implements ConnectionService.Listener {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        FrameLayout layout = new FrameLayout(this);
        layout.addView(gameView);
        setContentView(layout);

        Intent intent = new Intent(getApplicationContext(), ConnectionService.class);

        boolean bound = bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("GameActivity", "Service connected");

                ConnectionService connection = ((ConnectionService.Binder) binder).getService();
                connection.send("Hello from GameActivity\n");

                connection.setListener(GameActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("GameActivity", "Service disconnected");
            }
        }, BIND_AUTO_CREATE);

        Log.d("GameActivity", "Binding service success=" + bound);
    }

    @Override
    public void disconnected() {
        finish();
    }

    @Override
    public void lineReceived(String line) {
        Event event;
        try {
            String[] tokens = line.split("\\s+");
            event = Event.deserialize(tokens);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }
        gameView.onEventReceived(event);
    }
}
