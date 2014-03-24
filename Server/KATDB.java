// 
// KATDB.java
// kingsandthings/Server/
// @author Brandon Schurman
//
package KAT;

import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;


public class KATDB
{
    public static void setup(){
        dropTables();
        createTables();
    }

    private static void createTables(){
        try {
            // open db connection
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();


            /* create tables (if they do not already exist) */

            String sql;

            // users are identified by their unique uID (or username)
            sql = "create table if not exists users("
                + "uID integer primary key autoincrement,"
                + "username text unique not null);";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // the gID uniquely identifies a single game
            sql = "create table if not exists games("
                + "gID integer primary key autoincrement,"
                + "gameSize integer not null," // number of players allowed to play
                + "numPlayers integer not null," // number of players currently in game 
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
                + "pID integer not null,"
                + "gID integer not null," 
                + "type text not null,"
                + "fIMG text,"
                + "bIMG text,"
                + "primary key(pID, gID),"
                + "foreign key(gID) references games(gID));";
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
                + "pID integer not null,"
                + "primary key(uID, gID, pID),"
                + "foreign key(uID) references uses(uID),"
                + "foreign key(gID) references games(gID),"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // the cup for each game
            sql = "create table if not exists cups("
                + "gID integer,"
                + "pID integer,"
                + "primary key(gID, pID),"
                + "foreign key(gID) references games(gID),"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // small table to store any special character that are offside
            sql = "create table if not exists offside("
                + "pID primary key,"
                + "uID integer,"
                + "gID integer,"
                + "foreign key(pID) references pieces(pID),"
                + "foreign key(uID) references users(uID),"
                + "foreign key(gID) references games(gID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
            
            // a table of all existing creatures 
            sql = "create table if not exists creatures("
                + "pID integer not null,"
                + "gID integer not null,"
                + "name text not null,"
                + "combatVal integer not null,"
                + "flying integer not null," // treated as a boolean (1 or 0)
                + "ranged integer not null,"
                + "magic integer not null,"
                + "charging integer not null,"
                + "terrain text,"
                + "primary key(pID, gID),"
                + "foreign key(pID) references pieces(pID),"
                + "foreign key(gID) references games(gID),"
                + "foreign key(terrain) references tiles(terrain));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();

            // special characters
            sql = "create table if not exists specialCharacters("
                + "pID integer not null,"
                + "gID integer not null,"
                + "name text not null,"
                + "combatVal integer,"
                + "flying integer," // boolean (1 or 0)
                + "ranged integer,"
                + "magic integer,"
                + "charging integer,"
                + "primary key(pID, gID),"
                + "foreign key(gID) references pieces(gID)"
                + "foreign key(pID) references pieces(pID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
            
            // special income counters 
            sql = "create table if not exists specialIncomes("
            	+ "pID integer not null,"
            	+ "gID integer not null,"
            	+ "name text not null,"
            	+ "value integer not null,"
            	+ "treasure integer not null," // boolean 1 or 0
            	+ "primary key(pID, gID),"
            	+ "foreign key(pID) references pieces(pID),"
            	+ "foreign key(gID) references pieces(gID));";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
            
            // random events
            sql = "create table if not exists randomEvents("
            	+ "pID integer not null,"
            	+ "gID integer not null,"
            	+ "name text not null,"
            	+ "owner text,"
            	+ "primary key(pID, gID),"
            	+ "foreign key(pID) references pieces(pID),"
            	+ "foreign key(gID) references pieces(gID),"
            	+ "foreign key(owner) references players(username));";
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
    
    private static void dropTables(){
        try { 
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            String sql = "";

            /*
            sql = "drop table if exists users;";
            stmnt.executeUpdate(sql);
            */

            sql = "drop table if exists games;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists players;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists pieces;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists tiles;";
            stmnt.executeUpdate(sql);
            
            sql = "drop table if exists playerRacks;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists cups;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists offside;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists creatures;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists specialCharacters;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists specialIncomes;";
            stmnt.executeUpdate(sql);
            
            sql = "drop table if exists randomEvents;";
            stmnt.executeUpdate(sql);

            sql = "drop table if exists forts;";
            stmnt.executeUpdate(sql);

            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Adds a piece to game's cup
     * @param gID id of game
     * @param piece map containing details of piece
     */
    public static void addPiece( int gID, 
            HashMap<String,Object> piece ){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            String sql = "";
            
            String type = (String)piece.get("type");
            int pID = (Integer)piece.get("pID");
            
            // add to generic piece table first
            sql = "insert into pieces(pID, gID, type, fIMG, bIMG)"
                + "values("+pID+","+gID+",'"+type
                + "','"+piece.get("fIMG")+"','"+piece.get("bIMG")+"');";
            stmnt.executeUpdate(sql);
            stmnt.close();

            // add to correct table of piece type
            stmnt = db.createStatement();
            
            if( type.equals("Creature") ){
                sql = "insert into creatures(pID,gID,name,combatVal,"
                    + "flying,ranged,charging,magic)"
                    + "values("+pID+","+gID+",'"+piece.get("name")
                    + "',"+piece.get("combatVal")+","+piece.get("flying")
                    + ","+piece.get("ranged")+","+piece.get("charging")
                    + ","+piece.get("magic")+");";
                stmnt.executeUpdate(sql);
            } else if( type.equals("SpecialCharacter") || type.equals("TerrainLord") ){
            	sql = "insert into specialCharacters(pID,gID,name,combatVal,"
            		+ "flying,ranged,charging,magic)"
            		+ "values("+pID+","+gID+",'"+piece.get("name")
                    + "',"+piece.get("combatVal")+","+piece.get("flying")
                    + ","+piece.get("ranged")+","+piece.get("charging")
                    + ","+piece.get("magic")+");";
                stmnt.executeUpdate(sql);
            } else if( type.equals("Special Income") ){
            	sql = "insert into specialIncomes(pID,gID,name,value,treasure)"
            		+ "values("+pID+","+gID+",'"+piece.get("name")
            		+ "',"+piece.get("value")+","+piece.get("treasure")+");";
                stmnt.executeUpdate(sql);
            } else if( type.equals("Random Event") ){
            	sql = "insert into randomEvents(pID,gID,name,owner)"
            		+ "values("+pID+","+gID+",'"+piece.get("name")+"','"+piece.get("owner")+"');";
            	stmnt.executeUpdate(sql);
            } else {
            	System.err.println("Error adding piece to cup: unrecognized piece type: "+type);
            }
            stmnt.close();
            
            // add to cup
            stmnt = db.createStatement();
            sql = "insert into cups(gID,pID) "
            	+ "values("+gID+","+pID+");";
            stmnt.executeUpdate(sql);
            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    public static void addToCup( String username, 
            HashMap<String,Object> map ){
    	int uID = getUID(username);
    	int gID = getGID(uID);
        try {
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();

            String sql = "insert into cups(gID, PID)"
                + "values("+gID+","+map.get("pID")+");";
            stmnt.executeUpdate(sql);

            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Removes a piece from a game's cup
     * @param gID game ID corresponding to the cup
     * @param map details of piece to remove
     */
    public static void removeFromCup( int gID, 
            HashMap<String,Object> map ){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();

            String sql = "delete from cups where pID="+map.get("pID")+" AND gID="+map.get("gID")+";";
            stmnt.executeUpdate(sql);

            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Returns the user ID for a username
     * @param username user's login name
     * @return uID 
     */
    public static int getUID( String username ){
        int uID = -1;
        try { 
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            ResultSet rs;

            String sql = "select * from users where username = '"+username+"';";
            rs = stmnt.executeQuery(sql);
            if( rs.next() ){
            	uID = rs.getInt(1);
            }

            rs.close();
            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
        return uID;
    }

    /**
     * Returns the game ID for a user (if they are in a game)
     * @param uID id of user
     * @return gID
     */
    public static int getGID( int uID ){
        int gID = -1;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            ResultSet rs;
            
            String sql = "select * from players where uID = '"+uID+"';";
            rs = stmnt.executeQuery(sql);

            if( rs.next() ){
                gID  = rs.getInt("gID");
            } else {
                System.err.println("gID not found for "+uID+" in table players");
            }
            rs.close();
            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
        return gID;
    }
    
    /**
     * Creates a new game with a single user
     * @param uID id of first player 
     * @param map details of game (ex number of players)
     */
    public static void createGame( int uID, 
    		HashMap<String, Object> map ){
    	try { 
    		Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            
            int gameSize = (Integer)map.get("gameSize");
            
            String sql = "insert into games(gameSize, numPlayers, user1)"
            		+ "values("+gameSize+","+1+","+uID+");";
            stmnt.executeUpdate(sql);
            stmnt.close();
            
            stmnt = db.createStatement();
            sql = "select * from games where User1 = '"+uID+"';";
            ResultSet rs = stmnt.executeQuery(sql);
            rs.next();
            int gID = rs.getInt("gID");
            rs.close();
            stmnt.close();
            
            stmnt = db.createStatement();
            sql = "insert into players(uID, gID, color, gold)"
            		+ "values("+uID+","+gID+",'YELLOW',"+10+");";
            stmnt.executeUpdate(sql);
            stmnt.close();
            db.close();
    	} catch( Exception e ){
    		e.printStackTrace();
    	}
    	System.out.println("new game created!");
    }
    
    /**
     * Adds a user to a specific game
     * @param uID the id of the user
     * @param gID the id of the specified game
     * @return true if successful, false if no free game to join
     */
    public static boolean joinSpecifcGame( int uID, int gID, 
    		HashMap<String,Object> map ){
    	try { 
    		Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            ResultSet rs;
            
            String color = "";
            
            String sql = "select * from games where"
            		+ "gID = '"+gID+"';";
            rs = stmnt.executeQuery(sql);
            
            // check if the game exists
            if( rs.next() ){
            	int numPlayers = rs.getInt("numPlayers");
            	int gameSize = rs.getInt("gameSize");
            	stmnt.close();
            	rs.close();
            	
            	// check if there is room for another player
            	if( numPlayers < gameSize ){
            		numPlayers++;
            		stmnt = db.createStatement();
            		String playerNum = "User"+numPlayers;
            		sql = "update games set "+playerNum+"= "+uID+", numPlayers="+numPlayers
            				+ "where gID = "+gID+";"; 
            		stmnt.executeUpdate(sql);
            		stmnt.close();
            		// decide color for player
            		switch( numPlayers ){
            		case 2:
            			color = "BLUE";
            			break;
            		case 3:
            			color = "GREEN";
            			break;
            		case 4:
            			color = "RED";
            			break;
            		}
            	} else {
            		db.close();
            		return false;
            	}
            } else {
            	rs.close();
            	stmnt.close();
            	db.close();
            	return false;
            }
            
            
            // add the user to the players table
            sql = "insert into players(uID, gID, color, gold)"
            		+ "values("+uID+","+gID+",'"+color+"',"+10+");";
            stmnt = db.createStatement();
            stmnt.executeUpdate(sql);
            stmnt.close();
            db.close();
    	} catch( Exception e ){
    		e.printStackTrace();
    	}
    	return true;
    }
    
    /**
     * Adds a user to any free game
     * @param uID id for user
     * @param gameSize the number of players user would like to play with
     * @return true if added to a game, false if created a new game
     */
    public static boolean joinGame( int uID, 
    		HashMap<String,Object> map ){
    	boolean found = false;
    	try { 
    		Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            ResultSet rs;
            
            int gameSize = (Integer)map.get("gameSize");
            String color = "";
            
            String sql = "select * from games where gameSize = '"+gameSize+"';";
            rs = stmnt.executeQuery(sql);
            System.out.println("checking for a free game");
            // find the first open game with less players than the gameSize
            while( rs.next() ){
            	int numPlayers = rs.getInt("numPlayers");
            	int gID = rs.getInt("gID");
            	System.out.println("checking game "+gID+"...");
            	
            	// check if there is room for another player
            	if( numPlayers < gameSize ){
            		rs.close();
            		stmnt.close();
            		numPlayers++;
            		stmnt = db.createStatement();
            		String playerNum = "User"+numPlayers;
            		sql = "update games set "+playerNum+"= "+uID+", numPlayers="+numPlayers
            				+ "where gID = "+gID+";"; 
            		stmnt.executeUpdate(sql);
            		stmnt.close();
            		
            		// decide color for player
            		switch( numPlayers ){
            		case 2:
            			color = "BLUE";
            			break;
            		case 3:
            			color = "GREEN";
            			break;
            		case 4:
            			color = "RED";
            			break;
            		}
            		
            		// add the user to the players table
            		stmnt = db.createStatement();
                    sql = "insert into players(uID, gID, color, gold)"
                    		+ "values("+uID+","+gID+",'"+color+"',"+10+");";
                    stmnt.executeUpdate(sql);
                    stmnt.close();
                    found = true;
                    System.out.println("joining game with gID: "+gID);
            		break;
            	} 
            }
            if( !found ){
            	rs.close();
            	stmnt.close();
            	System.out.println("no free games to join");
            }
            db.close();
    	} catch( Exception e ){
    		e.printStackTrace();
    	}
    	return found;
    }

    public static void addCreatureToPlayerRack( String username, 
            HashMap<String,Object> map ){
        int uID = getUID(username);
        int gID = getGID(uID);
        try {
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            String sql = "";

            sql = "insert into playerRacks (uID, gID, pID)"
                + "values ("+uID+","+gID+","+map.get("pID")+");";
            stmnt.executeUpdate(sql);
           
            stmnt.close();
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    public static void updateCreatureForPlayer( String username,
            HashMap<String,Object> map ){
    }
    
    /**
     * Adds the details of the game to a HashMap, which may be the Body of a Message
     * @param map the HashMap to add contents to
     * @param gID the id of the game
     * @param uID the id of the user requesting game state
     */
    public static void getGameState( HashMap<String,Object> map, int gID, int uID ){
    	try {
            Class.forName("org.sqlite.JDBC");
            Connection db = DriverManager.getConnection("jdbc:sqlite:KAT.db");
            Statement stmnt = db.createStatement();
            ResultSet rs;
            String sql = "";

            // add all piece contained in cup
            sql = "select * from cups natural join pieces where gID='"+gID+"';";
            rs = stmnt.executeQuery(sql);
            ArrayList<Integer> cupData = new ArrayList<Integer>();
            while( rs.next() ){
            	cupData.add(rs.getInt("pID"));
            }
            map.put("cupData", cupData);
            rs.close();
            stmnt.close();
            
            // add details of all users in game (their names and gold)
            stmnt = db.createStatement();
            sql = "select * from games where gID='"+gID+"';";
            rs = stmnt.executeQuery(sql);
            
            
            int gameSize = rs.getInt("gameSize");
            int numPlayers = rs.getInt("numPlayers");
            map.put("gameSize", gameSize);
            map.put("numPlayers", numPlayers);
            
            int uIDs[] = new int[numPlayers];
            for( int i=0; i<numPlayers; i++ ){
            	uIDs[i] = rs.getInt("User"+(i+1));
            }
            rs.close();
            stmnt.close();
            
            for( int i=0; i<numPlayers; i++ ){
            	stmnt = db.createStatement();
            	sql = "select * from players, users where " 
            		+ "players.uID = users.uID and users.uID = "+uIDs[i]+";";
            	rs = stmnt.executeQuery(sql);
            	rs.next();
            	HashMap<String,Object> playerInfo = new HashMap<String,Object>();
            	playerInfo.put("username", rs.getString("username"));
            	playerInfo.put("color", rs.getString("color"));
            	playerInfo.put("gold", rs.getInt("gold"));
            	map.put("Player"+(i+1), playerInfo);
            	rs.close();
            	stmnt.close();
            }
            
            // now get the user's player rack
            // TODO
            
            // add game board 
            // TODO
            
            // lastly add any offside special characters
            // TODO
            
            db.close();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }
    
    public static void removeGame( int gID ){
        // something like 'remove * from games where gID = @param gID
    }
}
