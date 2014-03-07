package KAT;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/*
 * Class to represent the game Cup and its functionality.
 * Uses the singleton class pattern.
 */
public class TheCup {
    //An ArrayList of pieces remaining in the cup
    private ArrayList<Piece>      remainingPieces;
    private ArrayList<Piece>      originalPieces;
    //A unique and single instance of this class, retrieved by getInstance()
    private static TheCup         uniqueInstance;

    private TheCup() {
        remainingPieces = new ArrayList<Piece>();
        originalPieces = new ArrayList<Piece>();
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
    public ArrayList<Piece> drawPieces(int numberToDraw) {
        Random rand = new Random();
        ArrayList<Piece> pieces = new ArrayList<Piece>();
//        System.out.println("size of remainingPieces: " + remainingPieces.size());
        if (remainingPieces.size() == 0) {
            System.out.println("No more pieces left to draw.");
            return null;
        }

        for (int i = 0; i < numberToDraw; i++) {
            int index = rand.nextInt(remainingPieces.size());
            pieces.add(remainingPieces.get(index));
//            System.out.println(remainingPieces.get(index));
            remainingPieces.remove(index);
        }

        return pieces;
    }

    /* 
     * This method fills the remainingPieces arraylist with all initial game pieces.
     */
    public void initCup() {
    	BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "initCupCreatures.txt"));
            String line = null;
            while ((line = inFile.readLine()) != null) {
                Creature c = new Creature(line);
                remainingPieces.add(c);
                originalPieces.add(c);
               // System.out.println(c);
            }
            inFile.close();
            inFile = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "initCupIncome.txt"));
            while ((line = inFile.readLine()) != null) {
                SpecialIncome s = new SpecialIncome(line);
                remainingPieces.add(s);
                originalPieces.add(s);
                //System.out.println(s);
           }
           inFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found " + inFile);
        } catch (EOFException e) {
            System.out.println("EOF encountered");
        } catch (IOException e) {
            System.out.println("can't read from file");
        }
    }

    /*
     * Method to add a piece to the cup
     */
    public void addToCup(Piece p) {
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

    public ArrayList<Piece> getRemaining() { return remainingPieces; }
    public ArrayList<Piece> getOriginal() { return originalPieces; }
}