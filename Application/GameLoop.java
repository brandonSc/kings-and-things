package KAT;

import java.util.ArrayList;

/*
 * Class for handling the Game Loop and various game phases.
 * Uses the singleton class pattern.
 */
public class GameLoop {
	Player[] playerList; //list of the different players in the game. Strings for now until we have a Player class implementation.
	private static GameLoop uniqueInstance; //unique instance of the GameLoop class
	int phaseNumber; //int to keep track of which phase the game is on.
	boolean victory; //keeps track of whether the game has been one or not.
	private TheCup cup;

	/*
	 * Constructor.
	 */
	private GameLoop() {
		playerList = new Player[4];
		for (int i = 0; i < 4; i++)
			playerList[i] = new Player(""+i);
		phaseNumber = 8;
		victory = false;
	}

	/*
	 * returns a unique instance of the GameLoop class, unless one already exists.
	 */
	public static GameLoop getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new GameLoop();
        }
        return uniqueInstance;
    }

    /*
     * The first thing done in the game.
	 *
	 * (1) Initialize the Bank
     * (2) Initializes the Cup
     * (3) Determines which side of the special character pieces will be used.
     * (4) Populate the game board
     * (5) Determine Player starting positions
     * (6) Players pick their hexes.
     * (7) Players take 10 gold, 1 tower from the bank, 10 things from the cup.
     * (8) Players can exchange their "things" for ones drawn from the cup.
     * (9) Prepare the terrain deck.
     */
    public void initGame(TileDeck td) {
    	cup = TheCup.getInstance();
    	Board.populateGameBoard(td);
    }

    private void goldPhase() {
    	System.out.println("In the gold collection phase");
    }

    private void recruitPhase() {

    }

    private void randomEventPhase() {

    }

    private void movementPhase() {

    }

    private void combatPhase() {

    }

    private void constructionPhase() {

    }

    private void specialPowersPhase() {

    }

    private void changeOrderPhase() {
    	for (int i = 0; i < 4; i++)
    		System.out.print(playerList[i].getName() + " ,");
    	Player tmp = playerList[0];
    	playerList[0] = playerList[1];
    	playerList[1] = playerList[2];
    	playerList[2] = playerList[3];
    	playerList[3] = tmp;
    	System.out.println();
    	for (int i = 0; i < 4; i++)
    		System.out.print(playerList[i].getName() + " ,");
    	System.out.println();
    }

    public void playGame() {
    	System.out.println(phaseNumber);
    	switch (phaseNumber) {
    		case 1: System.out.println(phaseNumber + " gold phase");
    				goldPhase();
    				phaseNumber++;
    				break;
    		case 2: System.out.println(phaseNumber + " recruit phase");
    				recruitPhase();
    				phaseNumber++;
    				break;
    		case 3: System.out.println(phaseNumber + " random event phase");
    				randomEventPhase();
    				phaseNumber++;
    				break;
    		case 4: System.out.println(phaseNumber + " movement phase");
    				movementPhase();
    				phaseNumber++;
    				break;
    		case 5: System.out.println(phaseNumber + " combat phase");
    				combatPhase();
    				phaseNumber++;
    				break;
    		case 6: System.out.println(phaseNumber + " construction phase");
    				constructionPhase();
    				phaseNumber++;
    				break;
    		case 7: System.out.println(phaseNumber + " special powers phase");
    				specialPowersPhase();
    				phaseNumber++;
    				break;
    		case 8: System.out.println(phaseNumber + " change order phase");
    				changeOrderPhase();
    				phaseNumber = 1;
    				break;
    	}
    }
}