//
// Deerhunter.java
// kingsandthings/Model/
// @author Brandon Schurman
//

public class Deerhunter extends Creature 
{
    /**
     * CTOR
     */
    public Deerhunter(){ 
        super("path/to/front.png", "path/to/back.png", "Deerhunter", 
                "any", 4, false, false, false, false);
        setType("SpecialCharacter");
    }
    
    /**
     * The DeerHunter treats every hex as a movement one.
     * maybe need a special case for this character during movement?
     */
}
