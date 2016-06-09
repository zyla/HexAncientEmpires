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

    public ArrayList<Node> getReachableTiles(Point origin) {
        Tile tile  = gameMap.getTile(origin);
        if(tile.getType() == Tile.NONE || tile.getUnit()== null)
            throw new IllegalArgumentException("tile not good for BFS");

        int range = UnitMath.unitRange[tile.getUnit().getType()];
        ArrayList<Node> graph = getGraph();

        ArrayList<Node> inRange = new ArrayList<>();
        LinkedList<Node> queue = new LinkedList<>();

        queue.add(graph.get(gameMap.getMapIndex(origin)));

        while(!queue.isEmpty())
        {
            Node current = queue.remove();
            if(current.playerID != 0 && current.playerID != tile.getUnit().getPlayerID())
                inRange.add(current);
            for(int i = 0; i < 6 ; i++)
            {
                Point mateLoc = TileMath.neighbour(current.loc,i);
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
