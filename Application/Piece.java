package KAT;

/*
 * Base class used for representing one of the many board pieces (excluding the terrain tiles)
 */

public abstract class Piece {
	
	private String type;
	private String front; // path to image for front of piece
	private String back;  // path to image for back of piece
    private String terrainType;

	/*
	 * Default constructor
	 */
	public Piece() {
		type = "";
		front = "";
		back = "";
        terrainType = "";
	}

	/*
	 * Additional constructor
	 */
	public Piece(String t, String f, String b) {
		type = t;
		front = f;
		back = b;
		terrainType = "";
	}

	/*
	 * Get/Set methods
	 */
	public void setType(String s) { type = s; }
	public void setFront(String s) { front = s; }
	public void setBack(String s) { back = s; }
    public void setTerrain( String s ){ terrainType = s; }

	public String getType() { return type; }
	public String getFront() { return front; }
	public String getBack() { return back; }
    public String getTerrain() { return terrainType; }
}
