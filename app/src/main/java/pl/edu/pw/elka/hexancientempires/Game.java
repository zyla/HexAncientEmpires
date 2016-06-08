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

import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_HEIGHT;
import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_WIDTH;

/**
 * Handles some game logic and other stuff
 * moreover is cool named, so no worries m8s
 * Side note ♪ we should have one for user input something like Controller
 * Created by tomek on 05.06.16.
 */
public class Game {
    private static final long ANIMATION_TIME = 150;

    /** Special value for nextFrameDelay. Render next frame as fast as possible (considering rate limiting) */
    public static final long FRAME_IMMEDIATELY = 0;

    /** Special value for nextFrameDelay. Wait for event before rendering next frame. */
    public static final long FRAME_WAIT_FOR_EVENT = -1;

    private GameMap map;
    private ArrayList<Drawable> terrain;
    private ArrayList<Bitmap> unitsTextures;
    private Drawable cursor;

    //TODO store units here and draw frm here
    private List<Unit> units = new ArrayList<>();

    Paint paint = new Paint();

    private Point cursorPos = new Point(0, 0);

    //----------
    private Map<Point, UnitRangeBFS.Node> displayedRange = Collections.emptyMap();
    ArrayList<UnitAttackRange.Node> atakRange;
    ArrayList<UnitRangeBFS.Node> moveRange;

    private Animation unitAnimation = new Animation();
    private Message message = new Message();

    public void tileSelected(Point tilePos) {
        if(unitAnimation.isRunning()) {
            unitAnimation.stop();
        }

        Unit unit = map.getTile(cursorPos).unit;
        if(unit != null && isInRange(tilePos)) {
            unitAnimation.start(unit,
                ANIMATION_TIME, 
                pathToPixels(getPath(tilePos))
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

        message.show("Selected tile " + tilePos, 3000);
    }

    // TODO move to utils
    private static PointF toPointF(Point p) {
        return new PointF(p.x, p.y);
    }

    private static List<PointF> pathToPixels(Iterable<Point> xs) {
        ArrayList<PointF> result = new ArrayList<>();
        for(Point x: xs) {
            result.add(toPointF(TileMath.tileLocation(x)));
        }
        return result;
    }

    private void moveUnit(Unit unit, Point from, Point to) {
        map.getTile(from).unit = null;
        unit.loc = to;
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

        unitsTextures = new ArrayList<>(3);
        unitsTextures.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units1));
        unitsTextures.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units2));
        unitsTextures.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units3));

        cursor = context.getResources().getDrawable(R.drawable.cursor);

        map = GameMap.loadFromString(GameMap.MAP1);

        Point p =new Point (1,1);
        Unit u = new Unit(1,1,p);
        map.getTile(p).unit = u;
        units.add(u);
    }



    private void drawCursor(Canvas canvas, Point center) {
        final int width = TILE_WIDTH + 10, height = TILE_HEIGHT + 10;
        final int anchorX = width/2, anchorY = height/2;

        int x = center.x - anchorX, y = center.y - anchorY;

        cursor.setBounds(x, y, x + width, y + height);
        cursor.draw(canvas);
    }



    public void draw(Canvas canvas, PointF cameraOffset, Rect visibleArea) {
       canvas.save();
        {
        canvas.translate(cameraOffset.x, cameraOffset.y);
        drawMap(canvas, visibleArea);
        drawUnits(canvas);

            drawCursor(canvas, TileMath.tileCenter(cursorPos.x, cursorPos.y));

            drawMovingUnit(canvas);
        }
        canvas.restore();

        drawMessage(canvas, screenWidth, screenHeight);
    }

    private void drawMovingUnit(Canvas canvas) {
        if(unitAnimation.isRunning()) {
            PointF loc = unitAnimation.getCurrentPoint();
            drawUnit(canvas, unitAnimation.getUnit(), loc.x, loc.y);
        }
    }

    private void drawMessage(Canvas canvas, int screenWidth, int screenHeight) {
        if(message.isDisplaying()) {
            final int margin = 30;
            final int textSize = 40;
            final int padding = 30;

            Paint paint = new Paint();
            paint.setColor(0x80000000);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(margin, screenHeight - margin - textSize - padding, screenWidth - margin, screenHeight - margin, paint);

            paint.setColor(0xffffffff);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.MONOSPACE);
            canvas.drawText(message.getText(), screenWidth / 2, screenHeight - margin - padding, paint);
        }
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


    private final Path tilePath drawPath()    {
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

        canvas.save();
        {
            canvas.translate(loc.x, loc.y);
            terrain.get(tile.type).draw(canvas);

            /*Unit unit = tile.unit;
            if (unit != null
                    && !unitAnimation.isAnimating(unit)) { // moving unit is drawn separately
                drawUnit(canvas, unit, 0, 0);
            */

        }
        canvas.restore();
    }

    private void drawUnits(Canvas canvas) {
        //TODO make this for cool
        for(Unit unit :units){
            canvas.save();
            {
                Point loc = TileMath.tileLocation(unit.loc.x, unit.loc.y);
                canvas.translate(loc.x, loc.y);
                if (!unitAnimation.isAnimating(unit)) { // moving unit is drawn separately
                    drawUnit(canvas, unit, 0, 0);
                }
            }
            canvas.restore();
        }

    }



    private void drawUnit(Canvas canvas, Unit unit, float x, float y) {
        Bitmap bitmap = unitsTextures.get(unit.playerID - 1);
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
        long animationFrameTime = Math.min(frameTime, 16); // TODO fix this hack

        unitAnimation.update(animationFrameTime);

        message.update(frameTime);
    }

    public long nextFrameDelay() {
        if(unitAnimation.isRunning()) {
            return FRAME_IMMEDIATELY;
        } else if(message.isDisplaying()) {
            return message.getTimeLeft();
        } else {
            return FRAME_WAIT_FOR_EVENT;
        }
    }
}
