package pl.edu.pw.elka.hexancientempires;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * "please no copypasterino from stackoverflowerino"
 *              some Twitch.tv streamer
 * "Created" by tomek on 03.06.16.
 */
public class BFS {
    GameMap gameMap;
    LinkedList <Node> visited;
    int range;

    class Node{
        Point loc;      //location of node in the map
        int distance;   //
        Node parent;

        public Node(Point loc, int distance, Node parent) {
            this.loc = loc;
            this.distance = distance;
            this.parent = parent;
        }
    }

    public BFS(GameMap gameMap) {
        this.gameMap = gameMap;

    }

    public LinkedList<Node> getReachableTiles(Point origin) {
        //if something wrong with point abort mission
        Tile tile  = gameMap.getTile(origin.x,origin.y);
        if(tile.type == Tile.NONE || tile.unit == null)
            throw new IllegalArgumentException("tile not good for BFS");

        LinkedList<Node> checked = new LinkedList<>();

        gameMap.clearBFSdistances();
        int range = UnitMath.unitSpeed[tile.unit.type];
        this.visited = new LinkedList<>();
        visited.add(new Node(origin, 0, null));
        gameMap.setBFSdistance(origin,0 );

        while(!visited.isEmpty())
        {
            Node current = visited.remove();

            for(int i = 0; i < 6 ; i++){
                Point p;
                Tile t= gameMap.getTile(p = TileMath.neighbour(current.loc.x,current.loc.y,i));
                if(t.distanceBFS == Integer.MAX_VALUE) {
                    gameMap.setBFSdistance(p, current.distance + UnitMath.tileDistance[t.type]);

                }
            }
             checked.add(current);
        }
    return checked;
    }
}