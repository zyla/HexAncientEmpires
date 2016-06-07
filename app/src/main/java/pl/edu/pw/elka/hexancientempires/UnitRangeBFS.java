package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * After all this thinking I decided to not mess up GameMap too much
 * 1. I will copy some map info to my graph structure
 * 2. Determine using BFS WHERE and HOW unit can get to certain position
 * 3. Return that info to map so it can share that with display
 * 4. ? ? ?
 * 5. Profit
 *
 * Side note ♪ it would be cool if not whole map was being copied, but who cares
 * Created by tomek on 03.06.16.
 * You know mate.
 */
public class UnitRangeBFS {
    GameMap gameMap;

    public UnitRangeBFS(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public class Node{
        Point loc;      //location of node in the map
        int distance;   //distance measured with game speed currency
        Point parent;    // how we get there
        boolean allowed;
        int cost;

        public Node(Tile tile,Point loc) {
            this.loc = loc;
            this.distance = Integer.MAX_VALUE;
            this.cost = UnitMath.tileDistance[tile.type];
            if(tile.unit != null || tile.type == Tile.NONE) {
                allowed = false;
            }
            else {
                allowed = true;
            }
            parent = null;
        }

        public Node(Node node,int distance) {
            this.parent = null;
            this.allowed = true;
            this.loc = node.loc;
            this.distance = distance;
            this.cost = 0;//node.cost;
        }
    }
    private ArrayList< Node> getGraph(){
        int size = gameMap.getSize();

        ArrayList<Node> graph = new ArrayList<>(size);
        for(int i  = 0; i < size; i++ ) {
            graph.add(i, new UnitRangeBFS.Node(gameMap.getTile(i),gameMap.getMapLoc(i)));
        }
        return graph;
    }

    public Map<Point, Node> getReachableTiles(Point origin) {
        Tile tile  = gameMap.getTile(origin);
        if(tile.type == Tile.NONE || tile.unit == null)
            throw new IllegalArgumentException("tile not good for BFS");

        ArrayList<Node> graph = getGraph();
        Map<Point, Node> visited = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();
        int range = UnitMath.unitSpeed[tile.unit.type];

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
                if(mate.allowed == false)
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
