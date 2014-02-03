package KAT;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.geometry.Insets;

import java.util.Scanner;
import java.util.ArrayList;

public class Game extends Application {
    private Button doneButton;
    private Button selectButton;
    private InfoPanel infoPan;
    private Text helpText;

    public Button getDoneButton(){ return doneButton; }
    public Button getSelectButton(){ return selectButton; }
    public InfoPanel getInfoPanel(){ return infoPan; }
    public Text getHelpText(){ return helpText; }

	@Override
	public void start(Stage primaryStage) {
        Player user;

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
            user = new Player("RED");
        }
        
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1500,700);
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
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			
			Board hexBoard = new Board(root); // Must be called before new TileDeck
			
			String[] iterOnePreSet = new String[]{"FrozenWaste","Forest","Jungle","Plains","Sea","Forest","Swamp","Plains","FrozenWaste","Mountains",
					"FrozenWaste","Swamp","Desert","Swamp","Forest","Desert","Plains","Mountains","Jungle","Swamp","Mountains","Jungle",
					"Swamp","Desert","Forest","Plains","Forest","FrozenWaste","Jungle","Mountains","Desert","Plains","Jungle","Mountains",
					"Forest","FrozenWaste","Desert"};
			
			TileDeck theDeck = new TileDeck(root, iterOnePreSet);
			infoPan = new InfoPanel(root, theDeck);
			PlayerRackGUI rack = new PlayerRackGUI(root);
			TheCupGUI theCup = new TheCupGUI(root, rack);
			GameLoop.getInstance().initGame(theDeck, this);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
