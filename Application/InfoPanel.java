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
    private static double width, hieght;
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
		hieght = bp.getHeight() * 0.8;
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
						.height(hieght)
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
				.layoutY(hieght * 0.03)
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
			//contents = ClickObserver.getInstance().getClickedTerrain().getContents();
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
            /*
			if (currHex.getOwner() != null) {
				ObservableList<String> newItems = FXCollections.observableArrayList();
				ArrayList<String> tileList = new ArrayList<String>();
				tileList = PlayerRack.printList(currHex.getContents(currHex.getOwner().getName()));
				newItems.addAll(tileList);
				piecesList.setItems(newItems);
			}
            */
			// clear piece lists from previous view
//			for( int i=0; i<4; i++ ){
//				if( playerPieceLists[i] != null ){
//					infoNode.getChildren().remove(playerPieceLists[i]);
//					infoNode.getChildren().remove(listLables[i]);
//					playerPieceLists[i] = null;
//					listLables[i] = null;
//				}
//			}
//			HashMap<String,ArrayList<Piece>> map = currHex.getContents();
//			int posX = 10, i = 0;
//			// add a list view for each player on the hex (usually only one, unless during combaat)
//			for( String name : map.keySet() ){
//				listLables[i] = new Text(name+":");
//				final ArrayList<Piece> pieces = map.get(name);
//				final ObservableList<Group> data = FXCollections.observableArrayList();
//				// list an image button for each piece
//				for( Piece p : pieces ){
//					Group b = new Group();
//					
//					try {
//						ImageView img = new ImageView(new Image(p.getFront()));
//						img.setFitHeight(40);
//						img.setFitWidth(40);
//						b.getChildren().add(img);
//					} catch( Exception e ){
//						
//						b.getChildren().add(Creature.getBackImage());
//					}
//                    b.setPrefWidth(40);
//					b.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
//						@Override
//						public void handle(MouseEvent e) {
//							Piece piece = pieces.get(data.indexOf(b));
//							System.out.println(piece.getName()+", "+piece);
//							if( GameLoop.getInstance().getPhase() == 6 ){
//								if( piece instanceof Combatable ){
//									GameLoop.getInstance().attackPiece((Combatable)piece);
//								} else {
//									System.out.println("Select a piece that can engage in combat");
//								}
//							}
//						}
//					});
//					data.add(b);
//				}
//				playerPieceLists[i] = new ListView<Group>();
//				playerPieceLists[i].setItems(data);
//				playerPieceLists[i].setPrefWidth(80);
//				playerPieceLists[i].setPrefHeight(350);
//				playerPieceLists[i].setLayoutX(5+posX);
//				playerPieceLists[i].setLayoutY(190);
//				playerPieceLists[i].setOrientation(Orientation.VERTICAL);
//				playerPieceLists[i].getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//		        listLables[i].setLayoutY(180);
//		        listLables[i].setLayoutX(5+posX);
//		        infoNode.getChildren().add(listLables[i]);
//		        infoNode.getChildren().add(pieceLists[i]);
//		        posX += 90;
//		        i++;
//			}
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
				.layoutY(hieght * 0.03)
				.fitHeight(imageHex.getHeightNeeded() * 0.2)
				.preserveRatio(true)
				.build();
		
		markerImageView = ImageViewBuilder.create()
				.layoutX(width * 0.1)
				.layoutY(hieght * 0.03)
				.fitHeight(imageHex.getHeightNeeded() * 0.2)
				.preserveRatio(true)
				.build();

        playerPieceLists = HBoxBuilder.create()
        		.layoutX(0)
        		.layoutY(hieght * 0.25)
        		.build();
       
        for (int i = 0; i < numPlayers; i++) {
        	
        	VBox creatureList = new VBox();
        	creatureList.setPadding(new Insets(width*0.005));
        	
        	// Player name at top of list
        	Text playerNameTitle = TextBuilder.create()
        			.layoutX(hieght * 0.27)
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
        		
        		setupEvent(creatureNode, playerNames[i], j-1);
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
	
	private void setupEvent(Group g, String s, int j) {
		final int eventJ = j;
		final String eventS = s;
		g.setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				creatureClicked(eventS, eventJ);
			}
		});
	}
	
	// Could probably omit using clickObserver for this, but for consistency's sake...
	private void creatureClicked(String s, int j) {
		
		if (!((Rectangle)((Group)vBoxLists.get(s).getChildren().get(j+1)).getChildren().get(1)).isVisible()) {
			if (s.equals(ClickObserver.getInstance().getActivePlayer().getName())) {
				ClickObserver.getInstance().setClickedCreature((Creature)contents.get(s).get(j));
				((Rectangle)((Group)vBoxLists.get(s).getChildren().get(j+1)).getChildren().get(1)).setVisible(true);
				ClickObserver.getInstance().whenCreatureClicked();
				movers.add((Creature)contents.get(s).get(j));
			}
		} else {
			((Rectangle)((Group)vBoxLists.get(s).getChildren().get(j+1)).getChildren().get(1)).setVisible(false);
			movers.remove((Creature)contents.get(s).get(j));
		}
	}
	
	public static void addMover(Creature c) {
		
	}
	
	public static ArrayList<Piece> getMovers() { return movers; }
	
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
