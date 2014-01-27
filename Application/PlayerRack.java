package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;

public class PlayerRack {
    private ArrayList<String> piecesList;

    private static PlayerRack uniqueInstance;

    PlayerRack() {
        piecesList = new ArrayList<String>();
    }

    /**
     * @return the single unique instance of this class
     */
    public static PlayerRack getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new PlayerRack();
        }
        return uniqueInstance;
    }

    public ArrayList<String> getPieces() { return piecesList; }
}