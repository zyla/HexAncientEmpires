package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * to rename and refactor
 * Created by tomek on 08.06.16.
 */
public class GameLogic {
    List<Unit> units;
    HashMap<Point, UnitInfo> unitInfos;
    int playerID;
    GameMap map;

    public class UnitInfo {
        boolean moved;
        boolean attacked;
        Unit unit;

        public UnitInfo(Unit unit) {
            this.moved = false;
            this.attacked = false;
            this.unit = unit;
        }
    }

    public GameLogic(List<Unit> units, int playerID, GameMap map) {
        this.units = units;
        unitInfos = new HashMap<>();
        for(Unit unit :units){
            unitInfos.put(unit.getLoc(),new UnitInfo(unit));
        }
        this.playerID = playerID;
        this.map = map;
    }

    public boolean attack(Point attacking, Point defensing,int distance){
        Tile attacker = map.getTile(attacking);
        Tile attacked = map.getTile(defensing);

        if(attacker.getUnit() == null
                || attacked.getUnit() == null
                || attacker.getUnit().getPlayerID() != playerID
                || attacked.getUnit().getPlayerID()== playerID
                || unitInfos.get(attacked.getUnit().getLoc()).attacked)
            return false;

        unitInfos.get(attacking).attacked = true;

        int result = UnitMath.attack(attacker,attacked,distance);
        if(result == UnitMath.NO_ONE_DIE)
            return true;
        if(result == UnitMath.ATTACKED_DIE) {
            units.remove(attacked.getUnit());
            unitInfos.remove(attacked.getUnit().getLoc());
            attacked.setUnit(null);
        }else{//result == UnitMath.ATTACKING_DIE
            units.remove(attacker.getUnit());
            unitInfos.remove(attacker.getUnit().getLoc());
            attacker.setUnit(null);
        }
        return true;
    }

    public boolean move(Point from, Point to) {
        Tile tileFrom = map.getTile(from);
        Tile tileTo = map.getTile(to);

        if (tileFrom.getUnit() == null
                || tileTo.getUnit() != null
                || tileFrom.getUnit().getPlayerID() != playerID
                || unitInfos.get(tileFrom.getUnit().getLoc()).moved)
            return false;

        UnitInfo unitInfo = unitInfos.get(from);
        unitInfo.moved = true;
        unitInfos.remove(from);
        unitInfos.put(to, unitInfo);

        map.getTile(to).setUnit( tileFrom.getUnit());
        map.getTile(from).setUnit(null);

        tileTo.getUnit().setLoc(to);

        return true;
    }

    public static interface ActionListener<R> {
        /** The given Unit was moved along the specified path. */
        public R moved(Unit unit, List<Point> path);

        /** First given unit attacked second unit from range */
        public R attacked(Unit attacker, Unit attacked,int range);

        /** Nothing happened (no unit on source or target tile, or some other condition) */
        public R noAction();

        // TODO possibly make this more detailed (add explanations why an action can't be performed, etc.)
    }

    /**
     * Executes a suitable action (move or attack) depending on contents of the
     * source and the target tile.
     *
     * This method is responsible for checking move/attack ranges and computing
     * movement path (returned as argument to ActionListener.moved).
     *
     * Calls one of the methods of listener.
     *
     * @param from Tile that is currently selected
     * @param to Tile that was clicked
     * @param listener Listener that will be notified what happened
     */
    public <R> R action(Point from, Point to, ActionListener<R> listener) {
        Unit unitFrom = map.getTile(from).getUnit();

        if(unitFrom == null) {
            return listener.noAction();
        }

        Unit unitTo = map.getTile(to).getUnit();
        Map<Point, UnitMovementRange.Node> movementRange = new UnitMovementRange(map).getReachableTiles(unitFrom, from);
        Map<Point, UnitAttackRange.Node> attackRange = new UnitAttackRange(map).getReachableTiles(unitFrom, from);

        if (isInMovementRange(movementRange, to) && move(from, to)) {
            return listener.moved(unitFrom, getPath(movementRange, to));
        } else if (isInAttackRange(attackRange, to) && attack(from, to, getAttackDistance(attackRange, to))) {
            return listener.attacked(unitFrom, unitTo, getAttackDistance(attackRange, to));
        }

        return listener.noAction();
    }

    private boolean isInMovementRange(Map<Point, UnitMovementRange.Node> range, Point tilePos) {
        return range.containsKey(tilePos);
    }

    private boolean isInAttackRange(Map<Point,UnitAttackRange.Node> range, Point tilePos) {
        return range.containsKey(tilePos);
    }

    private int getAttackDistance(Map<Point,UnitAttackRange.Node> range, Point tilePos) {
        return range.get(tilePos).distance;
    }

    public static List<Point> getPath(Map<Point, UnitMovementRange.Node> range, Point current) {
        List<Point> way = new ArrayList<>();

        while (current != null) {
            way.add(current);
            current = range.get(current).parent;
        }

        Collections.reverse(way);
        return way;
    }

    /**
     * True if all possible moves in current turn are used.
     */
    public boolean isTurnFinished() {
        // TODO implement
        return false;
    }
}
