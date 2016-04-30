package pl.edu.pw.elka.hexancientempires;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zyla on 3/29/16.
 */
public class GameView extends View {
    private static final float TILE_SIZE = 256;
    private static final float TILE_COLUMN_OFFSET = TILE_SIZE * 1.5f;
    private static final float TILE_ROW_OFFSET = TILE_SIZE / 2;
    private static final float TILE_ODD_ROW_EXTRA_X_OFFSET = TILE_SIZE * 0.75f;

    public String text = "";

    private PointF cameraOffset = new PointF();
    private PointF lastTouchDown = new PointF();

    // DEBUG INFO
    private int numTilesRendered;

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
        long start = System.currentTimeMillis();
        numTilesRendered = 0;

        Paint paint = new Paint();
        paint.setColor(0xffff0000);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(0xffff0000);
        paint.setTextSize(30);
        canvas.drawText(text, 0, 30, paint);

        canvas.save();
        {
            canvas.translate(cameraOffset.x, cameraOffset.y);
            drawMap(canvas);
        }
        canvas.restore();

        long renderTime = System.currentTimeMillis() - start;

        paint.setColor(0x80000000);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(getWidth() - 200, 0, getWidth(), 80, paint);

        paint.setColor(0xffffffff);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format("tiles: %d", numTilesRendered), getWidth(), 30, paint);
        canvas.drawText(String.format("time: %dms", renderTime), getWidth(), 60, paint);
    }

    private void drawMap(Canvas canvas) {
        Rect visibleTiles = visibleTiles();
        for(int x = visibleTiles.left; x < visibleTiles.right; x++) {
            for(int y = visibleTiles.top; y < visibleTiles.bottom; y++) {
                drawTile(canvas, x, y);
            }
        }
    }

    Rect visibleTiles() {
        return new Rect(
                (int) Math.floor((-cameraOffset.x - TILE_ODD_ROW_EXTRA_X_OFFSET) / TILE_COLUMN_OFFSET),
                (int) Math.floor((-cameraOffset.y - TILE_ROW_OFFSET) / TILE_ROW_OFFSET),
                (int) Math.ceil((-cameraOffset.x + getWidth()) / TILE_COLUMN_OFFSET),
                (int) Math.ceil((-cameraOffset.y + getHeight()) / TILE_ROW_OFFSET)
        );
    }

    private void drawTile(Canvas canvas, int mapX, int mapY) {
        float xOffset = TILE_COLUMN_OFFSET * mapX + (mapY & 1) * TILE_ODD_ROW_EXTRA_X_OFFSET;
        float yOffset = TILE_ROW_OFFSET * mapY;

        if(xOffset + cameraOffset.x + TILE_SIZE < 0 || xOffset + cameraOffset.x > getWidth()
                || yOffset + cameraOffset.y + TILE_SIZE < 0 || yOffset + cameraOffset.y > getHeight())
            return;

        numTilesRendered++;

        Paint paint = new Paint();

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
        {
            canvas.translate(xOffset, yOffset);

            paint.setColor(0xffffffff);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, paint);

            paint.setColor(0xff000000);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawPath(path, paint);

            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(32);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.format("(%d, %d)", mapX, mapY), TILE_SIZE / 2, TILE_SIZE / 2, paint);
        }
        canvas.restore();
    }
}
