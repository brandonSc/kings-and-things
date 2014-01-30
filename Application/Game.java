package KAT;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

import java.util.Scanner;


public class Game extends Application {
	@Override
	public void start(Stage primaryStage) {
        if( false ){
            Scanner s = new Scanner(System.in);

            System.out.println("\nConnecting to Kings&Things server...\n");
            Client client = new Client("localhost", 8888);
            client.connect();
            
            System.out.println("Enter your username: ");
            String username = s.nextLine();

            System.out.println("\nSigning in...\n");
            client.sendLogin(username);
        }
        
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1500,700);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			
			Board hexBoard = new Board(root); // Must be called before new TileDeck
			
			String[] iterOnePreSet = new String[]{"FrozenWaste","Forest","Jungle","Plains","Sea","Forest","Swamp","Plains","FrozenWaste","Mountains",
					"FrozenWaste","Swamp","Desert","Swamp","Forest","Desert","Plains","Mountains","Jungle","Swamp","Mountains","Jungle",
					"Swamp","Desert","Forest","Plains","Forest","FrozenWaste","Jungle","Mountains","Desert","Plains","Jungle","Mountains",
					"Forest","FrozenWaste","Desert"};
			
			TileDeck theDeck = new TileDeck(root, iterOnePreSet);
			InfoPanel infoPan = new InfoPanel(root, theDeck);
			PlayerRackGUI rack = new PlayerRackGUI(root);
			TheCupGUI theCup = new TheCupGUI(root, rack);


		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}