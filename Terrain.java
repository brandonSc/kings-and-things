/*
 * Terrain class
 */
public class Terrain {
	/* 
	 * Enum to list the different possible types of terrain tiles
	 */
	public enum TERRAINS {
		JUNGLE, FROZENWASTE, FOREST, PLAINS, SWAMP, MOUNTAIN, DESERT, SEA
	}
	private String type;
	private boolean occupied; //True if another player owns it, otherwise false

	public Terrain() {
		type = "";
		occupied = false;
	}

	/* 
	 * Get/Set methods
	 */
	public boolean isOccupied() { return occupied; }
	public String getType() { return type; }

	public void setOccupied(boolean b) { occupied = b;}
	public void setType(String s) { type = s; }
}