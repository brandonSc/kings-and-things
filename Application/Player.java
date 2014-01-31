package KAT;

import java.util.ArrayList;

public class Player
{
    private String username;          // name used to login
    private PlayerRack playerRack;    // owned pieces not in play
    private ArrayList<Terrain> hexes; // owned hexes which contain pieces in play 
    private String avatar;            // path to avatar/icon image (eventual feature)

    public Player( String username, String avatar ){
        this.username = username;
        this.avatar = avatar;
        this.playerRack = PlayerRack.getInstance();
        this.hexes = new ArrayList<Terrain>();
    }

    public Player( String username ){
        this.username = username;
        this.playerRack = PlayerRack.getInstance();
        this.hexes = new ArrayList<Terrain>();
        this.avatar = "Images/";
    }

    public Player(){
        this.username = "User";
        this.avatar = "Images/";
        this.playerRack = PlayerRack.getInstance();
        this.hexes = new ArrayList<Terrain>();
    }

    /**
     * Adds the specified hex if it is not already
     * owned by this player
     */
    public void addHex( Terrain hex ){
        if( !hexes.contains(hex) ){
            hexes.add(hex);
        }
    }
    
    /**
     * Removes ownership of the player's hex
     */
    public void removeHex( Terrain hex ){
    	hexes.remove(hex);
    }

    /**
     * Adds a piece to the specified hex
     * @return false if there was an error adding the piece
     */
    public boolean playPiece( Piece piece, Terrain hex ){
        ArrayList<Piece> contents = hex.getContents();
        String terrainType = piece.getTerrain();

        // first add the hex if it is not already owned
        addHex(hex);
        
        // if the piece has a terrain type
        if( terrainType != "" ){
            // check if it matches the hex
            if( hex.getType() == terrainType ){
                contents.add(piece);
                return true;
            } else {
                // the piece does not match the terrain type
                for( Piece p : contents ){
                    // check for a matching terrain lord
                    if( p instanceof TerrainLord 
                    && ((TerrainLord)(p)).getTerrain() == terrainType ){
                        contents.add(piece);
                        return true;
                    }
                }
                // hex terrain does not match 
                // and does not contain an appropriate terrain lord
                return false;
            }
        } else {
            // there is no restriction on terrain type
            contents.add(piece);
            return true;
        }
    }

    /**
     * Adds and arraylist of pieces to the specified hex
     * and adds the hex to the user's list of owned hexes
     * @return false if there was an error adding a piece
     */ 
    public boolean playPieces( ArrayList<Piece> pieces, Terrain hex ){
        boolean success = true;
        
        for( Piece piece : pieces ){
            if( playPiece(piece, hex) == false ){
                success = false;
            }
        }

        return success;
    }

    public String getName(){ return this.username; }
    public String getAvatar(){ return this.avatar; }
    public PlayerRack getPlayerRack(){ return this.playerRack; }
    public ArrayList<Terrain> getHexes(){ return this.hexes; }

    public void setName( String username ){ this.username = username; }
    public void setAvatar( String avatar ){ this.avatar = avatar; }
}
