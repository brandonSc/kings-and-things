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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class InfoPanel {

	private static Group infoNode;//, textGroup;
	private static ImageView tileImageView, playerImageView, fortImageView, markerImageView;
	private static int[] currentTileCoords;
    private static Terrain currHex;
    private static HBox playerPieceLists;
    private static Text[] listLables;
    private static double width, height;
    private static int numPlayers;
    private static String[] playerNames;
    
    private static HashMap<String, ArrayList<Piece>> contents;
    private static HashMap<String, VBox> vBoxLists;
    private static ArrayList<Piece> movers;
	
    /*
	 * Constructors
	 */
	public InfoPanel (BorderPane bp){
		
		movers = new ArrayList<Piece>();
		vBoxLists = new HashMap<String, VBox>();
		numPlayers = GameLoop.getInstance().getNumPlayers();
		playerNames = new String[4];
		for (int i = 0; i < numPlayers; i++)
			playerNames[i] = GameLoop.getInstance().getPlayers()[i].getName();
		
		width = bp.getWidth() * 0.2;
		height = bp.getHeight() * 0.8;
		currentTileCoords = new int[]{-1, -1, -1};
		infoNode = GroupBuilder.create()
				.children(RectangleBuilder.create()
						.width(bp.getWidth() * 0.2)
						.height(bp.getHeight() * 0.8)
						.fill(Color.LIGHTGRAY)
						.build())
				.layoutX(0)
				.layoutY(5+bp.getHeight() * 0.1)
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
//        piecesList = new ListView<String>();
//        piecesList.setItems(null);
//        piecesList.setPrefWidth(100);
//        piecesList.setPrefHeight(150);
//        piecesList.setLayoutX(0);
//        piecesList.setLayoutY(bp.getHeight() * 0.25);
//        //infoNode.getChildren().add(piecesList);
//        listLables = new Text[4];
	}
	
	/*
	 * Displays tile info:
	 * 
	 * Type of terrain is displayed on top.
	 * List of Peices will be displayed
	 * Owner of tile etc
	 */
	public static void showTileInfo(Terrain t) {

		if (t.getCoords()[0] != currentTileCoords[0] || t.getCoords()[1] != currentTileCoords[1] || t.getCoords()[2] != currentTileCoords[2]) {
			// Image on top of panel
			// Includes owner marker and forts
			
			contents = t.getContents();			
		
            // re init fortImageView (so event handler works properly)
            //setUpImageViews();
            
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
			
			for (int i = 0; i < numPlayers; i++) {
				for (int j = 1; j < 11; j++) {
					// Set group non visible
					vBoxLists.get(playerNames[i]).getChildren().get(j).setDisable(true);
					vBoxLists.get(playerNames[i]).getChildren().get(j).setVisible(false);
					// set selected rectangle not visible
					((Group)vBoxLists.get(playerNames[i]).getChildren().get(j)).getChildren().get(1).setVisible(false);
				}
			}
			
			// add a list view for each player on the hex (usually only one, unless during combat)
			// includes some crazy casting for now
			for( String name : contents.keySet() ){
		        int j = 1;
		        for (Piece aPiece : contents.get(name)) {
		        	((ImageView)((Group)vBoxLists.get(name).getChildren().get(j)).getChildren().get(0)).setImage(aPiece.getImage());
		        	((Group)vBoxLists.get(name).getChildren().get(j)).setVisible(true);
		        	((Group)vBoxLists.get(name).getChildren().get(j)).setDisable(false);
		        	j++;
		        }
		        playerPieceLists.getChildren().add(vBoxLists.get(name));
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
       
        for (int i = 0; i < numPlayers; i++) {
        	
        	VBox creatureList = new VBox();
        	creatureList.setPadding(new Insets(width*0.005));
        	
        	// Player name at top of list
        	Text playerNameTitle = TextBuilder.create()
        			.layoutX(height * 0.27)
        			.layoutX(5 + i*width * 0.27)
        			.text(" " + GameLoop.getInstance().getPlayers()[i].getName() + ": ")
        			.build();
        	creatureList.getChildren().add(0, playerNameTitle);
        	
        	for (int j = 1; j < 11; j++) {
        		// Square that appears around a selected creature before moving
        		Rectangle creatureRec = RectangleBuilder.create()
        				.visible(false)
        				.stroke(Color.WHITESMOKE)
        				.strokeWidth(3)
        				.width(width*0.23)
        				.height(width*0.23)
        				.fill(Color.TRANSPARENT)
        				.build();
        		
        		// ImageView for creature
        		ImageView creatureImgV = ImageViewBuilder.create()
        				.fitHeight(width*0.23)
        				.preserveRatio(true)
        				.build();
        		
        		// Combined ImageView and Rectangle
        		Group creatureNode = GroupBuilder.create()
        				.visible(false)
        				.disable(true)
        				.build();
        		
        		setupCreatureEvent(creatureNode, playerNames[i], j-1);
        		creatureNode.getChildren().add(0, creatureImgV);
        		creatureNode.getChildren().add(1, creatureRec);
        		creatureList.getChildren().add(j, creatureNode);
        		
        	}
        	vBoxLists.put(playerNames[i], creatureList);
        }
        		
		infoNode.getChildren().add(tileImageView);
		infoNode.getChildren().add(fortImageView);
		infoNode.getChildren().add(markerImageView);
		infoNode.getChildren().add(playerPieceLists);
	}
	
	private void setupCreatureEvent(Group g, String s, int j) {
		final int eventJ = j;
		final String eventS = s;
		g.setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				creatureClicked(eventS, eventJ);
			}
		});
	}
	
	private void creatureClicked(String s, int j) {
		
		System.out.println("creatureClicked(String s, int j)");
		System.out.println("creatureClicked(String s, int j)");
		
		if (ClickObserver.getInstance().getCreatureFlag().equals("Movement: SelectMovers")) {
			System.out.println("Movement: SelectMovers");
			if (!((Rectangle)((Group)vBoxLists.get(s).getChildren().get(j+1)).getChildren().get(1)).isVisible() && !((Creature)contents.get(s).get(j)).doneMoving()) {
				
				if (s.equals(ClickObserver.getInstance().getActivePlayer().getName())) {
					ClickObserver.getInstance().setClickedCreature((Creature)contents.get(s).get(j));
					((Rectangle)((Group)vBoxLists.get(s).getChildren().get(j+1)).getChildren().get(1)).setVisible(true);
					ClickObserver.getInstance().whenCreatureClicked();
					movers.add((Creature)contents.get(s).get(j));
				}
			} else {
				((Rectangle)((Group)vBoxLists.get(s).getChildren().get(j+1)).getChildren().get(1)).setVisible(false);
				movers.remove((Creature)contents.get(s).get(j));
				if (movers.size() == 0)
					ClickObserver.getInstance().setTerrainFlag("");
			}
		} else if (ClickObserver.getInstance().getCreatureFlag().equals("Combat: SelectCreatureToAttack")) {
			System.out.println("Combat: SelectCreatureToAttack");
			if (!((Creature)contents.get(s).get(j)).getOwner().equals(ClickObserver.getInstance().getActivePlayer().getName())) {
				GameLoop.getInstance().attackPiece((Creature)contents.get(s).get(j));
			}
		}
	}
	
	public static ArrayList<Piece> getMovers() { return movers; }
	
	public static void removeMover(Creature c, int j) {
		
		movers.remove(c);
		if (movers.size() == 0)
			ClickObserver.getInstance().setTerrainFlag("");

		int k = 0;
		for (int i = 1; i < 11; i++) {
			if (((Group)vBoxLists.get(ClickObserver.getInstance().getActivePlayer().getName()).getChildrenUnmodifiable().get(i)).getChildrenUnmodifiable().get(1).isVisible()) {
				k++;
				if (k == j) {
					((Group)vBoxLists.get(ClickObserver.getInstance().getActivePlayer().getName()).getChildrenUnmodifiable().get(i)).getChildrenUnmodifiable().get(1).setVisible(false);
				}
			}
		}
	}
	
	
//	public ListView<Button> getCurrentList( String username ){
//		ListView<Button> list = null;
//		for( int i=0; i<4; i++ ){
//			if( listLables[i] != null ){
//				if( listLables[i].getText() == username ){
//					list = playerPieceLists[i];
//				}
//			}
//		}
//		return list;
//	}
}
