package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Handles some game logic and other stuff
 * moreover is cool named, so no worries m8s
 * Side note ♪ we should have one for user input something like Controller
 * Created by tomek on 05.06.16.
 */
public class Game {
    ArrayList<UnitAttackRange.Node> atakRange;
    ArrayList<UnitRangeBFS.Node> moveRange;

    GameMap map;
    private boolean rangeOn;

    public Game(GameMap map) {
        this.map = map;
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
    public void doSmth(Point tilePos) {
        if(isInRange(tilePos) && rangeOn){
            map.move(getPath(tilePos) );
            rangeOn = false;
        }
    }
}
