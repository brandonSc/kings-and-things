package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;

/*
 * Class to represent a Player's rack.
 */
public class PlayerRack {
    private ArrayList<String> piecesList; //list of the pieces on the rack.

    public PlayerRack() {
        piecesList = new ArrayList<String>();
    }

    public ArrayList<String> getPieces() { return piecesList; }
}