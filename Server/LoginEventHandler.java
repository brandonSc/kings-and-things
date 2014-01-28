package KAT;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.sql.*;

public class LoginEventHandler implements EventHandler
{
    public boolean handleEvent( Event event ){
        String username = (String)event.getMap().get("username");

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

                System.out.println("User '"+username+"' appears in table "+count+" times");
            }
        
            // close db
            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
            return false;
        }

        // send verification message to client
        Message m = new Message("LOGINSUCCESS", "SERVER");

        try {
            ObjectOutputStream oos = (ObjectOutputStream)event.getMap().get("OUTSTREAM");
            oos.writeObject(m);
        } catch( IOException e ){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
