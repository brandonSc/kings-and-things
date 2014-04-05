package KAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
    private static boolean networked = false;
    private int phaseNumber; //int to keep track of which phase the game is on.
    private TheCup cup;
    private Player player;
    private Player playerClicked;
    private Piece pieceClicked;
    private boolean isPaused, freeClicked, paidClicked, doneClicked;
    private int numPlayers = 0;
    private PlayerRackGUI rackG;
    private Coord[] startingPos;
    private String localPlayer;
    protected ArrayList<Coord> battleGrounds;
    private boolean syncronizer;

    /*
     * Constructor.
     */
    protected GameLoop() {
    	battleGrounds = new ArrayList<Coord>();
        phaseNumber = 0;
        cup = TheCup.getInstance();
        freeClicked = false;
        paidClicked = false;
        doneClicked = false;
        cup.initCup();
        // playerList = new Player[4];
    }

    /*
     * returns a unique instance of the GameLoop class, unless one already exists.
     */
    public static GameLoop getInstance(){
        if( networked ){
            return NetworkGameLoop.getInstance();
        }
        if(uniqueInstance == null){
            uniqueInstance = new GameLoop();
        }
        return uniqueInstance;
    }

    public static void setNetworked( boolean _networked ){
        networked = _networked;
    }

    public void setPlayers(ArrayList<Player> player) {
        int i = 0;
        playerList = new Player[4];
        
        // Create starting spots, will change this for fewer players in future
        Coord[] validPos = {  new Coord(2,-3,1),new Coord(2,1,-3),new Coord(-2,3,-1),new Coord(-2,-1,3) };
        startingPos = validPos;
        
        this.player = player.get(0);
        for (Player p : player) {
            playerList[i] = p;
            playerList[i].addGold(10);
            playerList[i].getPlayerRack().setOwner(playerList[i]);
            if (phaseNumber != -2)
                playerList[i].getPlayerRack().setPieces(cup.drawInitialPieces(10));
            System.out.println(playerList[i].getName() + ": "+ PlayerRack.printList(playerList[i].getPlayerRack().getPieces()));
            i++;
            numPlayers++;
       }
    }
//    public void addPlayer(Player p) {
//    	if (playerList == null || playerList.length == 0) {
//    		numPlayers = 1;
//    		playerList = new Player[1];
//    		playerList[0] = p;
//    	} else {
//	    	Player[] tempPlayerList = new Player[playerList.length + 1];
//	    	for (int i = 0; i < playerList.length; i++) {
//	    		tempPlayerList[i] = playerList[i];
//	    	}
//	    	tempPlayerList[tempPlayerList.length - 1] = p;
//	    	playerList = tempPlayerList;
//	    	for (Player delete : tempPlayerList) {
//	    		delete = null;
//	    	}
//	    	numPlayers++;
//    	}
//    }

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
        if (phaseNumber != -2)
            phaseNumber = -1;
        ClickObserver.getInstance().setTerrainFlag("Setup: deal");
        setButtonHandlers();
        PlayerBoard.getInstance().updateNumOnRacks();
    }
    
    public void addStartingHexToPlayer(){
        
        Terrain t = ClickObserver.getInstance().getClickedTerrain();
        
        if( t == null ){
            //System.out.println("Select a hex");     
        } else {
            Coord coords = t.getCoords();
            boolean valid = false;
            for( int i=0; i<startingPos.length; i++ ){
                if( !t.isOccupied() &&  startingPos[i].equals(coords)){
                     valid = true;

                     break;
                }
            }
            if( valid ){
                player.addHexOwned(t);
                t.setOwner(player);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                    	PlayerBoard.getInstance().updateGoldIncomePerTurn(player);
                    }
                });
                //System.out.println("selected "+t.getType());
            }
        }
    }
    
    public void addHexToPlayer(){
        Terrain t = ClickObserver.getInstance().getClickedTerrain();
        ArrayList<Terrain> hexes = player.getHexesOwned();
       
        boolean valid = false;
        for( Terrain h : hexes ){
            if( t.compareTo(h) == 1 &&  !t.isOccupied() ){
                valid = true;
                break;
            }
        }
        if( valid ){
            player.addHexOwned(t);
            t.setOwner(player);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                	PlayerBoard.getInstance().updateGoldIncomePerTurn(player);
                }
            });
            unPause();
        }
    }

    public void recruitSpecials() {
        if (doneClicked)
            unPause();
    }

    public void playThings() {
        if (doneClicked) {
            unPause();
        }
    }

    public void constructFort() {
        if (phaseNumber == 7) {
            if (doneClicked)
                unPause();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                PlayerRackGUI.disableAll();
            }
        });
        final Terrain t = ClickObserver.getInstance().getClickedTerrain();
        ArrayList<Terrain> hexes = player.getHexesOwned();

        for( Terrain h : hexes ){
            if( t.compareTo(h) == 0 ){
                // during setup phase, players are given a tower for free
                if( phaseNumber != 0 ){
                    player.spendGold(5);
                } 
                player.constructFort(t);
                unPause();
                break;
            }
        }
        System.out.println(player.calculateIncome());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                t.setFortImage();
            	PlayerBoard.getInstance().updateGold(player);
            	PlayerBoard.getInstance().updateGoldIncomePerTurn(player);
            }
        });
    }

    private void setupPhase() {
        // prompt each player to select their initial starting position
        ClickObserver.getInstance().setTerrainFlag("Setup: SelectStartTerrain");
        for (final Player p : playerList) {
        	
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            
            // Cover all terrains, uncover starting pos ones
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getRackGui().setOwner(player);
                	Board.applyCovers();
                	for (Coord spot : startingPos) {
                		if (!Board.getTerrainWithCoord(spot).isOccupied())
                			Board.getTerrainWithCoord(spot).uncover();
                	}
                    GUI.getHelpText().setText("Setup Phase: " + p.getName() 
                            + ", select a valid hex to start your kingdom.");
                }
            });
            while( isPaused ){
                int num = p.getHexesOwned().size();
                if( num == 1 ){
                    unPause();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	PlayerBoard.getInstance().updateGold(player);
                        }
                    });
                    System.out.println("done");
                }
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        pause();
        
        // Now that all players have selected starting spots, flip over all terrains
        // *Note:  Not sure I understand the rules with regards to this, but I think this is right
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	 Board.showTerrains();
                 Board.removeBadWaters();
            }
        });
        while( isPaused ){
            try { Thread.sleep(100); } catch( Exception e ){ return; }
        }
        
        // Check if player has at least two land hexes around starting spot
        for( final Player p : playerList ) {
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getHelpText().setText(p.getName() 
                            + ", select a water hex to replace with from deck");
                    Board.removeBadAdjWaters();
                }
            });
            while( isPaused ){
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
    			TileDeck.getInstance().slideOut();
            }
        });

        
        // next prompt each player to select an adjacent hex
        ClickObserver.getInstance().setTerrainFlag("Setup: SelectTerrain");
        // loop 2 times so each player adds 2 more hexes
        for( int i=0; i<2; i++ ){
            for( final Player p : playerList ) {
                this.player = p;
                ClickObserver.getInstance().setActivePlayer(this.player);
                pause();
                
                final ArrayList<Terrain> ownedHexes = player.getHexesOwned();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Board.applyCovers();
                    }
                });
            	for (Terrain t1 : ownedHexes) {
            		Iterator<Coord> keySetIterator = Board.getTerrains().keySet().iterator();
                	while(keySetIterator.hasNext()) {
                		Coord key = keySetIterator.next();
                		final Terrain t2 = Board.getTerrains().get(key);
                		if (t2.compareTo(t1) == 1 && !t2.isOccupied() && !t2.getType().equals("SEA")) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                        			t2.uncover();
                                }
                            });
                		}
                	}
            	}
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        GUI.getRackGui().setOwner(player);
                        GUI.getHelpText().setText("Setup Phase: " + p.getName() 
                                + ", select an adjacent hex to add to your kingdom.");
                    }
                });
                // forces the GameLoop thread to wait until unpaused
                while( isPaused ){
                    try { Thread.sleep(100); } catch( Exception e ){ return; }
                }
            }
        }
        // prompt each player to place their first tower
        ClickObserver.getInstance().setTerrainFlag("Construction: ConstructFort");
        for( final Player p : playerList ) {
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Board.applyCovers();
                    GUI.getRackGui().setOwner(player);
                    GUI.getHelpText().setText("Setup Phase: " + p.getName() 
                            + ", select one of your tiles to place a tower.");
                }
            });
            
            // sleeps to avoid null pointer (runLater is called before player.getHexesOwned() below)
            try { Thread.sleep(50); } catch( Exception e ){ return; }
            ArrayList<Terrain> ownedHexes = player.getHexesOwned();
            
            for (final Terrain t : ownedHexes) {

            	if (t.getOwner().getName().equals(player.getName())) { 
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                    		t.uncover();
                        }
                    });
            	}
            }
            while( isPaused ){
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        // allow players to add some or all things to their tiles.
        ClickObserver.getInstance().setTerrainFlag("RecruitingThings: PlaceThings");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(false);
            }
        });
        for (final Player p : playerList) {
            this.player = p;
            doneClicked = false;
            ClickObserver.getInstance().setClickedTerrain(p.getHexesOwned().get(2));
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                	ClickObserver.getInstance().whenTerrainClicked();
                    GUI.getRackGui().setOwner(player);
                    Board.applyCovers();
                    GUI.getHelpText().setText("Setup Phase: " + p.getName()
                            + ", place some or all of your things on a tile you own.");
                }
            });
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            ArrayList<Terrain> ownedHexes = player.getHexesOwned();
            for (final Terrain t : ownedHexes) {
            	if (t.getOwner().getName().equals(player.getName())) {
	                Platform.runLater(new Runnable() {
	                    @Override
	                    public void run() {
	                		t.uncover();
	                    }
	                });
            	}
            }
            
            while (isPaused) {
                try { Thread.sleep(100); } catch(Exception e) { return; }
            }
        }
        ClickObserver.getInstance().setTerrainFlag("");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Board.removeCovers();
            }
        });
    }

    /*
     * Used when loading one of the 3 premade game files. It probably doesn't have to wait 17 seconds, but on my virtual machine it does because
     * it's stupidly slow haha
     */
    private void loadingPhase() {
        System.out.println("Loading Phase");
        try { Thread.sleep(17000); } catch(InterruptedException e) { return; }
        ClickObserver.getInstance().setTerrainFlag("");
        System.out.println("Done loading");
    }

    /*
     * Each player in the game MUST do this phase.
     * Calculates the amount of gold that each player earns this turn.
     */
    private void goldPhase() {
        System.out.println("In the gold collection phase");
        GUI.getHelpText().setText("Gold Collection phase: income collected.");
        for (int i = 0; i < 4; i++) {
            playerList[i].addGold(playerList[i].calculateIncome());
            final int j = i;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    PlayerBoard.getInstance().updateGold(playerList[j]);
                    PlayerBoard.getInstance().updateGoldIncomePerTurn(playerList[j]);
                }
            });
        }
        try { Thread.sleep(2000); } catch( InterruptedException e ){ return; }
    }

    /*
     * Optional.
     * Players can attempt to recruit one special character.
     */
    private void recruitSpecialsPhase() {
        //ClickObserver.getInstance().setTerrainFlag("RecruitingSpecialCharacters");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(false);
            }
        });
        
        for (final Player p : playerList) {
            SpecialCharView.setCurrentPlayer(p);
            SpecialCharView.getSpecialButton().activate();
            SpecialCharView.getCharacterGrid().setVisible(false);
            doneClicked = false;
            this.player = p;

            pause();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    DiceGUI.getInstance().uncover();
                    GUI.getHelpText().setText(player.getName() + ", Try Your Luck At Recruiting A Special Character!");
                    GUI.getRackGui().setOwner(player);
                }
            });

            while (isPaused) {
                while (!doneClicked) {
                    try { Thread.sleep(100); } catch( Exception e ){ return; }
                }
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    DiceGUI.getInstance().cover();
                    SpecialCharView.getCharacterGrid().setVisible(false);
                }
            });
        }
    }

    /*
     * Players MUST do this.
     * Draw free things from the cup.
     * Buy paid recruits.
     * Trade unwanted things from their rack.
     * Place things on the board.
     */
    private void recruitThingsPhase() {

        ClickObserver.getInstance().setTerrainFlag("RecruitingThings: PlaceThings");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(false);
            }
        });
        int numToDraw = 0;
        boolean flag;
        
        for (final Player p : playerList) {
            doneClicked = false;
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(player);
            flag = true;
            pause();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GUI.getHelpText().setText("Recruitment Phase: " + p.getName()
                            + ", draw free/paid Things from The Cup, then click 'done'");
                    GUI.getRackGui().setOwner(player);
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
                            final int finNumToDraw = numToDraw;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    TheCupGUI.setFieldText(""+finNumToDraw);
                                }
                            });
                            flag = false;
                        }
                    }
                    if (paidClicked) {
                        flag = true;
                        if (flag) {
                        	Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                	PlayerBoard.getInstance().updateGold(player);
                                }
                            });
                            flag = false;
                        }
                    }
                    try { Thread.sleep(100); } catch( Exception e ){ return; }
                }
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(true);
            }
        });

        ClickObserver.getInstance().setTerrainFlag("");
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
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(false);
            }
        });
        for (Player p : playerList) {
        	player = p;
	        ClickObserver.getInstance().setActivePlayer(player);
	        ClickObserver.getInstance().setCreatureFlag("Movement: SelectMovers");
	        if (p.getHexesWithPiece().size() > 0) {
	        	ClickObserver.getInstance().setClickedTerrain(p.getHexesWithPiece().get(0));
	        }
	        pause();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                	ClickObserver.getInstance().whenTerrainClicked();
        	        GUI.getHelpText().setText("Movement Phase: " + player.getName()
                            + ", Move your armies");
                }
            });
	        
	        while (isPaused) {
            	try { Thread.sleep(100); } catch( Exception e ){ return; }  
	        }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(true);
            }
        });
        ClickObserver.getInstance().setCreatureFlag("");
    }

    /*
     * Optional, unless combat is declared on you.
     * Players may explore or fight battles.
     * 
     * List of important Data and description. Hope this helps keep the ideas clear:
     * 
     * 		- (Terrain) 							battleGround:	> The Terrain in which the combat is taking place
     * 		- (ArrayList<Player>) 					combatants:		> List of players with pieces on battleGround (Must fight if so)
     * 		- (HashMap<String, ArrayList<Piece>>) 	attackingPieces	> HashMap of players names to the pieces they have in battle 
     * 																  (including forts and city/village)
     * 		- (HashMap<String, Player>) 			toAttacks		> HashMap of players names to the Player they want to attack.
     * 																  Important for multiple players on a single Terrain, where each
     * 																  round each player can select a different player to attack
     * 		- (HashMap<String, ArrayList<Piece>>) 	successAttacks	> HashMap of player names to the pieces they have that had 
     * 																  successful attack. Changes each phase (From magic to ranged etc)
     * 		- (ArrayList<Piece>) 					phaseThings		> List of pieces that are to be used during a particular phase (ie magic
     * 																  , ranged etc). This is used to keep track of what pieces the gui should
     * 																  set up to be selected, and then dice rolled. 
     * 																  For example: 
     * 																  beginning of magic phase, phaseThings will be empty. First combatant has 
     * 																  two magic creatures which are added to phaseThings. They then roll for one, 
     * 																  it is removed, and then roll the other, which is removed. Now that phaseThings
     * 																  is of size 0, the next player repeats.
     * 		- (HashMap<String, ArrayList<Piece>>)	toInflict		> HashMap of players name to pieces they selected to take hits.
     */
    private void combatPhase() {
    	
    	pause();
    	ClickObserver.getInstance().setCreatureFlag("Combat: SelectCreatureToAttack");

		System.out.println(battleGrounds);
    	// Go through each battle ground a resolve each conflict
    	for (Coord c : battleGrounds) {
        	ClickObserver.getInstance().setTerrainFlag("");

        	System.out.println("Entering battleGround");
    		final Terrain battleGround = Board.getTerrainWithCoord(c);
    		
    		// List of players to battle in the terrain
    		ArrayList<Player> combatants = new ArrayList<Player>();
    		
    		// List of pieces that can attack (including forts, city/village)
    		HashMap<String, ArrayList<Piece>> attackingPieces = new HashMap<String, ArrayList<Piece>>();
    		
    		Iterator<String> keySetIterator = battleGround.getContents().keySet().iterator();
	    	while(keySetIterator.hasNext()) {
	    		String key = keySetIterator.next();
	    		
    			combatants.add(battleGround.getContents().get(key).getOwner());
    			attackingPieces.put(battleGround.getContents().get(key).getOwner().getName(), (ArrayList<Piece>) battleGround.getContents().get(key).getStack().clone()); 
    			
	    	}
	    	// if the owner of the terrain has no pieces, just a fort or city/village
			if (!combatants.contains(battleGround.getOwner()) && battleGround.getFort() != null) {
				combatants.add(battleGround.getOwner());
				attackingPieces.put(battleGround.getOwner().getName(), new ArrayList<Piece>());
			}
    				
    		// simulate a click on the first battleGround, cover all other terrains
    		ClickObserver.getInstance().setClickedTerrain(battleGround);
    		Platform.runLater(new Runnable() {
                @Override
                public void run() {
            		ClickObserver.getInstance().whenTerrainClicked();
            		Board.applyCovers();
            		ClickObserver.getInstance().getClickedTerrain().uncover();
                	ClickObserver.getInstance().setTerrainFlag("Disabled");
                }
            });

    		// add forts and city/village to attackingPieces
    		if (battleGround.getFort() != null) {
    			attackingPieces.get(battleGround.getOwner().getName()).add(battleGround.getFort());
    		}
    		// TODO implement city/village
//    		if (City and village stuff here)
    		
    		// Fight until all attackers are dead, or until the attacker becomes the owner of the hex
    		while (combatants.size() > 1) {
    			
    			// This hashMap keeps track of the player to attack for each  player
    			HashMap<String, Player> toAttacks = new HashMap<String, Player>();

				// each player selects which other player to attack in case of more than two combatants
    			if (combatants.size() > 2) {
    				
    				for (final Player p : combatants) {
        	    		pause();
        	    		ClickObserver.getInstance().setPlayerFlag("Attacking: SelectPlayerToAttack");
        	    		player = p;
	                	Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	PlayerBoard.getInstance().applyCovers();
	    	                	battleGround.coverPieces();
	    	        	        GUI.getHelpText().setText("Attack phase: " + player.getName()
	    	                            + ", select which player to attack");
		        	    	}
	    	            });
	                	for (final Player pl : combatants) {
	                		if (!pl.getName().equals(player.getName())) {
	                			Platform.runLater(new Runnable() {
	    	    	                @Override
	    	    	                public void run() {
	    	    	                	PlayerBoard.getInstance().uncover(pl);
	    	    	                }
	                			});
	                		}
	                	}
	                	while (isPaused) {
	                     	try { Thread.sleep(100); } catch( Exception e ){ return; }  
	         	        }
	                	
	                	// ClickObserver sets playerClicked, then unpauses. This stores what player is attacking what player
	                	toAttacks.put(p.getName(), playerClicked);
        	    		ClickObserver.getInstance().setPlayerFlag("");
	                	
        	    	}
    				PlayerBoard.getInstance().removeCovers();
    				
        	    } else { // Only two players fighting
        	    	
        	    	for (Player p1 : combatants) {
        	    		for (Player p2 : combatants) {
        	    			if (!p1.getName().equals(p2.getName())) {
        	        	    	toAttacks.put(p1.getName(), p2);
        	    			}
        	    		}
        	    	}
        	    }
    			
    			// Set up this HashMap that will store successful attacking pieces
    			HashMap<String, ArrayList<Piece>> successAttacks = new HashMap<String, ArrayList<Piece>>();
    			// Set up this HashMap that will store piece marked to get damage inflicted
				HashMap<String, ArrayList<Piece>> toInflict = new HashMap<String, ArrayList<Piece>>();
    			for (Player p : combatants) {
    				
    				successAttacks.put(p.getName(), new ArrayList<Piece>());
    				toInflict.put(p.getName(), new ArrayList<Piece>());
    			}
    			
    			// Array List of pieces that need to be used during a particular phase
				final ArrayList<Piece> phaseThings = new ArrayList<Piece>();
				
				// Notify next phase, wait for a second
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
	                	battleGround.coverPieces();
	                	GUI.getHelpText().setText("Attack phase: Next phase: Magic!");
	                }
	            });
				
				// Pause for 2 seconds between phases
				try { Thread.sleep(2000); } catch( Exception e ){ return; }
				
/////////////////////// Magic phase
    			for (final Player pl : combatants) {
    				
    				player = pl;
    				// Cover all pieces
    				Platform.runLater(new Runnable() {
    	                @Override
    	                public void run() {
    	            		battleGround.coverPieces();
    	                }
    	            });
    				
    				// For each piece, if its magic. Add it to the phaseThings array
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					if (p instanceof Combatable && ((Combatable)p).isMagic()) 
    						phaseThings.add(p);
    				}
    				
    				// uncover magic pieces for clicking
    				if (phaseThings.size() > 0) {
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	    				for (Piece mag : phaseThings) 
	            					mag.uncover();
	    	                }
	    	            });
    				}
    				
    				// Have user select a piece to attack with until there are no more magic pieces
    				while (phaseThings.size() > 0) {
    					
    					// Display message prompting user to select a magic piece
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", select a magic piece to attack with");
	    	                }
	    	            });

    					// Wait for user to select piece
        				pieceClicked = null;
        				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToAttackWith");
        				ClickObserver.getInstance().setFortFlag("Combat: SelectPieceToAttackWith");
    					while (pieceClicked == null) { try { Thread.sleep(100); } catch( Exception e ){ return; } }
	    				ClickObserver.getInstance().setCreatureFlag("");
	    				ClickObserver.getInstance().setFortFlag("");
	    				
	    				// hightlight piece that was selected, uncover the die to use, display message about rolling die
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	pieceClicked.highLight();
	    	                	DiceGUI.getInstance().uncover();
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() 
	    	                			+ ", roll the die. You need a " + ((Combatable)pieceClicked).getCombatValue() + " or lower");
	    	                }
    					});
    					
	                	// Dice is set to -1 while it is 'rolling'. This waits for the roll to stop, ie not -1
    					int attackStrength = -1;
    					while (attackStrength == -1) {
    						try { Thread.sleep(100); } catch( Exception e ){ return; }
    						attackStrength = Dice.getFinalVal();
    					}
						
    					// If the roll was successful. Add to successfulThings Array, and change it image. prompt Failed attack
    					if (attackStrength <= ((Combatable)pieceClicked).getCombatValue()) {
    						
    						successAttacks.get(player.getName()).add(pieceClicked);
    						Platform.runLater(new Runnable() {
    	    	                @Override
    	    	                public void run() {
    	    						((Combatable)pieceClicked).setAttackResult(true);
    	    						pieceClicked.cover();
    	    						pieceClicked.unhighLight();
    	    	                	GUI.getHelpText().setText("Attack phase: Successful Attack!");
    	    	                }
        					});
    						
    					} else { // else failed attack, update image, prompt Failed attack
    						Platform.runLater(new Runnable() {
		    	                @Override
		    	                public void run() {
		    						((Combatable)pieceClicked).setAttackResult(false);
		    						pieceClicked.cover();
    	    						pieceClicked.unhighLight();
		    	                	GUI.getHelpText().setText("Attack phase: Failed Attack!");
		    	                }
	    					});
    					}
    					
    					// Pause to a second for easy game play, remove the clicked piece from phaseThings
    					try { Thread.sleep(1000); } catch( Exception e ){ return; }
    					phaseThings.remove(pieceClicked);
    				}
    			}

				// For each piece that had success, player who is being attacked must choose a piece
    			// Gets tricky here. Will be tough for Networking :(
    			for (Player pl : combatants) {
    				
    				// Active player is set to the player who 'pl' is attack based on toAttack HashMap
    				player = toAttacks.get(pl.getName()); // 'defender'
    				final String plName = pl.getName();
    				
    				// For each piece of pl's that has a success (in successAttacks)
    				int i = 0;
    				for (final Piece p : successAttacks.get(plName)) {

    					// If there are more successful attacks then pieces to attack, break after using enough attacks
    					if (i >= attackingPieces.get(player.getName()).size()) {
    						break;
    					}
    					
    					// Display message, cover other players pieces, uncover active players pieces
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", Select a Piece to take a hit from " + plName + "'s " + p.getName());
	    	                	battleGround.coverPieces();
	    	                	battleGround.uncoverPieces(player.getName());
	    	                }
						});
    					try { Thread.sleep(100); } catch( Exception e ){ return; }
    					
	    				// Cover pieces already choosen to be inflicted. Wait to make sure runLater covers pieces already selected
	    				for (final Piece pi : toInflict.get(player.getName())) {
	    					Platform.runLater(new Runnable() {
		    	                @Override
		    	                public void run() {
			    					pi.cover();
		    	                }
							});
	    				}//TODO here is where a pause might be needed
	    				
	    				// Wait for user to select piece
    					pieceClicked = null;
	    				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToGetHit");
	    				ClickObserver.getInstance().setFortFlag("Combat: SelectPieceToGetHit");
    					while (pieceClicked == null) { try { Thread.sleep(100); } catch( Exception e ){ return; } }
    					ClickObserver.getInstance().setCreatureFlag("");
	    				ClickObserver.getInstance().setFortFlag("");
		    			
    					// Add to arrayList in HashMap of player to mark for future inflict. Cover pieces
    					toInflict.get(player.getName()).add(pieceClicked);
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	battleGround.coverPieces();
	    	                }
						});
    					i++;
    					try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				
    				}
    				// Clear successful attacks for next phase
    				successAttacks.get(pl.getName()).clear();
    			}
    			
    			// Remove little success and failure images, inflict if necessary
    			for (Player pl : combatants) {
    				
    				// Change piece image of success or failure to not visible
    				for (Piece p : attackingPieces.get(pl.getName())) 
    					((Combatable)p).resetAttack();
    				
					// inflict return true if the piece is dead, and removes it from attackingPieces if so
    				// Inflict is also responsible for removing from the CreatureStack
    				for (Piece p : toInflict.get(pl.getName())) {
						if (((Combatable)p).inflict()) 
							attackingPieces.get(pl.getName()).remove(p);
						if (attackingPieces.get(pl.getName()).size() == 0)
							attackingPieces.remove(pl.getName());
    				}
    				
    				// Clear toInflict for next phase
    				toInflict.get(pl.getName()).clear();
    			}

				// Update the InfoPanel gui for changed/removed pieces
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
    					InfoPanel.showTileInfo(battleGround);
    					battleGround.coverPieces();
	                }
				});
    			
    			// Check for defeated armies:
				// - find player with no more pieces on terrain
				// - remove any such players from combatants
				// - if only one combatant, end combat
    			Player baby = null;
    			while (combatants.size() != attackingPieces.size()) {
					for (Player pl : combatants) {
						if (!attackingPieces.containsKey(pl.getName())) {
							baby = pl;
						}
					}
					combatants.remove(baby);
    			}
				if (combatants.size() == 1) {
					break;
				}
    			
				// Notify next phase, wait for a second
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
	            		battleGround.coverPieces();
	                	GUI.getHelpText().setText("Attack phase: Next phase: Ranged!");
	                }
	            });
				
				// Pause for 2 seconds between phases
				try { Thread.sleep(2000); } catch( Exception e ){ return; }
				
				
				
//////////////////// Ranged phase
				for (final Player pl : combatants) {
    				
    				player = pl;
    				// Cover all pieces
    				Platform.runLater(new Runnable() {
    	                @Override
    	                public void run() {
    	            		battleGround.coverPieces();
    	                }
    	            });
    				
    				// For each piece, if its ranged. Add it to the phaseThings array
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					if (p instanceof Combatable && ((Combatable)p).isRanged()) 
    						phaseThings.add(p);
    				}
    				
    				// uncover ranged pieces for clicking
    				if (phaseThings.size() > 0) {
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	    				for (Piece ran : phaseThings) 
	            					ran.uncover();
	    	                }
	    	            });
    				}
    				
    				// Have user select a piece to attack with until there are no more ranged pieces
    				while (phaseThings.size() > 0) {
    					
    					// Display message prompting user to select a ranged piece
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", select a ranged piece to attack with");
	    	                }
	    	            });

    					// Wait for user to select piece
        				pieceClicked = null;
        				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToAttackWith");
        				ClickObserver.getInstance().setFortFlag("Combat: SelectPieceToAttackWith");
    					while (pieceClicked == null) { try { Thread.sleep(100); } catch( Exception e ){ return; } }
	    				ClickObserver.getInstance().setCreatureFlag("");
	    				ClickObserver.getInstance().setFortFlag("");
	    				
	    				// hightlight piece that was selected, uncover the die to use, display message about rolling die
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	pieceClicked.highLight();
	    	                	DiceGUI.getInstance().uncover();
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName()
	    	                            + ", roll the die. You need a " + ((Combatable)pieceClicked).getCombatValue() + " or lower");
	    	                }
    					});
    					
	                	// Dice is set to -1 while it is 'rolling'. This waits for the roll to stop, ie not -1
    					int attackStrength = -1;
    					while (attackStrength == -1) {
    						try { Thread.sleep(100); } catch( Exception e ){ return; }
    						attackStrength = Dice.getFinalVal();
    					}
						
    					// If the roll was successful. Add to successfulThings Array, and change it image. prompt Failed attack
    					if (attackStrength <= ((Combatable)pieceClicked).getCombatValue()) {
    						
    						successAttacks.get(player.getName()).add(pieceClicked);
    						Platform.runLater(new Runnable() {
    	    	                @Override
    	    	                public void run() {
    	    						((Combatable)pieceClicked).setAttackResult(true);
    	    						pieceClicked.cover();
    	    						pieceClicked.unhighLight();
    	    	                	GUI.getHelpText().setText("Attack phase: Successful Attack!");
    	    	                }
        					});
    						
    					} else { // else failed attack, update image, prompt Failed attack
    						Platform.runLater(new Runnable() {
		    	                @Override
		    	                public void run() {
		    						((Combatable)pieceClicked).setAttackResult(false);
		    						pieceClicked.cover();
    	    						pieceClicked.unhighLight();
		    	                	GUI.getHelpText().setText("Attack phase: Failed Attack!");
		    	                }
	    					});
    					}
    					
    					// Pause to a second for easy game play, remove the clicked piece from phaseThings
    					try { Thread.sleep(1000); } catch( Exception e ){ return; }
    					phaseThings.remove(pieceClicked);
    				}
    			}

				// For each piece that had success, player who is being attacked must choose a piece
    			// Gets tricky here. Will be tough for Networking :(
    			for (Player pl : combatants) {
    				
    				// Active player is set to the player who 'pl' is attack based on toAttack HashMap
    				player = toAttacks.get(pl.getName());
    				final String plName = pl.getName();
    				
    				// For each piece of pl's that has a success (in successAttacks)
    				int i = 0;
    				for (final Piece p : successAttacks.get(plName)) {

    					// If there are more successful attacks then pieces to attack, break after using enough attacks
    					if (i >= attackingPieces.get(player.getName()).size()) {
    						break;
    					}
    					
    					// Display message, cover other players pieces, uncover active players pieces
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", Select a Piece to take a hit from " + plName + "'s " + p.getName());
	    	                	battleGround.coverPieces();
	    	                	battleGround.uncoverPieces(player.getName());
	    	                }
						});
    					
	    				// Cover pieces already choosen to be inflicted. Wait to make sure runLater covers pieces already selected
	    				for (final Piece pi : toInflict.get(player.getName())) {
	    					Platform.runLater(new Runnable() {
		    	                @Override
		    	                public void run() {
			    					pi.cover();
		    	                }
							});
	    				}//TODO here is where a pause might be needed
	    				
	    				// Wait for user to select piece
    					pieceClicked = null;
	    				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToGetHit");
	    				ClickObserver.getInstance().setFortFlag("Combat: SelectPieceToGetHit");
    					while (pieceClicked == null) { try { Thread.sleep(100); } catch( Exception e ){ return; } }
    					ClickObserver.getInstance().setFortFlag("");
    					ClickObserver.getInstance().setCreatureFlag("");
		    			
    					// Add to arrayList in HashMap of player to mark for future inflict. Cover pieces
    					toInflict.get(player.getName()).add(pieceClicked);
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	battleGround.coverPieces();
	    	                }
						});
    					try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				i++;
    				}
    				// Clear successful attacks for next phase
    				successAttacks.get(pl.getName()).clear();
    			}
    			
    			// Remove little success and failure images, inflict if necessary
    			for (Player pl : combatants) {
    				
    				// Change piece image of success or failure to not visible
    				for (Piece p : attackingPieces.get(pl.getName())) 
    					((Combatable)p).resetAttack();
    				
					// inflict return true if the piece is dead, and removes it from attackingPieces if so
    				// Inflict is also responsible for removing from the CreatureStack
    				for (Piece p : toInflict.get(pl.getName())) {
						if (((Combatable)p).inflict()) 
							attackingPieces.get(pl.getName()).remove(p);
						if (attackingPieces.get(pl.getName()).size() == 0)
							attackingPieces.remove(pl.getName());
    				}
    				
    				// Clear toInflict for next phase
    				toInflict.get(pl.getName()).clear();
    			}

				// Update the InfoPanel gui for changed/removed pieces
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
    					InfoPanel.showTileInfo(battleGround);
    					battleGround.coverPieces();
	                }
				});
    			
    			// Check for defeated armies:
				// - find player with no more pieces on terrain
				// - remove any such players from combatants
				// - if only one combatant, end combat
				baby = null;
    			while (combatants.size() != attackingPieces.size()) {
					for (Player pl : combatants) {
						if (!attackingPieces.containsKey(pl.getName())) {
							baby = pl;
						}
					}
					combatants.remove(baby);
    			}
				if (combatants.size() == 1) {
					break;
				}
    			
				// Notify next phase, wait for a second
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
	            		battleGround.coverPieces();
	                	GUI.getHelpText().setText("Attack phase: Next phase: Melee!");
	                }
	            });
				
				// Pause for 2 seconds between phases
				try { Thread.sleep(2000); } catch( Exception e ){ return; }
				

///////////////////////////// Melee phase
				for (final Player pl : combatants) {
    				
    				player = pl;
    				// Cover all pieces
    				Platform.runLater(new Runnable() {
    	                @Override
    	                public void run() {
    	            		battleGround.coverPieces();
    	                }
    	            });
    				
    				// For each piece, if its melee. Add it to the phaseThings array
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					if (p instanceof Combatable && !(((Combatable)p).isRanged() || ((Combatable)p).isMagic())) 
    						phaseThings.add(p);
    				}
    				
    				// uncover melee pieces for clicking
    				if (phaseThings.size() > 0) {
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	    				for (Piece mel : phaseThings) 
	            					mel.uncover();
	    	                }
	    	            });
    				}
    				try { Thread.sleep(100); } catch( Exception e ){ return; }
    				
    				// Have user select a piece to attack with until there are no more melee pieces
    				while (phaseThings.size() > 0) {
    					
    					// Display message prompting user to select a melee piece
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", select a melee piece to attack with");
	    	                }
	    	            });

    					// Wait for user to select piece
        				pieceClicked = null;
        				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToAttackWith");
        				ClickObserver.getInstance().setFortFlag("Combat: SelectPieceToAttackWith");
    					while (pieceClicked == null) { try { Thread.sleep(100); } catch( Exception e ){ return; } }
	    				ClickObserver.getInstance().setCreatureFlag("");
	    				ClickObserver.getInstance().setFortFlag("");
	    				
	    				// Is it a charging piece?
	    				int charger;
	    				if (((Combatable)pieceClicked).isCharging()) {
	    					charger = 2;
	    				} else
	    					charger = 1;
	    				
	    				// do twice if piece is a charger
	    				for (int i = 0; i < charger; i++) {
	    					
		    				// hightlight piece that was selected, uncover the die to use, display message about rolling die
	    					Platform.runLater(new Runnable() {
		    	                @Override
		    	                public void run() {
		    	                	pieceClicked.highLight();
		    	                	DiceGUI.getInstance().uncover();
		    	                	GUI.getHelpText().setText("Attack phase: " + player.getName()
		    	                            + ", roll the die. You need a " + ((Combatable)pieceClicked).getCombatValue() + " or lower");
		    	                }
	    					});
	    					
		                	// Dice is set to -1 while it is 'rolling'. This waits for the roll to stop, ie not -1
	    					int attackStrength = -1;
	    					while (attackStrength == -1) {
	    						try { Thread.sleep(100); } catch( Exception e ){ return; }
	    						attackStrength = Dice.getFinalVal();
	    					}
							
	    					// If the roll was successful. Add to successfulThings Array, and change it image. prompt Failed attack
	    					if (attackStrength <= ((Combatable)pieceClicked).getCombatValue()) {
	    						
	    						successAttacks.get(player.getName()).add(pieceClicked);
	    						Platform.runLater(new Runnable() {
	    	    	                @Override
	    	    	                public void run() {
	    	    						((Combatable)pieceClicked).setAttackResult(true);
	    	    						pieceClicked.cover();
	    	    						pieceClicked.unhighLight();
	    	    	                	GUI.getHelpText().setText("Attack phase: Successful Attack!");
	    	    	                }
	        					});
	    						
	    					} else { // else failed attack, update image, prompt Failed attack
	    						Platform.runLater(new Runnable() {
			    	                @Override
			    	                public void run() {
			    						((Combatable)pieceClicked).setAttackResult(false);
			    						pieceClicked.cover();
	    	    						pieceClicked.unhighLight();
			    	                	GUI.getHelpText().setText("Attack phase: Failed Attack!");
			    	                }
		    					});
	    					}
	    					
	    					// If piece is charging, and it is its first attack, remove the cover again
	    					if (((Combatable)pieceClicked).isCharging() && i == 0) {
	    						Platform.runLater(new Runnable() {
			    	                @Override
			    	                public void run() {
			    	                	pieceClicked.uncover();
			    	                }
	    						});
	    					}
	    					
	    					// Pause to a second for easy game play, remove the clicked piece from phaseThings
	    					try { Thread.sleep(1000); } catch( Exception e ){ return; }
	    				}

    					phaseThings.remove(pieceClicked);
    				}
    			}

				// For each piece that had success, player who is being attacked must choose a piece
    			// Gets tricky here. Will be tough for Networking :(
    			for (Player pl : combatants) {
    				
    				// Active player is set to the player who 'pl' is attack based on toAttack HashMap
    				player = toAttacks.get(pl.getName());
    				final String plName = pl.getName();
    				
    				// For each piece of pl's that has a success (in successAttacks)
    				int i = 0;
    				for (final Piece p : successAttacks.get(plName)) {
    					
    					// If there are more successful attacks then pieces to attack, break after using enough attacks
    					if (i >= attackingPieces.get(player.getName()).size()) {
    						break;
    					}
    					
    					// Display message, cover other players pieces, uncover active players pieces
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", Select a Piece to take a hit from " + plName + "'s " + p.getName());
	    	                	battleGround.coverPieces();
	    	                	battleGround.uncoverPieces(player.getName());
	    	                }
						});
    					
	    				// Cover pieces already choosen to be inflicted. Wait to make sure runLater covers pieces already selected
	    				for (final Piece pi : toInflict.get(player.getName())) {
	    					Platform.runLater(new Runnable() {
		    	                @Override
		    	                public void run() {
			    					pi.cover();
		    	                }
							});
	    				}//TODO here is where a pause might be needed
	    				
	    				// Wait for user to select piece
    					pieceClicked = null;
	    				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToGetHit");
	    				ClickObserver.getInstance().setFortFlag("Combat: SelectPieceToGetHit");
    					while (pieceClicked == null) { try { Thread.sleep(100); } catch( Exception e ){ return; } }
    					ClickObserver.getInstance().setCreatureFlag("");
    					ClickObserver.getInstance().setFortFlag("");
		    			
    					// Add to arrayList in HashMap of player to mark for future inflict. Cover pieces
    					toInflict.get(player.getName()).add(pieceClicked);
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	battleGround.coverPieces();
	    	                }
						});
    					try { Thread.sleep(100); } catch( Exception e ){ return; }
	    				i++;
    				}
    				// Clear successful attacks for next phase
    				successAttacks.get(pl.getName()).clear();
    			}
    			
    			// Remove little success and failure images, inflict if necessary
    			for (Player pl : combatants) {
    				
    				// Change piece image of success or failure to not visible
    				for (Piece p : attackingPieces.get(pl.getName())) 
    					((Combatable)p).resetAttack();
    				
					// inflict return true if the piece is dead, and removes it from attackingPieces if so
    				// Inflict is also responsible for removing from the CreatureStack
    				for (Piece p : toInflict.get(pl.getName())) {
						if (((Combatable)p).inflict()) 
							attackingPieces.get(pl.getName()).remove(p);
						if (attackingPieces.get(pl.getName()).size() == 0)
							attackingPieces.remove(pl.getName());
    				}
    				
    				// Clear toInflict for next phase
    				toInflict.get(pl.getName()).clear();
    			}

				// Update the InfoPanel gui for changed/removed pieces
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
    					InfoPanel.showTileInfo(battleGround);
    					battleGround.coverPieces();
	                }
				});
    			
    			// Check for defeated armies:
				// - find player with no more pieces on terrain
				// - remove any such players from combatants
				// - if only one combatant, end combat
				baby = null;
    			while (combatants.size() != attackingPieces.size()) {
					for (Player pl : combatants) {
						if (!attackingPieces.containsKey(pl.getName())) {
							baby = pl;
						}
					}
					combatants.remove(baby);
    			}
				if (combatants.size() == 1) {
					break;
				}
    			
				// Notify next phase, wait for a second
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
	            		battleGround.coverPieces();
	                	GUI.getHelpText().setText("Attack phase: Next phase: Retreat!");
	                }
	            });
				
				// Pause for 2 seconds between phases
				try { Thread.sleep(2000); } catch( Exception e ){ return; }
    			
    			
//////////////////////// Retreat phase
				
				// Display message, activate done button
				Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
	                	GUI.getHelpText().setText("Attack phase: Retreat some of your armies?");
		                GUI.getDoneButton().setDisable(false);
	                }
	            }); 
				
				System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
				System.out.println(combatants);
				System.out.println(attackingPieces.keySet());
				System.out.println(attackingPieces.entrySet());
				
				
				// For each combatant, ask if they would like to retreat
		        for (Player pl : combatants) {
		        	
		        	player = pl;
			        ClickObserver.getInstance().setActivePlayer(player);
			        ClickObserver.getInstance().setCreatureFlag("Combat: SelectRetreaters");
			        
			        // Pause and wait for player to hit done button
			        pause();
		            Platform.runLater(new Runnable() {
		                @Override
		                public void run() {
		                	battleGround.coverPieces();
		                	battleGround.uncoverPieces(player.getName());
		                	battleGround.coverFort();
		        	        GUI.getHelpText().setText("Attack Phase: " + player.getName() + ", You can retreat your armies");
		                }
		            });
			        while (isPaused && battleGround.getContents(player.getName()) != null) {
		            	try { Thread.sleep(100); } catch( Exception e ){ return; }  
			        }	        
			        ClickObserver.getInstance().setTerrainFlag("Disabled");
			        
			        // TODO, maybe an if block here asking user if they would like to attack 
			        
			        // Re-populate attackingPieces to check for changes
			        attackingPieces.clear();
			        Iterator<String> keySetIterator2 = battleGround.getContents().keySet().iterator();
			    	while(keySetIterator2.hasNext()) {
			    		String key = keySetIterator2.next();
		    			attackingPieces.put(battleGround.getContents().get(key).getOwner().getName(), (ArrayList<Piece>) battleGround.getContents().get(key).getStack().clone()); 
			    	}
			    	// if the owner of the terrain has no pieces, just a fort or city/village
					if (!combatants.contains(battleGround.getOwner()) && battleGround.getFort() != null) {
						attackingPieces.put(battleGround.getOwner().getName(), new ArrayList<Piece>());
					}
					if (battleGround.getFort() != null) {
						attackingPieces.get(battleGround.getFort().getOwner().getName()).add(battleGround.getFort());
					}
//					if (battleGround city/village)
					// TODO city/village
			        
					
			        // Check if all the players pieces are now gone
			        if (!attackingPieces.containsKey(player.getName())) {
			        	
			        	// Display message, and remove player from combatants
			        	Platform.runLater(new Runnable() {
			                @Override
			                public void run() {
			        	        GUI.getHelpText().setText("Attack Phase: " + player.getName() + " has retreated all of their pieces!");
			                }
			            });
			        	
			        	// If there is only 1 player fighting for the hex, 
			        	if (attackingPieces.size() == 1) 
			        		break;
			        	
			        	// Pause because somebody just retreated
			        	try { Thread.sleep(2000); } catch( Exception e ){ return; }
			        }
		        }
		        
		        // Done button no longer needed
		        Platform.runLater(new Runnable() {
		            @Override
		            public void run() {
		                GUI.getDoneButton().setDisable(true);
		            }
		        });
		        ClickObserver.getInstance().setCreatureFlag("");
		        ClickObserver.getInstance().setFortFlag("");
		        
		        // Check for defeated armies:
				// - find player with no more pieces on terrain
				// - remove any such players from combatants
				// - if only one combatant, end combat
    			baby = null;
    			while (combatants.size() != attackingPieces.size()) {
					for (Player pl : combatants) {
						if (!attackingPieces.containsKey(pl.getName())) {
							baby = pl;
						}
					}
					combatants.remove(baby);
    			}
				if (combatants.size() == 1) {
					break;
				}
    			
    			
    		}
    		battleGround.coverFort();
    		//// Post Combat
			// TODO winner of battle owns hex
			// TODO check forts, city/village and special incomes if they are kept or lost/damaged 
			// 		- Citadels are not lost or damaged
			// 		- if tower is damaged, it is destroyed
			//		- if keep/castle is damaged, its level is lowered by one
			// 		- roll dice, a 1 or 6 is kept/not damaged. 2 to 5 the piece is destroyed/damaged
    	}

    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
//				InfoPanel.showBattleStats();
            	Board.removeCovers();
            }
		});
    	
		ClickObserver.getInstance().setTerrainFlag("");
		ClickObserver.getInstance().setPlayerFlag("");
		ClickObserver.getInstance().setCreatureFlag("");
		ClickObserver.getInstance().setFortFlag("");
		battleGrounds.clear();
    }

    /*
     * Optional.
     * Each player may build forts.
     */
    private void constructionPhase() {
        ClickObserver.getInstance().setTerrainFlag("Construction: ConstructFort");
        for( final Player p : playerList ) {
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            pause();
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Board.applyCovers();
                    GUI.getRackGui().setOwner(player);
                    GUI.getHelpText().setText("Construction Phase: " + p.getName() 
                            + ", select one of your tiles to build a new tower, or upgrade an existing one.");
                }
            });
            ArrayList<Terrain> ownedHexes = player.getHexesOwned();
            for (final Terrain t : ownedHexes) {
                if (t.getOwner().getName().equals(player.getName())) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            t.uncover();
                        }
                    });
                }
            }
            while( isPaused ){
                try { Thread.sleep(100); } catch( Exception e ){ return; }
            }
        }
        ClickObserver.getInstance().setTerrainFlag("");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Board.removeCovers();
            }
        });
    }

    /*
     * Optional.
     * Master Thief and Assassin Primus may use their special powers.
     */
    private void specialPowersPhase() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getHelpText().setText("Special Powers Phase");
            }
        });

        for (Player p : playerList) {
            pause();
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);

            System.out.println("--- Currently on " + player.getName());
            
            for (Terrain hex : player.getHexesWithPiece()) {
                for (Piece pc : hex.getContents(player.getName()).getStack()) {
                    if (pc.getName().equals("Master Thief") || pc.getName().equals("Assassin Primus")) {
                        if (((Performable)pc).hasSpecial()) {
                            System.out.println(pc.getName() + " is performing their special ability for " + player.getName());
                            ((Performable)pc).specialAbility();
                            System.out.println("--- done ability");
                        }
                    }
                }
            }

            System.out.println("--- done with all hexes for " + player.getName());

            // while (isPaused) {
            //     try { Thread.sleep(100); } catch( Exception e ){ return; }  
            // }
        }
        System.out.println("--- done with all players");
        ClickObserver.getInstance().setPlayerFlag("");
        System.out.println("done with the powers phase!");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DiceGUI.getInstance().setFaceValue(0);
                DiceGUI.getInstance().cover();
            }
        });
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
            case -2:System.out.println(phaseNumber + " loading phase number");
                    loadingPhase();
                    System.out.println("Actually done loading");
                    phaseNumber = 1;
                    System.out.println(phaseNumber + " after loading");
                    break;
            case 0: System.out.println(phaseNumber + " setup phase");
                    setupPhase();
                    doneClicked = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.getDoneButton().setDisable(true);
                        }
                    });
                    phaseNumber++;
                    break;
            case 1: System.out.println(phaseNumber + " gold phase");
                    goldPhase();
                    doneClicked = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.getDoneButton().setDisable(true);
                        }
                    });
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
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.getDoneButton().setDisable(false);
                        }
                    });
                    phaseNumber++;
                    break;
            case 4: System.out.println(phaseNumber + " random event phase");
                    randomEventPhase();
                    doneClicked = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.getDoneButton().setDisable(false);
                        }
                    });
                    phaseNumber++;
                    break;
            case 5: System.out.println(phaseNumber + " movement phase");
                    movementPhase();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.getDoneButton().setDisable(true);
                        }
                    });
                    phaseNumber++;
                    break;
            case 6: System.out.println(phaseNumber + " combat phase");
                    combatPhase();
                    phaseNumber++;
                    break;
            case 7: System.out.println(phaseNumber + " construction phase");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            GUI.getDoneButton().setDisable(false);
                        }
                    });
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	GUI.getDoneButton().setOnAction(new EventHandler(){
            		@Override
    				public void handle(Event event) {
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
        });
        
    }

    public void stop(){
        unPause();
    }

    public void setFree(boolean b) { freeClicked = b; }
    public void setPaid(boolean b) { paidClicked = b; }
    public void setStartingPos(Coord[] c) { startingPos = c; }
    public Coord[] getStartingPos() { return startingPos; }
    public int getPhase() { return phaseNumber; }
    public int getNumPlayers() { return numPlayers; }
    public Player[] getPlayers() { return playerList; }
    public Player getPlayer(){ return this.player; }
    public ArrayList<Coord> getBattleGrounds() { return battleGrounds; }
    public boolean getPaused() { return isPaused; }
    
    public void setLocalPlayer(String s) { localPlayer = s; }
    public void setPhase(int i) { phaseNumber = i; }
    public void setPlayerClicked(Player p) { playerClicked = p; }
    public void setPieceClicked(Piece p) { pieceClicked = p; }
    
    public void setSyncronizer(boolean b) { syncronizer = b; }
    public boolean getSyncronizer() { return syncronizer; }
    
}
