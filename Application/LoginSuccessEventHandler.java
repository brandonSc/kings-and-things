package KAT;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginSuccessEventHandler implements EventHandler
{
    @SuppressWarnings("unchecked")
	public boolean handleEvent( Event event ){
    	boolean error = false;
    	ObjectOutputStream oos = (ObjectOutputStream)event.getMap().get("OUTSTREAM");
    	
        try {
            boolean needsCupData = (boolean)event.getMap().get("needsCupData");
            
            // check if the user is the first in the game, and needs to provide contents of TheCup
            if( needsCupData ){
            	Player player = NetworkGameLoop.getInstance().getPlayer();
            	Message m = new Message("CUPDATA", player.getName());
            	ArrayList<Piece> cupData = TheCup.getInstance().getOriginal();
            	ArrayList<Integer> pIDs = new ArrayList<Integer>();
            	for( Piece p : cupData ){
            		pIDs.add(p.getPID());
            		m.getBody().put(""+p.getPID(), p.toMap());
            	}
            	m.getBody().put("cupData", pIDs);
            	m.getBody().put("username", player.getName());
            	oos.writeObject(m);
            	oos.flush();
            } 
        } catch( NullPointerException e ){
            System.err.println("message body does not contain 'needsCupData'");
            e.printStackTrace();
            error = true;
        } catch( IOException e ){
        	System.err.println("error sending message");
        	e.printStackTrace();
        	error = true;
        }

        return !error;
    }
}
