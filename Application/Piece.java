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

	// Additional
	public Piece(String t, String f, String b) {
		type = t;
		front = f;
		back = b;
		terrainType = "";
		showPeice = false;
	}

	/*
	 * -------------Get/Set methods
	 */
	public void setType(String s) { type = s; }
	public void setFront(String s) { front = s; }
	public void setBack(String s) { back = s; }
    public void setTerrain( String s ){ terrainType = s; }

	public String getType() { return type; }
	public String getFront() { return front; }
	public String getBack() { return back; }
    public String getTerrain() { return terrainType; }
    
    /*
     *-------------Class methods
     */
    public static void setBaseImages() {
    	
    	frozenWaste_DragonRider = new Image("Images/FrozenWaste_DragonRider.png");
    	frozenWaste_ElkHerd = new Image("Images/FrozenWaste_ElkHerd.png");
    	creature_Back = new Image("Images/Creature_Back.png");
    }
    
    /*
     * -------------Instance methods
     */
    public Image getImage() { return image; }
    
}
