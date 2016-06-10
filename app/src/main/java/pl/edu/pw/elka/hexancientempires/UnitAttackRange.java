package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by tomek on 05.06.16.
 */
public class UnitAttackRange {
    GameMap gameMap;

    public UnitAttackRange(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public class Node {
        Point loc;      //location of node in the map
        int distance;
        int playerID;

        public Node(Tile tile, Point loc) {
            this.loc = loc;
            distance = Integer.MAX_VALUE;
            if(tile.getUnit() != null)
                this.playerID = tile.getUnit().getPlayerID();
            else playerID = 0;
        }
   }

    private ArrayList< Node> getGraph(){
        int size = gameMap.getSize();

        ArrayList<Node> graph = new ArrayList<>(size);
        for(int i  = 0; i < size; i++ ) {
            graph.add(i, new UnitAttackRange.Node(gameMap.getTile(i),gameMap.getMapLoc(i)));
        }
        return graph;
    }

    public Map<Point, Node> getReachableTiles(Unit unit, Point origin) {
        int range = UnitMath.unitRange[unit.getType()];
        ArrayList<Node> graph = getGraph();

        Map<Point,Node> inRange = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();

        queue.add(graph.get(gameMap.getMapIndex(origin)));

        while(!queue.isEmpty())
        {
            Node current = queue.remove();
            if(current.playerID != 0 && current.playerID != unit.getPlayerID())
                inRange.put(current.loc,current);
            for(int i = 0; i < 6 ; i++)
            {
                Point mateLoc = TileMath.neighbour(current.loc,i);

                if(!gameMap.containsTile(mateLoc))
                  continue;
                
                Node mate = graph.get(gameMap.getMapIndex(mateLoc));
                if( mate.distance == Integer.MAX_VALUE) {
                    mate.distance = current.distance + 1;
                    graph.set(gameMap.getMapIndex(mateLoc),mate);
                    if (mate.distance < range)
                        queue.add(mate);
                }
            }
        }
        return inRange;
    }
}
