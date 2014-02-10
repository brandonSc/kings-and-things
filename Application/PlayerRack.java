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
    private ArrayList<Piece> piecesList; //list of the pieces on the rack.
    private static Player owner;

    public PlayerRack() {
        piecesList = new ArrayList<Piece>();
    }

    public ArrayList<Piece> getPieces() { return piecesList; }

    public static ArrayList<String> printList(ArrayList<Piece> pList) {
        ArrayList<String> newList = new ArrayList<String>();
        for (Piece p : pList)
            newList.add(p.getName());
        return newList;
    }

    public void setOwner(Player p) { owner = p; }
    public Player getOwner() { return owner; }
}
