package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class that provides methods capable to compute attack range of certain unit
 * Uses BFS algorithm because id don't takes obstacles in account
 */
public class UnitAttackRange {
    GameMap gameMap;

    public UnitAttackRange(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    /**
     * Nested class used to describe graph of the map for the algorithm in simple way
     */
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

    /**
     * Creates structure of the graph
     */
    private ArrayList< Node> getGraph(){
        int size = gameMap.getSize();

        ArrayList<Node> graph = new ArrayList<>(size);
        for(int i  = 0; i < size; i++ ) {
            graph.add(i, new UnitAttackRange.Node(gameMap.getTile(i),gameMap.getMapLoc(i)));
        }
        return graph;
    }

    /**
     * Returns information where certain unit can attack from certain position based on map topography
     * @param unit
     * @param origin
     * @return inRange
     */
    public Map<Point, Node> getReachableTiles(Unit unit, Point origin) {
        int range = UnitMath.unitRange[unit.getType()];
        ArrayList<Node> graph = getGraph();

        Map<Point,Node> inRange = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();

        Node originNode = graph.get(gameMap.getMapIndex(origin));
        originNode.distance = 0;
        queue.add(originNode);

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
                    if (mate.distance <= range)
                        queue.add(mate);
                }
            }
        }
        return inRange;
    }
}
