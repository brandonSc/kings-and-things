package KAT;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player
{
    private String username;          // name used to login
    private PlayerRack playerRack;    // owned pieces not in play
    private ArrayList<Terrain> hexes; // owned hexes which contain pieces in play
    private String controlMarker;     // path to control marker image
    private int gold;                 // player's total earned gold
    private Color color;
    private Image marker;
    private static Image yellowMarker, redMarker, blueMarker, greenMarker;

    public Player( String username, String color ){
        this.username = username;
        this.playerRack = new PlayerRack();
        this.hexes = new ArrayList<Terrain>();
        this.setColor(color);
        this.gold = 0; // perhaps set to 10 ?
    }

    public Player( String color ){
        this.username = "User";
        this.playerRack = new PlayerRack();
        this.hexes = new ArrayList<Terrain>();
        this.setColor(color);
        this.gold = 0;
    }

    /**
     * Adds the specified hex if it is not already
     * owned by this player
     */
    public void addHex( Terrain hex ){
        if( !hexes.contains(hex) ){
            hexes.add(hex);
            hex.setOccupied(username);
            hex.setOwner(this);
        }
    }
    
    /**
     * Removes ownership of the player's hex
     */
    public void removeHex( Terrain hex ){
    	hexes.remove(hex);
        hex.removeControl(username);
        hex.setOwner(null);
    }

    /**
     * Either constructs a tower on a terrain hex 
     * or upgrades an already existing fort
     */
    public void constructFort( Terrain hex ){
        ArrayList<Piece> contents = hex.getContents(username);

        for( Piece p : contents ){
            if( p instanceof Fort ){
                ((Fort)p).upgrade();
                return;
            }
        }
        contents.add(new Fort());
    }

    /**
     * Adds a piece to the specified hex
     * @return false if there was an error adding the piece
     */
    public boolean playPiece( Piece piece, Terrain hex ){
        ArrayList<Piece> contents = hex.getContents(username);
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
        /*
        contents.add(piece);
        return true;
        */
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

    /*
     * Calculates the income of the player.
     * --------------------------------------------------
     * 1 gold per each controlled hex
     * 1 gold per combat value of each controlled fort
     * Value of the Special income counters ON THE BOARD
     * 1 gold per controlled special character
     * --------------------------------------------------
     */
    public int calculateIncome() {
        int income = 0;

        income += getHexes().size(); 
        
        for( Terrain hex : hexes ){
            for( Piece p : hex.getContents(username) ){
                if( p instanceof Fort ){
                    income += ((Fort)(p)).getCombatValue();
                } else if( p instanceof SpecialCharacter ){
                    income += 1;
                }
                // else if( p instanceof SpecialIncomeCounter ){
                // // TODO implement Special Income Counters, add income accordingly
                // }
            }
        }

        return income;
    }

    /*
     * Gets and sets
     */
    public String getName(){ return this.username; }
    public PlayerRack getPlayerRack(){ return this.playerRack; }
    public ArrayList<Terrain> getHexes(){ return this.hexes; }
    public Color getColor() { return this.color; }

    public void setName( String username ){ this.username = username; }
    public void addGold( int amount ){ this.gold += amount; }
    public int getGold(){ return this.gold; }
    public void removeGold(int amount) { this.gold -= amount; }
    public Image getImage() { return marker; }
    
    /**
     * Removes gold from player's income
     * @return same amount specified if there is enough, else -1
     */
    public int spendGold( int amount ){ 
        if( amount <= gold ){
            this.gold -= amount;
            return amount;
        } else {
            return -1;
        }
    }
    
    public void setColor( String color ){
        switch( color ){
            case "BLUE": 
                marker = blueMarker;
                this.color = Color.BLUE;
                break;
            case "GREEN":
            	marker = greenMarker;
                this.color = Color.GREEN;
                break;
            case "RED":
            	marker = redMarker;
                this.color = Color.RED;
                break;
            case "YELLOW":
            	marker = yellowMarker;
                this.color = Color.YELLOW;
                break;
        }
    }
    
    public static void setClassImages () {
    	yellowMarker = new Image("Images/Control_Yellow.png");
    	greenMarker = new Image("Images/Control_Green.png");
    	blueMarker = new Image("Images/Control_Blue.png");
    	redMarker = new Image("Images/Control_Red.png");
    }
    
}

