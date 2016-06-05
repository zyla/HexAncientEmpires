package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.LinkedList;

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
        boolean allowed;
        int distance;

        public Node(Tile tile, Point loc,int playerID) {
            this.loc = loc;
            distance = Integer.MAX_VALUE;
            if (tile.unit != null && tile.type != Tile.NONE && playerID != tile.unit.playerID) {
                allowed = true;
            } else {
                allowed = false;
            }
        }
   }

    private ArrayList< Node> getGraph(int playerID){
        int size = gameMap.getSize();

        ArrayList<Node> graph = new ArrayList<>(size);
        for(int i  = 0; i < size; i++ ) {
            graph.add(i, new UnitAttackRange.Node(gameMap.getTile(i),gameMap.getMapLoc(i),playerID));
        }
        return graph;
    }

    public ArrayList<Node> getReachableTiles(Point origin) {
        Tile tile  = gameMap.getTile(origin);
        if(tile.type == Tile.NONE || tile.unit == null)
            throw new IllegalArgumentException("tile not good for BFS");

        int range = UnitMath.unitRange[tile.unit.type];
        ArrayList<Node> graph = getGraph(tile.unit.playerID);

        ArrayList<Node> inRange = new ArrayList<>();
        LinkedList<Node> queue = new LinkedList<>();

        queue.add(graph.get(gameMap.getMapIndex(origin)));

        while(!queue.isEmpty())
        {
            Node current = queue.remove();
            inRange.add(current);
            for(int i = 0; i < 6 ; i++)
            {
                Point mateLoc = TileMath.neighbour(current.loc,i);
                Node mate = graph.get(gameMap.getMapIndex(mateLoc));
                if(mate.allowed == false)
                    continue;
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
