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

import java.util.ArrayList;
import java.util.Collections;

import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_HEIGHT;
import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_WIDTH;

/**
 * Handles some game logic and other stuff
 * moreover is cool named, so no worries m8s
 * Side note ♪ we should have one for user input something like Controller
 * Created by tomek on 05.06.16.
 */
public class Game {

    private GameMap map;
    private ArrayList<Drawable> terrain;
    private ArrayList<Bitmap> units;
    private Drawable cursor;

    Paint paint = new Paint();

    //Debug Info
    private int numTilesRendered;

    private Point cursorPos = new Point(0, 0);

    //----------
    private ArrayList<UnitRangeBFS.Node> displayedRange;// = Collections.emptyList();
    ArrayList<UnitAttackRange.Node> atakRange;
    ArrayList<UnitRangeBFS.Node> moveRange;
    private boolean rangeOn;

    public void tileSelected(Point tilePos) {
        //TODO process some other things than unit movement
        if(isInRange(tilePos) && rangeOn){
            map.move(getPath(tilePos) );
            rangeOn = false;
        }
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
//        displayedRange = new UnitRangeBFS(map).getReachableTiles(new Point (1,1));
//        game.setMoveRange(displayedRange);


 //       this.map = map;
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

        for(UnitRangeBFS.Node node: displayedRange) {
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

            if (tile.unit != null) {
                Unit unit = tile.unit;
                Bitmap bitmap = units.get(unit.playerID - 1);
                int unitWidth = bitmap.getWidth() / 12, unitHeight = bitmap.getHeight() / 2;
                android.graphics.Rect areaToCrop = new android.graphics.Rect(unit.type * unitWidth, 0, (unit.type + 1) * unitWidth, unitHeight);
                RectF areaToDraw = new RectF(0, 0, TILE_WIDTH, TILE_HEIGHT);

                canvas.drawBitmap(bitmap, areaToCrop, areaToDraw, null);
            }
        }
        canvas.restore();
    }



    public void setMoveRange(ArrayList<UnitRangeBFS.Node> moveRange) {
        this.rangeOn = true;
        this.moveRange = moveRange;


    }


    public ArrayList<Point> getPath(Point current){
        ArrayList<Point> way = new ArrayList<>();

        while (current!= null){
            way.add(current);
            current = moveRange.get(getIndex(moveRange,current)).parent;
        }

        Collections.reverse(way);
        return way;
    }

    private int getIndex(ArrayList<UnitRangeBFS.Node> moveRange,Point loc){
        for(int i = 0; i < moveRange.size();i++ ){
            if(moveRange.get(i).loc == loc)
                return i;
        }
        throw new IllegalArgumentException("move range path is broken");
    }

    private boolean isInRange(Point tilePos){
        for(int i = 0; i < moveRange.size();i++)
            if(moveRange.get(i).loc == tilePos)
                return true;
        return false;
    }

    public int getWidth() {
        return map.getWidth();
    }

    public int getHeight() {
        return map.getHeight();
    }
}
