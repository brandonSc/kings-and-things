/*
 * The Creature class inherits from the Piece class
 */
public class Creature extends Piece {
	private String 	name;
	private String 	terrainType;
	private int    	combatValue;
	private boolean flying;
	private boolean magic;
	private boolean charge;
	private boolean ranged;
	private boolean special;

	/*
	 * Default Constructor
	 */
	public Creature() {
		super();
		this.name = "";
		this.terrainType = "";
		this.combatValue = 0;
		this.flying = false;
		this.magic = false;
		this.charge = false;
		this.ranged = false;
		this.special = false;
	}

	/* 
	 * Additional constructor
	 */
	public Creature(String t, String f, String b, String n, String tT, int cV, boolean fL, boolean m, boolean c, boolean r, boolean s) {
		super(t, f, b);
		this.name = n;
		this.terrainType = tT;
		this.combatValue = cV;
		this.flying = fL;
		this.magic = m;
		this.charge = c;
		this.ranged = r;
		this.special = s;
	}

	/* 
	 * Get/Set methods
	 */
	public void setName(String s) { name = s; }
	public void setTerrainType(String s) { terrainType = s; }
	public void setCombatValue(int i) { combatValue = i; }
	public void setFlying(boolean b) { flying = b; }
	public void setMagic(boolean b) { magic = b; }
	public void setCharge(boolean b) { charge = b; }
	public void setRanged(boolean b) { ranged = b; }
	public void setSpecial(boolean b) { special = b; }

	public String getName() { return name; }
	public String getTerrainType() { return terrainType; }
	public int getCombatValue() { return combatValue; }
	public boolean getFlying() { return flying; }
	public boolean getMagic() { return magic; }
	public boolean getCharge() { return charge; }
	public boolean getRanged() { return ranged; }
	public boolean getSpecial() { return special; }
}