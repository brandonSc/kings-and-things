package KAT;
/*
 * InfoPanel.java:
 * 
 * A panel on the left of the game that shows info on the object which was last clicked:
 * 
 * 		Tiles: Shows the tile type, and a list of Pieces on it
 * 		Players: maybe a small blurb about what they have got on the board etc...
 * 		More stuff:....
 * 
 * 
 */

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradientBuilder;
import javafx.scene.paint.StopBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InfoPanel {

	private static Group infoNode;//, textGroup;
	private static ImageView tileImageView, playerImageView, fortImageView, markerImageView;
	private static int[] currentTileCoords;
    private static Terrain currHex;
    private static HBox playerPieceLists;
    private static HBox playerNameTitles;
    private static Text[] listLables;
    private static double width, height;
    private static int numPlayers;
    private static String[] playerNames;
    
    private static HashMap<String, CreatureStack> contents;
    private static HashMap<String, Group> vBoxLists;
    private static HashMap<String, Text> textTitles;
    private static ArrayList<Piece> movers;
	
    /*
	 * Constructors
	 */
	public InfoPanel (BorderPane bp){
		
		movers = new ArrayList<Piece>();
		vBoxLists = new HashMap<String, Group>();
		textTitles = new HashMap<String, Text>();
		numPlayers = GameLoop.getInstance().getNumPlayers();
		playerNames = new String[4];
		for (int i = 0; i < numPlayers; i++)
			playerNames[i] = GameLoop.getInstance().getPlayers()[i].getName();
		
		width = bp.getWidth() * 0.25;
		height = bp.getHeight();
		currentTileCoords = new int[]{-1, -1, -1};
		infoNode = GroupBuilder.create()
				.children(RectangleBuilder.create()
						.width(width)
						.height(height)
						.fill(Color.DARKSLATEGRAY)
						.opacity(0.5)
						.build())
				.layoutX(0)
				.layoutY(0)
				.clip(RectangleBuilder.create()
						.width(width + 30)
						.height(height)
						.arcHeight(60)
						.arcWidth(60)
						.x(-30)
						.build())
				.build();
		bp.getChildren().add(infoNode);
		setUpImageViews();
        currHex = null;
	}
	
	/*
	 * Displays tile info:
	 * 
	 * Type of terrain is displayed on top.
	 * List of Peices will be displayed
	 * Owner of tile etc
	 */
	public static void showTileInfo(Terrain t) {

		if (t.getCoords().getX() != currentTileCoords[0] || t.getCoords().getY() != currentTileCoords[1] || t.getCoords().getZ() != currentTileCoords[2]) {
			// Image on top of panel
			// Includes owner marker and forts
			
			contents = t.getContents();			
		
            // re init fortImageView (so event handler works properly)
            fortImageView = ImageViewBuilder.create()
				.layoutX(width * 0.5)
				.layoutY(height * 0.03)
				.fitHeight(fortImageView.getLayoutBounds().getHeight())
				.preserveRatio(true)
				.build();
            infoNode.getChildren().add(fortImageView);
            
			
			// Clear things from infoPanel
			movers.clear();					
			fortImageView.setVisible(false);
			markerImageView.setVisible(false);
			
			// Change to new images
			tileImageView.setImage(t.getImage());
			tileImageView.setVisible(true);
			playerNameTitles.setVisible(true);
			if (t.getOwner() != null) {
				markerImageView.setImage(t.getOwner().getImage());
				markerImageView.setVisible(true);
				if (t.getFort() != null) {
                    final Fort fort = t.getFort();
					fortImageView.setImage(fort.getImage());
					fortImageView.setVisible(true);
                    fortImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, 
                            new EventHandler<MouseEvent>(){
                        @Override 
                        public void handle( MouseEvent e ){
                            if( GameLoop.getInstance().getPhase() == 6 ){
                                GameLoop.getInstance().attackPiece(fort);
                            }
                            System.out.println(fort.getName()+", "+fort);
                        }
                    });
				}
			}
			
			// Disables and hides all the boxes before showing the new ones
			playerPieceLists.getChildren().clear();
			Rectangle rec = (Rectangle) playerNameTitles.getChildren().remove(0);
			playerNameTitles.getChildren().clear();
			playerNameTitles.getChildren().add(rec);
			
			
			
			double creatureHeight = width * 0.23;
			Iterator<String> keySetIterator = contents.keySet().iterator();
	    	while(keySetIterator.hasNext()) {
	    		String key = keySetIterator.next();
	    	
	    		vBoxLists.get(key).getChildren().clear();
	    		playerNameTitles.getChildren().add(textTitles.get(key));
	    		
	    		// Currently, the number of creatures before the lists starts squeezing is 6. This will be changed to accomidate different screen sizes
	    		if (contents.get(key).getStack().size() <= 6) {
	    			for (int i = 0; i < contents.get(key).getStack().size(); i++) {
		    			vBoxLists.get(key).getChildren().add(contents.get(key).getStack().get(i).getPieceNode());
		    			vBoxLists.get(key).getChildren().get(i).relocate(0, creatureHeight * i);
		    			
		    			if (ClickObserver.getInstance().getActivePlayer().getName().equals(key))
		    				contents.get(key).getStack().get(i).uncover();
		    			else
		    				contents.get(key).getStack().get(i).cover();
		    			if (contents.get(key).getStack().get(i).doneMoving())
		    				contents.get(key).getStack().get(i).cover();
		    		}
	    		} else if (contents.get(key).getStack().size() > 6) {
	    			double offset = (width * 0.23 * contents.get(key).getStack().size() - ((Rectangle)infoNode.getClip()).getHeight() * 0.75) / (contents.get(key).getStack().size() - 1);
	    			
	    			for (int i = 0; i < contents.get(key).getStack().size(); i++) {
		    			vBoxLists.get(key).getChildren().add(contents.get(key).getStack().get(i).getPieceNode());
		    			vBoxLists.get(key).getChildren().get(i).relocate(0, (creatureHeight - offset) * i);
		    			if (ClickObserver.getInstance().getActivePlayer().getName().equals(key))
		    				contents.get(key).getStack().get(i).uncover();
		    			else
		    				contents.get(key).getStack().get(i).cover();
		    			if (contents.get(key).getStack().get(i).doneMoving())
		    				contents.get(key).getStack().get(i).cover();
		    		}
	    		}
	    		playerPieceLists.getChildren().add(vBoxLists.get(key));
	
	    	}
			
			currHex = t;
		} 
	}
    
	// Should maybe use ClickObserver instead of this?
    public Terrain getCurrHex(){
        return currHex;
    }
	
	/*
	 * Is called on setup of infopanel. Creates image views needed for terrain (Hex)
	 */
	private void setUpImageViews() {
		
		Hex imageHex = new Hex(width, true);
		tileImageView = ImageViewBuilder.create()
				.clip(imageHex)
				.layoutX(-imageHex.getWidthNeeded() + width)
				.layoutY(-imageHex.getHeightNeeded() * 0.65)
				.fitHeight(imageHex.getHeightNeeded())
				.preserveRatio(true)
				.build();
		
		fortImageView = ImageViewBuilder.create()
				.layoutX(width * 0.5)
				.layoutY(height * 0.03)
				.fitHeight(imageHex.getHeightNeeded() * 0.2)
				.preserveRatio(true)
				.build();
		
		markerImageView = ImageViewBuilder.create()
				.layoutX(width * 0.1)
				.layoutY(height * 0.03)
				.fitHeight(imageHex.getHeightNeeded() * 0.2)
				.preserveRatio(true)
				.build();

        playerPieceLists = HBoxBuilder.create()
        		.layoutX(0)
        		.layoutY(height * 0.25)
        		.build();
        
        // That stupid box that holds the player names
        // Was playing around with some paint properties
        // Will look better is the future
        playerNameTitles = HBoxBuilder.create()
        		.layoutY(height * 0.2)
        		.visible(false)
        		.padding(new Insets(width*0.005))
        		.children(RectangleBuilder.create()
        				.height(height * 0.05)
        				.width(width)
        				.managed(false)
        				.fill(LinearGradientBuilder.create()
        						.stops(
        							StopBuilder.create()
        								.color(Color.AZURE)
        								.offset(0.5)
        								.build(),
        							StopBuilder.create()
        								.color(Color.DARKBLUE)
        								.offset(0)
        								.build(),
        							StopBuilder.create()
        								.color(Color.DARKBLUE)
        								.offset(1)
        								.build())
        						.startX(1)
        						.startY(0)
        						.build())
        				.arcHeight(10)
        				.arcWidth(30)
        				.build())		
        		.build();
       
        for (int i = 0; i < numPlayers; i++) {
        	
        	Group creatureList = new Group();
        	
        	// Player name at top of list (in playerNameTitles)
        	Text playerNameTitle = TextBuilder.create()
        			.wrappingWidth(width * 0.23)
        			.text(" " + GameLoop.getInstance().getPlayers()[i].getName() + ": ")
        			.build();
        	
        	textTitles.put(playerNames[i], playerNameTitle);
        	vBoxLists.put(playerNames[i], creatureList);
        }
        
		infoNode.getChildren().add(tileImageView);
		infoNode.getChildren().add(fortImageView);
		infoNode.getChildren().add(markerImageView);
		infoNode.getChildren().add(playerNameTitles);
		infoNode.getChildren().add(playerPieceLists);
	}
	
	
	public static ArrayList<Piece> getMovers() { return movers; }
	
	public static double getWidth() { return width; }
	
}
