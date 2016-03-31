package pl.edu.pw.elka.hexancientempires;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zyla on 3/29/16.
 */
public class GameView extends View {
    public String text = "";

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xffff0000);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(0xff00ff00);
        paint.setTextSize(30);
        canvas.drawText(text, 0, 30, paint);
    }
}
