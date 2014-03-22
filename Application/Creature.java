package KAT;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.GaussianBlurBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.StrokeType;
import java.util.HashMap;
import java.util.StringTokenizer;

/*
 * The Creature class inherits from the Piece class
 * 
 * Note* The creature "Genie" in the old game has been replaced with "Djinn" in the new
 */
public class Creature extends Piece implements Combatable, Movable {
	
	protected static Image creature_Back = new Image("Images/Creature_Back.png");
	
	private int    	combatValue;
	private boolean flying;
	private boolean magic;
	private boolean charging;
	private boolean ranged;
	private boolean aboutToMove;
	//private CreatureStack stackedIn;
	
	private Group pieceNode;
	
	private boolean doneMoving;
	private int movesLeft;

	/**
	 * ----------Constructor
	 */
	public Creature( String front, String back, String name, 
            String terrainType, int combatValue, 
            boolean flying, boolean magic, boolean charging, boolean ranged ){
		super("Creature", front, "Images/creature_Back", name);

		this.doneMoving = false;
		this.aboutToMove = false;
		this.setTerrain(terrainType.toUpperCase());
		this.combatValue = combatValue;
		this.flying = flying;
		this.magic = magic;
		this.charging = charging;
		this.ranged = ranged;
		this.imageBack = creature_Back;
		
		this.resetMoves();
	}

	public Creature(String input) {
		super("Creature", "", "Images/creature_Back", "");
		separateInput(input);
		setType("Creature");
		this.resetMoves();
	}
	
	public Creature( HashMap<String,Object> map ){
		super(map);
		this.doneMoving = false;
		this.aboutToMove = false;
		this.movesLeft = 4;
		this.combatValue = (Integer)map.get("combatVal");
		int combatVal = (Integer)map.get("combatVal");
		boolean flying = ((Integer)map.get("flying") == 1) ? true : false;
		boolean ranged = ((Integer)map.get("ranged") == 1) ? true : false;
		boolean magic = ((Integer)map.get("magic") == 1) ? true : false;
		boolean charging = ((Integer)map.get("charging") == 1) ? true : false;
	}

	/* 
	 * ----------Get/Set methods
	 */
	public void setName(String s) { super.setName(s); }
	public void setCombatValue(int i) { combatValue = i; }
	public void setFlying(boolean b) { flying = b; }
	public void setMagic(boolean b) { magic = b; }
	public void setCharging(boolean b) { charging = b; }
	public void setRanged(boolean b) { ranged = b; }
	public void setMovesLeft(int i) { movesLeft = i; }
	public void setInPlay(boolean b) {  // If Creatures are not in play, creates the things needed for them. Vis-versa if it is in play, and is put out of game

		if (!inPlay && b) {
			setupImageView();
			setupEvents();
		} else if (inPlay && !b) {
			imageFront = null;
			pieceImgV.setImage(null);
			pieceNode.setOnMouseClicked(null);
			pieceNode.getChildren().clear();
		}
		inPlay = b;
	}

	private void separateInput(String in) {
		String[] input = in.split(",");
		setFront(input[0]);
		setBack(input[1]);
		setName(input[2]);
		setTerrain(input[3]);
		setCombatValue(Integer.parseInt(input[4]));
		setFlying((input[5].equals("true")) ? true : false);
		setMagic((input[6].equals("true")) ? true : false);
		setCharging((input[7].equals("true")) ? true : false);
		setRanged((input[8].equals("true")) ? true : false);
	}

	//public void setStackedIn(CreatureStack cs) { stackedIn = cs; }
	
	public static Image getBackImage() { return creature_Back; }
	
	public String getName() {
		return name; 
	}
	//public CreatureStack getStackedIn() { return stackedIn; }
	
	@Override
	public Group getPieceNode() { 
		if (!inPlay) 
			setInPlay(true);
		return pieceNode;
	}
	@Override
	public Image getImage() {
		if (!inPlay) 
			setInPlay(true);
		return imageFront;
	}
	
	/*
	 * ------------ Combatable methods
	 */
	public int getCombatValue() { return combatValue; }
	public boolean isFlying() { return flying; }
	public boolean isMagic() { return magic; }
	public boolean isCharging() { return charging; }
	public boolean isRanged() { return ranged; }
    /**
     * Call when this creature is hit during combat
     */ 
    public void inflict(){
        //TheCup.getInstance().addToCup(this); // return to cup
        // should remove this creature from the hex
    }

    /*
     * ------------Instance methods
     */
    /*
     * Silly method to generate a string based on a boolean. Used for the toString method
     */
    private String boolString(boolean b) {
    	String tmp;
    	if (b)
    		tmp = "Yes";
    	else
    		tmp = "No";
    	return tmp;
    }
    
    @Override
    public String toString() {
    	String str = name + "\nTerrain: "+ getTerrain()
    					  + "\nCombat Value: "+combatValue
    					  + "\nFlying? "+boolString(flying)
    					  + "\nMagic? "+boolString(magic)
    					  + "\nCharging? "+boolString(charging)
    					  + "\nRanged? "+boolString(ranged)
    					  + "\n";
    	return str;
    }
	
	public Creature getClassInstance() { return this; }
	
	private void setupImageView() {
		// Loads image
		if (front != null && !front.equals(""))
			this.imageFront = new Image(front);
		else
			this.imageFront = creature_Back;
		
		pieceNode = GroupBuilder.create()
				.clip( RectangleBuilder.create()
						.width(InfoPanel.getWidth() * 0.23)
						.height(InfoPanel.getWidth() * 0.23)
						.build()) 
				.build();
		
		// Creates ImageView
		pieceImgV = ImageViewBuilder.create()
				.image(imageFront)
				.fitHeight(InfoPanel.getWidth() * 0.23)
				.preserveRatio(true)
				.build();
		
		// Small outline around creatures
		pieceRecBorderOutline = RectangleBuilder.create()
				.width(InfoPanel.getWidth() * 0.23)
				.height(InfoPanel.getWidth() * 0.23)
				.strokeWidth(1)
				.strokeType(StrokeType.INSIDE)
				.stroke(Color.BLACK)
				.fill(Color.TRANSPARENT)
				.effect(new GaussianBlur(2))
				.clip( RectangleBuilder.create()
						.width(InfoPanel.getWidth() * 0.23)
						.height(InfoPanel.getWidth() * 0.23)
						.build())
				.disable(true)
				.build();
		
		// Create rectangle around creature
		pieceRecBorder = RectangleBuilder.create()
				.width(InfoPanel.getWidth() * 0.23)
				.height(InfoPanel.getWidth() * 0.23)
				.strokeWidth(5)
				.strokeType(StrokeType.INSIDE)
				.stroke(Color.WHITESMOKE)
				.fill(Color.TRANSPARENT)
				.effect(new GaussianBlur(5))
				.clip( RectangleBuilder.create()
						.width(InfoPanel.getWidth() * 0.23)
						.height(InfoPanel.getWidth() * 0.23)
						.build())
				.visible(false)
				.disable(true)
				.build();
		
		// Create rectangle to cover image and disable clicks
		pieceRecCover = RectangleBuilder.create()
				.width(InfoPanel.getWidth() * 0.23)
				.height(InfoPanel.getWidth() * 0.23)
				.fill(Color.DARKSLATEGRAY)
				.opacity(0.5)
				.visible(false)
				.disable(true)
				.build();
		
		// Add to pieceNode
		pieceNode.getChildren().add(0, pieceImgV);
		pieceNode.getChildren().add(1, pieceRecBorderOutline);
		pieceNode.getChildren().add(2, pieceRecBorder);
		pieceNode.getChildren().add(3, pieceRecCover);
		
	}

	private void setupEvents() { 
		
		pieceImgV.setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				clicked();
			}
		});
    }
	private void clicked() { 
		ClickObserver.getInstance().setClickedCreature(this);
        ClickObserver.getInstance().whenCreatureClicked();
	}
	
	// covers the creature and uncovers (Will change to cover/uncover like terrains)
	// public void uncover() {
	// 	pieceRecCover.setVisible(false);
	// 	pieceRecCover.setDisable(true);
	// }
	// public void cover() {
	// 	pieceRecCover.setVisible(true);
	// 	pieceRecCover.setDisable(false);
	// }
	// This border is the white square around the creature when selected
	public void setRecBorder(boolean b) {
		pieceRecBorder.setVisible(b);
	}
	
	/*
	 * ------------ Movable methods 
	 */
	public int movesLeft() { return movesLeft; }
	public boolean doneMoving() { return doneMoving; }
	public void resetMoves() { movesLeft = 4; }
	public void move(Terrain t) {
		if (isFlying())
			movesLeft--;
		else
			movesLeft = movesLeft - t.getMoveCost();
		if (movesLeft <= 0)
			doneMoving = true;
	}
	
	// Checks to see if this can move between two terrains. Will be usefull for flying creatures soon
	public boolean canMoveTo(Terrain from, Terrain to) {
		if (from.compareTo(to) == 1 && this.movesLeft > 0 && !to.getType().equals("SEA") && !(this.movesLeft < 2 && (to.getType().equals("JUNGLE") || to.getType().equals("MOUNTAINS") || to.getType().equals("FOREST") || to.getType().equals("SWAMP")))) {
			System.out.println(this.name + " can move from " + from.getCoords().toString() + " to " + to.getCoords().toString());
			return true;
		}
		else {
			System.out.println(this.name + " cannot move from " + from.getCoords().toString() + " to " + to.getCoords().toString());
			System.out.println("movesLeft: " + movesLeft + "  | from.compareTo(to)" + from.compareTo(to));
			
			return false;
		}
	}
	
	// When a creature is clicked in the infoPanel, this gets toggled. (selecting and deselecting creatures)
	public void toggleAboutToMove() {
		if (aboutToMove) {
			aboutToMove = false;
			setRecBorder(false);
		} else {
			aboutToMove = true;
			setRecBorder(true);
		}
	}
	public void setAboutToMove(boolean b) { aboutToMove = b; }
	public boolean isAboutToMove() { return aboutToMove; }  		
	
    @Override
    public HashMap<String,Object> toMap(){ 
        HashMap<String,Object> map = super.toMap();
        map.put("combatVal", new Integer(combatValue));
        map.put("ranged", new Integer((ranged) ? 1 : 0));
        map.put("magic", new Integer((magic) ? 1 : 0));
        map.put("charging", new Integer((charging) ? 1 : 0));
        map.put("flying", new Integer((flying) ? 1 : 0));
        map.put("orientation", new Integer(1));
        return map;
    }

    /*
     * Method to determine whether a creature is plyabale.
     * A creature is only playable in the setup phase AFTER all the initial tiles have been selected and the towers have been placed,
     * or during the recruit things phase.
     */
    @Override
    public boolean isPlayable() {
    	if (GameLoop.getInstance().getPhase() == 0 || GameLoop.getInstance().getPhase() == 3)
    		return true;
    	else
    		return false;
    }
}
