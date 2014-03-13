package KAT;

import javafx.scene.Group;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

/*
 * Base class used for representing one of the many board pieces (excluding the terrain tiles)
 */

public abstract class Piece {

	protected String    type;
	protected String    front; // path to image for front of piece
	protected String    back;  // path to image for back of piece
	protected String    terrainType;
	protected Player    owner;
    protected boolean showPeice;
    protected Image   imageFront, imageBack;
    protected String  name;
    protected boolean inPlay;	// Used for things like setting up imageViews etc. No point in doing so if the Creature/Special character has yet to be pulled from cup
    
    protected Group pieceNode;
    protected Rectangle pieceRecBorder, pieceRecCover, pieceRecBorderOutline;
    protected ImageView pieceImgV;
    protected static Integer uniqueCode = 0; // to identify each piece on the server
    protected Integer pID; // each piece should be given a unique pID 
    
    protected static GaussianBlur gBlur2 = new GaussianBlur(2);

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
        inPlay = false;
        pID = uniqueCode++;
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
		inPlay = false;
		name = n;
        pID = uniqueCode++;
	}

    public Piece(String t, String f, String b, String n, Integer pID) {
        type = t;
        front = f;
        back = b;
        terrainType = "";
        showPeice = false;
        inPlay = false;
        name = n;
        this.pID = pID;
    }

	/*
	 * -------------Get/Set methods
	 */
	public void setName(String s) { name = s; }
	public void setType(String s) { type = s; }
	public void setFront(String s) { front = s; }
	public void setBack(String s) { back = s; }
    public void setTerrain( String s ){ terrainType = s; }
    public void setOwner(Player p) { owner = p; }

    public String getName() { return name; }
	public String getType() { return type; }
	public String getFront() { return front; }
	public String getBack() { return back; }
    public String getTerrain() { return terrainType; }
    public Player getOwner() { return owner; }
    public abstract Group getPieceNode();
    public abstract Image getImage();
    
    /*
     * -------------Instance methods
     */

    public Piece getClassInstance() { return this; }
    protected HashMap<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("name", name);
        map.put("type", type);
        map.put("pID", pID);
        map.put("fIMG", front);
        map.put("bIMG", back);
        return map;
    }

    /*
     * Method to determine if a piece is playable at a certain stage in the game.
     */
    public boolean isPlayable() {
        return false;
    }
}
