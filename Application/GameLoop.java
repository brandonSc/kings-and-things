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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
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
	        pause();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
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
     */
    private void combatPhase() {
    	
    	try { Thread.sleep(100); } catch( Exception e ){ return; }
    	pause();
    	System.out.println("Entering combat phase");
    	ClickObserver.getInstance().setTerrainFlag("");
    	ClickObserver.getInstance().setCreatureFlag("Combat: SelectCreatureToAttack");
    	
    	// Go through each battle ground a resolve each conflict
    	for (Coord c : battleGrounds) {

        	System.out.println("Entering first battleGround");
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

        	    		player = p;
	                	Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {

	    	                	PlayerBoard.getInstance().applyCovers();
	    	                	if (!player.getName().equals(p.getName()))
	    	                		PlayerBoard.getInstance().uncover(player);
	    	        	        GUI.getHelpText().setText("Attack phase: " + player.getName()
	    	                            + ", select which player to attack");
		        	    	}
	    	            });
	                	while (isPaused) {
	                     	try { Thread.sleep(100); } catch( Exception e ){ return; }  
	         	        }
	                	
	                	// ClickObserver sets playerClicked, then unpauses. This stores what player is attacking what player
	                	toAttacks.put(p.getName(), playerClicked);
	                	
        	    	}
    				
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
    			for (Player pl : combatants) {
    				
    				successAttacks.put(pl.getName(), new ArrayList<Piece>());
    				
    			}
    			
    			// 
				final ArrayList<Piece> phaseThings = new ArrayList<Piece>();
				
    			////////// Magic phase
    			for (final Player pl : combatants) {
    				
    				player = pl;
    				// Cover all pieces
    				Platform.runLater(new Runnable() {
    	                @Override
    	                public void run() {
    	            		battleGround.coverPieces();
    	                }
    	            });
    				
    				// For each piece, if its magic. Add it to the magicThings array
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					
    					if (p instanceof Combatable && ((Combatable)p).isMagic()) {

    						phaseThings.add(p);
    						
    					}
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
    				pieceClicked = null;
    				while (phaseThings.size() > 0) {
    					
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName() + ", select a magic piece to attack with");
	    	                }
	    	            });
    					
        				ClickObserver.getInstance().setCreatureFlag("Combat: SelectCreatureToAttackWith");
    					while (pieceClicked == null) { 
    						try { Thread.sleep(100); } catch( Exception e ){ return; }
    					}
	    				ClickObserver.getInstance().setCreatureFlag("");
	    				
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
    						try { Thread.sleep(50); } catch( Exception e ){ return; }
    						attackStrength = Dice.getFinalVal();
    					}

						
    					// If the roll was successful. Add to successfulThings Array, and change it image
    					if (attackStrength <= ((Combatable)pieceClicked).getCombatValue()) {

    						successAttacks.get(pl.getName()).add(pieceClicked);
    						
    						Platform.runLater(new Runnable() {
    	    	                @Override
    	    	                public void run() {
    	    						((Combatable)pieceClicked).setAttackResult(true);
    	    						pieceClicked.cover();
    	    						pieceClicked.unhighLight();
    	    	                	GUI.getHelpText().setText("Attack phase: Successful Attack!");
    	    	                }
        					});
    						
    						
    					} else { // else failed atatck
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
    					// For usability, pause for a second
    					try { Thread.sleep(1000); } catch( Exception e ){ return; }
    					
    					phaseThings.remove(pieceClicked);
    					pieceClicked = null;
    					
    				}
    			}

				// For each piece that had success, player who is being attacked must choose a piece
    			// Gets tricky here. Will be tough for Networking :(
    			for (Player pl : combatants) {
    				
    				// Player to attack
    				final Player defender = toAttacks.get(pl.getName());
    				player = defender;
    				final String plName = pl.getName();
    				
    				// For each piece of pl's that has a success
    				for (final Piece p : successAttacks.get(pl.getName())) {
	    				Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	GUI.getHelpText().setText("Attack phase: " + player.getName()
	    	                            + ", Select a Piece to take a hit from " + plName + "'s " + p.getName());
	    	                	battleGround.coverPieces();
	    	                	battleGround.uncoverPieces(defender.getName());
	    	                }
						});
	    				
	    				ClickObserver.getInstance().setCreatureFlag("Combat: SelectPieceToGetHit");
    					while (pieceClicked == null) { 
    						try { Thread.sleep(100); } catch( Exception e ){ return; }
    					}
    					ClickObserver.getInstance().setCreatureFlag("");
		    			
    					System.out.println(pieceClicked);
    					((Combatable)pieceClicked).inflict();
    					
    					Platform.runLater(new Runnable() {
	    	                @Override
	    	                public void run() {
	        					InfoPanel.showTileInfo(battleGround);
	    	                }
						});
	    				
    				}
    				successAttacks.get(pl.getName()).clear();
    				
    				
    				
    			}
    			
    			// Remove little success and failure images
    			for (Player pl : combatants) {
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					((Combatable)p).resetAttack();
    				}
    			}
    			
    			System.out.println("Made it past Magic!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				// TODO check for defeated armies
    			
    			//// Ranged phase
    			
    			for (Player pl : combatants) {
    				
    				ArrayList<Piece> rangedThings = new ArrayList<Piece>();
    				
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					
    					if (p instanceof Combatable && ((Combatable)p).isRanged()) {

    						rangedThings.add(p);
        					// TODO have InfoPanel cover all non ranged creatures and forts
    						
    					}
    				}
    				
    				while (rangedThings.size() > 0) {
    					
    					// TODO roll for each ranged piece. Have user select piece to roll for. Once piece is rolled for, remove from rangedThings
    					
    					// TODO somehow mark each piece that had a successful roll
    				}
    			}
    			// TODO for each player, select a piece to take a hit from each of the attacking players successful attackers

				// TODO check for defeated armies
    			
    			//// Melee phase
    			
    			for (Player pl : combatants) {
    				
    				ArrayList<Piece> meleeThings = new ArrayList<Piece>();
    				
    				for (Piece p : attackingPieces.get(pl.getName())) {
    					
    					if (p instanceof Combatable && !(((Combatable)p).isRanged() || ((Combatable)p).isMagic())) {

    						meleeThings.add(p);
        					// TODO have InfoPanel cover all non melee creatures and forts
    						
    					}
    				}
    				
    				while (meleeThings.size() > 0) {
    					
    					// TODO roll for each melee piece. Have user select piece to roll for. Once piece is rolled for, remove from meleeThings
    					// TODO roll twice for charging piece
    					
    					// TODO somehow mark each piece that had a successful roll
    				}
    			}
    			// TODO for each player, select a piece to take a hit from each of the attacking players successful attackers

				// TODO check for defeated armies
    			
    			
    			//// Retreat phase
    			// TODO if just attacker and defender: attack can retreat first, then defender
    			// TODO if multiple attackers: attacker to the left of defender can retreat first
    			
    			
    			//// Post Combat
    			// TODO winner of battle owns hex
    			// TODO check forts, city/village and special incomes if they are kept or lost/damaged 
    			// 		- Citadels are not lost or damaged
    			// 		- if tower is damaged, it is destroyed
    			//		- if keep/castle is damaged, its level is lowered by one
    			// 		- roll dice, a 1 or 6 is kept/not damaged. 2 to 5 the piece is destroyed/damaged
    			
    		}
    	}
    	
    	
    	
    	
    	
    	
//    	for( Player p : playerList ){
//    		this.player = p;
//    		ArrayList<Terrain> hexes = player.getHexesWithPiece();
//    		ClickObserver.getInstance().setActivePlayer(player);
//	    	for( final Terrain t : hexes ){
//	    		// check if player is on another player's hex
//	    		if( t.getContents().keySet().size() > 1 ){
//	    			System.out.println("combat");
//	    			CreatureStack pieces = t.getContents(player.getName());
//	    			// magic phase, attack an enemy creature for each owned magic creature
//	    			for( Piece piece : pieces.getStack() ){ 
//	    				if( piece instanceof Creature ){
//	    					final Creature c = (Creature)piece;
//	    					if( c.isMagic() ){
//	    						// should roll dice first, if less than combatValue, then skip while loop
//	    						Platform.runLater(new Runnable() {
//	    			                @Override
//	    			                public void run() {
//	    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
//	    	    								+c.getName()+" to attack.");
//	                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
//	    			                }
//	    			            });
//	    						while( isPaused ){
//	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
//	    				    	}
//	    					}
//	    				}
//	    			}
//	    			// ranged phase, attack an enemy creature for each owned ranged creature
//	    			for( Piece piece : pieces.getStack() ){ 
//	    				if( piece instanceof Creature ){
//	    					final Creature c = (Creature)piece; 
//	    					if( c.isFlying() ){
//	    						Platform.runLater(new Runnable() {
//	    			                @Override
//	    			                public void run() {
//	    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
//	    	    								+c.getName()+" to attack.");
//	                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
//	    			                }
//	    			            });
//	    						pause();
//	    						// should roll dice first, if less than combatValue, then skip while loop
//	    						while( isPaused ){
//	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
//	    				    	}
//	    					}
//	    				}
//	    			}
//	    			// melee phase, attack an enemy creature for all other owned creatures
//	    			for( Piece piece : pieces.getStack() ){ 
//	    				if( piece instanceof Creature ){
//	    					final Creature c = (Creature)piece; 
//	    					if( c.isCharging() ){
//	    						// attack twice for charging creatures
//	    						for( int i=0; i<2; i++ ){
//		    						// should roll dice first, if less than combatValue, then skip while loop
//		    						Platform.runLater(new Runnable() {
//		    			                @Override
//		    			                public void run() {
//		    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
//		    	    								+c.getName()+" to attack.");
//		                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
//		    			                }
//		    			            });
//		    						pause();
//		    						while( isPaused ){
//		    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
//		    				    	}
//	    						}
//	    					} else if( !c.isMagic() && !c.isRanged() && !c.isCharging() ){
//                                Platform.runLater(new Runnable() {
//	    			                @Override
//	    			                public void run() {
//	    			                	GUI.getHelpText().setText("Magic Combat Phase: "+player.getName()+", select an enemy creature for "
//	    	    								+c.getName()+" to attack.");
//	                                    GUI.getInfoPanel().showTileInfo(t); // present this hex
//	    			                }
//	    			            });
//                                pause();
//	    						while( isPaused ){
//	    				    		try { Thread.sleep(100); } catch( Exception e ){ return; }
//	    				    	}
//	    					}
//	    				}
//	    			}
//	    		}
//	    	}
//    	}  
//    	ClickObserver.getInstance().setTerrainFlag("");
    }
    
    public void attackPiece( Combatable piece ){
    	System.out.println("Attacking piece");
    	final Terrain t = ClickObserver.getInstance().getClickedTerrain();
    	
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	GUI.getInfoPanel().showTileInfo(t);
            }
        });
    	unPause();
    	System.out.println("done attacking");
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
    
    public void setLocalPlayer(String s) { localPlayer = s; }
    public void setPhase(int i) { phaseNumber = i; }
    public void setPlayerClicked(Player p) { playerClicked = p; }
    public void setPieceClicked(Piece p) { pieceClicked = p; }
    
}
