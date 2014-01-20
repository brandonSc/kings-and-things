package KAT;

import java.util.Random;
import java.util.ArrayList;

/*
 * Class to represent the game Cup and its functionality
 *
 * Note: for now, all of the arraylists are for strings until we get a more solid
 * implementation of "things"
 */
public class TheCup {
    //An ArrayList of pieces remaining in the cup
    private ArrayList<String> remainingPieces = new ArrayList<String>();
    //A unique and single instance of this class, retrieved by getInstance()
    private static TheCup uniqueInstance;

    private TheCup() {
        initCup();
    }

    /**
     * @return the single unique instance of this class
     */
    public static TheCup getInstance(){
        if( uniqueInstance == null ){
            uniqueInstance = new TheCup();
        }
        return uniqueInstance;
    }

    /*
     * Function to randomly draw pieces from the Cup. Returns an arraylist of the pieces drawn.
     */
    public ArrayList<String> drawPieces(int numberToDraw) {
        Random rand = new Random();
        ArrayList<String> pieces = new ArrayList<String>();
        if (remainingPieces.size() == 0) {
            System.out.println("No more pieces left to draw.");
            return null;
        }

        for (int i = 0; i < numberToDraw; i++) {
            int index = rand.nextInt(remainingPieces.size());
            pieces.add(remainingPieces.get(index));
            remainingPieces.remove(index);
        }

        return pieces;
    }

    /* 
     * This method fills the remainingPieces arraylist with all initial game pieces.
     */
    public void initCup() {
        for (int i = 0; i < 234; i++) {
            remainingPieces.add("" + i);
        }
    }

    /*
     * Method to add a piece to the cup
     */
    public void addToCup(String p) {
        remainingPieces.add(p);
    }

    //Might become useful when we start using "things"
    public ArrayList<String> printList(ArrayList<String> list) {
        ArrayList<String> newList = new ArrayList<String>();
        for(String p: list) {
            newList.add(p);
        }
        return newList;
    }

    public ArrayList<String> getRemaining() { return remainingPieces; }
}
