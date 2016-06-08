package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.List;

/**
 * to rename and refactor
 * Created by tomek on 08.06.16.
 */
public class GameLogic {

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

    public GameLogic(List<Unit> units) {
        for(Unit unit :units){
          //  unitInfos.add(new UnitInfo(unit));
        }

    }
}
