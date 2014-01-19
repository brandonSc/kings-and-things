package KAT;
/*
 * The Creature class inherits from the Piece class
 */
public class Creature extends Piece implements Combatable 
{
	private String 	name;
	private String 	terrainType;
	private int    	combatValue;
	private boolean flying;
	private boolean magic;
	private boolean charging;
	private boolean ranged;

	/**
	 * Constructor
	 */
	public Creature( String front, String back, String name, 
            String terrainType, int combatValue, boolean flying, 
            boolean magic, boolean charging, boolean ranged ){
		super("Creature", front, back);
		this.name = name;
		this.terrainType = terrainType;
		this.combatValue = combatValue;
		this.flying = flying;
		this.magic = magic;
		this.charging = charging;
		this.ranged = ranged;
	}

    /**
     * Call when this creature is hit during combat
     */ 
    public void inflict(){
        TheCup.getInstance().addToCup(this); // return to cup
        // should remove this creature from the hex and player's posetion
    }

	/* 
	 * Get/Set methods
	 */
	public void setName(String s) { name = s; }
	public void setTerrainType(String s) { terrainType = s; }
	public void setCombatValue(int i) { combatValue = i; }
	public void setFlying(boolean b) { flying = b; }
	public void setMagic(boolean b) { magic = b; }
	public void setCharging(boolean b) { charging = b; }
	public void setRanged(boolean b) { ranged = b; }

	public String getName() { return name; }
	public String getTerrainType() { return terrainType; }
	public int getCombatValue() { return combatValue; }
	public boolean isFlying() { return flying; }
	public boolean isMagic() { return magic; }
	public boolean isCharging() { return charging; }
	public boolean isRanged() { return ranged; }
}
