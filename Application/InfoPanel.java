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

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.HashMap;

public class InfoPanel {

	private static Group infoNode;//, textGroup;
	private static ImageView currentImageView, tileImageView, playerImageView;
	private static Image tileImage;
	private static int[] currentTileCoords;
	private static TileDeck tileDeck;
    private static Terrain currHex;
    private static ListView<String> piecesList;
    private static ListView<Button>[] pieceLists;
    private static Text[] listLables;
	
    /*
	 * Constructors
	 */
	public InfoPanel (BorderPane bp, TileDeck td){
		
		tileDeck = td;
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
						.width(bp.getWidth() * 0.2 + 30)
						.height(bp.getHeight() * 0.8)
						.arcHeight(60)
						.arcWidth(60)
						.x(-30)
						.build())
				.build();
		bp.getChildren().add(infoNode);
		setUpImageViews();
        currHex = null;
        piecesList = new ListView<String>();
        piecesList.setItems(null);
        piecesList.setPrefWidth(100);
        piecesList.setPrefHeight(150);
        piecesList.setLayoutX(0);
        piecesList.setLayoutY(bp.getHeight() * 0.25);
        //infoNode.getChildren().add(piecesList);
        pieceLists = new ListView[4];
        listLables = new Text[4];
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
			infoNode.getChildren().remove(currentImageView);
			tileImage = t.getImage();
			tileImageView.setImage(tileImage);
			currentImageView = tileImageView;
			infoNode.getChildren().add(currentImageView);
			
			// List of things on that tile. Owner of tile
			
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
			for( int i=0; i<4; i++ ){
				if( pieceLists[i] != null ){
					infoNode.getChildren().remove(pieceLists[i]);
					infoNode.getChildren().remove(listLables[i]);
					pieceLists[i] = null;
					listLables[i] = null;
				}
			}
			HashMap<String,ArrayList<Piece>> map = currHex.getContents();
			int posX = 10, i = 0;
			// add a list view for each player on the hex (usually only one, unless during combaat)
			for( String name : map.keySet() ){
				listLables[i] = new Text(name+":");
				final ArrayList<Piece> pieces = map.get(name);
				final ObservableList<Button> data = FXCollections.observableArrayList();
				// list an image button for each piece
				for( Piece p : pieces ){
					final Button b = new Button();
					try {
						ImageView img = new ImageView(new Image(p.getFront()));
						img.setFitHeight(40);
						img.setFitWidth(40);
						b.setGraphic(img);
					} catch( Exception e ){
						b.setText(p.getName());
					}
                    b.setPrefWidth(40);
					b.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
						@Override
						public void handle(MouseEvent e) {
							Piece piece = pieces.get(data.indexOf(b));
							System.out.println(piece.getName()+", "+piece);
							if( GameLoop.getInstance().getPhase() == 6 ){
								if( piece instanceof Combatable ){
									GameLoop.getInstance().attackPiece((Combatable)piece);
								} else {
									System.out.println("Select a piece that can engage in combat");
								}
							}
						}
					});
					data.add(b);
				}
		        pieceLists[i] = new ListView<Button>();
		        pieceLists[i].setItems(data);
		        pieceLists[i].setPrefWidth(80);
		        pieceLists[i].setPrefHeight(350);
		        pieceLists[i].setLayoutX(5+posX);
		        pieceLists[i].setLayoutY(190);
		        pieceLists[i].setOrientation(Orientation.VERTICAL);
		        pieceLists[i].getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		        listLables[i].setLayoutY(180);
		        listLables[i].setLayoutX(5+posX);
		        infoNode.getChildren().add(listLables[i]);
		        infoNode.getChildren().add(pieceLists[i]);
		        posX += 90;
		        i++;
			}
		} 
	}
    
    public Terrain getCurrHex(){
        return currHex;
    }
	
	/*
	 * Is called on setup of infopanel. Creates image views needed for terrain (Hex)
	 */
	private void setUpImageViews() {
		Hex imageHex = new Hex(infoNode.getChildren().get(0).getLayoutBounds().getWidth(), true);
		tileImageView = ImageViewBuilder.create()
				.clip(imageHex)
				.layoutX(-imageHex.getWidthNeeded() + infoNode.getChildren().get(0).getLayoutBounds().getWidth())
				.layoutY(-imageHex.getHeightNeeded() * 0.5)
				.fitHeight(imageHex.getHeightNeeded())
				.preserveRatio(true)
				.build();

	}
	
	public ListView<Button> getCurrentList( String username ){
		ListView<Button> list = null;
		for( int i=0; i<4; i++ ){
			if( listLables[i] != null ){
				if( listLables[i].getText() == username ){
					list = pieceLists[i];
				}
			}
		}
		return list;
	}
}
