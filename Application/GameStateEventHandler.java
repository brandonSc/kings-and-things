package KAT;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameStateEventHandler implements EventHandler
{
	@SuppressWarnings("unchecked")
	public boolean handleEvent( Event event ){
        int gameSize = (Integer)event.get("gameSize");
        int numPlayers = (Integer)event.get("numPlayers");
        Player[] players = NetworkGameLoop.getInstance().getPlayers();

        // update player list
        for( int i=0; i<numPlayers; i++ ){
            HashMap<String,Object> playerInfo 
                = (HashMap<String,Object>)event.get("Player"+(i+1));
            String username = (String)playerInfo.get("username");
            String color = (String)playerInfo.get("color");
            Player newPlayer = new Player(username, color);
            newPlayer.setGold((Integer)playerInfo.get("gold"));
            NetworkGameLoop.getInstance().addPlayer(newPlayer);
        }
        
        // now check for any tile changes
        // TODO
        
        // check for any pieces added to the cup
        ArrayList<Integer> cupData = (ArrayList<Integer>)event.get("cupData");
        TheCup theCup = TheCup.getInstance();

        for( Integer pID : cupData ){
        	if( !theCup.containsPiece(pID) ){
        		// data contains a piece not in the cup, add the new one
        		HashMap<String,Object> map = (HashMap<String,Object>)event.get(""+pID);
        		String type = (String)map.get("type");
        		if( type.equals("Creature") ){
        			theCup.addToCup(new Creature(map));
        		} else if( type.equals("SpecialCharacter") ){
        			// TODO add cases for necessary special character. This will be tedious...
        			theCup.addToCup(new SpecialCharacter(map));
        		} else if( type.equals("Random Event") ){
        			// TODO add cases for random events
        		} else if( type.equals("Magic Event") ){
        			// TODO add cases for each magic event
        		} else if( type.equals("Special Income") ){
        			theCup.addToCup(new SpecialIncome(map));
        		} else {
        			System.err.println("error: type not recognized: "+type);
        		}
        	} 
        }
        
        // check for any pieces to be removed from the cup 
        // (would like a more efficient way of doing this)
        ArrayList<Piece> theCupPieces = theCup.getRemaining();
        ArrayList<Integer> theCupPIDS = new ArrayList<Integer>();
        for( Piece p : theCupPieces ){
        	theCupPIDS.add(p.getPID());
        }
        for( Integer pID : theCupPIDS ){
        	if( !cupData.contains(pID) ){
        		theCup.removePiece(pID);
        	}
        }
        
        // check for any pieces not in player rack
        // TODO
        
        // check for any offside special characters
        // TODO

        return true;
    }
}

