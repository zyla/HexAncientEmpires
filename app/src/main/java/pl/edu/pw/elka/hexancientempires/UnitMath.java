package pl.edu.pw.elka.hexancientempires;

/**
 * this class is whole about fighting
 * but fighting is highly dependant on tile that unit is standing at
 * so more of a tile.unit fighting
 * Created by tomek on 02.06.16.
 */
public class UnitMath {
    public static final int STARTING_HP = 100;
    public static final int ATACKED_DIE = -1;//no need if attack removes units
    public static final int ATACKING_DIE = 1;
    public static final int NOONE_DIE = 0;


    public static final int[] unitDamage = {
        50,//WARRIOR
        50,//ARCHER
        50,//WATERELEM
        50,//WIZZARD
        50,//WISP
        50,//WOLF
        50,//GOLEM
        50,//CATEPULT
        50,//DREGON
        50,//SKELETON
        50 //KING
    };

    public static final int[] unitRange = {
        1,//WARRIOR
        2,//ARCHER
        1,//WATERELEM
        1,//WIZZARD
        2,//WISP
        1,//WOLF
        1,//GOLEM
        5,//CATEPULT
        2,//DREGON
        1,//SKELETON
        1 //KING
    };

    public static final int[] unitSpeed = {
        4,//WARRIOR
        4,//ARCHER
        3,//WATERELEM
        2,//WIZZARD
        4,//WISP
        7,//WOLF
        3,//GOLEM
        2,//CATEPULT
        8,//DREGON
        4,//SKELETON
        4 //KING
    };

    public static final int[] tileDefense = {
            0,//NONE
            15,//CASTLE
            5,//GRASS
            15,//MOUNTAIN
            0,//ROAD
            10,//TREE
            0//WATER
    };

    //strange naming for value of speed that you have to pay to get on the tile
    public static final int[] tileDistance = {
            Integer.MAX_VALUE,//NONE
            3,//CASTLE
            2,//GRASS
            3,//MOUNTAIN
            1,//ROAD
            2,//TREE
            3//WATER
    };

    //I have to change value of hp in one unit, but idk if possible in java
    public int attack(Tile attacking, Tile attacked, int distance){
        int attackValue1 = unitDamage[attacking.unit.type];
        int attackedHp = attacked.unit.hp;
        int attackedDef = tileDefense[attacked.type];

        if(attackedHp <= attackValue1 - attackedDef)
            return ATACKED_DIE;
        attacked.unit.hp = attackedHp + attackedDef - attackValue1;

        if(distance > unitRange[attacked.unit.type])
            return NOONE_DIE;

        int attackingHp = attacking.unit.hp;
        int attackValue2 = unitDamage[attacked.unit.type];
        int attackingDef = tileDefense[attacking.type];

        if(attackingHp <= attackValue2 - attackingDef)
            return ATACKING_DIE;

        attacking.unit.hp = attackingHp + attackingDef - attackValue2;
        return NOONE_DIE;
    }
}
