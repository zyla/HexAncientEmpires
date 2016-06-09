package pl.edu.pw.elka.hexancientempires;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this, new NullConnection());
        FrameLayout layout = new FrameLayout(this);
        layout.addView(gameView);
        setContentView(layout);
    }

    private static class NullConnection implements Connection {
        public boolean isServer() { return true; }
        public void sendEvent(Event event) {}
    }
}
