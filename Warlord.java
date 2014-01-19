//
// Warlord.java
// kingsandthings/
// @author Brandon Schurman
//

public class Warlord extends Creature
{
    /**
     * CTOR
     */
    public Warlord(){
        super("path/to/front.png", "path/to/back.png", "Warlord", 
                "any", 5, false, false, false, false);
        setType("SpecialCharacter"); 
    }
    
    /**
     * The Warlord can get one ememy creature per battle 
     * to join his side (before combat).
     */
}
