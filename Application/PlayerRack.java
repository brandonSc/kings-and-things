package KAT;

import java.util.ArrayList;

/*
 * Class to represent a Player's rack.
 * Uses the Observer pattern to broadcast changes to the PlayerRackGUI.
 */
public class PlayerRack implements Subject {
    private ArrayList<Piece> piecesList; //list of the pieces on the rack.
    private ArrayList<Observer> observers;
    private Player    owner;

    public PlayerRack() {
        piecesList = new ArrayList<Piece>();
        observers = new ArrayList<Observer>();
    }

    public void registerObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        observers.get(0).update();
    }

    public void addPiece(Piece p) {
        piecesList.add(p);
        System.out.println(piecesList.size() + "=== piecesList size after adding");
        PlayerBoard.getInstance().updateNumOnRack(owner);
        notifyObservers();
    }

    public void removePiece(Piece p) {
        piecesList.remove(p);
        PlayerBoard.getInstance().updateNumOnRack(owner);
        notifyObservers();
    }

    public void removePiece(int i) {
        piecesList.remove(i);
        PlayerBoard.getInstance().updateNumOnRack(owner);
        notifyObservers();
    }

    public ArrayList<Piece> getPieces() { return piecesList; }
    public void setPieces(ArrayList<Piece> p) { piecesList = p; }

    public static ArrayList<String> printList(ArrayList<Piece> pList) {
        ArrayList<String> newList = new ArrayList<String>();
        for (Piece p : pList)
            newList.add(p.getName());
        return newList;
    }

    public void setOwner(Player p) { 
        owner = p;
        for (Piece pc : piecesList) {
            if (pc.getOwner() == null)
                pc.setOwner(owner);
        }
    }

    public Player getOwner() { return owner; }
    
    public int getNumOnRack() { return piecesList.size(); }
}
