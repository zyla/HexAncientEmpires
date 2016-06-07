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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import pl.edu.pw.elka.hexancientempires.UnitRangeBFS.Node;

import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_HEIGHT;
import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_WIDTH;

/**
 * Handles some game logic and other stuff
 * moreover is cool named, so no worries m8s
 * Side note ♪ we should have one for user input something like Controller
 * Created by tomek on 05.06.16.
 */
public class Game {
    private static final long ANIMATION_TIME = 300;

    private GameMap map;
    private ArrayList<Drawable> terrain;
    private ArrayList<Bitmap> units;
    private Drawable cursor;

    Paint paint = new Paint();

    //Debug Info
    private int numTilesRendered;

    private Point cursorPos = new Point(0, 0);

    //----------
    private Map<Point, UnitRangeBFS.Node> displayedRange = Collections.emptyMap();
    ArrayList<UnitAttackRange.Node> atakRange;
    ArrayList<UnitRangeBFS.Node> moveRange;

    private Animation unitAnimation = new Animation();

    public void tileSelected(Point tilePos) {
        Unit unit = map.getTile(cursorPos).unit;
        if(unit != null && isInRange(tilePos)) {
            unitAnimation.start(unit,
                ANIMATION_TIME, 
                toPointF(TileMath.tileLocation(cursorPos)),
                toPointF(TileMath.tileLocation(tilePos))
            );
            moveUnit(unit, cursorPos, tilePos);
        }

        cursorPos = tilePos;

        unit = map.getTile(cursorPos).unit;

        if(unit != null) {
            displayedRange = new UnitRangeBFS(map).getReachableTiles(cursorPos);
        } else {
            displayedRange = Collections.emptyMap();
        }
    }

    // TODO move to utils
    private static PointF toPointF(Point p) {
        return new PointF(p.x, p.y);
    }

    private void moveUnit(Unit unit, Point from, Point to) {
        map.getTile(from).unit = null;
        map.getTile(to).unit = unit;
    }

    public Game(Context context) {
        terrain = new ArrayList<>(7);
        terrain.add(context.getResources().getDrawable(R.drawable.l));
        terrain.add(context.getResources().getDrawable(R.drawable.c));
        terrain.add(context.getResources().getDrawable(R.drawable.g));
        terrain.add(context.getResources().getDrawable(R.drawable.m));
        terrain.add(context.getResources().getDrawable(R.drawable.r));
        terrain.add(context.getResources().getDrawable(R.drawable.t));
        terrain.add(context.getResources().getDrawable(R.drawable.w));

        for(int i = 0 ; i < terrain.size(); i++) {
            terrain.get(i).setBounds(0, 0, TileMath.TILE_WIDTH, TileMath.TILE_HEIGHT);
        }

        units = new ArrayList<>(3);
        units.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units1));
        units.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units2));
        units.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units3));

        cursor = context.getResources().getDrawable(R.drawable.cursor);

        map = GameMap.loadFromString(GameMap.MAP1);

        map.getTile(1, 1).unit = new Unit(1,1);
    }



    private void drawCursor(Canvas canvas, Point center) {
        final int width = TILE_WIDTH + 10, height = TILE_HEIGHT + 10;
        final int anchorX = width/2, anchorY = height/2;

        int x = center.x - anchorX, y = center.y - anchorY;

        cursor.setBounds(x, y, x + width, y + height);
        cursor.draw(canvas);
    }



    public void draw(Canvas canvas, PointF cameraOffset, Rect visibleArea, long lastFrameTime) {
        numTilesRendered = 0;

        canvas.translate(cameraOffset.x, cameraOffset.y);
        drawMap(canvas, visibleArea);

        drawCursor(canvas, TileMath.tileCenter(cursorPos.x, cursorPos.y));
        drawDebugInfo(canvas,lastFrameTime);

        drawMovingUnit(canvas);
    }

    private void drawMovingUnit(Canvas canvas) {
        if(unitAnimation.isRunning()) {
            drawUnit(canvas,
                unitAnimation.getUnit(),
                unitAnimation.getCurrentX(),
                unitAnimation.getCurrentY()
            );
        }
    }

    private void drawDebugInfo(Canvas canvas,long lastFrameTime) {
        Paint paint = new Paint();
        paint.setColor(0x80000000);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(getWidth() - 200, 0, getWidth(), 80, paint);

        paint.setColor(0xffffffff);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.MONOSPACE);
        canvas.drawText(String.format("tiles: %3d", numTilesRendered), getWidth(), 30, paint);
        canvas.drawText(String.format("fps: %3d", 1000/Math.max(lastFrameTime, 1)), getWidth(), 60, paint);
    }


    private void drawMap(Canvas canvas, Rect visibleArea) {
        Rect visibleTiles = visibleArea;
        for(int x = visibleTiles.left; x <= visibleTiles.right; x++) {
            for(int y = visibleTiles.top; y <= visibleTiles.bottom; y++) {
                drawTile(canvas, x, y);
            }
        }

        drawRange(canvas);
    }

    private void drawRange(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0x80ff0000);
        paint.setStyle(Paint.Style.FILL);

        for(UnitRangeBFS.Node node: displayedRange.values()) {
            Point loc = TileMath.tileLocation(node.loc.x, node.loc.y);

            canvas.translate(loc.x, loc.y);
            canvas.drawPath(tilePath, paint);
            canvas.translate(-loc.x, -loc.y);
        }
    }


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
        Tile tile = map.getTile(mapX,mapY);

        numTilesRendered++;

        canvas.save();
        {
            canvas.translate(loc.x, loc.y);
            terrain.get(tile.type).draw(canvas);

            Unit unit = tile.unit;
            if (unit != null
                    && !unitAnimation.isAnimating(unit)) { // moving unit is drawn separately
                drawUnit(canvas, unit, 0, 0);
            }
        }
        canvas.restore();
    }

    private void drawUnit(Canvas canvas, Unit unit, float x, float y) {
        Bitmap bitmap = units.get(unit.playerID - 1);
        int unitWidth = bitmap.getWidth() / 12, unitHeight = bitmap.getHeight() / 2;
        android.graphics.Rect areaToCrop = new android.graphics.Rect(unit.type * unitWidth, 0, (unit.type + 1) * unitWidth, unitHeight);
        RectF areaToDraw = new RectF(x, y, x + TILE_WIDTH, y + TILE_HEIGHT);

        canvas.drawBitmap(bitmap, areaToCrop, areaToDraw, null);
    }

    public ArrayList<Point> getPath(Point current) {
        ArrayList<Point> way = new ArrayList<>();

        while (current != null) {
            way.add(current);
            current = displayedRange.get(current).parent;
        }

        Collections.reverse(way);
        return way;
    }

    private boolean isInRange(Point tilePos) {
        return displayedRange.containsKey(tilePos);
    }

    public int getWidth() {
        return map.getWidth();
    }

    public int getHeight() {
        return map.getHeight();
    }

    public void update(long frameTime) {
        unitAnimation.update(frameTime);
    }

    public boolean needsNextFrame() {
        return unitAnimation.isRunning();
    }
}
