package pl.edu.pw.elka.hexancientempires;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
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
    private PointF cameraOffset = new PointF(-TILE_WIDTH/4, -TILE_HEIGHT/2);
    private PointF lastTouchDown = new PointF();
    private long lastFrameTime; // for FPS reporting


    /** True if current touch stroke is a movement, not a click */
    private boolean isMovement;

    /** Maximum movement distance for a click event, squared */
    private static final float MAX_CLICK_EVENT_DISTANCE_SQ = 100;

    private Game game;

    // DEBUG INFO
    private long lastFrameStartedAt;
    private boolean framePending;

    // java == wtf
    private final Runnable processFrameRunnable = new Runnable() {
        public void run() {
            processFrame();
        }
    };

    public GameView(Context context) {
        super(context);
        game = new Game(context);

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
                    requestFrame();
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

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        requestFrame();
    }

    private void requestFrame() {
        if(!framePending) {
            framePending = true;
            long now = System.currentTimeMillis();
            postDelayed(processFrameRunnable, Math.max(0, 16 - (now - lastFrameStartedAt)));
        }
    }

    private void processFrame() {
        framePending = false;
        long frameTime = System.currentTimeMillis() - lastFrameStartedAt;
        lastFrameTime = frameTime;
        lastFrameStartedAt = System.currentTimeMillis();

        game.update(Math.min(frameTime, 16));
        invalidate();

        if(game.needsNextFrame()) {
            requestFrame();
        }
    }

    private void setCameraOffset(float x, float y) {
        // TODO refactor
        Point lastTile = TileMath.tileLocation(game.getWidth(), game.getHeight());
        cameraOffset.x = Math.max(Math.min(x, -TILE_WIDTH/4), -lastTile.x + getWidth());
        cameraOffset.y = Math.max(Math.min(y, -TILE_HEIGHT/2), -lastTile.y + TILE_HEIGHT/2 + getHeight());
    }

    private void tileClicked(Point tilePos) {
        game.tileSelected(tilePos);

        requestFrame();
    }

    /** Squared magnitude of a two-dimensional vector (x, y) */
    private static float mag2(float x, float y) {
        return x*x + y*y;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        {
            game.draw(canvas, cameraOffset, visibleArea(), lastFrameTime);
        }
        canvas.restore();

    }

    private Rect visibleArea() {
        Rect visableAreaInPixels = new Rect(
                (int) -cameraOffset.x,
                (int) -cameraOffset.y,
                (int) -cameraOffset.x + getWidth(),
                (int) -cameraOffset.y + getHeight()
        );

        return TileMath.visibleTiles(visableAreaInPixels);
    }
}
