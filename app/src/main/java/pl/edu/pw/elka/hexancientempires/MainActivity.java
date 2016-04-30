package pl.edu.pw.elka.hexancientempires;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        FrameLayout layout = new FrameLayout(this);
        layout.addView(gameView);
        setContentView(layout);
    }
}
