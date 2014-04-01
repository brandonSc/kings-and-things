package KAT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class KATClient extends Client
{
    public KATClient( String host, int port ){
        super(host, port);
        registerHandler("PLAYERJOIN", new JoinGameEventHandler());
        registerHandler("LOGINSUCCESS", new LoginSuccessEventHandler());
        registerHandler("GAMESTATE", new GameStateEventHandler());
    }
    
    public void postLogin( String username, int gameSize ){
        Message m = new Message("LOGIN", username);
        m.getBody().put("username", username);
        m.getBody().put("gameSize", gameSize);

        try {
            this.oos.writeObject(m);
            this.oos.flush();
        } catch( IOException e ){
            e.printStackTrace();
        } catch( NullPointerException e ){
            e.printStackTrace();
        }
    }

    public void getGameState( String username ){
        Message m = new Message("GAMESTATE", username);
        m.getBody().put("username", username);

        try {
            this.oos.writeObject(m);
            this.oos.flush();
        } catch( IOException e ){
            e.printStackTrace();
        } catch( NullPointerException e ){
            e.printStackTrace();
        }
    }

    public void postGameState( Player player ){
        Message m = new Message("UPDATEGAME", player.getName());
        m.getBody().put("username", player.getName());
        
        // add contents of the cup
        ArrayList<Piece> theCup = TheCup.getInstance().getRemaining();
        ArrayList<Integer> pIDs = new ArrayList<Integer>();
        for( Piece p : theCup ){
        	pIDs.add(p.getPID());
        	HashMap<String,Object> piece = p.toMap();
        	m.getBody().put(""+p.getPID(), piece);
        	
        }
        m.getBody().put("pIDs", pIDs);
        
        // add contents of this users playerRack
        
        // add game tiles
        
        // add special characters
        

        try {
            this.oos.writeObject(m);
            this.oos.flush();
        } catch( IOException e ){
            e.printStackTrace();
        } catch( NullPointerException e ){
            e.printStackTrace();
        }
    }
}
