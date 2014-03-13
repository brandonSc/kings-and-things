package KAT;

import java.util.HashMap;
import java.util.Iterator;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.GlowBuilder;
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
    private static double height;
    private static double width;
	private static Group animView;
	private static boolean displayAnim;		// true will show movement animations, false will not
	private static Hex staticHexClip;
	private static Glow glow;
    
    private String type;
    private boolean occupied; //True if another player owns it, otherwise false
    private boolean showTile; // Upside down or not
    private Image tileImage;
    private Coord coord;
    private Group hexNode;
    private Hex hexClip;
    private ImageView tileImgV;
    private int moveCost;

    private HashMap<String, CreatureStack> contents; // map of usernames to pieces (Creatures)
    
    private ImageView fortImgV;
    private Fort fort;
    
    private Player owner;
    private ImageView ownerMarkerImgV;
    
    private Rectangle cover;
    
    /*
     * Constructors:
     */
    
    public Terrain(String t) {
    	setType(t);
    	showTile = true;
        occupied = false;
        displayAnim = true;
        tileImgV = new ImageView();
        
        contents = new HashMap<String,CreatureStack>();
        
//        stackNodes = new HashMap<String, Group>();
//        stacksImgV = new HashMap<String, ImageView>();
//        stacksRec = new HashMap<String, Rectangle>();

        hexClip = new Hex(sideLength * Math.sqrt(3), true);
        hexNode = GroupBuilder.create()
//        		.clip(hexClip)
        		.children(tileImgV)
        		.build();
        
        setTileImage();
        setupEvents();
//		setupStackImageViews();
		setupMarkerImageView();
		setupFortImageView();
		setupCover();
    }
    
    /* 
     * Get/Set methods
     */
    public boolean isOccupied() { return occupied; }
    public String getType() { return type; }
    public Image getImage() { return tileImage; }
    public Group getNode() { return hexNode; }
    public Coord getCoords() { return coord; }
    public Fort getFort() { return fort; }
    public int getMoveCost() { return moveCost; }
    
    public void setFort(Fort f) { fort = f; }

    /**
     * @return a map of usernames to an arraylist of their pieces
     */
    public HashMap<String,CreatureStack> getContents() { return contents; }
    
    /**
     * @return an arraylist of pieces owned by a user
     */
    public CreatureStack getContents( String username ){ return contents.get(username); }
    
    public void setOccupied(boolean b) { occupied = b; }
    public void removeControl(String username) { 
    	contents.remove(username);
    	ownerMarkerImgV.setVisible(false);
    	occupied = false;
    }

    public Player getOwner() { return owner; }

    public void setOwner(Player p) { 
    	owner = p; 
    	occupied = true;
    	ownerMarkerImgV.setImage(owner.getImage());
    }

    public void setType(String s) { 
    	this.type = s.toUpperCase();
    	switch (type) {
    	case "DESERT":
    		tileImage = baseTileImageDesert;
        	moveCost = 1;
    		break;
    	case "FOREST":
    		tileImage = baseTileImageForest;
    		moveCost = 2;
    		break;
    	case "FROZENWASTE":
    		tileImage = baseTileImageFrozenWaste;
        	moveCost = 1;
    		break;
    	case "JUNGLE":
    		tileImage = baseTileImageJungle;
    		moveCost = 2;
    		break;
    	case "MOUNTAINS":
    		tileImage = baseTileImageMountain;
    		moveCost = 2;
    		break;
    	case "PLAINS":
    		tileImage = baseTileImagePlains;
        	moveCost = 1;
    		break;
    	case "SEA":
    		tileImage = baseTileImageSea;
        	moveCost = 1;
    		break;
    	case "SWAMP":
    		tileImage = baseTileImageSwamp;
    		moveCost = 2;
    		break;
    	default: 
    		type = null;
        	moveCost = 5;
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
    	tileImgV.setFitHeight(height * 1.01); // 1.01 to compensate for images not overlapping properly
    	tileImgV.setPreserveRatio(true);
    }
     
    public void setFortImage() {
    	if (fort != null)
    		fortImgV.setImage(fort.getImage());
    }
    public void setCoords(Coord xyz) { coord = xyz; }
    public void setClip() { hexNode.setClip(hexClip); }
    
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
        staticHexClip = new Hex(sideLength * Math.sqrt(3), true);
        height = staticHexClip.getHeightNeeded();
        width = staticHexClip.getWidthNeeded();
    	glow = GlowBuilder.create().build();
		setupAnim();
    }
    public static void setSideLength(double sl) { sideLength = sl; }
   
    public Terrain positionNode(Group bn, double xoff, double yoff) {
    	
    	// Move each hex to the correct position
    	// Returns itself so this can be used in line when populating the game board (see Board.populateGameBoard())
    	hexNode.relocate(1.5 * hexClip.getSideLength() * (coord.getX() + 3) - xoff, - yoff + (6 - coord.getY() + coord.getZ()) * sideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*sideLength)/6);
    	setTileImage();
    	bn.getChildren().add(0, hexNode);
    	return this;
    }
        
    /*
     * The fucntion called when a tile is clicked on
     */
    private void clicked() {
    	
        ClickObserver.getInstance().setClickedTerrain(this);
        PlayerRackGUI.update();
        ClickObserver.getInstance().whenTerrainClicked();
    }
    
    /*
     * Setup methods
     */
    // Temp method used for when a terrain is clicked (even if covered) to print to system. Used for debugging
    private void setupEvents() { 
    	
    	//terrain is clicked
    	tileImgV.setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				clicked();
			}
		});
    	tileImgV.setOnMouseEntered(new EventHandler(){
			@Override
			public void handle(Event event) {
				tileImgV.setEffect(glow);
			}
		});
    	tileImgV.setOnMouseExited(new EventHandler(){
			@Override
			public void handle(Event event) {
				tileImgV.setEffect(null);
			}
		});
    }
    private static void setupAnim() {
		
		Hex smallHole = new Hex(height * 0.8, true);
		smallHole.relocate(width/2 - smallHole.getWidthNeeded()/2, height/2 - smallHole.getHeightNeeded()/2);
		Shape donutHex = Path.subtract(staticHexClip, smallHole);
		donutHex.setFill(Color.WHITESMOKE);
		donutHex.setEffect(new GaussianBlur());
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

    // Moves the selection animation to the clicked terrain
    public void moveAnim () {
    	if (!hexNode.getChildren().contains(animView))
    		hexNode.getChildren().add(animView);
    }
    
    // All these setup methods setup GUI things. Might merge soon
    private void setupMarkerImageView() {
    	ownerMarkerImgV = ImageViewBuilder.create()
    			.fitHeight(height*19/83)
    			.preserveRatio(true)
    			.mouseTransparent(true)
    			.build();
    	ownerMarkerImgV.relocate(width*0.2, height * 0.99 - height*19/83);
    	hexNode.getChildren().add(ownerMarkerImgV);
    }
    
    private void setupFortImageView() {
    	fortImgV = ImageViewBuilder.create()
    			.fitHeight(height*19/83)
    			.preserveRatio(true)
    			.mouseTransparent(true)
    			.build();
    	fortImgV.relocate(width*0.6, height*0.99 - height*19/83);
    	hexNode.getChildren().add(fortImgV);
    }

    private void setupCover() {
    	cover = RectangleBuilder.create()
    			.height(height)
    			.width(width)
    			.opacity(0.5)
    			.fill(Color.DARKSLATEGRAY)
    			.disable(true)
    			.visible(false)
    			.build();
    	hexNode.getChildren().add(cover);
    }
    
    // Just a calculation method. Finds the (x,y) position within the node for the stack based on how many are in the terrain
    public double[] findPositionForStack(int i) {
    	double offset = 8;
    	double centerX = width/2;
    	double centerY = height/2;
    	double stackHeight = CreatureStack.getWidth() + offset;
    	double x = 0, y = 0;
    	switch (contents.size()) {
			case 1:
				x = centerX - stackHeight/2 + offset/2;
				y = centerY - stackHeight/2;
				break;
			case 2:
				x = centerX + (i-1) * stackHeight + offset/2;
				y = centerY - stackHeight/2;
				break;
			case 3:
				x = centerX - ((double)Math.round(0.51/(i+1))/2 + (i%2))  * stackHeight + offset/2;
				y = centerY + ((double)Math.round((i + 0.1)/2) - 1) * stackHeight - centerY * 0.06;
				break;
			case 4:
				x = centerX - ((i+1)%2 * stackHeight) + offset/2;
				y = centerY + (Math.floor(i/2) - 1) * stackHeight - centerY * 0.06;
				break;
			default:
				System.out.println("Case 0!");
				break;
    	}
    	return new double[]{x, y};
    }
    
    // Adds a creature to a stack.
    // If no stack is in this Terrain, then a new stack is created.
    public void addToStack(String player, Creature c, boolean secretly) {
    	int numOfPrev = contents.size();
    	
    	// If the stack does not exist on the terrain yet, create a new stack at the proper position
    	if (contents.get(player) == null || contents.get(player).isEmpty()) {
    		CreatureStack newStack = new CreatureStack(player);
    		contents.put(player, newStack);
    		hexNode.getChildren().add(contents.get(player).getCreatureNode());
    		numOfPrev = 0;
    		int j = 0;
	    	Iterator<String> keySetIterator = contents.keySet().iterator();
	    	while(keySetIterator.hasNext()) {
	    		String key = keySetIterator.next();
	    		if (key.equals(player)) {
	    			System.out.println("break");
	    			break;
	    		}
	    		j++;
	    	}
	    	newStack.getCreatureNode().setTranslateX(findPositionForStack(j)[0]);
			newStack.getCreatureNode().setTranslateY(findPositionForStack(j)[1]);
    	}
    	
    	// Makes stack instantly visible if in setup mode (ie, piece played from rack)
    	if (GameLoop.getInstance().getPhase() <= 0) 
    		contents.get(player).getCreatureNode().setVisible(true);
    	
    	// Add the creature to the stack
    	if (!secretly)
    		contents.get(player).addCreature(c);
    	else
    		contents.get(player).addCreatureNoUpdate(c);

    	if (numOfPrev != contents.size()) {
	    	int i = 0;
	    	Iterator<String> keySetIterator = contents.keySet().iterator();
	    	while(keySetIterator.hasNext()) {
	    		String key = keySetIterator.next();
				
				if (displayAnim && !(i == contents.size() - 1 && numOfPrev < contents.size())) {
//					contents.get(key).getCreatureNode().setTranslateX(findPositionForStack(i)[0]);
//					contents.get(key).getCreatureNode().setTranslateY(findPositionForStack(i)[1]);
					contents.get(key).moveWithinTerrain(findPositionForStack(i)[0], findPositionForStack(i)[1]);
				} else {
					contents.get(key).getCreatureNode().setTranslateX(findPositionForStack(i)[0]);
					contents.get(key).getCreatureNode().setTranslateY(findPositionForStack(i)[1]);
				}
				i++;
	    	}
			
		}

    	// Display everything if animations are not to be shown, or the game phase is not in movement
    	// (Everything will be shown after movement animation otherwise)
    //	if (!displayPT || GameLoop.getInstance().getPhase() != 5)
    }
    
    // Removes a single creature from a stack.
    public Creature removeFromStack(String player, Creature c) {
    	contents.get(player).removeCreature(c);
 
    	return c;
    }
    
    // If the player has no creatures on this tile, the key-value entry is removed
    public void clearTerrainHM(String player) {
    	if (contents.get(player).getStack().isEmpty()) {
    		hexNode.getChildren().remove(contents.get(player).getCreatureNode());
    		contents.remove(player);
    		int i = 0;
	    	Iterator<String> keySetIterator = contents.keySet().iterator();
	    	while(keySetIterator.hasNext()) {
	    		String key = keySetIterator.next();
				
				if (displayAnim) {
					contents.get(key).moveWithinTerrain(findPositionForStack(i)[0], findPositionForStack(i)[1]);
				} else {
					contents.get(key).getCreatureNode().setTranslateX(findPositionForStack(i)[0]);
					contents.get(key).getCreatureNode().setTranslateY(findPositionForStack(i)[1]);
				}
				i++;
	    	}
    	}	
    }
    
    // Moves a stack from another terrain to this one
    // *Note: Moves happen like so: First the stack is added to the destination terrain (And set to not visible). Then 
    //     	the board animates the stack moving, and when the animation is done, the board deletes the node that was used for
    //		moving, and sets the original to visible. This way the stack can be accessed right away (before animation is done, 
    //		preventing null pointers)
    public int moveStack(Terrain from){
    	int numMoved = 0;
    	int numOfPrev = contents.size();
    	String activePlayer = ClickObserver.getInstance().getActivePlayer().getName();
    	for (int i = from.getContents(activePlayer).getStack().size() - 1; i >= 0 ; i--) {
    		if (from.getContents(activePlayer).getStack().get(i).isAboutToMove()) {
    			Creature mover = from.removeFromStack(activePlayer, from.getContents(activePlayer).getStack().get(i));
    			mover.setAboutToMove(false);
    			mover.setRecBorder(false);
    			mover.move(this);
    			addToStack(activePlayer, mover, true);
    			numMoved++;
    		}
    	}
    	if (numOfPrev == 0)
    		contents.get(activePlayer).getCreatureNode().setVisible(false);
		InfoPanel.showTileInfo(from);
    	ClickObserver.getInstance().setTerrainFlag("");
    	return numMoved;
    }
    
    /**
     * Implemented from Comparable interface
     * @return distance to other hex, i.e. 0 if equal, 1 if adjacent or >1
     */
	@Override
	public int compareTo(Terrain other) {
		return this.coord.compareTo(other.getCoords());
	}
	public int compareTo(Coord coord) {
		return this.coord.compareTo(coord);		
	}
	
	// Counts the number of creatures about to move. Used for moving between terrains
	public int countMovers(String player) {
		int movers = 0;
		for (int i = 0; i < contents.get(player).getStack().size(); i++) {
			if (contents.get(player).getStack().get(i).isAboutToMove())
				movers++;
		}
		return movers;
	}
	
	// These two set the rectangle node that covers and prevents mouse clicks
	public void cover() {
		cover.setVisible(true);
		cover.setDisable(false);
	}
	public void uncover() {
		cover.setVisible(false);
		cover.setDisable(true);
	}
	
	@Override
	public String toString() {
		return "Terrain:\nType: " + type + 
				"\nCoord: \n" + coord + 
				"\nOccupied: " + occupied + 
				"\nCovered: " + cover.isVisible();
	}
}
