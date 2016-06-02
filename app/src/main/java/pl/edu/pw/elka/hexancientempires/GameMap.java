package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that represents board
 * loads map from string
 * Created by tomek on 29.04.16.
 */
public class GameMap {
    ArrayList<Tile> tiles;
    int mapWidth ;
    int mapHeight ;
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

    /*context.getResources().getString(R.string.map1);
*/
    public GameMap(){
        String separators = "[ ]+";
        String[] tokens = savedmap.split(separators);

        mapWidth  = Integer.parseInt(tokens[0]);
        mapHeight = Integer.parseInt(tokens[1]);

        tiles = new ArrayList<>( mapHeight * mapWidth );
        for (int i = 2; i < (mapHeight * mapWidth + 2); i++) {
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
        tiles.get(1).unit = new Unit(0,0);
    }

    public Tile getTile(int mapX, int mapY){
        if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight ) {
            return new Tile(Tile.NONE);
        }
        return new Tile (tiles.get(mapY * mapWidth +  mapX));
    }
}
