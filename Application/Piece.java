package KAT;

import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.HashMap;

/*
 * Base class used for representing one of the many board pieces (excluding the terrain tiles)
 */

public abstract class Piece {
	
	protected static Image attackingSuccessImg;
	protected static Image attackingFailImg;
	protected static Image chargeAttackDoubleSuccessImg;
	protected static Image chargeAttackOneSuccessImg;
	protected static Image chargeAttackDoubleFailImg;
	protected static Glow glow;

	protected String    type;
	protected String    front; // path to image for front of piece
	protected String    back;  // path to image for back of piece
	protected String    terrainType;
	protected Player    owner;
    protected boolean showPeice;
    protected Image   imageFront, imageBack;
    protected String  name;
    protected boolean doneMoving;
    protected boolean inPlay;	// Used for things like setting up imageViews etc. No point in doing so if the Creature/Special character has yet to be pulled from cup
    private CreatureStack stackedIn;
    protected Group pieceNode;
    protected Shape pieceSelectBorder, pieceCover, pieceBorderOutline;
    protected ImageView pieceImgV, attackResultImgV;
    private   static Integer uniqueCode = 0; // to identify each piece on the server
    protected Integer pID; // each piece should be given a unique pID 
    protected boolean attackMode;
    protected int chargeAttackSuccess;
    protected Image mouseOverImage;

	/*
	 * Constructors
	 */
    //Default
	public Piece() {
		glow = new Glow();
		type = "";
		front = "";
		back = "";
        terrainType = "";
        showPeice = false;
        attackMode = false;
//        attackSuccess = false;
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
		attackMode = false;
//		attackSuccess = false;
		name = n;
		glow = new Glow();
        pID = uniqueCode++;
	}
	
	public Piece( HashMap<String,Object> map ){
		this.type = (String)map.get("type");
		this.front = (String)map.get("fIMG");
		this.back = (String)map.get("bIMG");
		this.name = (String)map.get("name");
		this.pID = (Integer)map.get("pID");
		terrainType = "";
		glow = new Glow();
		showPeice = false;
		inPlay = false;
//		attackSuccess = false;
		attackMode = false;
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
    public void setPID(int pID){ this.pID = pID; }
    public void setStackedIn(CreatureStack cs) { stackedIn = cs; }
    
    public String getName() { return name; }
	public String getType() { return type; }
	public String getFront() { return front; }
	public String getBack() { return back; }
    public String getTerrain() { return terrainType; }
    public int getPID(){ return pID; }
    public Player getOwner() { return owner; }
    public abstract Group getPieceNode();
    public abstract Image getImage();
    public CreatureStack getStackedIn() { return stackedIn; }
    
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

    public void uncover() {
    	if (!(this instanceof Fort)) {
			pieceCover.setVisible(false);
			pieceCover.setDisable(true);
    	}
	}

	public void cover() {
		if (!(this instanceof Fort)) {
			pieceCover.setVisible(true);
			pieceCover.setDisable(false);
    	}
	}
	
	public void highLight() {
		pieceSelectBorder.setVisible(true);
	}
	public void unhighLight() {
		pieceSelectBorder.setVisible(false);
	}

	public boolean doneMoving() { return doneMoving; }
	
	public static void setClassImages() {
		attackingSuccessImg = new Image("Images/Attacking_Success.png");
		attackingFailImg = new Image("Images/Attacking_Fail.png");
		chargeAttackDoubleSuccessImg = new Image("Images/Attack_ChargeDoubleSuccess.png");
		chargeAttackOneSuccessImg = new Image("Images/Attack_ChargeOneSuccess.png");
		chargeAttackDoubleFailImg = new Image("Images/Attack_ChargeDoubleFail.png");
		
	}
}
