package KAT;
// 
// GrandDuke.java
// kingsandthings/
// @author Brandon Schurman
// 
import java.util.ArrayList;

public class GrandDuke extends SpecialCharacter 
{
    /**
     * CTOR
     */
    public GrandDuke(){
        super("path/to/front.png", "path/to/back.png", "Grand Duke", 
                4, false, false, false, false);
    }

    /**
     * The GrandDuke inflicts one damage to each fort before battle.
     * @param forts - a set of all forts contained on a hex (datatype may change)
     */
    void attack( ArrayList<Fort> forts ){
        for( Fort f : forts ){
            f.inflict();
        }
    }
}
