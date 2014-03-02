package KAT;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.sql.*;

/**
 * An asynchronous, multithreaded server using the Reactor pattern
 */
public class ReactorServer implements EventHandler 
{
    // a map of event handlers with their type as a key
    private HashMap<String,EventHandler> eventHandlers;
    private boolean running;
    private int port;

    ReactorServer( int port ){
        this.eventHandlers = new HashMap<String,EventHandler>();
        this.port = port;
        this.running = false;

        init();
        kATDB.setup();
    }

    public void init(){
        registerHandler("LOGIN", new LoginEventHandler());
    }

    public void stop(){
        running = false;
    }

    public void start(){
        ServerSocket ss = null;
        running = true;

        try {
            ss = new ServerSocket(port);
            System.out.println("\nListening on port: " + port);
            System.out.println();
        } catch (IOException e) {
            stop();
            e.printStackTrace();
        }

        while( running ){
            try {
                final Socket s = ss.accept();

                new Thread(new Runnable(){
                    public void run(){
                        try {
                            service(s);
                        } catch( Exception e ){
                            e.printStackTrace();
                        } finally {
                            try {
                                if( s != null ){
                                    s.close();
                                }
                            } catch( IOException e ){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start(); // execute a new thread for each client
            } catch( Exception e ){
                stop();
                e.printStackTrace();
            }
        }
        try {
            if( ss != null ){
                ss.close();
            }
        } catch( IOException e ){
            e.printStackTrace();
        }
    }

    private void service( final Socket s )throws IOException, ClassNotFoundException {
        boolean connected = true;

        // estabalish IO streams
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

        // run loop: listen for messages from client
        while( running && connected ){
            Message m = (Message)ois.readObject();
            System.out.println("Received Message: " + m);

            // create and dispatch a new event
            String type = m.getHeader().getType();
            Event event = new Event(type, m.getBody().getMap());
            event.put("OUTSTREAM", oos);
            boolean error = false;

            switch( type ){
                case "CONNECT":
                    System.out.println("client connected");
                    break;
                case "LOGIN":
                    // handle login event
                    error = !handleEvent(event);
                    break;
                case "EXIT":
                    // exit the while loop, thus endnig the client's thread
                    connected = false;
                    s.close();
                    break;
                default:
                    error = true;
                    System.err.println("Error: unrecognized message type: " + type);
                    break;
            }

            if( error ){
                // should notify client there was an error processing the message
                System.err.println("Error: handling event: "+event);
            }
        }
    }

    public void registerHandler( String type, EventHandler handler ){
        eventHandlers.put(type, handler);
    }

    public void deregisterHandler( String type ){
        eventHandlers.remove(type);
    }

    /**
     * Dispatches an Event to an EventHandler.
     * @return true if successfully handled, false otherwise
     */
    @Override
    public boolean handleEvent( Event event ){
        EventHandler handler = eventHandlers.get(event.getType());
        if( handler != null ){
            try {
                return handler.handleEvent(event);
            } catch( IOException e ){
                System.err.println("Error: no handler registered for type: "+event.getType());
                return false;
            }
        } else {
            return false;
        }
    }

    private void setupDB(){
        try {
            // open db connection
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();


            /* create tables (if they do not already exist) */

            String sql;

            // users are identified by their unique username or uID
            sql = "create table if not exists users("
                + "uID integer primary key autoincrement,"
                + "username text unique not null);";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // the gID uniquely identifies a single game
            sql = "create table if not exists games("
                + "gID integer primary key autoincrement,"
                + "gameSize integer not null," // total number of players
                + "user1 integer not null," 
                + "user2 integer,"
                + "user3 integer," 
                + "user4 integer,"
                + "foreign key(user1) references users(uID),"
                + "foreign key(user2) references users(uID),"
                + "foreign key(user3) references users(uID),"
                + "foreign key(user4) references users(uID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
            
            // players are users with extra game specific values
            sql = "create table if not exists players("
                + "uID integer not null,"
                + "gID integer not null,"
                + "color text not null,"
                + "gold integer not null,"
                + "primary key(uID, gID),"
                + "foreign key(uID) references users(uID),"
                + "foreign key(gID) references games(gID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // a table of all existing pieces
            sql = "create table if not exists pieces("
                + "pID integer primary key autoincrement,"
                + "type text not null,"
                + "fIMG text,"
                + "bIMG text);";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // game board tiles
            sql = "create table if not exists tiles("
                + "gID integer not null,"
                + "x integer not null,"
                + "y integer not null,"
                + "z integer not null,"
                + "terrain text not null,"
                + "orientation integer not null," // 1 for face-up, 0 face-down
                + "uID integer,"
                + "pID integer,"
                + "primary key(gID, x, y, z),"
                + "foreign key(gID) references games(gID),"
                + "foreign key(uID) references users(uID),"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // player racks correspond to a user and a game
            sql = "create table if not exists playerRacks("
                + "uID integer not null,"
                + "gID integer not null,"
                + "pID integer,"
                + "primary key(uID, gID),"
                + "foreign key(uID) references uses(uID),"
                + "foreign key(gID) references games(gID),"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // the cup for each game
            sql = "create table if not exists gameCups("
                + "gID integer primary key,"
                + "pID integer,"
                + "foreign key(gID) references games(gID),"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // small table to store any special character that are offside
            sql = "create table if not exists offside("
                + "pID primary key,"
                + "uID integer,"
                + "foreign key(pID) references pieces(pID),"
                + "foreign key(uID) references users(uID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();


            /* the following tables are extensions of the 'pieces' table 
             * and acts as a static list of all default pieces */
            
            // a table of all existing creatures 
            sql = "create table if not exists creatures("
                + "pID integer not null,"
                + "name text not null,"
                + "combatVal integer not null,"
                + "flying integer not null," // treated as a boolean (1 or 0)
                + "ranged integer not null,"
                + "magic integer not null,"
                + "charging integer not null,"
                + "terrain text,"
                + "primary key(pID),"
                + "foreign key(pID) references pieces(pID),"
                + "foreign key(terrain) references tiles(terrain));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // special characters
            sql = "create table if not exists specialCharacters("
                + "pID integer not null,"
                + "name text not null,"
                + "combatVal integer,"
                + "flying integer," // boolean (1 or 0)
                + "ranged integer,"
                + "magic integer,"
                + "charging integer,"
                + "primary key(pID),"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();


            /* need a second version of the pieces tables because some of their
             * attributes are subject to change during gameplay */

            // creatures
            sql = "create table if not exists gameCreatures("
                + "pID integer not null,"
                + "gID integer not null,"
                + "name text not null,"
                + "combatVal integer not null,"
                + "flying integer not null,"
                + "ranged integer not null,"
                + "magic integer not null,"
                + "charging integer not null,"
                + "terrain text,"
                + "orientation integer not null," // 1 for faceup, 0 for bluff
                + "primary key(pID, gID),"
                + "foreign key(pID) references pieces(pID),"
                + "foreign key(gID) references games(gID)," 
                + "foreign key(terrain) references tiles(terrain));"; 
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
            
            // special characters
            sql = "create table if not exists gameSpecialCharacters("
                + "pID integer,"
                + "gID integer,"
                + "name text not null,"
                + "combatVal integer,"
                + "flying integer," // boolean (1 or 0)
                + "ranged integer,"
                + "magic integer,"
                + "charging integer,"
                + "primary key(pID, gID),"
                + "foreign key(pID) references pieces(pID),"
                + "foreign key(gID) references games(gID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();


            // forts are specific to an owner-hex
            sql = "create table if not exists forts("
                + "uID integer not null,"
                + "gID integer not null,"
                + "x integer not null,"
                + "y integer not null,"
                + "z integer not null,"
                + "combatVal integer not null,"
                + "neutralized integer not null," // 1 for true, else 0
                + "primary key(uID, gID, x, y, z),"
                + "foreign key(uID) references users(uID),"
                + "foreign key(gID) references games(gID),"
                + "foreign key(x) references tiles(x),"
                + "foreign key(y) references tiles(y),"
                + "foreign key(x) references tiles(z));"; 
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
        
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        } 
    }
    
    /**
     * @param args pass args[0] as the port, default is 8888
     */
    public static void main( String args[] ){

        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8888;

        System.out.println("\nStarting Server: Kings&Things...");

        new ReactorServer(port).start();
    }
}
