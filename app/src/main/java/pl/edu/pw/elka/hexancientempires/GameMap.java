package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;


/**
 * Class that represents board.
 */
public class GameMap {
    private ArrayList<Tile> tiles;
    private final int mapWidth;
    private final int mapHeight;

    public static final String MAP1 = "13 13 " +
            "x x x x x x x x x x x x x " +
            "x g g g g g g g g g g g x " +
            "x g r r r r r r r r r g x " +
            "x g r g g g g g g g r g x " +
            "x g r g w g g g w g r g x " +
            "x g r g g w w w g g r g x " +
            "x g r g t g w g t g r g x " +
            "x g r g g w m w g g r g x " +
            "x g c g g m m m g g c g x " +
            "x g g g t m m m t g g g x " +
            "x g g t t m m m t t g g x " +
            "x t g g g t t t g g g t x " +
            "x x x x x x x x x x x x x ";

    /**
     * Constructs a GameMap
     *
     * @param width width
     * @param height height
     * @param tiles List of tiles (of length width * height), in column-major order.
     */
    public GameMap(int width, int height, ArrayList<Tile> tiles) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.tiles = tiles;
    }

    /** @return a mutable reference to tile at (mapX, mapY). */
    public Tile getTile(int mapX, int mapY){
        if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight ) {
            return new Tile(Tile.NONE);
        }
        return tiles.get(mapY * mapWidth +  mapX);
    }

    /** @return a mutable reference to tile at loc. */
    public Tile getTile(Point loc){
        return getTile(loc.x, loc.y);
    }

    /** @return a mutable reference to tile at given index in column-major order. */
    public Tile getTile(int index) {
        if(index < 0 || index >= tiles.size())
            throw new IndexOutOfBoundsException("Tile of this index don't exists in map");
         return tiles.get(index);
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

    /** @return index of given point in column-major order */
    public int getMapIndex(Point loc)
    {
        int index = loc.y * mapWidth + loc.x;
        if(index < 0 || index >= tiles.size())
            throw new  IndexOutOfBoundsException("Tile of this location don't exists in map");
        return  index;
    }

    /** @return true if given point is in range of the map */
    public boolean containsTile(Point loc) {
        return loc.x >= 0 && loc.y >= 0 && loc.x < mapWidth && loc.y < mapHeight;
    }

    /** @return location of ith point in column-major order */
    public Point getMapLoc(int i) {
        return new Point(i % mapWidth, i / mapWidth);
    }

    /**
     * Parses a Map from textual representation.
     *
     * Map format is like {@link #MAP1}.
     */
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
                    break;
            }
        }

        return new GameMap(mapWidth, mapHeight, tiles);
    }
}
