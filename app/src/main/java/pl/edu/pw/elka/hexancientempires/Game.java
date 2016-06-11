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
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_HEIGHT;
import static pl.edu.pw.elka.hexancientempires.TileMath.TILE_WIDTH;

/**
 * Handles some game logic and other stuff
 */
public class Game {
    private static final long ANIMATION_TIME = 150;

    /** Special value for nextFrameDelay. Render next frame as fast as possible (considering rate limiting) */
    public static final long FRAME_IMMEDIATELY = 0;

    /** Special value for nextFrameDelay. Wait for event before rendering next frame. */
    public static final long FRAME_WAIT_FOR_EVENT = -1;

    private static final int SERVER_PLAYER_ID = 1;
    private static final int CLIENT_PLAYER_ID = 2;

    private final Connection connection;

    private final int myPlayerID;

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

    /**
     * Handles user's selection of a tile.
     */
    public void tileSelected(final Point newCursorPos) {
        if(unitAnimation.isRunning()) {
            unitAnimation.stop();
        }

        if(processAction(myPlayerID, cursorPos, newCursorPos)) {
            sendAction(cursorPos, newCursorPos);
        }

        cursorPos = newCursorPos;

        updateTileState();
    }

    private void updateTileState() {
        Unit unit = map.getTile(cursorPos).getUnit();

        if(isMyTurn() && unit != null) {

            movementRange = gameLogic.canMove(unit) ?
                new UnitMovementRange(map).getReachableTiles(unit, cursorPos) :
                Collections.<Point, UnitMovementRange.Node>emptyMap();

            attackRange = gameLogic.canAttack(unit) ?
                new UnitAttackRange(map).getReachableTiles(unit, cursorPos) :
                Collections.<Point, UnitAttackRange.Node>emptyMap();

        } else {
            movementRange = Collections.emptyMap();
            attackRange = Collections.emptyMap();
        }
    }

    /**
     * Sends action to GameLogic and displays the result in the UI.
     *
     * @return whether any action happened.
     */
    private boolean processAction(int playerID, final Point from, final Point to) {
        if(playerID != gameLogic.playerID) {
            return false;
        }

        return gameLogic.action(from, to, new GameLogic.ActionListener<Boolean>() {
            public Boolean moved(Unit unit, List<Point> path) {
                message.show(unit + " moved to " + path.get(path.size() - 1), 3000);
                unitAnimation.start(unit,
                    ANIMATION_TIME, 
                    Utils.pathToPixels(path)
                );
                return true;
            }

            public Boolean attacked(Unit attacker, Unit attacked, int range) {
                message.show(attacker + " attacked " +attacked, 3000);
                return true;
            }

            public Boolean noAction() {
                return false;
            }
        });
    }

    private void sendAction(Point from, Point to) {
        connection.sendEvent(new Event.Action(from, to));
    }

    /**
     * Creates a Game.
     *
     * @param context Context to load assets from
     * @param connection Connection to talk to
     */
    public Game(Context context, Connection connection) {
        this.connection = connection;
        this.myPlayerID = connection.isServer()? SERVER_PLAYER_ID: CLIENT_PLAYER_ID;

        Log.d("Game", "myPlayerId = " + myPlayerID);

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

        placeUnit(new Unit(Unit.ARCHER, SERVER_PLAYER_ID, new Point(2, 7)));
        placeUnit(new Unit(Unit.WARRIOR, SERVER_PLAYER_ID, new Point(3, 7)));
        placeUnit(new Unit(Unit.GOLEM, SERVER_PLAYER_ID, new Point(4, 7)));
        placeUnit(new Unit(Unit.WOLF, SERVER_PLAYER_ID, new Point(2, 8)));
        placeUnit(new Unit(Unit.CATAPULT, SERVER_PLAYER_ID, new Point(4, 8)));


        placeUnit(new Unit(Unit.ARCHER, CLIENT_PLAYER_ID, new Point(10, 7)));
        placeUnit(new Unit(Unit.WARRIOR, CLIENT_PLAYER_ID, new Point(11, 7)));
        placeUnit(new Unit(Unit.GOLEM, CLIENT_PLAYER_ID, new Point(9, 7)));
        placeUnit(new Unit(Unit.WOLF, CLIENT_PLAYER_ID, new Point(11, 8)));
        placeUnit(new Unit(Unit.CATAPULT, CLIENT_PLAYER_ID, new Point(9, 8)));


        startTurn(SERVER_PLAYER_ID);
    }

    private void placeUnit(Unit unit) {
        map.getTile(unit.getLoc()).setUnit(unit);
        units.add(unit);
    }

    private void startTurn(int playerID) {
        Log.d("Game", "nextTurn, player=" + playerID);
        gameLogic = new GameLogic(units, playerID, map);
    }

    /**
     * Handles user's click of "Finish turn" button.
     */
    public void finishTurnClicked() {
        if(isMyTurn()) {
            finishTurn();
            connection.sendEvent(new Event.FinishTurn());
        }
        updateTileState();
    }

    private void finishTurn() {
        startTurn(oppositePlayerID(gameLogic.playerID));
    }

    private boolean isMyTurn() {
        return gameLogic.playerID == myPlayerID;
    }

    private static int oppositePlayerID(int playerID) {
        return playerID == CLIENT_PLAYER_ID? SERVER_PLAYER_ID: CLIENT_PLAYER_ID;
    }

    private void drawCursor(Canvas canvas, Point center) {
        final int width = TILE_WIDTH + 10, height = TILE_HEIGHT + 10;
        final int anchorX = width/2, anchorY = height/2;

        int x = center.x - anchorX, y = center.y - anchorY;

        cursor.setBounds(x, y, x + width, y + height);
        cursor.draw(canvas);
    }


    /**
     * Renders the game.
     *
     * @param canvas Canvas to draw on
     * @param cameraOffset camera offset
     * @param visibleArea visible area of the map (in tile coordinates)
     * @param screenWidth screen width in pixels
     * @param screenHeight screen height in pixels
     */
    public void draw(Canvas canvas, PointF cameraOffset, Rect visibleArea, int screenWidth, int screenHeight) {
        canvas.save();
        {
            canvas.translate(cameraOffset.x, cameraOffset.y);

            drawMap(canvas, visibleArea);

            drawUnits(canvas);

            drawMovementRange(canvas);

            drawAttackRange(canvas);

            drawCursor(canvas, TileMath.tileCenter(cursorPos.x, cursorPos.y));
        }
        canvas.restore();

        if(message.isDisplaying()) {
            drawMessage(canvas, message.getText(), screenWidth, screenHeight, 120);
        }
        
        String turnMessage = isMyTurn() ?
            (gameLogic.isTurnFinished() ? "No moves left in this turn" : "Your turn") :
            "Waiting for Player " + oppositePlayerID(myPlayerID);
        drawMessage(canvas, turnMessage, screenWidth, screenHeight, 30);
    }

    private void drawMessage(Canvas canvas, String text, int screenWidth, int screenHeight, int bottom) {
        final int margin = 30;
        final int textSize = 40;
        final int padding = 30;

        Paint paint = new Paint();
        paint.setColor(0x80000000);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(margin, screenHeight - bottom - textSize - padding, screenWidth - margin, screenHeight - bottom, paint);

        paint.setColor(0xffffffff);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.MONOSPACE);
        canvas.drawText(text, screenWidth / 2, screenHeight - bottom - padding + 5, paint);
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

    /**
     * Updates the game's animations.
     *
     * @param frameTime time elapsed since last update
     */
    public void update(long frameTime) {
        unitAnimation.update(frameTime);
        message.update(frameTime);
    }

    /**
     * Returns desired time to next frame.
     *
     * This can be a time value in milliseconds or one of the special values (FRAME_IMMEDIATELY, FRAME_WAIT_FOR_EVENT).
     */
    public long nextFrameDelay() {
        if(unitAnimation.isRunning()) {
            return FRAME_IMMEDIATELY;
        } else if(message.isDisplaying()) {
            return message.getTimeLeft();
        } else {
            return FRAME_WAIT_FOR_EVENT;
        }
    }

    /**
     * Handles event coming from network connection.
     */
    public void eventReceived(Event event) {
        event.accept(new Event.Visitor<Void>() {
            public Void action(Event.Action event) {
                processAction(oppositePlayerID(myPlayerID), event.from, event.to);
                return null;
            }

            public Void finishTurn(Event.FinishTurn event) {
                if(!isMyTurn()) {
                    Game.this.finishTurn();
                }
                return null;
            }
        });
        updateTileState();
    }

    /**
     * @return local player ID
     */
    public int getMyPlayerID() {
        return myPlayerID;
    }
}
