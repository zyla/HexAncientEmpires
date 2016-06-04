package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

import static pl.edu.pw.elka.hexancientempires.UnitRangeBFS.*;


/**
 * Class that represents board
 * loads map from string
 * Created by tomek on 29.04.16.
 */
public class GameMap {
    ArrayList<Tile> tiles;
    int mapWidth ;
    int mapHeight ;
    int mapSize;
    ArrayList<UnitRangeBFS.Node> currentlyDisplying;
    private static final String savedmap = "11 11" +
            " g g g g g g g g g g g" +
            " g r r r r r r r r r g" +
            " g r g g g g g g g r g" +
            " g r g w g g g w g r g" +
            " g r g g w w w g g r g" +
            " g r g t g w g t g r g" +
            " g r g g w m w g g r g" +
            " g c g g m m m g g c g" +
            " g g g t m m m t g g g" +
            " g g t t m m m t t g g" +
            " t g g g t t t g g g t";

    public GameMap(){
        String separators = "[ ]+";
        String[] tokens = savedmap.split(separators);

        mapWidth  = Integer.parseInt(tokens[0]);
        mapHeight = Integer.parseInt(tokens[1]);
        mapSize = mapHeight * mapWidth;

        tiles = new ArrayList<>( mapSize );
        for (int i = 2; i < (mapSize + 2); i++) {
            switch (tokens[i]) {
                case "c": tiles.add(new Tile(Tile.CASTLE));
                    break;
                case "g": tiles.add(new Tile(Tile.GRASS));
                    break;
                case "m": tiles.add(new Tile(Tile.MOUNTAIN));
                    break;
                case "r": tiles.add(new Tile(Tile.ROAD));
                    break;
                case "t": tiles.add(new Tile(Tile.TREE));
                    break;
                case "w": tiles.add(new Tile(Tile.WATER));
                    break;
                default: tiles.add(new Tile(Tile.NONE));
                    break;
            }
        }
        tiles.set(1,new Tile(tiles.get(1),new Unit(1,1)));
        UnitRangeBFS bfs = new UnitRangeBFS(this);
        this.setRangeDisplay(bfs.getReachableTiles(new Point (1,0)));
       // tiles.set(1,new Tile(tiles.get(1),true,null));
    }

    public Tile getTile(int mapX, int mapY){
        if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight ) {
            return new Tile(Tile.NONE);
        }
        return new Tile (tiles.get(mapY * mapWidth +  mapX));
    }
    public Tile getTile(Point loc){
        if(loc.x < 0 || loc.y < 0 || loc.x >= mapWidth || loc.y >= mapHeight ) {
            return new Tile(Tile.NONE);
        }
        return new Tile (tiles.get(loc.y * mapWidth +  loc.x));
    }

    public Tile getTile(int index){
        if(index < 0 || index >= mapSize)
            return new Tile(Tile.NONE);//TODO should throw exception
        return new Tile (tiles.get(index));
    }

    public int getSize() {
        return mapHeight * mapWidth;
    }

    public int getWidth() {
        return mapWidth;
    }

    public int getHeight() {
        return mapHeight;
    }

    public int getMapIndex(Point loc)
    {
        int index = loc.y * mapWidth + loc.x;
        if(index < 0 || index >= mapSize)
            return 0; //TODO throw impossible exception
        return  index;
    }

    public void setRangeDisplay(ArrayList<UnitRangeBFS.Node> inRange ) {
        currentlyDisplying = inRange;
        for(int i = 0 ; i < inRange.size(); i++){
            int mapIndex = getMapIndex(inRange.get(i).loc);
            Point previous = inRange.get(i).parent;
            tiles.set(mapIndex, new Tile(tiles.get(mapIndex),true,previous));
        }
        return;
    }

    public void clearRangeDisplay() {
        if(currentlyDisplying == null)
            return;
        currentlyDisplying = null;
        for(int i = 0; i < currentlyDisplying.size(); i++){
            int mapIndex = getMapIndex(currentlyDisplying.get(i).loc);
            tiles.set(mapIndex, new Tile(tiles.get(mapIndex),false,null));
        }
    }

    public Point getMapLoc(int i) {
        return new Point(i % mapWidth, i / mapWidth);
    }
}
