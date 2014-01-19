package KAT;
// 
// Marksman.java
// kingsandthings/
// @author Brandon Schurman
//

public class Marksman extends Creature
{
    private static int combatValue2 = 2;
    
    public Marksman(){
        super("path/to/front.png", "path/to/back.png", "Marksman", 
                "any", 5, false, false, false, false);
        setType("SpecialCharacter");
    }
    
    /**
     * The Marksman can use a lower combat value
     * with the advantage of choosing which enemy creature to attack.
     * This method may need to be tweaked depending on how 
     * the game controller works
     */
    public int getOtherCombatValue(){
        return combatValue2;
    }
}
