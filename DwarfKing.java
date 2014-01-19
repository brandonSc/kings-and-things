package KAT;
//
// DwarfKing.java
// kingsandthings/
// @author Brandon Schurman
//

public class DwarfKing extends Creature 
{
    /**
     * CTOR
     */ 
    public DwarfKing(){
        super("path/to/front.png", "path/to/back.png", "Dwarf King", 
                "any", 5, false, false, false, false);
        setType("SpecialCharacter");
    }

    /**
     * Dwarf King doubles income from mines. 
     * Figure out how we will do this later...
     */
}
