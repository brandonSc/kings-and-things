package KAT;
	

/*
 * Made the because I couldn't get the fXML to work in Game...didn't want to overwrite what you've done
 * 
 * This, along with the updated Terrain, and Board, need to be cleaned up still (including comments), and I will come back to do so. Just
 * wanted to upload something.
 * Also, Only loads desert tiles for now, but you get the idea.
 */
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
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

			Board hexBoard = new Board(root);
			InfoPanel infoPan = new InfoPanel(root);
			hexBoard.populateGameBoard(new TileDeck());
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
