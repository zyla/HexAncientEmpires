package pl.edu.pw.elka.hexancientempires;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;
/**
 * Class that represents board
 * Created by tomek on 29.04.16.
 * I have no idea what I am doing lol
 */
public class GameMap {
    //TODO we should read file with map
    ArrayList<Tile> tiles;
    int mapWidth;
    int mapHeight;

    public GameMap(){
        //reading from some file here to make map
        int mapWidth  = 2;
        int mapHeight = 2;
        tiles = new ArrayList<>(mapWidth * mapHeight);
        /*and now tiles*/
        for(int i = 0;i < 4; i++)
            tiles.add(new Tile(Tile.GRASS));
    }

    public int getType(int mapX, int mapY){
        if((mapY * mapWidth +  mapX) < 0 || (mapY * mapWidth +  mapX) > (mapWidth* mapHeight)) {
            return Tile.NONE;
        }
        return tiles.get(mapY * mapWidth +  mapX).type;
    }
}
