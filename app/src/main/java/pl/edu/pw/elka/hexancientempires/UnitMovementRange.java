package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class that provides methods capable to compute movement range of certain unit
 * Uses Dijkstra's algorithm because cost of tiles differ
 */
public class UnitMovementRange {
    GameMap gameMap;

    public UnitMovementRange(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    /**
     * Nested class used to describe graph of the map for the algorithm in simple way
     */
    public class Node{
        Point loc;      //location of node in the map
        int distance;   //distance measured with game speed currency
        Point parent;    // how we get there
        boolean allowed;
        int cost;

        public Node(Tile tile,Point loc) {
            this.loc = loc;
            this.distance = Integer.MAX_VALUE;
            this.cost = UnitMath.tileDistance[tile.getType()];
            allowed = !(tile.getUnit() != null || tile.getType() == Tile.NONE);
            parent = null;
        }

        public Node(Node node,int distance) {
            this.parent = null;
            this.allowed = true;
            this.loc = node.loc;
            this.distance = distance;
            this.cost = node.cost;
        }
    }

    /**
     * Creates structure of the graph
     */
    private ArrayList< Node> getGraph(){
        int size = gameMap.getSize();

        ArrayList<Node> graph = new ArrayList<>(size);
        for(int i  = 0; i < size; i++ ) {
            graph.add(i, new UnitMovementRange.Node(gameMap.getTile(i),gameMap.getMapLoc(i)));
        }
        return graph;
    }

    /**
     * Returns information where and how certain unit can get from certain position based on map topography
     * @param unit
     * @param origin
     * @return visited
     */
    public Map<Point, Node> getReachableTiles(Unit unit, Point origin) {
        ArrayList<Node> graph = getGraph();
        Map<Point, Node> visited = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();
        int range = UnitMath.unitSpeed[unit.getType()];

        int originIndex = gameMap.getMapIndex(origin);
        graph.set(originIndex,new Node(graph.get(originIndex),0));

        queue.add(graph.get(originIndex));

        while(!queue.isEmpty())
        {
            Node current = queue.remove();
            for(int i = 0; i < 6 ; i++)
            {
                Point mateLoc = TileMath.neighbour(current.loc,i);
                Node mate = graph.get(gameMap.getMapIndex(mateLoc));
                if(!mate.allowed)
                    continue;
                if( current.distance + mate.cost < mate.distance ) {
                    mate.distance = current.distance + mate.cost;
                    mate.parent = current.loc;
                    graph.set(gameMap.getMapIndex(mateLoc),mate);
                    if (mate.distance < range)
                        queue.add(mate);
                }
            }
        }

        for(Node node: graph) {
            if(node.distance <= range)
                visited.put(node.loc, node);
        }

        return visited;
    }
}
