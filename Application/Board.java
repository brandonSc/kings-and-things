package KAT;


import java.util.ArrayList;
import java.util.Arrays;

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
	
	private static ArrayList<Terrain> terrains;
	private static Group boardNode;
	private boolean showTiles; //Tiles upside down or not?
	private Hex hexClip;
	private static Hex smallHexClip;
	private static double height = 650;
	private static int boardAnimCount;
	private static int[][] coordList;
	private static PathTransition pathTransition;
	private static double smallHexSideLength;
	private static Group nodePT;
	private static Shape selectionClipper;
	
	/*
	 * Constructors
	 */
 	public Board(BorderPane bp) {
 		
 		nodePT =  new Group();
 		nodePT.setDisable(true);
		showTiles = false;
		terrains = new ArrayList<Terrain>();
		generateHexes(bp);
		showTiles = false;
		boardAnimCount = 0;
		coordList = new int[][]{
				{0, 0, 0},
				{0, 1, -1},{1, 0, -1},{1, -1, 0},{0, -1, 1},{-1, 0, 1},{-1, 1, 0},
				{-1, 2, -1},{0, 2, -2},{1, 1, -2},{2, 0, -2},{2, -1, -1},{2, -2, 0},{1, -2, 1},{0, -2, 2},{-1, -1, 2},{-2, 0, 2},{-2, 1, 1},{-2, 2, 0},
				{-2, 3, -1},{-1, 3, -2},{0, 3, -3},{1, 2, -3},{2, 1, -3},{3, 0, -3},{3, -1, -2},{3, -2, -1},{3, -3, 0},{2, -3, 1},{1, -3, 2},{0, -3, 3},{-1, -2, 3},{-2, -1, 3},{-3, 0, 3},{-3, 1, 2},{-3, 2, 1},{-3, 3, 0}
		};

		bp.getChildren().add(0, boardNode);
		
	}
	
	/*
	 * getters and setters
	 */
	public ArrayList<Terrain> getTerrains() { return terrains; }
	public static double getHeight() { return height; }
	
	public void setTerrains(ArrayList<Terrain> terrains) { this.terrains = terrains; }
	
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
		if (boardAnimCount == 0) {
			for (int i = 0; i < 37; i++) 
				terrains.add(td.getNoRemove(i));
		}
		if (boardAnimCount < 37) {
			final int[] tempCoord = coordList[boardAnimCount];
			final double x = - td.getTileDeckNode().getLayoutX() + boardNode.getLayoutX() + 1.5 * smallHexSideLength * (tempCoord[0] + 3) + smallHexClip.getWidthNeeded();
			final double y = - td.getTileDeckNode().getLayoutY() + boardNode.getLayoutY() + (6 - tempCoord[1] + tempCoord[2]) * smallHexSideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*smallHexSideLength)/6 + smallHexClip.getHeightNeeded()/4 - boardAnimCount * 1.5;
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
							finishedMove(tempCoord, td, x, y);
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
	private static void finishedMove(int[] xyz, TileDeck td, double x, double y) {
		terrains.add(td.getTopTile().positionNode(boardNode, xyz, x - smallHexClip.getWidthNeeded()/2, y - smallHexClip.getHeightNeeded()/2));
		boardAnimCount++;
	}
	
	// Controls the animation of the moving stacks
	public static void animStackMove(Terrain from, final Terrain to, Image movingImg) {
    	
		double imgVHeight = smallHexClip.getHeightNeeded() * 27/83 * 0.8;
		ImageView imgVPT = ImageViewBuilder.create()
			.image(movingImg)
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
		path.getElements().add(new MoveTo(1.5 * smallHexSideLength * (from.getCoords()[0] + 3) + imgVHeight/2 + from.getStacksNode().get(ClickObserver.getInstance().getActivePlayer().getName()).getLayoutX(),
				(6 - from.getCoords()[1] + from.getCoords()[2]) * smallHexClip.getHeightNeeded()/2 + (Math.sqrt(3)*smallHexSideLength)/6 + imgVHeight/2 + from.getStacksNode().get(ClickObserver.getInstance().getActivePlayer().getName()).getLayoutY()));
		// LineTo path element that marks the end of the transition
		path.getElements().add(new LineTo(1.5 * smallHexSideLength * (to.getCoords()[0] + 3) + imgVHeight/2 + to.getStacksNode().get(ClickObserver.getInstance().getActivePlayer().getName()).getLayoutX(),
				(6 - to.getCoords()[1] + to.getCoords()[2]) * smallHexClip.getHeightNeeded()/2 + (Math.sqrt(3)*smallHexSideLength)/6 + imgVHeight/2 + to.getStacksNode().get(ClickObserver.getInstance().getActivePlayer().getName()).getLayoutY()));
		
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
					to.setStacksImages();
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
}

