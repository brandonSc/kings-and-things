import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View-Controller for the Kings & Things game board.
 * Event handlers and application logic may be found here.
 * See Game.fxml for the actual view implementation
 */ 
public class Game extends Application
{
    @Override
    public void start( Stage stage ){
        Parent root; 

        try {
            root = FXMLLoader.load(getClass().getResource("Game.fxml"));
            Scene scene = new Scene(root, 1200, 800); // window size
            stage.setTitle("Kings & Things");
            stage.setScene(scene);
            stage.show();
        } catch( IOException e ){
            e.printStackTrace();
        }
    }

    /**
     * main might eventually be in a different file
     */ 
    public static void main( String args[] ){
        System.out.println(" -[ Kings & Things ]- ");
        launch(args);
    }
}
