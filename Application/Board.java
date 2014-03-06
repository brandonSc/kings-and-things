package KAT;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.PathTransitionBuilder;
import javafx.animation.Transition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.RadialGradientBuilder;
import javafx.scene.paint.Stop;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Board {
	
	private static HashMap <Coord, Terrain> terrains;
	private boolean showTiles; //Tiles upside down or not?
	private static Coord[] coordList;

	private static int boardAnimCount;
	private static double smallHexSideLength;
	private static double height = 650;
	private static PathTransition pathTransition;
	private Hex hexClip;
	private static Hex smallHexClip;
	private static Group nodePT;		// The node that shows the stacks moving from one terrain to another
	private static Group boardNode;
	
	/*
	 * Constructors
	 */
 	public Board(BorderPane bp) {
 		
 		nodePT =  new Group();
 		nodePT.setDisable(true);
		showTiles = false;
		terrains = new HashMap <Coord, Terrain>();
		generateHexes(bp);
		showTiles = false;
		boardAnimCount = 0;
		coordList = new Coord[]{
				new Coord(0, 0, 0),
				new Coord(0, 1, -1),new Coord(1, 0, -1),new Coord(1, -1, 0),new Coord(0, -1, 1),new Coord(-1, 0, 1),new Coord(-1, 1, 0),
				new Coord(-1, 2, -1),new Coord(0, 2, -2),new Coord(1, 1, -2),new Coord(2, 0, -2),new Coord(2, -1, -1),new Coord(2, -2, 0),new Coord(1, -2, 1),new Coord(0, -2, 2),new Coord(-1, -1, 2),new Coord(-2, 0, 2),new Coord(-2, 1, 1),new Coord(-2, 2, 0),
				new Coord(-2, 3, -1),new Coord(-1, 3, -2),new Coord(0, 3, -3),new Coord(1, 2, -3),new Coord(2, 1, -3),new Coord(3, 0, -3),new Coord(3, -1, -2),new Coord(3, -2, -1),new Coord(3, -3, 0),new Coord(2, -3, 1),new Coord(1, -3, 2),new Coord(0, -3, 3),new Coord(-1, -2, 3),new Coord(-2, -1, 3),new Coord(-3, 0, 3),new Coord(-3, 1, 2),new Coord(-3, 2, 1),new Coord(-3, 3, 0)
		};

		bp.getChildren().add(0, boardNode);
		
	}
	
	/*
	 * getters and setters
	 */
 	public static HashMap<Coord, Terrain> getTerrains() { return terrains; }
	public static double getHeight() { return height; }
	
	public void setTerrains(HashMap<Coord, Terrain> terrains) { this.terrains = terrains; }
	
	/*
	 * Creates the hex shapes used for clipping for the board pane,
	 * Calculates the child hexes size (terrain peices)
	 * Sets up the hex shaped animation for a selected tile
	 */
	private void generateHexes(BorderPane bp) {
		
		// Set up large hex that defines the board:
		hexClip = new Hex(height, false);
		boardNode = GroupBuilder.create()
				.clip(hexClip)
				.layoutX((bp.getWidth() - hexClip.getWidthNeeded())/2)
				.layoutY((bp.getHeight() - hexClip.getHeightNeeded())/2)
				.build();
		
		// Calculate small hex size
		smallHexSideLength = (height * 3)/(Math.sqrt(3)*22);
		Terrain.setSideLength(smallHexSideLength);
		smallHexClip = new Hex(smallHexSideLength * Math.sqrt(3), true);

	}
	
	/*
	 * Moves terrain pieces from TileDeck to Board. Sweet anim
	 */
	public static void populateGameBoard(final TileDeck td) {
		int numHexes;
		if (boardAnimCount == 0) {
			for (int i = 0; i < 37; i++)  {
				terrains.put(coordList[i], td.getNoRemove(td.getDeckSize() - i - 1));
				terrains.get(coordList[i]).setCoords(coordList[i]);
			}
		}
		if (boardAnimCount < 37) {
			final Coord tempCoord = coordList[boardAnimCount];
			final double x = - td.getTileDeckNode().getLayoutX() + boardNode.getLayoutX() + 1.5 * smallHexSideLength * (tempCoord.getX() + 3) + smallHexClip.getWidthNeeded();
			final double y = - td.getTileDeckNode().getLayoutY() + boardNode.getLayoutY() + (6 - tempCoord.getY() + tempCoord.getZ()) * smallHexSideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*smallHexSideLength)/6 + smallHexClip.getHeightNeeded()/4 - boardAnimCount * 1.5;
			Path path = new Path();
			path.getElements().add(new MoveTo(smallHexClip.getWidthNeeded()/2, smallHexClip.getHeightNeeded()/2));
			path.getElements().add(new LineTo(x, y));
			pathTransition = PathTransitionBuilder.create()
					.duration(Duration.millis(50))
					.path(path)
					.orientation(PathTransition.OrientationType.NONE)
					.autoReverse(false)
					.cycleCount(1)
					.node(td.getTopTileNoRemove().getNode())
					.onFinished(new EventHandler(){
						@Override
						public void handle(Event event) {
							finishedMove(td, x, y);
							populateGameBoard(td);
						}
					})
					.build();
		
			pathTransition.play();
		} else
			td.slideOut();		
	}
	
	/*
	 * Called by populateGameBoard. Used to workaround changing non-final objects in eventHandler
	 */
	private static void finishedMove(TileDeck td, double x, double y) {
		td.getTopTile().positionNode(boardNode, x - smallHexClip.getWidthNeeded()/2, y - smallHexClip.getHeightNeeded()/2);
		boardAnimCount++;
	}
	
	// Controls the animation of the moving stacks
	public static void animStackMove(final Terrain from, final Terrain to, final String player) {
    	
		double imgVHeight = smallHexClip.getHeightNeeded() * 27/83 * 0.8;
		ImageView imgVPT = ImageViewBuilder.create()
			.image(to.getContents(ClickObserver.getInstance().getActivePlayer().getName()).getStack().get(0).getImage())
			.fitHeight(imgVHeight)
			.preserveRatio(true)
			.build();
		
		nodePT.getChildren().add(imgVPT);
		nodePT.setVisible(true);
		boardNode.getChildren().add(nodePT);
    	PathTransition pt;
    	Path path = new Path();
    	
    	// MoveTo and LineTo, both relative to the boardNode, so there is some math to convert from the smallHexNodes
    	// MoveTo path element that marks the start of the transition
    	Group mover = from.getContents(ClickObserver.getInstance().getActivePlayer().getName()).getCreatureNode();
    	Group moverTo = to.getContents(ClickObserver.getInstance().getActivePlayer().getName()).getCreatureNode();
		from.clearTerrainHM(ClickObserver.getInstance().getActivePlayer().getName());
		
		path.getElements().add(new MoveTo(1.5 * smallHexSideLength * (from.getCoords().getX() + 3) + imgVHeight/2 + mover.getTranslateX(),
				(6 - from.getCoords().getY() + from.getCoords().getZ()) * smallHexClip.getHeightNeeded()/2 + (Math.sqrt(3)*smallHexSideLength)/6 + imgVHeight/2 + mover.getTranslateY()));
		// LineTo path element that marks the end of the transition
		path.getElements().add(new LineTo(1.5 * smallHexSideLength * (to.getCoords().getX() + 3) + imgVHeight/2 + moverTo.getTranslateX(),
				(6 - to.getCoords().getY() + to.getCoords().getZ()) * smallHexClip.getHeightNeeded()/2 + (Math.sqrt(3)*smallHexSideLength)/6 + imgVHeight/2 + moverTo.getTranslateY()));
		
		
		pt = PathTransitionBuilder.create()
			.duration(Duration.millis(1000))
			.path(path)
			.node(nodePT)
			.orientation(PathTransition.OrientationType.NONE)
			.autoReverse(false)
			.cycleCount(1)
			.onFinished(new EventHandler(){
				@Override
				public void handle(Event event) {
					ClickObserver.getInstance().setTerrainFlag("");
					to.getContents(player).getCreatureNode().setVisible(true);
					to.getContents(player).updateImage();
					deleteNodePT();
					
				}
			})
			.build();
		pt.play();
	}
    
	// Delete the node containing the image of the moving stack animation once the stack has completed its move
	private static void deleteNodePT() { 
		boardNode.getChildren().remove(nodePT);
		nodePT.setVisible(false);
	}
	
	// removes the nodes shading out the terrain
	public static void removeCovers() {
		Iterator<Coord> keySetIterator = terrains.keySet().iterator();
    	while(keySetIterator.hasNext()) {
    		Coord key = keySetIterator.next();
    		terrains.get(key).uncover();
    	}
	}
	// covers all terrains
	public static void applyCovers() {
		Iterator<Coord> keySetIterator = terrains.keySet().iterator();
		int i = 0;
    	while(keySetIterator.hasNext()) {
    		Coord key = keySetIterator.next();
    		terrains.get(key).cover();
    		i++;
    	}
	}
	
	// covers all terrains, except the ones in the passed arraylist
	public static void applyCovers(ArrayList<Coord> coords) {
		Iterator<Coord> keySetIterator = terrains.keySet().iterator();
    	while(keySetIterator.hasNext()) {
    		Coord key = keySetIterator.next();
    		terrains.get(key).cover();
			for (Coord coord : coords) {
				if (terrains.get(key).compareTo(coord) == 0)
					terrains.get(key).uncover();
			}
    	}
	}
	// covers all terrains, except the ones this creature can move to
	public static void applyCovers(Creature c) {
		Iterator<Coord> keySetIterator = terrains.keySet().iterator();
    	while(keySetIterator.hasNext()) {
    		Coord key = keySetIterator.next();
			if (!c.canMoveTo(ClickObserver.getInstance().getClickedTerrain(), terrains.get(key)))
				terrains.get(key).cover();
		}
	}
	
	// Returns the Terrain with the required Coord
	public static Terrain getTerrainWithCoord(Coord c) {
		Iterator<Coord> keySetIterator = terrains.keySet().iterator();
    	while(keySetIterator.hasNext()) {
    		Coord key = keySetIterator.next();
    		
    		if (key.isEqual(c))
    			return terrains.get(key);
    		
    	}
    	return null;
	}
}

