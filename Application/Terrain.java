package KAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/*
 * Terrain class
 */
public class Terrain implements Comparable<Terrain> {
    
    private static Image baseTileImageDesert, baseTileImageForest, baseTileImageFrozenWaste, baseTileImageJungle, baseTileImageMountain, baseTileImagePlains, baseTileImageSea, baseTileImageSwamp, baseTileImageUpsideDown;
    private static String imageSet = "01"; // Was trying different images, this will be removed in future.
    private static double sideLength;
	private static Group animView;
    
    private String type;
    private boolean occupied; //True if another player owns it, otherwise false
    private boolean showTile; // Upside down or not
    private Image tileImage;
    private int[] coords;
    private Group hexNode;
    private Hex hexClip;
    private ImageView tileImgV;

    private HashMap<String,ArrayList<Piece>> contents; // map of usernames to pieces
    private HashMap<String, Group> stacksNode;
    private HashMap<String, ImageView> stacksImgV;
    private Player owner;
    private ImageView ownerMarkerImgV;
    private ImageView fortImgV;
    
    /*
     * Constructors:
     */
    public Terrain() {
        setType("unknown");
        occupied = false;
    	showTile = false;
        coords = new int[]{0, 0, 0};
        contents = new HashMap<String,ArrayList<Piece>>();
    	hexClip = new Hex(sideLength, true);
        stacksNode = new HashMap<String, Group>();
        stacksImgV = new HashMap<String, ImageView>();
    	
    	hexNode = GroupBuilder.create()
    			.clip(hexClip)
    			.build();
    	
    	setupEvents();
		setupAnim();
		setupStackImageViews();
    }
    
    public Terrain(String t) {
    	setType(t);
    	showTile = true;
        occupied = false;
        tileImgV = new ImageView();
        coords = new int[]{0, 0, 0};
        contents = new HashMap<String,ArrayList<Piece>>();
        hexClip = new Hex(sideLength * Math.sqrt(3), true);
        stacksNode = new HashMap<String, Group>();
        stacksImgV = new HashMap<String, ImageView>();
        
        hexNode = GroupBuilder.create()
        		.clip(hexClip)
        		.children(tileImgV)
        		.build();
        
        setTileImage();
        setupEvents();
		setupAnim();
		setupStackImageViews();
		setupMarkerImageView();
    }
    
    /* 
     * Get/Set methods
     */
    public boolean isOccupied() { return !contents.isEmpty(); }
    public String getType() { return type; }
    public Image getImage() { return tileImage; }
    public Group getNode() { return hexNode; }
    public int[] getCoords() { return coords; }

    /**
     * @return a map of usernames to an arraylist of their pieces
     */
    public HashMap<String,ArrayList<Piece>> getContents() { return contents; }
    
    /**
     * @return an arraylist of pieces owned by a user
     */
    public ArrayList<Piece> getContents( String username ){ return contents.get(username); }
    
    public void setOccupied(String username) { 
        contents.put(username, new ArrayList<Piece>());
    }
    public void removeControl(String username) {
        contents.remove(username);
    }

    public Player getOwner() { return owner; }

    public void setOwner(Player p) { owner = p; }

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
    private void setTileImage() {
    	
    	if (showTile)
    		tileImgV.setImage(tileImage);
    	else
    		tileImgV.setImage(baseTileImageUpsideDown);
    	tileImgV.setFitHeight(hexClip.getHeightNeeded() * 1.01); // 1.01 to compensate for images not overlapping properly
    	tileImgV.setPreserveRatio(true);
    }
    /*
     * When a stack is added to a terrain, or the top creature is changed or flipped, call this
     */
    private void setStacksImages() {
    	
    	Iterator<String> keySetIterator = contents.keySet().iterator();
    	while(keySetIterator.hasNext()){
    		String key = keySetIterator.next();
    		if (!contents.get(key).isEmpty()) {
	    		stacksImgV.get(key).setImage(contents.get(key).get(0).getImage());
	    		stacksNode.get(key).setVisible(true);
	    		stacksNode.get(key).setDisable(false);
    		}
    	}
    	
    }
    // Sets marker image for owner. Called by ClickObserver
    public void setOwnerImage() {
    	if (owner != null)
    		ownerMarkerImgV.setImage(owner.getImage());
    }
    public void setCoords(int[] xyz) { coords = xyz; }
    
    /*
     * runs when board is constructed. Loads one image of each tile type
     */
    public static void setClassImages() {
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
   
    public Terrain positionNode(Group bn, int[] xyz, double xoff, double yoff) {
    	
    	// Move each hex to the correct position
    	// Returns itself so this can be used in line when populating the game board (see Board.populateGameBoard())
    	coords = xyz;
    	hexNode.relocate(1.5 * hexClip.getSideLength() * (coords[0] + 3) - xoff, - yoff + (6 - coords[1] + coords[2]) * sideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*sideLength)/6);
    	setTileImage();
    	bn.getChildren().add(0, hexNode);
    	return this;
    }
        
    /*
     * The fucntion called when a tile is clicked on
     */
    private void clicked() {
    	
        ClickObserver.getInstance().setClickedTerrain(this);
        if (!hexNode.getChildrenUnmodifiable().contains(animView))
        	hexNode.getChildren().add(animView);
        PlayerRackGUI.update();
        ClickObserver.getInstance().whenTerrainClicked();
    }
    
    /*
     * Setup methods
     */
    private void setupEvents() { 
    	
    	//terrain is clicked
    	hexNode.setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				clicked();
			}
		});
    }
    private void setupAnim() {
		
		Hex smallHole = new Hex(hexClip.getHeightNeeded() * 0.8, true);
		smallHole.relocate(hexClip.getWidthNeeded()/2 - smallHole.getWidthNeeded()/2, hexClip.getHeightNeeded()/2 - smallHole.getHeightNeeded()/2);
		Shape donutHex = Path.subtract(hexClip, smallHole);
		donutHex.setFill(Color.WHITESMOKE);
		animView = GroupBuilder.create()
				.children(donutHex)
				.build();
		
    	final Animation tileSelected = new Transition() {
    	     {
    	         setCycleDuration(Duration.millis(1700));
    	         setCycleCount(INDEFINITE);
    	         setAutoReverse(true);
    	     }
    	     protected void interpolate(double frac) { animView.setOpacity(frac * 0.8); }
    	};
    	tileSelected.play();
	}
    
    /*- Sets up the images for each players stack on this terrain, as well as small 
     *  colored rectangle indicating who owns the stack
     *
     *- Currently each player has a pre-determined stack location. In the future the 
     *  location will be dependent on how many stacks are on the terrain.
     */
    private void setupStackImageViews() {
    	
    	for (int i = 0; i < GameLoop.getInstance().getNumPlayers(); i++) {
    		
    		String thePlayerName = GameLoop.getInstance().getPlayers()[i].getName();
    		double stackHeight = hexClip.getHeightNeeded() * 27/83 * 0.8;
			stacksImgV.put(thePlayerName, ImageViewBuilder.create()
	    			  .fitWidth(stackHeight)
	    			  .preserveRatio(true)
	    			  .build());
			stacksNode.put(thePlayerName, GroupBuilder.create()
					.layoutX(hexClip.getWidthNeeded()/2 - ((i+1)%2 * stackHeight) + ((i%2)*2 - 1) * stackHeight * 0.08)
					.layoutY(hexClip.getHeightNeeded() * (Math.floor(i/2) * 0.3 + 0.3))
					.disable(true)
					.visible(false)
					.build());
			stacksNode.get(thePlayerName).getChildren().add(stacksImgV.get(thePlayerName));
			stacksNode.get(thePlayerName).getChildren().add(RectangleBuilder.create()
    				.width(stackHeight * 1.05)
    				.height(stackHeight * 1.05)
    				.stroke(GameLoop.getInstance().getPlayers()[i].getColor())
    				.strokeWidth(2)
    				.fill(Color.TRANSPARENT)
    				.build());
			hexNode.getChildren().add(stacksNode.get(thePlayerName));
		}
    }
    
    private void setupMarkerImageView() {
    	ownerMarkerImgV = ImageViewBuilder.create()
    			.fitHeight(hexClip.getHeightNeeded()*19/83)
    			.preserveRatio(true)
    			.build();
    	ownerMarkerImgV.relocate(hexClip.getWidthNeeded()*0.2, hexClip.getHeightNeeded()*0.01);
    	hexNode.getChildren().add(ownerMarkerImgV);
    }
    
    public void addToStack(String player, Creature c) {
    	if (contents.get(player) == null)
    		contents.put(player, new ArrayList<Piece>());
    	contents.get(player).add(c);
    	setStacksImages();
    }

    /**
     * Implemented from Comparable interface
     * @return distance to other hex, i.e. 0 if equal, 1 if adjacent or >1
     */
	@Override
	public int compareTo(Terrain other) {
		int diffX = this.coords[0] - other.coords[0];
		int diffY = this.coords[1] - other.coords[1];
		int diffZ = this.coords[2] - other.coords[2];
		int distance = (int)Math.sqrt(Math.pow(diffX,2)+Math.pow(diffY,2)+Math.pow(diffZ,2));
		return distance;
	}
}
