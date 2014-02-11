package KAT;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Base class used for representing one of the many board pieces (excluding the terrain tiles)
 */

public abstract class Piece {

	private String type;
	private String front; // path to image for front of piece
	private String back;  // path to image for back of piece
    private String terrainType;
    private String owner;
    protected boolean showPeice;
    protected Image imageFront;
    protected Image imageBack;
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
    public void setOwner(String s) { owner = s; }

    public String getName() { return name; }
	public String getType() { return type; }
	public String getFront() { return front; }
	public String getBack() { return back; }
    public String getTerrain() { return terrainType; }
    public String getOwner() { return owner; }
    
    /*
     * -------------Instance methods
     */
    public Image getImage() { return imageFront; }

    public Piece getClassInstance() { return this; }
}