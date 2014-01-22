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


public class Game extends Application {
	@Override
	public void start(Stage primaryStage) {
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1300,700);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			Board hexBoard = new Board(root);
			InfoPanel infoPan = new InfoPanel(root);
			hexBoard.populateGameBoard(new TileDeck());
			TheCupGUI theCup = new TheCupGUI(root);
			 

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
