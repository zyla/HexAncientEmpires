package pl.edu.pw.elka.hexancientempires;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_WIDTH;
import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_HEIGHT;

/**
 * Created by zyla on 3/29/16.
 */
public class GameView extends View {
    public String text = "";

    private PointF cameraOffset = new PointF(-TILE_WIDTH/4, -TILE_HEIGHT/2);
    private PointF lastTouchDown = new PointF();

    /** True if current touch stroke is a movement, not a click */
    private boolean isMovement;

    /** Maximum movement distance for a click event, squared */
    private static final float MAX_CLICK_EVENT_DISTANCE_SQ = 100;

    private GameMap Map = new GameMap();
    private ArrayList<Drawable> terrain;
    private ArrayList<Bitmap> units;

    // DEBUG INFO
    private int numTilesRendered;

    private Drawable cursor;
    private Point cursorPos = new Point(0, 0);

    public GameView(Context context) {
        super(context);
        terrain = new ArrayList<>(6);
        terrain.add(context.getResources().getDrawable(R.drawable.c));
        terrain.add(context.getResources().getDrawable(R.drawable.g));
        terrain.add(context.getResources().getDrawable(R.drawable.m));
        terrain.add(context.getResources().getDrawable(R.drawable.r));
        terrain.add(context.getResources().getDrawable(R.drawable.t));
        terrain.add(context.getResources().getDrawable(R.drawable.w));

        for(int i = 0 ; i < terrain.size(); i++) {
            terrain.get(i).setBounds(0, 0, TILE_WIDTH, TILE_HEIGHT);
        }

        units = new ArrayList<>(3);
        units.add(BitmapFactory.decodeResource(getResources(), R.drawable.units1));
        units.add(BitmapFactory.decodeResource(getResources(), R.drawable.units2));
        units.add(BitmapFactory.decodeResource(getResources(), R.drawable.units3));

        cursor = context.getResources().getDrawable(R.drawable.cursor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float offsetX, offsetY;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchDown.set(event.getX(), event.getY());
                isMovement = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                offsetX = event.getX() - lastTouchDown.x;
                offsetY = event.getY() - lastTouchDown.y;

                if (!isMovement && mag2(offsetX, offsetY) >= MAX_CLICK_EVENT_DISTANCE_SQ) {
                    isMovement = true;
                }

                if (isMovement) {
                    setCameraOffset(cameraOffset.x + offsetX, cameraOffset.y + offsetY);
                    lastTouchDown.set(event.getX(), event.getY());
                    postInvalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (!isMovement) {
                    tileClicked(TileMath.tileHitTest((int) (event.getX() - cameraOffset.x), (int) (event.getY() - cameraOffset.y)));
                }
                return true;
        }
        return false;
    }

    private void setCameraOffset(float x, float y) {
        // TODO refactor
        Point lastTile = TileMath.tileLocation(Map.getWidth(), Map.getHeight());
        cameraOffset.x = Math.max(Math.min(x, -TILE_WIDTH/4), -lastTile.x + getWidth());
        cameraOffset.y = Math.max(Math.min(y, -TILE_HEIGHT/2), -lastTile.y + TILE_HEIGHT/2 + getHeight());
    }

    private void tileClicked(Point tilePos) {
        cursorPos = tilePos;
        postInvalidate();
    }

    /** Squared magnitude of a two-dimensional vector (x, y) */
    private static float mag2(float x, float y) {
        return x*x + y*y;
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

            drawCursor(canvas, TileMath.tileCenter(cursorPos.x, cursorPos.y));
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

    private Rect visibleArea() {
        return new Rect(
                (int) -cameraOffset.x,
                (int) -cameraOffset.y,
                (int) -cameraOffset.x + getWidth(),
                (int) -cameraOffset.y + getHeight()
        );
    }

    private void drawMap(Canvas canvas) {
        Rect visibleTiles = TileMath.visibleTiles(visibleArea());
        for(int x = visibleTiles.left; x <= visibleTiles.right; x++) {
            for(int y = visibleTiles.top; y <= visibleTiles.bottom; y++) {
                drawTile(canvas, x, y);
            }
        }
    }

    private void drawCursor(Canvas canvas, Point center) {
        final int width = TILE_WIDTH + 10, height = TILE_HEIGHT + 10;
        final int anchorX = width/2, anchorY = height/2;

        int x = center.x - anchorX, y = center.y - anchorY;

        cursor.setBounds(x, y, x + width, y + height);
        cursor.draw(canvas);
    }

    Paint paint = new Paint();

    private final Path tilePath;
    {
        float xgap = TILE_WIDTH / 4;

        tilePath = new Path();
        tilePath.moveTo(xgap, 0);
        tilePath.lineTo(TILE_WIDTH - xgap, 0);
        tilePath.lineTo(TILE_WIDTH, TILE_HEIGHT / 2);
        tilePath.lineTo(TILE_WIDTH - xgap, TILE_HEIGHT);
        tilePath.lineTo(xgap, TILE_HEIGHT);
        tilePath.lineTo(0, TILE_HEIGHT / 2);
        tilePath.lineTo(xgap, 0);
    }

    private void drawTile(Canvas canvas, int mapX, int mapY) {
        Point loc = TileMath.tileLocation(mapX, mapY);
        Tile tile = Map.getTile(mapX,mapY);

        //ask Map if File is an element of the map
        if(tile.type == Tile.NONE)
          return; //or draw empty tile instead

        numTilesRendered++;

        canvas.save();
        {
            canvas.translate(loc.x, loc.y);
            terrain.get(tile.type - 1).draw(canvas);

            if (tile.unit != null) {
                Unit unit = tile.unit;
                Bitmap bitmap = units.get(unit.playerID - 1);
                int unitWidth = bitmap.getWidth() / 12, unitHeight = bitmap.getHeight() / 2;
                android.graphics.Rect areaToCrop = new android.graphics.Rect(unit.type * unitWidth, 0, (unit.type + 1) * unitWidth, unitHeight);
                RectF areaToDraw = new RectF(0, 0, TILE_WIDTH, TILE_HEIGHT);

                canvas.drawBitmap(bitmap, areaToCrop, areaToDraw, null);
            }

            if(tile.isDisplayRange() == true) {
                paint.setColor(0x80ff0000);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(tilePath, paint);
            }
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(32);
            paint.setTextAlign(Paint.Align.CENTER);
        }
        canvas.restore();
    }
}
