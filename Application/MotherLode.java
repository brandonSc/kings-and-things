package KAT;

public class MotherLode extends RandomEvent {

	public MotherLode() {
		super("Images/Event_MotherLode.png", "Images/Creature_Back.png", "Mother Lode");
		setOwner(null);
		setDescription("You can collect LOTS of gold!\nSee the Leaflet for the in-depth rules.");
	}

	/*
	 * The owner collects gold equal to 2 * income from special income counters
	 * (this includes cities and villages).
	 *
	 * If the player owns the Dwarf King, the value of mines are quadrupled.
	 */
	public void performAbility() {
		int income = 0;
        
        for( Terrain hex : getOwner().getHexesWithPiece() ){
        	if (hex.getContents(getOwner().getName()) != null) {
	            for( Piece p : hex.getContents(getOwner().getName()).getStack() ){
	                if( p instanceof SpecialIncome ){
	                    income += (((SpecialIncome)(p)).getValue() * 2);
	                }
	            }
        	}
        }
        getOwner().addGold(income);
	}
}