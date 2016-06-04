package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.LinkedList;

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
            this.cost = node.cost;
        }
    }
    private ArrayList< Node> getGraph(){
        int size = gameMap.getSize();
        int W = gameMap.getWidth();

        ArrayList<Node> graph = new ArrayList<>(size);
        for(int i  = 0; i < size; i++ ) {
            Point loc  = new Point (i % W, i/W);
            graph.add(i, new UnitRangeBFS.Node(gameMap.getTile(loc),loc));
        }
        return graph;
    }

    public ArrayList<Node> getReachableTiles(Point origin) {
        Tile tile  = gameMap.getTile(origin.x,origin.y);
        if(tile.type == Tile.NONE || tile.unit == null)
            throw new IllegalArgumentException("tile not good for BFS");

        ArrayList<Node> graph = getGraph();
        ArrayList<Node> visited = new ArrayList<>();
        LinkedList<Node> queue = new LinkedList<>();
        int range = UnitMath.unitSpeed[tile.unit.type];

        int originIndex = origin.y * gameMap.mapWidth +origin.x;
        graph.set(originIndex,new Node(graph.get(originIndex),0));

        queue.add(graph.get(originIndex));

        while(!queue.isEmpty())
        {
            Node current = queue.remove();
            for(int i = 0; i < 6 ; i++)
            {
                Point mateLoc = TileMath.neighbour(current.loc,i);
                Node mate = graph.get(mateLoc.y * gameMap.getWidth() + mateLoc.x);
                if(mate.allowed == false)
                    continue;
                if( current.distance + mate.cost < mate.distance ) {
                    mate.distance = current.distance + mate.cost;
                    mate.parent = current.loc;
                    graph.set(mateLoc.y * gameMap.getWidth() + mateLoc.x,mate);
                    if (mate.distance < range)
                        queue.add(mate);
                }
            }
        }

        for(int i = 0; i < graph.size(); i++){
            if(graph.get(i).distance <= range)
                visited.add(graph.get(i));
        }

        return visited;
    }
}