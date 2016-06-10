package pl.edu.pw.elka.hexancientempires;

/**
 * this class is whole about fighting
 * but fighting is highly dependant on tile that unit is standing at
 * so more of a tile.unit fighting
 * Created by tomek on 02.06.16.
 */
public class UnitMath {
    public static final int STARTING_HP = 100;
    public static final int ATTACKED_DIE = -1;//no need if attack removes units
    public static final int ATTACKING_DIE = 1;
    public static final int NO_ONE_DIE = 0;


    public static final int[] unitDamage = {
        50,//WARRIOR
        50,//ARCHER
        50,//LIZARD
        50,//WIZARD
        50,//WISP
        50,//WOLF
        80,//GOLEM
        90,//CATAPULT
        50,//DRAGON
        50,//KING
        50//SKELETON
};

    public static final int[] unitRange = {
        1,//WARRIOR
        2,//ARCHER
        1,//LIZARD
        1,//WIZARD
        2,//WISP
        1,//WOLF
        1,//GOLEM
        5,//CATAPULT
        2,//DRAGON
        1,//KING
        1 //SKELETON
};

    public static final int[] unitSpeed = {
        4,//WARRIOR
        4,//ARCHER
        3,//LIZARD
        2,//WIZARD
        4,//WISP
        7,//WOLF
        3,//GOLEM
        2,//CATAPULT
        8,//DRAGON
        4,//KING
        4//SKELETON
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
    public static int attack(Tile attacking, Tile attacked, int distance){
        int attackValue1 = unitDamage[attacking.getUnit().getType()];
        int attackedHp = attacked.getUnit().getHp();
        int attackedDef = tileDefense[attacked.getType()];

        if(attackedHp <= attackValue1 - attackedDef)
            return ATTACKED_DIE;
        attacked.getUnit().setHp( attackedHp + attackedDef - attackValue1);

        if(distance > unitRange[attacked.getUnit().getType()])
            return NO_ONE_DIE;

        int attackingHp = attacking.getUnit().getHp();
        int attackValue2 = unitDamage[attacked.getUnit().getType()];
        int attackingDef = tileDefense[attacking.getType()];

        if(attackingHp <= attackValue2 - attackingDef)
            return ATTACKING_DIE;

        attacking.getUnit().setHp( attackingHp + attackingDef - attackValue2);
        return NO_ONE_DIE;
    }
}
