package KAT;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.sql.*;

public class LoginEventHandler implements EventHandler
{
    public boolean handleEvent( Event event ){
        String username = (String)event.getMap().get("username");
        
        // create verification message to send back to client
        Message m = new Message("LOGINSUCCESS", "SERVER");
        ObjectOutputStream oos = (ObjectOutputStream)event.getMap().get("OUTSTREAM");

        Connection db;
        Statement stmnt;

        try {
            // open db connection
            Class.forName("org.sqlite.JDBC");
            db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            stmnt = db.createStatement();

            // create query string
            String query = "select count(*) from users where username = '"+username+"';";
            
            // validate login with db
            ResultSet rs = stmnt.executeQuery(query);

            while( rs.next() ){
                int count = rs.getInt("count(*)");
    
                // user is not in the database
                if( count == 0 ){
                    // create  new tuple
                    query = "insert into users (username) values ('"+username+"');";
                    stmnt.executeUpdate(query);
                }

                System.out.println("User '"+username+"' appeared in table "+count+" times");
            }
            
            // check if the user is currently playing a game
            int uID = KATDB.getUID(username);
            query = "select count(*) from players where uID = '"+uID+"';";
            rs = stmnt.executeQuery(query);
            
            if( rs.next() ){
            	int count = rs.getInt("count(*)");
            	
            	if( count == 0 ){
            		// not in a game, join a new one
            		boolean needsCupData = !KATDB.joinGame(uID, 4);
            		m.getBody().put("needsCupData", needsCupData);
            	}
            }         
            
            int gID = KATDB.getGID(uID);
            KATDB.getGameState(m.getBody().getMap(), gID);
        } catch( Exception e ){
        	e.printStackTrace();
        }

        try {
            oos.writeObject(m);
        } catch( IOException e ){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
