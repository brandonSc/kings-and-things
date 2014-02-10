package KAT;

import java.awt.Image;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.application.Platform;

/*
 * Class for handling the Game Loop and various game phases.
 * Uses the singleton class pattern.
 */
public class GameLoop {
	private Player[] playerList; //list of the different players in the game. Strings for now until we have a Player class implementation.
	private static GameLoop uniqueInstance; //unique instance of the GameLoop class
    private static Game GUI;
	private int phaseNumber; //int to keep track of which phase the game is on.
	private TheCup cup;
    private Player player;
    private boolean isPaused;
    private int numPlayers = 0;
    private PlayerRackGUI rackG;

	/*
	 * Constructor.
	 */
	private GameLoop() {
		phaseNumber = 0;
        cup = TheCup.getInstance();
        // playerList = new Player[4];
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

    public void setPlayers(ArrayList<Player> player) {
        int i = 0;
        playerList = new Player[4];
        this.player = player.get(0);
        for (Player p : player) {
            playerList[i] = p;
            playerList[i].addGold(10);
            playerList[i].getPlayerRack().setOwner(playerList[i]);
            playerList[i].getPlayerRack().getPieces().addAll(cup.iterationOneInit().get(i));
            System.out.println(playerList[i].getName() + ": "+ PlayerRack.printList(playerList[i].getPlayerRack().getPieces()));
            i++;
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
    public void initGame(TileDeck td, Game GUI) {
        rackG = GUI.getRackGui();
    	cup = TheCup.getInstance();
        cup.initCup();
        this.GUI = GUI;
//        setupListeners();
   // 	Board.populateGameBoard(td);
        pause();
        phaseNumber = -1; 
        ClickObserver.getInstance().setFlag("TileDeck: deal");
        setButtonHandlers();
    }
    
    public void addStartingHexToPlayer(){
    	final int[][] validPos = { 
    		{2,-3,1},{2,1,-3},{-2,3,-1},{-2,-1,3}
    	};
    	
        Terrain t = ClickObserver.getInstance().getClickedTerrain();

        if( t == null ){
            System.out.println("Select a hex");     
        } else {
        	int[] coords = t.getCoords();
        	for( int i=0; i<validPos.length; i++ ){
                if( !t.isOccupied() 
                &&  validPos[i][0] == coords[0] 
                &&  validPos[i][1] == coords[1] 
                &&  validPos[i][2] == coords[2] ){
                     player.addHex(t);
                     t.setOwner(player);
                     System.out.println("selected "+t.getType());
                     break;
                }
            }
        }
    }
    
    public void addHexToPlayer(){
    	Terrain t = ClickObserver.getInstance().getClickedTerrain();
    	ArrayList<Terrain> hexes = player.getHexes();
    	
    	for( Terrain h : hexes ){
    		if( t.compareTo(h) == 1
            &&  !t.isOccupied() ){
    			player.addHex(t);
    			t.setOwner(player);
    			unPause();
    			break;
    		}
    	}
    }

    public void constructFort(){
        Terrain t = ClickObserver.getInstance().getClickedTerrain();
        ArrayList<Terrain> hexes = player.getHexes();

        for( Terrain h : hexes ){
            if( t.compareTo(h) == 0 ){
                // during setup phase, players are given a tower for free
                if( phaseNumber != -1 ){
                    player.spendGold(5);
                } 
                player.constructFort(t);
                unPause();
                break;
            }
        }
        GUI.updateGold(player);
    }

    private void setupPhase(){
        // prompt each player to select their initial starting position
    	ClickObserver.getInstance().setFlag("Terrain: SelectStartTerrain");
        for (Player p : playerList) {
            this.player = p;
        	ClickObserver.getInstance().setActivePlayer(this.player);
        	pause();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getRackGui().setOwner(player);
                }
            });
            // GUI.getRackGui().setOwner(p);
            GUI.getHelpText().setText("Setup Phase: " + p.getName() 
                    + ", select a valid hex to start your kingdom.");
	        while( isPaused ){
	        	int num = p.getHexes().size();
	            if( num == 1 ){
	            	unPause();
                    GUI.updateGold(player);
	            	System.out.println("done");
	            }
	            try { Thread.sleep(100); } catch( Exception e ){ return; }
	        }
        }
        // next prompt each player to select an adjacent hex
        ClickObserver.getInstance().setFlag("Terrain: SelectTerrain");
        // loop 2 times so each player adds 2 more hexes
        for( int i=0; i<2; i++ ){
        	for( Player p : playerList ){
        		this.player = p;
        		ClickObserver.getInstance().setActivePlayer(this.player);
        		pause();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GUI.getRackGui().setOwner(player);
                    }
                });
                // GUI.getRackGui().setOwner(p);
                GUI.getHelpText().setText("Setup Phase: " + p.getName() 
                        + ", select an adjacent hex to add to your kingdom.");
                // forces the GameLoop thread to wait until unpaused
        		while( isPaused ){
    	            try { Thread.sleep(100); } catch( Exception e ){ return; }
        		}
        	}
        }
        // lastly, prompt each player to place their first tower
        ClickObserver.getInstance().setFlag("Terrain: ConstructFort");
        for( Player p : playerList ){
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getRackGui().setOwner(player);
                }
            });
            // GUI.getRackGui().setOwner(p);
            GUI.getHelpText().setText("Setup Phase: " + p.getName() 
                    + ", select one of your tiles to place a tower.");
            while( isPaused ){
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
    	ClickObserver.getInstance().setFlag("");
    }

    /*
     * Each player in the game MUST do this phase.
     * Calculates the amount of gold that each player earns this turn.
     */
    private void goldPhase() {
    	System.out.println("In the gold collection phase");
        for (int i = 0; i < 4; i++){
            playerList[i].addGold(playerList[i].calculateIncome());
            GUI.updateGold(playerList[i]);
        }
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
        GUI.getHelpText().setText("Recruitment Phase: "
                + "draw free Things from The Cup, then click 'done'");
        GUI.getDoneButton().setDisable(false);
        TheCupGUI.update();
        int numToDraw = 0;
        for (int i = 0; i < 1; i++) {
            pause();
            System.out.println((int)Math.ceil(playerList[i].getHexes().size() / 2.0));
            boolean flag = true;
            while (isPaused) {
                if( flag ){
                    numToDraw = (int)Math.ceil(playerList[i].getHexes().size() / 2.0);
                    TheCupGUI.setFieldText(""+numToDraw);
                    flag = false;
                }
                if (TheCupGUI.getPaused()) {
                    unPause();
                    TheCupGUI.setPaused(false);
                }
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
        GUI.getHelpText().setText("Movement Phase: ");
        GUI.getDoneButton().setDisable(false);
        pause();
        
        while( isPaused ){
            try { Thread.sleep(100); } catch( Exception e ){ return; }
        }
        GUI.getDoneButton().setDisable(true);
    }

    /*
     * Optional, unless combat is declared on you.
     * Players may explore or fight battles.
     */
    private void combatPhase() {
    	pause();
    	ArrayList<Terrain> hexes = player.getHexes();
    	ClickObserver.getInstance().setFlag("Combat: disableTerrainSelection");
    	
    	for( Player p : playerList ){
    		this.player = p;
	    	for( Terrain t : hexes ){
	    		// check if player is on another player's hex
	    		if( t.getContents().keySet().size() > 1 ){
	    			System.out.println("combat");
	    			ArrayList<Piece> pieces = t.getContents(player.getName());
	    			// magic phase, attack an enemy creature for each owned magic creature
	    			for( Piece piece : pieces ){ 
	    				if( piece instanceof Creature ){
	    					Creature c = (Creature)piece;
	    					if( c.isMagic() ){
	    						// should roll dice first, if less than combatValue, then skip while loop
	    						GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
	    								+c.getName()+" to attack.");
	    						while( isPaused ){
	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				    	}
	    					}
	    				}
	    			}
	    			// ranged phase, attack an enemy creature for each owned ranged creature
	    			for( Piece piece : pieces ){ 
	    				if( piece instanceof Creature ){
	    					Creature c = (Creature)piece; 
	    					if( c.isFlying() ){
	    						// should roll dice first, if less than combatValue, then skip while loop
	    						GUI.getHelpText().setText("Ranged Combat Phase: "+player.getName()+", select an enemy creature for "
	    								+c.getName()+" to attack.");
	    						while( isPaused ){
	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				    	}
	    					}
	    				}
	    			}
	    			// melee phase, attack an enemy creature for all other owned creatures
	    			for( Piece piece : pieces ){ 
	    				if( piece instanceof Creature ){
	    					Creature c = (Creature)piece; 
	    					if( c.isCharging() ){
	    						// attack twice for charging creatures
	    						for( int i=0; i<2; i++ ){
		    						// should roll dice first, if less than combatValue, then skip while loop
		    						GUI.getHelpText().setText("Ranged Combat Phase: "+player.getName()+", select an enemy creature for "
		    								+c.getName()+" to attack.");
		    						while( isPaused ){
		    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
		    				    	}
	    						}
	    					} else if( !c.isMagic() && !c.isRanged() && !c.isCharging() ){
	    						GUI.getHelpText().setText("Ranged Combat Phase: "+player.getName()+", select an enemy creature for "
	    								+c.getName()+" to attack.");
	    						while( isPaused ){
	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				    	}
	    					}
	    				}
	    			}
	    		}
	    	}
    	}  
    	ClickObserver.getInstance().setFlag("");
    }
    
    public void attackPiece( Combatable piece ){
    	piece.inflict();
    	unPause();
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
    	switch (phaseNumber) {
            case 0: System.out.println(phaseNumber + " setup phase");
                    setupPhase();
                    phaseNumber = 3;
                    break;
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
    
    public void pause(){
        isPaused = true;
    }

    public void unPause(){
        isPaused = false;
    }

    void setButtonHandlers(){
        GUI.getDoneButton().setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle( ActionEvent e ){
            	unPause();
            }
        });
    }

    public int getPhase() { return phaseNumber; }
    public int getNumPlayers() { return numPlayers; }
    public Player[] getPlayers() { return playerList; }
    
    public void setPhase(int i) { phaseNumber = i; }
}
