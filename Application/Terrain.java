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
public class Terrain {
    
    private static Image baseTileImageDesert, baseTileImageForest, baseTileImageFrozenWaste, baseTileImageJungle, baseTileImageMountain, baseTileImagePlains, baseTileImageSea, baseTileImageSwamp, baseTileImageUpsideDown;
    private static String imageSet = "01"; // Was trying different images, this will be removed in future.
    private static double sideLength;
	private static Group animView;
    
    private String type;
    private boolean occupied; //True if another player owns it, otherwise false
    private boolean showTile; // Upside down or not
    private Image tileImage;
    private HashMap<String,ArrayList<Piece>> contents; // map of usernames to pieces
    private int[] coords;
    private Group hexNode;
    private Hex hexClip;
    private ImageView tileImgV;
    private HashMap<String, Group> stacksNode;
    private HashMap<String, ImageView> stacksImgV;
    private Player owner;
    
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
        
        setTileImageView();
        setupEvents();
		setupAnim();
		setupStackImageViews();
    }
    
    /* 
     * Get/Set methods
     */
    public boolean isOccupied() { return contents.isEmpty(); }
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
    private void setTileImageView() {
    	
    	if (showTile)
    		tileImgV.setImage(tileImage);
    	else
    		tileImgV.setImage(baseTileImageUpsideDown);
    	tileImgV.setFitHeight(hexClip.getHeightNeeded() * 1.01); // 1.01 to compensate for images not overlapping properly
    	tileImgV.setPreserveRatio(true);
    }
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
    public void setCoords(int[] xyz) { coords = xyz; }
    
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
   
    public Terrain positionNode(Group bn, int[] xyz, double xoff, double yoff) {
    	
    	// Move each hex to the correct position
    	// Returns itself so this can be used in line when populating the game board (see Board.populateGameBoard())
    	coords = xyz;
    	hexNode.relocate(1.5 * hexClip.getSideLength() * (coords[0] + 3) - xoff, - yoff + (6 - coords[1] + coords[2]) * sideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*sideLength)/6);
    	setTileImageView();
    	bn.getChildren().add(0, hexNode);
    	return this;
    }
        
    /*
     * The fucntion called when a tile is clicked on
     */
    private void clicked() {
        InfoPanel.showTileInfo(this);
        if (!hexNode.getChildrenUnmodifiable().contains(animView))
        	hexNode.getChildren().add(animView);
        setStacksImages();
        PlayerRackGUI.update();
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
    /*
     * Sets up the images for each players stack on this terrain, as well as small colored rectangle indicating who owns the stack
     */
    private void setupStackImageViews() {
    	
    	for (int i = 0; i < GameLoop.getInstance().getNumPlayers(); i++) {
    		
    		String thePlayerName = GameLoop.getInstance().getPlayers()[i].getName();
			stacksImgV.put(thePlayerName, ImageViewBuilder.create()
	    			  .fitWidth(hexClip.getWidthNeeded() * 0.2)
	    			  .preserveRatio(true)
	    			  .build());
			stacksNode.put(thePlayerName, GroupBuilder.create()
					.layoutX(hexClip.getWidthNeeded() * ((i%2) * 0.4 + 0.2))
					.layoutY(hexClip.getHeightNeeded() * (Math.floor(i/2) * 0.3 + 0.3))
					.disable(true)
					.visible(false)
					.build());
			stacksNode.get(thePlayerName).getChildren().add(stacksImgV.get(thePlayerName));
			stacksNode.get(thePlayerName).getChildren().add(RectangleBuilder.create()
    				.width(hexClip.getWidthNeeded() * 0.2)
    				.height(hexClip.getWidthNeeded() * 0.2)
    				.stroke(GameLoop.getInstance().getPlayers()[i].getColor())
    				.strokeWidth(2)
    				.fill(Color.TRANSPARENT)
    				.build());
			hexNode.getChildren().add(stacksNode.get(thePlayerName));
		}
    }
}
