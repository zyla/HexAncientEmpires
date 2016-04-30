package pl.edu.pw.elka.hexancientempires;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zyla on 3/29/16.
 */
public class GameView extends View {
    public String text = "";

    private PointF cameraOffset = new PointF();
    private PointF lastTouchDown = new PointF();

    public GameView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchDown.set(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                cameraOffset.x += event.getX() - lastTouchDown.x;
                cameraOffset.y += event.getY() - lastTouchDown.y;
                lastTouchDown.set(event.getX(), event.getY());
                postInvalidate();
                return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xffffffff);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(0xffff0000);
        paint.setTextSize(30);
        canvas.drawText(text, 0, 30, paint);

        canvas.translate(cameraOffset.x, cameraOffset.y);
        drawMap(canvas);
    }

    private void drawMap(Canvas canvas) {
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
                drawTile(canvas, x, y);
            }
        }
    }

    private void drawTile(Canvas canvas, int mapX, int mapY) {
        Paint paint = new Paint();
        paint.setColor(0xff000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        final float TILE_SIZE = 256;
        float xgap = TILE_SIZE / 4;

        Path path = new Path();
        path.moveTo(xgap, 0);
        path.lineTo(TILE_SIZE - xgap, 0);
        path.lineTo(TILE_SIZE, TILE_SIZE / 2);
        path.lineTo(TILE_SIZE - xgap, TILE_SIZE);
        path.lineTo(xgap, TILE_SIZE);
        path.lineTo(0, TILE_SIZE / 2);
        path.lineTo(xgap, 0);

        canvas.save();
        canvas.translate(2 * (TILE_SIZE - xgap) * mapX, TILE_SIZE * mapY / 2);
        if((mapY & 1) == 1) {
            canvas.translate(TILE_SIZE - xgap, 0);
        }
        canvas.drawPath(path, paint);
        canvas.restore();
    }
}
