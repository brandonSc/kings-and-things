package KAT;

import java.util.ArrayList;

/*
 * Class for handling the Game Loop and various game phases.
 * Uses the singleton class pattern.
 */
public class GameLoop {
	private Player[] playerList; //list of the different players in the game. Strings for now until we have a Player class implementation.
	private static GameLoop uniqueInstance; //unique instance of the GameLoop class
	private int phaseNumber; //int to keep track of which phase the game is on.
	private TheCup cup;

	/*
	 * Constructor.
	 */
	private GameLoop() {
		playerList = new Player[4];
		for (int i = 0; i < 4; i++)
			playerList[i] = new Player(""+i);
		phaseNumber = 3;
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

    /*
     * Each player in the game MUST do this phase.
     * Calculates the amount of gold that each player earns this turn.
     */
    private void goldPhase() {
    	System.out.println("In the gold collection phase");
        for (int i = 0; i < 4; i++)
            playerList[i].calculateIncome();
    }

    /*
     * Optional.
     * Players can attempt to recruit one special character.
     */
    private void recruitSpecialsPhase() {

    }

    /*
     * Players MUST do this.
     * Draw things from the cup.
     * Trade unwanted things from their rack
     * Place things on the board.
     */
    private void recruitThingsPhase() {

    }

    /*
     * Optional.
     * Each player can play ONE random event from their rack.
     */
    private void randomEventPhase() {

    }

    /*
     * Optional.
     * Players may attempt to move their counters around the board.
     */
    private void movementPhase() {

    }

    /*
     * Optional, unless combat is declared on you.
     * Players may explore or fight battles.
     */
    private void combatPhase() {

    }

    /*
     * Optional.
     * Each player may build forts.
     */
    private void constructionPhase() {

    }

    /*
     * Optional.
     * Master Thief and Assassin Primus may use their special powers.
     */
    private void specialPowersPhase() {

    }

    /* 
     * Mandatory
     * This happens last. The player order gets shifted by 1, i.e. 1st->4th, 2nd->1st, etc.
     */
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

    /*
     * Main loop of the game. Uses a phase number to determine which phase the game should be in.
     */
    public void playGame() {
    	System.out.println(phaseNumber);
    	switch (phaseNumber) {
    		case 1: System.out.println(phaseNumber + " gold phase");
    				goldPhase();
    				phaseNumber++;
    				break;
    		case 2: System.out.println(phaseNumber + " recruit specials phase");
    				recruitSpecialsPhase();
    				phaseNumber++;
    				break;
            case 3: System.out.println(phaseNumber + " recruit things phase");
                    recruitThingsPhase();
                    phaseNumber++;
                    break;
    		case 4: System.out.println(phaseNumber + " random event phase");
    				randomEventPhase();
    				phaseNumber++;
    				break;
    		case 5: System.out.println(phaseNumber + " movement phase");
    				movementPhase();
    				phaseNumber++;
    				break;
    		case 6: System.out.println(phaseNumber + " combat phase");
    				combatPhase();
    				phaseNumber++;
    				break;
    		case 7: System.out.println(phaseNumber + " construction phase");
    				constructionPhase();
    				phaseNumber++;
    				break;
    		case 8: System.out.println(phaseNumber + " special powers phase");
    				specialPowersPhase();
    				phaseNumber++;
    				break;
    		case 9: System.out.println(phaseNumber + " change order phase");
    				changeOrderPhase();
    				phaseNumber = 1;
    				break;
    	}
    }

    public int getPhase() { return phaseNumber; }
}