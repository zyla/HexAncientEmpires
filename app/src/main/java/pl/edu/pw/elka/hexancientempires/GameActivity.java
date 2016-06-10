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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class GameActivity extends AppCompatActivity implements ConnectionService.Listener, Connection {
    private GameView gameView;
    private ConnectionService connectionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getApplicationContext(), ConnectionService.class);

        boolean bound = bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("GameActivity", "Service connected");

                connectionService = ((ConnectionService.Binder) binder).getService();
                connectionService.setListener(GameActivity.this);

                gameView = new GameView(GameActivity.this, GameActivity.this);
                FrameLayout layout = new FrameLayout(GameActivity.this);
                layout.addView(gameView);
                setContentView(layout);

                setTitle("Game [Player " + gameView.getMyPlayerID() + "]");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("GameActivity", "Service disconnected");
            }
        }, BIND_AUTO_CREATE);

        Log.d("GameActivity", "Binding service success=" + bound);
    }

    public void connected() {}

    @Override
    public void disconnected() {
        finish();
    }

    @Override
    public void lineReceived(String line) {
        Event event;
        try {
            event = Event.deserialize(line);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }
        gameView.onEventReceived(event);
    }

    public void sendEvent(Event event) {
        connectionService.send(event.serialize());
    }

    public boolean isServer() {
        return connectionService.isServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_finish_turn) {
            gameView.finishTurnClicked();
            return true;
        }

        return false;
    }
}
