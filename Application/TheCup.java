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

    public ArrayList<Piece> drawInitialPieces(int numberToDraw) {
        Random rand = new Random();
        ArrayList<Piece> pieces = new ArrayList<Piece>();
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
     * Function to randomly draw pieces from the Cup. Returns an arraylist of the pieces drawn.
     */
    public HashMap<Integer,Integer> drawPieces(int numberToDraw) {
        Random rand = new Random();
        HashMap<Integer,Integer> pieces = new HashMap<Integer,Integer>();
//        System.out.println("size of remainingPieces: " + remainingPieces.size());
        if (remainingPieces.size() == 0) {
            System.out.println("No more pieces left to draw.");
            return null;
        }

        for (int i = 0; i < numberToDraw; i++) {
            int index = rand.nextInt(remainingPieces.size());
            pieces.put(i,originalPieces.indexOf(remainingPieces.get(index)));
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
            //File used to read in the different creatures.
            inFile = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "initCupCreatures.txt"));
            String line = null;
            while ((line = inFile.readLine()) != null) {
                Creature c = new Creature(line);
                remainingPieces.add(c);
                originalPieces.add(c);
            }
            inFile.close();
            //File used to read in the different special incomes.
            inFile = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "initCupIncome.txt"));
            while ((line = inFile.readLine()) != null) {
                SpecialIncome s = new SpecialIncome(line);
                remainingPieces.add(s);
                originalPieces.add(s);
           }
           inFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found " + inFile);
        } catch (EOFException e) {
            System.out.println("EOF encountered");
        } catch (IOException e) {
            System.out.println("can't read from file");
        }
        //Adding the Random Events to the cup.
        remainingPieces.add(new BigJuju());
        originalPieces.add(new BigJuju());
        remainingPieces.add(new DarkPlague());
        originalPieces.add(new DarkPlague());
        remainingPieces.add(new Defection());
        originalPieces.add(new Defection());
        remainingPieces.add(new GoodHarvest());
        originalPieces.add(new GoodHarvest());
        remainingPieces.add(new MotherLode());
        originalPieces.add(new MotherLode());
        remainingPieces.add(new Teeniepox());
        originalPieces.add(new Teeniepox());
        remainingPieces.add(new TerrainDisaster());
        originalPieces.add(new TerrainDisaster());
        remainingPieces.add(new Vandals());
        originalPieces.add(new Vandals());
        remainingPieces.add(new WeatherControl());
        originalPieces.add(new WeatherControl());
        remainingPieces.add(new WillingWorkers());
        originalPieces.add(new WillingWorkers());

        //Adding the Magic Events to the cup.
        remainingPieces.add(new Balloon());
        originalPieces.add(new Balloon());
        remainingPieces.add(new Bow());
        originalPieces.add(new Bow());
        remainingPieces.add(new DispelMagicScroll());
        originalPieces.add(new DispelMagicScroll());
        remainingPieces.add(new DustOfDefense());
        originalPieces.add(new DustOfDefense());
        remainingPieces.add(new Elixir());
        originalPieces.add(new Elixir());
        remainingPieces.add(new Fan());
        originalPieces.add(new Fan());
        remainingPieces.add(new Firewall());
        originalPieces.add(new Firewall());
        remainingPieces.add(new Golem());
        originalPieces.add(new Golem());
        remainingPieces.add(new LuckyCharm());
        originalPieces.add(new LuckyCharm());
        remainingPieces.add(new Sword());
        originalPieces.add(new Sword());
        remainingPieces.add(new Talisman());
        originalPieces.add(new Talisman());
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
