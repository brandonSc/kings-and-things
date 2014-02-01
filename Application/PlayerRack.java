package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;

/*
 * Class to represent a Player's rack.
 * Uses the singleton class pattern.
 */
public class PlayerRack {
    private ArrayList<String> piecesList; //list of the pieces on the rack.

    private static PlayerRack uniqueInstance; //unique instance of the rack.

    private PlayerRack() {
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