package KAT;

import java.util.ArrayList;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.PathTransitionBuilder;
import javafx.animation.Transition;
import javafx.animation.TransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Translate;
import javafx.scene.transform.TranslateBuilder;
import javafx.util.Duration;

public class CreatureStack {

	private static double width, height;
	
	private ArrayList<Creature> stack;
	private Player owner;
	
	private Group stackNode;
	private ImageView stackImgV;
	private Rectangle stackRecBorder;
	
	private TranslateTransition moveWithin;
	
	/*
	 * --------- Constructors
	 */
	public CreatureStack(Player owner) {

		stackNode = new Group();
		this.owner = owner;
		stack = new ArrayList<Creature>();
//		moveWithin = new PathTransition();
		setupImageView();
	}
	
	public CreatureStack(String owner) {
		stackNode = new Group();
		for (Player p : GameLoop.getInstance().getPlayers()) {
			if (p.getName().equals(owner))
				this.owner = p;
		}
		stack = new ArrayList<Creature>();
//		moveWithin = new PathTransition();
		setupImageView();
	}
	
	/*
	 * ---------- Gets and sets
	 */
	public void setOwner(Player p) { owner = p; }
	public ArrayList<Creature> getStack() { return stack; }
	public Player getOwner() { return owner; }
	public Group getCreatureNode() { return stackNode; }
	public static double getWidth() { 
		if (width == 0)
			width = Game.getWidth() * 0.016;
		return width; 
	}
	
	/*
	 * -------- Instance methods
	 */
	public void addCreature(Creature c) {
		stack.add(0, c);
		c.setStackedIn(this);
		updateImage();
	}
	
	// Adds a creature to the stack without updating the image (used when board animates moving stacks)
	public void addCreatureNoUpdate(Creature c) {
		c.setStackedIn(this);
		stack.add(0, c);
	}
	
	public Creature getCreature(int i) {
		return stack.get(i);
	}
	
	public Creature removeCreature(Creature c) {
		stack.remove(c);
		c.setStackedIn(null);
		updateImage();
		if (stack.size() == 0)
			stackNode.getChildren().clear();
		return c;
	}
	
	public Creature removeCreature(int i) {
		Creature c = stack.remove(i);
		c.setStackedIn(null);
		updateImage();
		if (stack.size() == 0)
			stackNode.getChildren().clear();
		return c;
	}
	
	// updates the image on the top of the stack (top creature, or more likely upside down creature)
	public void updateImage() {
		if (stack.size() > 0)
			stackImgV.setImage(stack.get(0).getImage());
		else
			stackImgV.setImage(null);
	}
	
	public boolean isEmpty() { return stack.isEmpty(); }
	
	private void setupImageView() {
		
		width = getWidth();
		stackNode.setMouseTransparent(true);
		
		// Creates ImageView
		stackImgV = ImageViewBuilder.create()
				.fitHeight(width)
				.preserveRatio(true)
				.build();
		
		// Create rectangle around stack
		stackRecBorder = RectangleBuilder.create()
				.width(width)
				.height(width)
				.strokeWidth(3)
				.stroke(owner.getColor())
				.strokeType(StrokeType.CENTERED)
				.fill(Color.TRANSPARENT)
				.effect(new GaussianBlur(3))
				.build();
		
		// Add to pieceNode
		stackNode.getChildren().add(0, stackImgV);
		stackNode.getChildren().add(1, stackRecBorder);
		stackNode.setVisible(false);
	}

	// Moves the stack to the correct spot when a new stack is added to the same terrain
	public void moveWithinTerrain(final double x, final double y) {

		moveWithin = TranslateTransitionBuilder.create()
				.fromX(stackNode.getTranslateX())
				.fromY(stackNode.getTranslateY())
				.toX(x)
				.toY(y)                    
                .interpolator(Interpolator.EASE_BOTH)
				.node(stackNode)
				.duration(Duration.millis(200))
				.cycleCount(1)
				.build();
	
		moveWithin.play();
	}
	
	// When a creature is clicked from the info panel that has overlapping creatures, this properly displays them
	public void cascade(Creature c) {
		int index = stack.indexOf(c);
		if (index != -1) {
			for (int i = index; i < stack.size(); i++) 
				stack.get(i).getPieceNode().toBack();
			for (int i = index - 1; i >= 0; i--) 
				stack.get(i).getPieceNode().toBack();
		}
	}
}
