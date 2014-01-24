package KAT;

import java.util.ArrayList;

import javafx.animation.Transition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Terrain class
 */
public class Terrain {
    /* 
     * Enum to list the different possible types of terrain tiles
     */
	public static enum TERRAINS {
            JUNGLE, FROZENWASTE, FOREST, PLAINS, SWAMP, MOUNTAIN, DESERT, SEA
    }
    // TODO: clean up the following Images maybe?
    private static Image baseTileImageDesert, baseTileImageForest, baseTileImageFrozenWaste, baseTileImageJungle, baseTileImageMountain, baseTileImagePlains, baseTileImageSea, baseTileImageSwamp, baseTileImageUpsideDown;
    private static String imageSet = "01"; // Was trying different images, this will be removed in future.
    private static double sideLength;
    
    private String type;
    private boolean occupied; //True if another player owns it, otherwise false
    private boolean showTile; // Upside down or not
    private Image tileImage;
    private ArrayList<Piece> contents; // List of peices on this hex
    private int[] coords;
    private Group hexNode;
    private Hex hexClip;
    private ImageView tileImgV;
    private static Transition tileSelected;
    
    /*
     * Constructors:
     */
    public Terrain() {
        setType("unknown");
        occupied = false;
    	showTile = false;
        coords = new int[]{0, 0, 0};
        contents = new ArrayList<Piece>();
        hexNode = new Group();
    	hexClip = new Hex(sideLength, true);
    	hexNode.setClip(hexClip);
    }
    
    public Terrain(String t) {
    	setType(t);
    	showTile = false;
        occupied = false;
        coords = new int[]{0, 0, 0};
        contents = new ArrayList<Piece>();
        hexClip = new Hex(sideLength * Math.sqrt(3), true);
        hexNode = new Group();
        hexNode.setClip(hexClip);
        
        tileImgV = new ImageView();
    	hexNode.getChildren().add(tileImgV);
    	
    }
    

    /* 
     * Get/Set methods
     */
    public boolean isOccupied() { return occupied; }
    public String getType() { return type; }
    public Image getImage() { return tileImage; }
    public Group getNode() { return hexNode; }
    public int[] getCoords() { return coords; }

    public void setOccupied(boolean b) { occupied = b;}
    public void setType(String s) { 
    	this.type = s.toUpperCase();
    	switch (type) {
    	case "DESERT":
    		tileImage = baseTileImageDesert;
    		break;
    	case "FOREST":
    		tileImage = baseTileImageForest;
    		break;
    	case "FROZENWASTE":
    		tileImage = baseTileImageFrozenWaste;
    		break;
    	case "JUNGLE":
    		tileImage = baseTileImageJungle;
    		break;
    	case "MOUNTAINS":
    		tileImage = baseTileImageMountain;
    		break;
    	case "PLAINS":
    		tileImage = baseTileImagePlains;
    		break;
    	case "SEA":
    		tileImage = baseTileImageSea;
    		break;
    	case "SWAMP":
    		tileImage = baseTileImageSwamp;
    		break;
    	default: 
    		type = null;
    		tileImage = baseTileImageUpsideDown;
    		break;
    	}
    }
    public void setShowTile(boolean s) { showTile = s; }
    public void setImageViews() {
    	
    	if (showTile)
    		tileImgV.setImage(tileImage);
    	else
    		tileImgV.setImage(baseTileImageUpsideDown);
    	tileImgV.setFitHeight(hexClip.getHeightNeeded() * 1.01); // 1.01 to compensate for images not overlapping properly
    	tileImgV.setPreserveRatio(true);
    	// TODO add imageViews for each Peice on hex
    }
    
    /*
     * runs when board is constructed. Loads one image of each tile type
     */
    public static void setBaseImages() {
    	baseTileImageDesert = new Image("Images/Hex_desert_" + imageSet + ".png");
    	baseTileImageForest = new Image("Images/Hex_forest_" + imageSet + ".png");
    	baseTileImageFrozenWaste = new Image("Images/Hex_frozenwaste_" + imageSet + ".png");
    	baseTileImageJungle = new Image("Images/Hex_jungle_" + imageSet + ".png");
    	baseTileImageMountain = new Image("Images/Hex_mountains_" + imageSet + ".png");
    	baseTileImagePlains = new Image("Images/Hex_plains_" + imageSet + ".png");
    	baseTileImageSea = new Image("Images/Hex_sea_" + imageSet + ".png");
    	baseTileImageSwamp = new Image("Images/Hex_swamp_" + imageSet + ".png");
    	baseTileImageUpsideDown = new Image("Images/Hex_upsidedown_" + imageSet + ".png");
    }
    public static void setSideLength(double sl) { sideLength = sl; }
   
    public Terrain positionNode(Group bn, int[] xyz) {
    	
    	// Move each hex to the correct position
    	// Returns itself so this can be used in line when populating the game board (see Board.populateGameBoard())
    	// Also sets up mouseClick event
    	coords = xyz;
    	hexNode.relocate(1.5 * hexClip.getSideLength() * (coords[0] + 3), (6 - coords[1] + coords[2]) * sideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*sideLength)/6);
    	setImageViews();
    	bn.getChildren().add(1, hexNode);
    	
		hexNode.setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				clicked();
			}
		});
		
    	return this;
    }
    
    private void clicked() {
    	showTile = true;
        setImageViews();
        InfoPanel.showTileInfo(this);
        Board.setSelectedAnimationLocation(coords);
        
    }
    
    
        
}
