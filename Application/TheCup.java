package KAT;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Class to represent the game Cup and its functionality.
 * Uses the singleton class pattern.
 *
 * Note: for now, all of the arraylists are for strings until we get a more solid
 * implementation of "things"
 */
public class TheCup {
    //An ArrayList of pieces remaining in the cup
    private ArrayList<Piece>      remainingPieces;
    private ArrayList<Piece>      originalPieces;
    private HashMap<String,Piece> toReturn;
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
        toReturn = new HashMap<String,Piece>();
        for (int i = 0; i < 146; i++) {
            String tmp = "" + i;
            Creature c = new Creature("","",tmp,"",0,false,false,false,false);
            remainingPieces.add(c);
            originalPieces.add(c);
        }
        for (int i = 146; i < 234; i++) {
            String tmp = "" + i;
            Creature c = new Creature("","",tmp,"",0,false,false,false,false);
            remainingPieces.add(c);
            originalPieces.add(c);
        }

        toReturn.put("Cyclops",new Creature("Images/Mountains_Cyclops.png","Images/Creature_Back.png","Cyclops","MOUNTAIN",5,false,false,false,false));
        toReturn.put("Mountain Men",new Creature("Images/Mountains_MoutainMen.png","Images/Creature_Back.png","Mountain Men","MOUNTAIN",1,false,false,false,false));
        toReturn.put("Goblins",new Creature("Images/Mountains_Goblins.png","Images/Creature_Back.png","Goblins","MOUNTAIN",1,false,false,false,false));
    }

    public HashMap<Integer,ArrayList<Piece>> iterationOneInit() {
        HashMap<Integer,ArrayList<Piece>> iterOneList = new HashMap<Integer,ArrayList<Piece>>();
        for (int i = 0; i < 4; i++) {
            iterOneList.put(i, new ArrayList<Piece>());
        }
        ArrayList<Piece> values = iterOneList.get(0);
        values.add(new Creature("Images/Desert_OldDragon.png","Images/Creature_Back.png","Old Dragon","DESERT",4,true,true,false,false));
        values.add(new Creature("Images/Desert_GiantSpider.png","Images/Creature_Back.png","Giant Spider","DESERT",1,false,false,false,false));
        values.add(new Creature("Images/Jungle_Elephant.png","Images/Creature_Back.png","Elephant","JUNGLE",4,false,false,true,false));
        values.add(new Creature("Images/Mountains_BrownKnight.png","Images/Creature_Back.png","Brown Knight","MOUNTAIN",4,false,false,true,false));
        values.add(new Creature("Images/Mountains_Giant.png","Images/Creature_Back.png","Giant","MOUNTAIN",4,false,false,false,true));
        values.add(new Creature("Images/Mountains_Dwarves2R.png","Images/Creature_Back.png","Dwarves","MOUNTAIN",2,false,false,false,true));
        values.add(new Creature("Images/Desert_Skeletons.png","Images/Creature_Back.png","Skeletons","DESERT",1,false,false,false,false));
        values.add(new Creature("Images/Jungle_Watusi.png","Images/Creature_Back.png","Watusi","JUNGLE",2,false,false,false,false));
        values.add(new Creature("Images/Mountains_Goblins.png","Images/Creature_Back.png","Goblins","MOUNTAIN",1,false,false,false,false));
        values.add(new Creature("Images/Mountains_Ogre.png","Images/Creature_Back.png","Ogre","MOUNTAIN",2,false,false,false,false));
        values = iterOneList.get(1);
        values.add(new Creature("Images/Jungle_PterodactylWarriors.png","Images/Creature_Back.png","Pterodactyl Warriors","JUNGLE",2,true,false,false,true));
        values.add(new Creature("Images/Desert_Sandworm.png","Images/Creature_Back.png","Sandworm","DESERT",3,false,false,false,false));
        values.add(new Creature("Images/Forest_GreenKnight.png","Images/Creature_Back.png","Green Knight","FOREST",4,false,false,true,false));
        values.add(new Creature("Images/Desert_Dervish.png","Images/Creature_Back.png","Dervish","DESERT",2,false,true,false,false));
        values.add(new Creature("Images/Jungle_Crocodiles.png","Images/Creature_Back.png","Crocodiles","JUNGLE",2,false,false,false,false));
        values.add(new Creature("Images/Desert_Nomads.png","Images/Creature_Back.png","Nomads","DESERT",1,false,false,false,false));
        values.add(new Creature("Images/Forest_Druid.png","Images/Creature_Back.png","Druid","FOREST",3,false,true,false,false));
        values.add(new Creature("Images/Forest_WalkingTree.png","Images/Creature_Back.png","Walking Tree","FOREST",5,false,false,false,false));
        values.add(new Creature("Images/Jungle_CrawlingVines.png","Images/Creature_Back.png","Crawling Vines","JUNGLE",6,false,false,false,false));
        values.add(new Creature("Images/Forest_Bandits.png","Images/Creature_Back.png","Bandits","FOREST",2,false,false,false,false));
        values = iterOneList.get(2);
        values.add(new Creature("Images/Plains_Centaur.png","Images/Creature_Back.png","Centaur","PLAINS",2,false,false,false,false));
        values.add(new Creature("Images/Desert_CamelCorps.png","Images/Creature_Back.png","Camel Corps","DESERT",3,false,false,false,false));
        values.add(new Creature("Images/Plains_Farmers.png","Images/Creature_Back.png","Farmers","PLAINS",1,false,false,false,false));
        values.add(new Creature("Images/Plains_Farmers.png","Images/Creature_Back.png","Farmers","PLAINS",1,false,false,false,false));
        values.add(new Creature("Images/Desert_Djinn.png","Images/Creature_Back.png","Djinn","DESERT",4,false,true,false,false));
        values.add(new Creature("Images/Desert_Skeletons.png","Images/Creature_Back.png","Skeletons","DESERT",1,false,false,false,false));
        values.add(new Creature("Images/Jungle_Pygmies.png","Images/Creature_Back.png","Pygmies","JUNGLE",2,false,false,false,false));
        values.add(new Creature("Images/Plains_GreatHunter.png","Images/Creature_Back.png","Great Hunter","PLAINS",4,false,false,false,true));
        values.add(new Creature("Images/Desert_Nomads.png","Images/Creature_Back.png","Nomads","DESERT",1,false,false,false,false));
        values.add(new Creature("Images/Jungle_WitchDoctor.png","Images/Creature_Back.png","Witch Doctor","JUNGLE",2,false,true,false,false));
        values = iterOneList.get(3);
        values.add(new Creature("Images/Plains_Tribesmen2.png","Images/Creature_Back.png","Tribesmen","PLAINS",2,false,false,false,false));
        values.add(new Creature("Images/Swamp_GiantLizard.png","Images/Creature_Back.png","Giant Lizard","SWAMP",2,false,false,false,false));
        values.add(new Creature("Images/Plains_Villains.png","Images/Creature_Back.png","Villains","PLAINS",2,false,false,false,false));
        values.add(new Creature("Images/Jungle_Tigers.png","Images/Creature_Back.png","Tigers","JUNGLE",3,false,false,false,false));
        values.add(new Creature("Images/Swamp_VampireBat.png","Images/Creature_Back.png","Vampire Bat","SWAMP",4,true,false,false,false));
        values.add(new Creature("Images/Plains_Tribesmen2.png","Images/Creature_Back.png","Tribesmen","PLAINS",2,false,false,false,false));
        values.add(new Creature("Images/Swamp_DarkWizard.png","Images/Creature_Back.png","Dark Wizard","SWAMP",1,true,true,false,false));
        values.add(new Creature("Images/Swamp_BlackKnight.png","Images/Creature_Back.png","Black Knight","SWAMP",3,false,false,true,false));
        values.add(new Creature("Images/Jungle_GiantApe.png","Images/Creature_Back.png","Giant Ape","JUNGLE",5,false,false,false,false));
        values.add(new Creature("Images/Plains_BuffaloHerd3.png","Images/Creature_Back.png","Buffalo Herd","PLAINS",3,false,false,false,false));

        return iterOneList;
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

    public HashMap<String,Piece> iterOneSecondDraw() { return toReturn; }
    public ArrayList<Piece> getRemaining() { return remainingPieces; }
    public ArrayList<Piece> getOriginal() { return originalPieces; }
}