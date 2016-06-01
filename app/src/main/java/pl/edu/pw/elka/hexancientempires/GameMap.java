package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that represents board
 * Created by tomek on 29.04.16.
 * I have no idea what I am doing lol
 */
public class GameMap {
    ArrayList<Tile> tiles;
    int mapWidth ;
    int mapHeight ;
    private static final String savedmap = "10 10" +
            " g g g g g g g g g g" +
            " g r r r r r r r r g" +
            " g r g g g g g g r g" +
            " g r g g g g g g r g" +
            " g r g w w w w g r g" +
            " g r g g w w g g r g" +
            " g r g g w w g g r g" +
            " g c g t m m t g c g" +
            " g g t t m m t t g g" +
            " g g t t m m t t g g";

    /*context.getResources().getString(R.string.map1);
*/
    public GameMap(){
        String separators = "[ ]+";
        String[] tokens = savedmap.split(separators);

        mapWidth  = 10;//Integer.parseInt(tokens[0]);
        mapHeight = 10;// Integer.parseInt(tokens[1]);

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
    }

    public int getType(int mapX, int mapY){
        if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >=mapHeight ) {
            return Tile.NONE;
        }
        return tiles.get(mapY * mapWidth +  mapX).type;
    }
}
