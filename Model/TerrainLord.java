// 
// TerrainLord.java
// kingsandthings/Model/
// @author Brandon Schurman
//

public class TerrainLord extends Creature
{
    /**
     * CTOR
     */
    public TerrainLord( String terrainType ){
        super("", "", "", terrainType, 4, false, false, false, false);
        
        String name = "";
        String front = ""; // TODO set these to the correct
        String back = "";  // image path in switch below

        switch( terrainType ){
            case "DESERT":
                name = "Desert Master";
                break;
            case "FOREST":
                name = "Forest King";
                break;
            case "ICE":
                name = "Ice Lord";
                break;
            case "JUNGLE":
                name = "Jungle Lord";
                break;
            case "MOUNTAIN":
                name = "Mountain King";
                break;
            case "PLAINS":
                name = "Plains Lord";
                break;
            case "SWAMP":
                name = "Swamp King";
                break;
            default:
                System.err.println("TerrainLord error: Unrecognized terrainType");
                break;
        }
        setType("TerrainLord");
        setName(name);
        setFront(front);
        setBack(back);
    }
}
