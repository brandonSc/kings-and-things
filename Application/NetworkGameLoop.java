package KAT;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.application.Platform;

/*
 * Class for handling the Game Loop and various game phases.
 * Uses the singleton class pattern.
 */
public class NetworkGameLoop extends GameLoop {
    private Player[] playerList; //list of the different players in the game. Strings for now until we have a Player class implementation.
    private static Game GUI;
    private int phaseNumber; //int to keep track of which phase the game is on.
    private static NetworkGameLoop uniqueInstance;
    private TheCup cup;
    private Player player;
    private boolean isPaused, freeClicked, paidClicked, doneClicked;
    private int numPlayers;
    private int gameSize;
    private PlayerRackGUI rackG;
    private KATClient client;

    /*
     * Constructor.
     */
    private NetworkGameLoop() {
        phaseNumber = 0;
        numPlayers = 1;
        gameSize = 2;
        cup = TheCup.getInstance();
        freeClicked = false;
        paidClicked = false;
        doneClicked = false;
        // cup.initCup(); // called already in super constructor
        // playerList = new Player[4];
        client = new KATClient("localhost", 8888);
        client.connect();
        // should display this message in the gui to notify user 
        System.out.println("Connecting to server ...");
        try { Thread.sleep(1000); } catch( Exception e ){ }
    }

    /*
     * returns a unique instance of the GameLoop class, unless one already exists.
     */
    public static NetworkGameLoop getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new NetworkGameLoop();
        }
        return uniqueInstance;
    }

    public void setPlayers(ArrayList<Player> player) {
        System.out.println("Enter username: ");
        java.util.Scanner s = new java.util.Scanner(System.in);
        String username = s.nextLine();
        this.player = new Player(username, "YELLOW");

        client.sendLogin(username, 2);
        System.out.println("Waiting for more players...");
        pauseForNet();
        /*
        int i = 0;
        playerList = new Player[4];
        this.player = player.get(0);
        playerList[0] = this.player;
        numPlayers++;

        for (Player p : player) {
            playerList[i] = p;
            playerList[i].addGold(10);
            playerList[i].getPlayerRack().setOwner(playerList[i]);
            playerList[i].getPlayerRack().setPieces(cup.drawInitialPieces(10));
            System.out.println(playerList[i].getName() + ": "+ PlayerRack.printList(playerList[i].getPlayerRack().getPieces()));
            i++;
            numPlayers++;
       }
       */
    }

    public void addPlayer(Player p) {
    	if (playerList == null || playerList.length == 0) {
    		numPlayers = 1;
    		playerList = new Player[1];
    		playerList[0] = p;
    	} else {
	    	Player[] tempPlayerList = new Player[playerList.length + 1];
	    	for (int i = 0; i < playerList.length; i++) {
	    		tempPlayerList[i] = playerList[i];
	    	}
	    	tempPlayerList[tempPlayerList.length - 1] = p;
	    	playerList = tempPlayerList;
	    	for (Player delete : tempPlayerList) {
	    		delete = null;
	    	}
	    	numPlayers++;
    	}
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
    public void initGame(Game GUI) {
        rackG = GUI.getRackGui();

        this.GUI = GUI;
//        setupListeners();
        pause();
        phaseNumber = -1; 
        ClickObserver.getInstance().setTerrainFlag("Setup: deal");
        setButtonHandlers();
    }
    
    public void addStartingHexToPlayer(){
        final Coord[] validPos = { 
            new Coord(2,-3,1),new Coord(2,1,-3),new Coord(-2,3,-1),new Coord(-2,-1,3)
        };
        
        Terrain t = ClickObserver.getInstance().getClickedTerrain();
        
        if( t == null ){
            System.out.println("Select a hex");     
        } else {
            Coord coords = t.getCoords();
            for( int i=0; i<validPos.length; i++ ){
                if( !t.isOccupied() &&  validPos[i].equals(coords)){
                     player.addHexOwned(t);
                     t.setOwner(player);
                     System.out.println("selected "+t.getType());
                     break;
                }
            }
        }
        unPause();
    }
    
    public void addHexToPlayer(){
        Terrain t = ClickObserver.getInstance().getClickedTerrain();
        ArrayList<Terrain> hexes = player.getHexesOwned();
        
        for( Terrain h : hexes ){
            if( t.compareTo(h) == 1 &&  !t.isOccupied() ){
                player.addHexOwned(t);
                t.setOwner(player);
                unPause();
                break;
            }
        }
    }

    public void playThings() {
        if (doneClicked) {
            unPause();
        }
    }

    public void constructFort() {
        PlayerRackGUI.disableAll();
        Terrain t = ClickObserver.getInstance().getClickedTerrain();
        ArrayList<Terrain> hexes = player.getHexesOwned();

        for( Terrain h : hexes ){
            if( t.compareTo(h) == 0 ){
                // during setup phase, players are given a tower for free
                if( phaseNumber != -1 ){
                    player.spendGold(5);
                } 
                player.constructFort(t);
                t.setFortImage();
                unPause();
                break;
            }
        }
        PlayerBoard.getInstance().updateGold(player);
    }

    private void setupPhase() {
        System.out.println("setup phase!");
        // prompt each player to select their initial starting position
        ClickObserver.getInstance().setTerrainFlag("Setup: SelectStartTerrain");
        // Covering all terrains that are not valid selections
        final Coord[] startSpots = {
            new Coord(2,-3,1),new Coord(2,1,-3),new Coord(-2,3,-1),new Coord(-2,-1,3)
        };
        ClickObserver.getInstance().setActivePlayer(this.player);

        GUI.getHelpText().setText("Setup Phase: " + player.getName() 
                + ", select a valid hex to start your kingdom.");
        pause();
        GUI.getHelpText().setText("Setup Phase: waiting for other players...");
        pauseForNet();

        // next prompt each player to select an adjacent hex
        ClickObserver.getInstance().setTerrainFlag("Setup: SelectTerrain");
        // loop 2 times so each player adds 2 more hexes
        for( int i=0; i<2; i++ ){
            ClickObserver.getInstance().setActivePlayer(this.player);           
            final ArrayList<Terrain> ownedHexes = player.getHexesOwned();
            Board.applyCovers();
            for (Terrain t1 : ownedHexes) {
                Iterator<Coord> keySetIterator = Board.getTerrains().keySet().iterator();
                while(keySetIterator.hasNext()) {
                    Coord key = keySetIterator.next();
                    Terrain t2 = Board.getTerrains().get(key);
                    if (t2.compareTo(t1) == 1 && !t2.isOccupied() && !t2.getType().equals("SEA"))
                        t2.uncover();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GUI.getRackGui().setOwner(player);
                    }
                });
                GUI.getHelpText().setText("Setup Phase: " + this.player.getName() 
                        + ", select an adjacent hex to add to your kingdom.");
                // forces the GameLoop thread to wait until unPause() is called
                pause();
            }
        }
        // prompt each player to place their first tower
        ClickObserver.getInstance().setTerrainFlag("Construction: ConstructFort");
        for( Player p : playerList ) {
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            
            Board.applyCovers();
            ArrayList<Terrain> ownedHexes = player.getHexesOwned();
            for (Terrain t : ownedHexes) {
            	if (t.getOwner().getName().equals(player.getName()))
            		t.uncover();
            }
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getRackGui().setOwner(player);
                }
            });
            GUI.getHelpText().setText("Setup Phase: " + player.getName() 
                    + ", select one of your tiles to place a tower.");
            while( isPaused ){
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        // allow players to add some or all things to their tiles.
        ClickObserver.getInstance().setTerrainFlag("RecruitingThings: PlaceThings");
        GUI.getDoneButton().setDisable(false);
        for (Player p : playerList) {
            this.player = p;
            doneClicked = false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getRackGui().setOwner(player);
                }
            });
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            
            Board.applyCovers();
            ArrayList<Terrain> ownedHexes = player.getHexesOwned();
            for (Terrain t : ownedHexes) {
            	if (t.getOwner().getName().equals(player.getName()))
            		t.uncover();
            }
            
            GUI.getHelpText().setText("Setup Phase: " + player.getName()
                    + ", place some or all of your things on a tile you own.");
        }
        ClickObserver.getInstance().setTerrainFlag("");
        Board.removeCovers();
    }

    /*
     * Each player in the game MUST do this phase.
     * Calculates the amount of gold that each player earns this turn.
     */
    private void goldPhase() {
        System.out.println("In the gold collection phase");
        GUI.getHelpText().setText("Gold Collection phase: income collected.");
        for (int i = 0; i < 4; i++){
            playerList[i].addGold(playerList[i].calculateIncome());
            PlayerBoard.getInstance().updateGold(playerList[i]);
        }
        try { Thread.sleep(2000); } catch( InterruptedException e ){ return; }
    }

    /*
     * Optional.
     * Players can attempt to recruit one special character.
     */
    private void recruitSpecialsPhase() {
        // skip for first iteration
    }

    /*
     * Players MUST do this.
     * Draw free things from the cup.
     * Buy paid recruits.
     * Trade unwanted things from their rack.
     * Place things on the board.
     */
    private void recruitThingsPhase() {
        GUI.getDoneButton().setDisable(false);
        int numToDraw = 0;
        boolean flag;
        
        for (Player p : playerList) {
            doneClicked = false;
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(player);
            flag = true;
            pause();
            GUI.getHelpText().setText("Recruitment Phase: " + p.getName()
                + ", draw free/paid Things from The Cup, then click 'done'");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getRackGui().setOwner(player);
                    System.out.println(GUI.getRackGui().getOwner().getName() + " -recruit run later");
                    TheCupGUI.update();
                }
            });
            
            while (isPaused) {
                while (!doneClicked) {
                    if (freeClicked) {
                        if (flag) {
                            System.out.println(player.getName() + " -clicked free");
                            numToDraw = (int)Math.ceil(player.getHexesOwned().size() / 2.0);
                            System.out.println(numToDraw + " -num to draw");
                            TheCupGUI.setFieldText(""+numToDraw);
                            flag = false;
                        }
                    }
                    if (paidClicked) {
                        flag = true;
                        if (flag) {
                            PlayerBoard.getInstance().updateGold(player);
                            flag = false;
                        }
                    }
                    try { Thread.sleep(100); } catch( Exception e ){ return; }
                }
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        GUI.getDoneButton().setDisable(true);
    }

    /*
     * Optional.
     * Each player can play ONE random event from their rack.
     */
    private void randomEventPhase() {
        // skip for first iteration
    }

    /*
     * Optional.
     * Players may attempt to move their counters around the board.
     */
    private void movementPhase() {
        GUI.getDoneButton().setDisable(false);
        for (Player p : playerList) {
        	player = p;
	        ClickObserver.getInstance().setActivePlayer(player);
	        ClickObserver.getInstance().setCreatureFlag("Movement: SelectMovers");
	        pause();
	        GUI.getHelpText().setText("Movement Phase: " + player.getName()
                    + ", Move your armies");
	        
	        while (isPaused) {
            	try { Thread.sleep(100); } catch( Exception e ){ return; }  
	        }
        }
        GUI.getDoneButton().setDisable(true);
        ClickObserver.getInstance().setCreatureFlag("");
    }

    /*
     * Optional, unless combat is declared on you.
     * Players may explore or fight battles.
     */
    private void combatPhase() {
    	pause();
    	ClickObserver.getInstance().setTerrainFlag("Disabled");
    	ClickObserver.getInstance().setCreatureFlag("Combat: SelectCreatureToAttack");
    	for( Player p : playerList ){
    		this.player = p;
    		ArrayList<Terrain> hexes = player.getHexesWithPiece();
    		ClickObserver.getInstance().setActivePlayer(player);
	    	for( final Terrain t : hexes ){
	    		// check if player is on another player's hex
	    		if( t.getContents().keySet().size() > 1 ){
	    			System.out.println("combat");
	    			CreatureStack pieces = t.getContents(player.getName());
	    			// magic phase, attack an enemy creature for each owned magic creature
	    			for( Piece piece : pieces.getStack() ){ 
	    				if( piece instanceof Creature ){
	    					final Creature c = (Creature)piece;
	    					if( c.isMagic() ){
	    						// should roll dice first, if less than combatValue, then skip while loop
	    						Platform.runLater(new Runnable() {
	    			                @Override
	    			                public void run() {
	    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
	    	    								+c.getName()+" to attack.");
	                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
	    			                }
	    			            });
	    						while( isPaused ){
	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				    	}
	    					}
	    				}
	    			}
	    			// ranged phase, attack an enemy creature for each owned ranged creature
	    			for( Piece piece : pieces.getStack() ){ 
	    				if( piece instanceof Creature ){
	    					final Creature c = (Creature)piece; 
	    					if( c.isFlying() ){
	    						Platform.runLater(new Runnable() {
	    			                @Override
	    			                public void run() {
	    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
	    	    								+c.getName()+" to attack.");
	                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
	    			                }
	    			            });
	    						pause();
	    						// should roll dice first, if less than combatValue, then skip while loop
	    						while( isPaused ){
	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				    	}
	    					}
	    				}
	    			}
	    			// melee phase, attack an enemy creature for all other owned creatures
	    			for( Piece piece : pieces.getStack() ){ 
	    				if( piece instanceof Creature ){
	    					final Creature c = (Creature)piece; 
	    					if( c.isCharging() ){
	    						// attack twice for charging creatures
	    						for( int i=0; i<2; i++ ){
		    						// should roll dice first, if less than combatValue, then skip while loop
		    						Platform.runLater(new Runnable() {
		    			                @Override
		    			                public void run() {
		    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
		    	    								+c.getName()+" to attack.");
		                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
		    			                }
		    			            });
		    						pause();
		    						while( isPaused ){
		    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
		    				    	}
	    						}
	    					} else if( !c.isMagic() && !c.isRanged() && !c.isCharging() ){
                                Platform.runLater(new Runnable() {
	    			                @Override
	    			                public void run() {
	    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
	    	    								+c.getName()+" to attack.");
	                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
	    			                }
	    			            });
                                pause();
	    						while( isPaused ){
	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				    	}
	    					}
	    				}
	    			}
	    		}
	    	}
    	}  
    	ClickObserver.getInstance().setTerrainFlag("");
    }
    
    public void attackPiece( Combatable piece ){
    	System.out.println("Attacking piece");
    	Terrain t = ClickObserver.getInstance().getClickedTerrain();
    	
    	if( piece instanceof Creature ){
    		t.removeFromStack(player.getName(), (Creature)piece);
    		if( t.getContents(player.getName()).isEmpty() ){
    			player.removeHex(t);
    		}
    	}
        if( piece instanceof Fort ){
            /*
        	if( t.getOwner().getName() != player.getName() ){
        		System.out.println(
                        "Oops! that is your own tower, select something else");
        		return;
        	}
            */
        }
    	piece.inflict();
    	GUI.getInfoPanel().showTileInfo(t);
    	unPause();
    	System.out.println("done attacking");
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
     * Temporary for iteration one since they want us to skip the changing order phase.
     */
    private void changeOrderPhaseIterOne() {

    }

    /*
     * Main loop of the game. Uses a phase number to determine which phase the game should be in.
     */
    public void playGame() {
        switch (phaseNumber) {
            case 0: System.out.println(phaseNumber + " setup phase");
                    setupPhase();
                    doneClicked = false;
                    GUI.getDoneButton().setDisable(true);
                    phaseNumber++;
                    break;
            case 1: System.out.println(phaseNumber + " gold phase");
                    goldPhase();
                    doneClicked = false;
                    GUI.getDoneButton().setDisable(true);
                    phaseNumber++;
                    break;
            case 2: System.out.println(phaseNumber + " recruit specials phase");
                    recruitSpecialsPhase();
                    doneClicked = false;
                    phaseNumber++;
                    break;
            case 3: System.out.println(phaseNumber + " recruit things phase");
                    doneClicked = false;
                    recruitThingsPhase();
                    GUI.getDoneButton().setDisable(false);
                    phaseNumber++;
                    break;
            case 4: System.out.println(phaseNumber + " random event phase");
                    randomEventPhase();
                    doneClicked = false;
                    GUI.getDoneButton().setDisable(false);
                    phaseNumber++;
                    break;
            case 5: System.out.println(phaseNumber + " movement phase");
                    movementPhase();
                    GUI.getDoneButton().setDisable(true);
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
                    changeOrderPhaseIterOne();
                    phaseNumber = 1;
                    break;
        }
    }
    
    public void pause(){
        isPaused = true;
        while( isPaused ){
            try { Thread.sleep(100); } catch( Exception e ){ return; }
        }
    }

    public void pauseForNet(){
        isPaused = true;
        while( isPaused ){
            try { Thread.sleep(5000); } catch( Exception e ){ return; }
            client.getGameState(this.player.getName());
        }
    }

    public void unPause(){
        isPaused = false;
    }

    void setButtonHandlers(){
        GUI.getDoneButton().setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle( ActionEvent e ){
                doneClicked = true;
                if( phaseNumber == 3 ) {
                    freeClicked = false;
                    paidClicked = false;
                    doneClicked = true;
                    unPause();
                }
            	unPause();
            }
        });
    }

    public void stop(){
        client.disconnect();
        unPause();
    }

    public void setFree(boolean b) { freeClicked = b; }
    public void setPaid(boolean b) { paidClicked = b; }
    public int getPhase() { return phaseNumber; }
    public int getNumPlayers() { return numPlayers; }
    public Player[] getPlayers() { return playerList; }
    public Player getPlayer(){ return this.player; }
    
    public void setPhase(int i) { phaseNumber = i; }
}