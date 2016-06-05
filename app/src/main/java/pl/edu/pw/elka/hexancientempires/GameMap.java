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
    private ArrayList<Tile> tiles;
    public final int mapWidth;
    public final int mapHeight;

    public static final String MAP1 = "11 11" +
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

    public GameMap(int width, int height, ArrayList<Tile> tiles) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.tiles = tiles;
    }

    /** Returns a mutable reference to tile at (mapX, mapY). */
    public Tile getTile(int mapX, int mapY){
        if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight ) {
            return new Tile(Tile.NONE);
        }
        return tiles.get(mapY * mapWidth +  mapX);
    }

    public Tile getTile(Point loc){
        return getTile(loc.x, loc.y);
    }

    public Tile getTile(int index) {
        if(index < 0 || index >= tiles.size())
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
        if(index < 0 || index >= tiles.size())
            return 0; //TODO throw impossible exception
        return  index;
    }

    public Point getMapLoc(int i) {
        return new Point(i % mapWidth, i / mapWidth);
    }

    public static GameMap loadFromString(String source) {
        String separators = "[ ]+";
        String[] tokens = source.split(separators);

        int mapWidth  = Integer.parseInt(tokens[0]);
        int mapHeight = Integer.parseInt(tokens[1]);
        int mapSize = mapHeight * mapWidth;

        ArrayList<Tile> tiles = new ArrayList<>( mapSize );
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
                    // FIXME handle syntax errors
                    break;
            }
        }

        return new GameMap(mapWidth, mapHeight, tiles);
    }
}
