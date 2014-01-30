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
	private double height = 650;
	private static Group animView;
	private static int boardAnimCount;
	private static int[][] coordList;
	private static PathTransition pathTransition;
	private static double smallHexSideLength;
	
	/*
	 * Constructors
	 */
	public Board(BorderPane bp) {
		showTiles = false;
		terrains = new ArrayList<Terrain>();
		Terrain.setBaseImages();
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
				.id("BN")
				.build();
		
		
		// Calculate small hex size
		smallHexSideLength = (height * 3)/(Math.sqrt(3)*22);
		Terrain.setSideLength(smallHexSideLength);
		smallHexClip = new Hex(smallHexSideLength * Math.sqrt(3), true);
		
		
		/* anim stuff  ------------------------------------------------------------*/
		// Would like to move this into its own class maybe???
		
		Hex smallHole = new Hex(smallHexClip.getHeightNeeded() * 0.8, true);
		smallHole.relocate(smallHexClip.getWidthNeeded()/2 - smallHole.getWidthNeeded()/2, smallHexClip.getHeightNeeded()/2 - smallHole.getHeightNeeded()/2);
		Shape donutHex = Path.subtract(smallHexClip, smallHole);
		donutHex.setFill(Color.WHITESMOKE);
		animView = GroupBuilder.create()
				.children(donutHex)
				.build();
		boardNode.getChildren().add(animView);
		
		
    	final Animation tileSelected = new Transition() {
    	     {
    	         setCycleDuration(Duration.millis(1700));
    	         setCycleCount(INDEFINITE);
    	         setAutoReverse(true);
    	     }
    	     protected void interpolate(double frac) { animView.setOpacity(frac * 0.8); }
    	};
    	tileSelected.play(); 
		/* end anim stuff  ------------------------------------------------------------*/

	}
	
	/*
	 * Moves terrain pieces from TileDeck to Board. Sweet anim
	 */
	public static void populateGameBoard(final TileDeck td) {
		
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
	
	/*
	 * moves the animation for the selected tile piece
	 */
	public static void setSelectedAnimationLocation(int[] xyz) {
    	
		animView.relocate(1.5 * smallHexClip.getSideLength() * (xyz[0] + 3), (6 - xyz[1] + xyz[2]) * smallHexClip.getSideLength() * Math.sqrt(3)/2 + (Math.sqrt(3)*smallHexClip.getSideLength())/6);
    	
    }
	
}
