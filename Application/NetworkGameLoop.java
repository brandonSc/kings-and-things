package KAT;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Coord[] startingPos;
    private KATClient client;
    private Player playerTurn;

    /*
     * Constructor.
     */
    private NetworkGameLoop() {
        phaseNumber = 0;
        numPlayers = 1;
        gameSize = 2; // this should be set from GUI by user
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
        //this.player = player.get(0); 
        this.playerList = new Player[gameSize];
        playerList[0] = this.player;
        numPlayers = 1;
        this.playerTurn = this.player;

        client.postLogin(this.player.getName(), gameSize);
        System.out.println("Waiting for more players...");
        waitForOtherPlayers(2000);
        System.out.println("end setPlayers()");
    }

    public void addPlayer(Player p) {
        boolean newPlayer = true;
    	for( int i=0; i<numPlayers; i++ ){
    		if( playerList[i].getName().equals(p.getName()) ){
                playerList[i] = p;
                newPlayer = false;
                if( p.getName().equals(this.player.getName()) ){
                    this.player = p;
                }
            }
    	}
        if( newPlayer ){
            System.out.println("Player "+p.getName()+" just joined the game");
            playerList[numPlayers] = p;
            numPlayers++;
        
            System.out.println("numPlayers="+numPlayers+", gameSize="+gameSize);
            if( numPlayers == gameSize ){
                System.out.println("unPause!");
                unPause();
            }
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
        if (phaseNumber != -2)
            phaseNumber = -1;
        ClickObserver.getInstance().setTerrainFlag("Setup: deal");
        setButtonHandlers();
        PlayerBoard.getInstance().updateNumOnRacks();
        // Create starting spots, will change this for fewer players in future
        Coord[] validPos = {  new Coord(2,-3,1),new Coord(2,1,-3),new Coord(-2,3,-1),new Coord(-2,-1,3) };
        startingPos = validPos;
    }

    public void addStartingHexToPlayer(){
    	System.out.println("addStartingHexToPlayer()");
        
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
		        // update server
		        HashMap<String,Object> map = new HashMap<String,Object>();
		        map.put("updateType", "addTile");
		        map.put("tile", t.toMap());
		        map.put("changeTurns", true);
		        client.postGameState(map);
		        unPause();
            }
        }
    }

    public void addHexToPlayer(){
    	System.out.println("addHexToPlayer()");
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
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("updateType", "addTile");
            map.put("tile", t.toMap());
            map.put("changeTurns", true);
            client.postGameState(map);
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

    /**
     * Steps of setup phase:
     *  (1) if not user's turn, wait for other players
     *  (2) select starting hex then wait for other players
     *  (3) reveal game board
     *  (4) select adjacent hex and wait for other players
     *  (5) select second adjacent hex; wait for other players
     *  (6) select a hex to place first tower. Wait for players online
     *  (7) prompt to place things on board. wait for players online
     */
    private void setupPhase() {
        // prompt each player to select their initial starting position
        ClickObserver.getInstance().setActivePlayer(this.player);
        
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
            }
        });
        // do not continue if it is not the player's turn
        if( !player.getName().equals(playerTurn.getName()) ){
        	ClickObserver.getInstance().setTerrainFlag("Disabled");
        	waitForOtherPlayers(2000);
        }
        ClickObserver.getInstance().setTerrainFlag("Setup: SelectStartTerrain");
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getHelpText().setText("Setup Phase: " + player.getName() 
                        + ", select a valid hex to start your kingdom.");
            }
        });
        
        // wait for user to select their first hex
        waitForUser();
        // then wait for other players, checking for changes every 2 seconds
        waitForOtherPlayers(2000);        
       
        // Now that all players have selected starting spots, flip over all terrains
        // *Note:  Not sure I understand the rules with regards to this, but I think this is right
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	 Board.showTerrains();
                 //Board.removeBadWaters();
            }
        });
        
        // Check if player has at least two land hexes around starting spot
        ClickObserver.getInstance().setActivePlayer(this.player);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getHelpText().setText(player.getName() 
                        + ", select a water hex to replace with from deck");
                //Board.removeBadAdjWaters();
            }
        });
        /* this doen't work right now 
        
        // wait for user to replace hex
        waitForUser();
        // wait for other players
        waitForOtherPlayers(2000);
        */

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
            ClickObserver.getInstance().setActivePlayer(this.player);
            
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
                    GUI.getHelpText().setText("Setup Phase: " + player.getName() 
                            + ", select an adjacent hex to add to your kingdom.");
                }
            });
            // wait for user to select hex
            waitForUser();
            // then wait for other players
            waitForOtherPlayers(2000);
        }
        
        // prompt player to place their first tower
        ClickObserver.getInstance().setTerrainFlag("Construction: ConstructFort");
        ClickObserver.getInstance().setActivePlayer(this.player);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Board.applyCovers();
                GUI.getRackGui().setOwner(player);
                GUI.getHelpText().setText("Setup Phase: " + player.getName() 
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
        // wait for user
        waitForUser();
        // wait for other players
        waitForOtherPlayers(2000);
        
        // allow players to add some or all things to their tiles.
        ClickObserver.getInstance().setTerrainFlag("RecruitingThings: PlaceThings");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getDoneButton().setDisable(false);
            }
        });
    
        // ask to place initial things on board
        doneClicked = false;
        ClickObserver.getInstance().setClickedTerrain(player.getHexesOwned().get(2));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	ClickObserver.getInstance().whenTerrainClicked();
                GUI.getRackGui().setOwner(player);
                Board.applyCovers();
                GUI.getHelpText().setText("Setup Phase: " + player.getName()
                        + ", place some or all of your things on a tile you own.");
            }
        });
        ClickObserver.getInstance().setActivePlayer(this.player);
        // wait for user
        waitForUser();
        // wait for other players
        waitForOtherPlayers(2000);

        ArrayList<Terrain> ownedTiles = player.getHexesOwned();
        for (final Terrain t : ownedTiles) {
        	if (t.getOwner().getName().equals(player.getName())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                		t.uncover();
                    }
                });
        	}
        }
        // wait for user
        waitForUser();
        // wait for other players
        waitForOtherPlayers(2000);
        
        ClickObserver.getInstance().setTerrainFlag("");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Board.removeCovers();
            }
        });
    }
    /*
    private void setupPhase() {
        // prompt each player to select their initial starting position
        ClickObserver.getInstance().setTerrainFlag("Setup: SelectStartTerrain");
        // Covering all terrains that are not valid selections
        final Coord[] startSpots = {
            new Coord(2,-3,1),new Coord(2,1,-3),new Coord(-2,3,-1),new Coord(-2,-1,3)
        };
        ClickObserver.getInstance().setActivePlayer(this.player);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GUI.getRackGui().setOwner(player);
                Board.applyCovers();
                for (Coord spot : startSpots) {
                    if (!Board.getTerrainWithCoord(spot).isOccupied())
                        Board.getTerrainWithCoord(spot).uncover();
                }
            }
        });
        if( !player.getName().equals(playerTurn.getName()) ){
            waitForOtherPlayers(2000);
        }
        GUI.getHelpText().setText("Setup Phase: " + player.getName() 
                + ", select a valid hex to start your kingdom.");
        waitForUser();
        ClickObserver.getInstance().setTerrainFlag("Disabled");
        GUI.getHelpText().setText("Setup Phase: waiting for other players...");
        waitForOtherPlayers(2000);

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
                waitForUser();
                waitForOtherPlayers(2000);
            }
        }
        // prompt each player to place their first tower
        ClickObserver.getInstance().setTerrainFlag("Construction: ConstructFort");
        for( Player p : playerList ) {
            this.player = p;
            ClickObserver.getInstance().setActivePlayer(this.player);
            waitForUser();
            
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
            waitForUser();
            
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
    */

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
            waitForUser();
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
	        waitForUser();
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
    	waitForUser();
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
	    						waitForUser();
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
		    						waitForUser();
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
                                waitForUser();
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
    
    public void waitForUser(){
        isPaused = true;
        while( isPaused ){
            try { Thread.sleep(100); } catch( Exception e ){ return; }
        }
    }

    /**
     * @param updateInterval time to wait between state updates in milliseconds
     */
    public void waitForOtherPlayers( int updateInterval ){
        isPaused = true;
        ClickObserver.getInstance().setTerrainFlag("Disabled");
        ClickObserver.getInstance().setFortFlag("Disabled");
        ClickObserver.getInstance().setCreatureFlag("Disabled");
        ClickObserver.getInstance().setPlayerFlag("Disabled");
        while( isPaused ){
        	if( playerTurn != null && playerList[1] != null ){
        		playerTurn = playerList[1]; // to adjust for latency from server
        		GUI.getHelpText().setText("Waiting for "+playerTurn.getName() 
        				+ " to finish their turn");
        	}
            try { Thread.sleep(updateInterval); } catch( Exception e ){ return; }
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

    public void setPlayerTurn( Player player ){ this.playerTurn = player; }
    public Player getPlayerTurn(){ return this.playerTurn; }
    public void setFree(boolean b) { freeClicked = b; }
    public void setPaid(boolean b) { paidClicked = b; }
    public int getPhase() { return phaseNumber; }
    public int getNumPlayers() { return numPlayers; }
    public Player[] getPlayers() { return playerList; }
    public Player getPlayer(){ return this.player; }
    public int getGameSize(){ return this.gameSize; }
    public void setGameSize( int gameSize ){ this.gameSize = gameSize; }
    public void setPlayer( Player p ){ this.player = p; }

    public void setPhase(int i) { phaseNumber = i; }
}
