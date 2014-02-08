package KAT;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Base class used for representing one of the many board pieces (excluding the terrain tiles)
 */

public abstract class Piece {
	
	
	protected static Image creature_Back, frozenWaste_DragonRider, frozenWaste_ElkHerd;
	
	private String type;
	private String front; // path to image for front of piece
	private String back;  // path to image for back of piece
    private String terrainType;
    protected boolean showPeice;
    protected Image image;
    protected String name;

	/*
	 * Constructors
	 */
    //Default
	public Piece() {
		type = "";
		front = "";
		back = "";
        terrainType = "";
        showPeice = false;
	}
	/*
	 * Additional constructor
	 */
	public Piece(String t, String f, String b, String n) {
		type = t;
		front = f;
		back = b;
		terrainType = "";
		showPeice = false;
		name = n;
	}

	/*
	 * -------------Get/Set methods
	 */
	public void setName(String s) { name = s; }
	public void setType(String s) { type = s; }
	public void setFront(String s) { front = s; }
	public void setBack(String s) { back = s; }
    public void setTerrain( String s ){ terrainType = s; }

    public String getName() { return name; }
	public String getType() { return type; }
	public String getFront() { return front; }
	public String getBack() { return back; }
    public String getTerrain() { return terrainType; }
    
    /*
     * -------------Instance methods
     */
    public Image getImage() { return image; }

    public Piece getClassInstance() { return this; }
}
