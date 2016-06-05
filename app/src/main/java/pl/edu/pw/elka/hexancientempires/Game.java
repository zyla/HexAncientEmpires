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

    public ArrayList<Point> getPath(ArrayList<UnitRangeBFS.Node> moveRange,Point current){
        ArrayList<Point> way = new ArrayList<>();
        way.add(current);

        while (current!= null){
            current = moveRange.get(getIndex(moveRange,current)).parent;
            way.add(current);
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
}
