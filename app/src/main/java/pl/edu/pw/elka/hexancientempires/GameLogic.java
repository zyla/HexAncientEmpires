package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
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

    public class UnitInfo{
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
            unitInfos.put(unit.loc,new UnitInfo(unit));
        }
        this.playerID = playerID;
        this.map = map;
    }

    public void Attack(Point attacking, Point defensing,int distance){
        Tile attacker = map.getTile(attacking);
        Tile attacked = map.getTile(defensing);

        if(unitInfos.get(attacker.unit.loc).unit.playerID !=playerID
                || unitInfos.get(attacked.unit.loc).unit.playerID == playerID)
            return;

        int result = UnitMath.attack(attacker,attacked,distance);
        if(result == UnitMath.NO_ONE_DIE)
            return;
        if(result == UnitMath.ATTACKED_DIE) {
            units.remove(attacked.unit);
            unitInfos.remove(attacked.unit.loc);
            attacked.unit = null;
        }else{//result == UnitMath.ATTACKING_DIE
            units.remove(attacker.unit);
            unitInfos.remove(attacker.unit.loc);
            attacker.unit = null;
        }

    }
}
