package KAT;

public class MotherLode extends RandomEvent {
	private Player owner;

	public MotherLode() {
		super("Images/Event_MotherLode.png", "Images/Creature_Back.png", "Mother Lode");
		owner = null;
		setDescription("You can collect LOTS of gold! See the Leaflet for the in-depth rules.");
	}

	public void setOwner(Player p) { owner = p; }
	public Player getOwner() { return owner; }


	/*
	 * The owner collects gold equal to 2 * income from special income counters
	 * (this includes cities and villages).
	 *
	 * If the player owns the Dwarf King, the value of mines are quadrupled.
	 */
	public void performAbility() {
		int income = 0;
        
        for( Terrain hex : owner.getHexesWithPiece() ){
        	if (hex.getContents(owner.getName()) != null) {
	            for( Piece p : hex.getContents(owner.getName()).getStack() ){
	                if( p instanceof SpecialIncome ){
	                    income += (((SpecialIncome)(p)).getValue() * 2);
	                }
	            }
        	}
        }
        owner.addGold(income);
	}
}