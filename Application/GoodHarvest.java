package KAT;

public class GoodHarvest extends RandomEvent {

	public GoodHarvest() {
		super("Images/Event_GoodHarvest.png", "Images/Creature_Back.png", "Good Harvest");
		setOwner(null);
		setDescription("You may collect gold as if this were the Gold Collection Phase!\nSee the Leaflet for the in-depth rules.");
	}

	/*
	 * The owner of this event can collect gold as if this were the gold collection phase.
	 * Do not collect gold for special income counters (including cities and villages).
	 */
	public void performAbility() {
		int income = 0;

        income += getOwner().getHexesOwned().size(); 
        
        for( Terrain hex : getOwner().getHexesWithPiece() ){
        	if (hex.getContents(getOwner().getName()) != null) {
	            for( Piece p : hex.getContents(getOwner().getName()).getStack() ){
	                if( p instanceof Fort ){
	                    income += ((Fort)(p)).getCombatValue();
	                } else if( p instanceof SpecialCharacter ){
	                    income += 1;
	                }
	            }
        	}
        }
        getOwner().addGold(income);
	}
}