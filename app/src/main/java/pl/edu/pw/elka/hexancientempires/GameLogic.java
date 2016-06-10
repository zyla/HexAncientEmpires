package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents state of a game turn.
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

    /**
     * @param units all units in the game
     * @param playerID ID of player whose turn it is
     * @param map the map
     */
    public GameLogic(List<Unit> units, int playerID, GameMap map) {
        this.units = units;
        unitInfos = new HashMap<>();
        for(Unit unit :units){
            unitInfos.put(unit.getLoc(),new UnitInfo(unit));
        }
        this.playerID = playerID;
        this.map = map;
    }

    private boolean attack(Point attacking, Point defensing,int distance){
        Tile attacker = map.getTile(attacking);
        Tile attacked = map.getTile(defensing);

        if(attacker.getUnit() == null
                || !canAttack(attacker.getUnit())
                || attacked.getUnit() == null
                || attacked.getUnit().getPlayerID() == playerID)
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

    private boolean move(Point from, Point to) {
        Tile tileFrom = map.getTile(from);
        Tile tileTo = map.getTile(to);

        if (tileFrom.getUnit() == null || !canMove(tileFrom.getUnit())
                || tileTo.getUnit() != null)
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

    private static List<Point> getPath(Map<Point, UnitMovementRange.Node> range, Point current) {
        List<Point> way = new ArrayList<>();

        while (current != null) {
            way.add(current);
            current = range.get(current).parent;
        }

        Collections.reverse(way);
        return way;
    }

    /** @return true if the unit can be moved in this turn */
    public boolean canMove(Unit unit) {
        return unit.getPlayerID() == playerID && !unitInfos.get(unit.getLoc()).moved;
    }

    /** @return true if the unit can attack in this turn */
    public boolean canAttack(Unit unit) {
        return unit.getPlayerID() == playerID && !unitInfos.get(unit.getLoc()).attacked;
    }

    /** @return true if no moves are left. */
    public boolean isTurnFinished() {
        for(UnitInfo info: unitInfos.values()) {
            if(info.unit.getPlayerID() == playerID && (!info.attacked || !info.moved)) {
                return false;
            }
        }

        return true;
    }
}
