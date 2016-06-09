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

    private final Connection connection;

    private GameMap map;
    private ArrayList<Drawable> terrain;
    private ArrayList<Bitmap> unitTextures;
    private Drawable cursor;
    private GameLogic gameLogic;

    private List<Unit> units = new ArrayList<>();

    private Point cursorPos = new Point(0, 0);

    //----------
    private Map<Point, UnitMovementRange.Node> movementRange = Collections.emptyMap();
    private Map<Point, UnitAttackRange.Node> attackRange = Collections.emptyMap();

    private Animation unitAnimation = new Animation();
    private Message message = new Message();

    public void tileSelected(final Point newCursorPos) {
        if(unitAnimation.isRunning()) {
            unitAnimation.stop();
        }

        if(processAction(cursorPos, newCursorPos)) {
            sendAction(cursorPos, newCursorPos);
        }

        cursorPos = newCursorPos;

        Unit unit = map.getTile(cursorPos).getUnit();

        if(unit != null) {
            movementRange = new UnitMovementRange(map).getReachableTiles(unit, cursorPos);
            attackRange = new  UnitAttackRange(map).getReachableTiles(unit, cursorPos);
        } else {
            movementRange = Collections.emptyMap();
            attackRange = Collections.emptyMap();
        }
    }

    /**
     * Returns whether an action happened.
     */
    private boolean processAction(final Point from, final Point to) {
        return gameLogic.action(from, to, new GameLogic.ActionListener<Boolean>() {
            public Boolean moved(Unit unit, List<Point> path) {
                unitAnimation.start(unit,
                    ANIMATION_TIME, 
                    Utils.pathToPixels(path)
                );
                return true;
            }
            public Boolean attacked(Unit attacker, Unit attacked, int range){
                return true;
            }

            public Boolean noAction() {
                // TODO maybe show information about terrain?
                message.show("Selected tile " + to, 3000);
                return false;
            }
        });
    }

    private void sendAction(Point from, Point to) {
        connection.sendEvent(new Event.Action(from, to));
    }

    public Game(Context context, Connection connection) {
        this.connection = connection;

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

        unitTextures = new ArrayList<>(3);
        unitTextures.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units1));
        unitTextures.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units2));
        unitTextures.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.units3));

        cursor = context.getResources().getDrawable(R.drawable.cursor);

        map = GameMap.loadFromString(GameMap.MAP1);

        int playerID = 1;
        Point p = new Point (1,1);
        Unit u = new Unit(1,playerID,p);
        map.getTile(p).setUnit(u);
        units.add(u);

        int playerID_2 = 2;
        Point p2 = new Point (2,2);
        Unit u2 = new Unit(1,playerID_2,p2);
        map.getTile(p2).setUnit(u2);
        units.add(u2);


        gameLogic = new GameLogic(units,playerID,map);
    }



    private void drawCursor(Canvas canvas, Point center) {
        final int width = TILE_WIDTH + 10, height = TILE_HEIGHT + 10;
        final int anchorX = width/2, anchorY = height/2;

        int x = center.x - anchorX, y = center.y - anchorY;

        cursor.setBounds(x, y, x + width, y + height);
        cursor.draw(canvas);
    }



    public void draw(Canvas canvas, PointF cameraOffset, Rect visibleArea, int screenWidth, int screenHeight) {
        canvas.save();
        {
            canvas.translate(cameraOffset.x, cameraOffset.y);

            drawMap(canvas, visibleArea);

            drawMovementRange(canvas);

            drawAttackRange(canvas);

            drawUnits(canvas);

            drawCursor(canvas, TileMath.tileCenter(cursorPos.x, cursorPos.y));
        }
        canvas.restore();

        drawMessage(canvas, screenWidth, screenHeight);
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
        for(int x = visibleArea.left; x <= visibleArea.right; x++) {
            for(int y = visibleArea.top; y <= visibleArea.bottom; y++) {
                drawTile(canvas, x, y);
            }
        }
    }

    private void drawMovementRange(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0x800000ff);
        paint.setStyle(Paint.Style.FILL);

        for(UnitMovementRange.Node node: movementRange.values()) {
            Point loc = TileMath.tileLocation(node.loc.x, node.loc.y);

            canvas.translate(loc.x, loc.y);
            canvas.drawPath(tilePath, paint);
            canvas.translate(-loc.x, -loc.y);
        }
    }

    private void drawAttackRange(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0x80ff0000);
        paint.setStyle(Paint.Style.FILL);

        for(UnitAttackRange.Node node: attackRange.values()) {
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

        canvas.save();
        {
            canvas.translate(loc.x, loc.y);
            terrain.get(tile.getType()).draw(canvas);
        }
        canvas.restore();
    }

    private void drawUnits(Canvas canvas) {
        //TODO make this for cool
        for(Unit unit :units){
            PointF loc =
                unitAnimation.isAnimating(unit) ?
                    unitAnimation.getCurrentPoint() :
                    Utils.toPointF(TileMath.tileLocation(unit.getLoc().x, unit.getLoc().y));

            drawUnit(canvas, unit, loc.x, loc.y);
        }

    }

    private void drawUnit(Canvas canvas, Unit unit, float x, float y) {
        Bitmap bitmap = unitTextures.get(unit.getPlayerID() - 1);
        int unitWidth = bitmap.getWidth() / 12, unitHeight = bitmap.getHeight() / 2;
        android.graphics.Rect areaToCrop = new android.graphics.Rect(unit.getType() * unitWidth, 0, (unit.getType() + 1) * unitWidth, unitHeight);
        RectF areaToDraw = new RectF(x, y, x + TILE_WIDTH, y + TILE_HEIGHT);

        canvas.drawBitmap(bitmap, areaToCrop, areaToDraw, null);
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

    public void eventReceived(Event event) {
        event.accept(new Event.Visitor<Void>() {
            public Void action(Event.Action event) {
                processAction(event.from, event.to);
                return null;
            }
        });
    }
}
