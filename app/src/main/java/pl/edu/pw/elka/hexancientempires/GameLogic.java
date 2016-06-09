package pl.edu.pw.elka.hexancientempires;

import java.util.HashMap;
import java.util.List;

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

}
