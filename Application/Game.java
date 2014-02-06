package KAT;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.Scanner;


public class Game extends Application {
    private Button doneButton;
    private Button selectButton;
    private InfoPanel infoPan;
    private Text helpText;
    private BorderPane root;
    private Board hexBoard;
    
    public Button getDoneButton(){ return doneButton; }
    public Button getSelectButton(){ return selectButton; }
    public InfoPanel getInfoPanel(){ return infoPan; }
    public Text getHelpText(){ return helpText; }
    public Board getBoard() { return hexBoard; }

	@Override
	public void start(Stage primaryStage) {
        Player user = null;
        // will move this to GameLoop later
        if( false ){
            Scanner s = new Scanner(System.in);

            System.out.println("\nConnecting to Kings&Things server...\n");
            Client client = new Client("localhost", 8888);
            client.connect();
            
            System.out.println("Enter your username: ");
            String username = s.nextLine();

            System.out.println("\nSigning in...\n");
            client.sendLogin(username);

            user = new Player(username, "RED");
        } else {
            user = new Player("User", "RED");
        }
		
		try {
            java.util.ArrayList<Player> tmp = new java.util.ArrayList<Player>();
            tmp.add(user);

			root = new BorderPane();
            HBox hbox = new HBox(10);
            hbox.setPadding(new Insets(10, 10, 10, 10));
            selectButton = new Button("Select");
            doneButton = new Button("Done");
            selectButton.setDisable(true);
            doneButton.setDisable(true);
            hbox.getChildren().addAll(selectButton, doneButton);
            helpText = new Text("initializing...");
            root.setTop(helpText);
            root.setBottom(hbox);
			Scene scene = new Scene(root,1500,700);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			
			hexBoard = new Board(root); // Must be called before new TileDeck
			Piece.setBaseImages();
			
			String[] iterOnePreSet = new String[]{"FrozenWaste","Forest","Jungle","Plains","Sea","Forest","Swamp","Plains","FrozenWaste","Mountains",
					"FrozenWaste","Swamp","Desert","Swamp","Forest","Desert","Plains","Mountains","Jungle","Swamp","Mountains","Jungle",
					"Swamp","Desert","Forest","Plains","Forest","FrozenWaste","Jungle","Mountains","Desert","Plains","Jungle","Mountains",
					"Forest","FrozenWaste","Desert"};
            GameLoop.getInstance().setPlayers(tmp);
			TileDeck theDeck = new TileDeck(root, iterOnePreSet);
			infoPan = new InfoPanel(root, theDeck);
			PlayerRackGUI rack = new PlayerRackGUI(root, user);
			TheCupGUI theCup = new TheCupGUI(root, rack);
			
			GameLoop.getInstance().initGame(theDeck, this);
			rack.generateButtons();

            // execute playGame method in a background thread 
            // as to not block main GUI thread
            new Thread(new Runnable(){
                public void run(){
                    while( true ){ 
                        GameLoop.getInstance().playGame();
                    }
                }
            }).start();
            
            System.out.println("Size of terrains: " + hexBoard.getTerrains().size());
            System.out.println(hexBoard.getTerrains().get(0).getContents());
            ArrayList tmpAry = new ArrayList<Piece>();
            tmpAry.add(new Creature("front", "back", "DragonRider", "FrozenWaste", 3, true, false, false, true));
            tmpAry.add(new Creature("front", "back", "ElkHerd", "FrozenWaste", 2, false, false, false, false));
            
            hexBoard.getTerrains().get(0).getContents().put("User", tmpAry);
            
           
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
