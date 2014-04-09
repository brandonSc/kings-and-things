package KAT;

import java.util.Iterator;
import java.util.HashMap;

public class BigJuju extends RandomEvent {

	public BigJuju() {
		super("Images/Event_BigJulu.png", "Images/Creature_Back.png", "Big Juju");
		setOwner(null);
		setDescription("Big JuJu can change one hex on the board to something completely different!\nView the Leaflet for the in-depth rules.");
	}

	/*
	 * Procedure to determine whether BigJuJu is playable during the random event phase.
	 *
	 * If the Owner has no magic creatures on the field, Big JuJu is not playable.
	 */
	@Override
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() != 4)
			return false;

		for (Terrain t : getOwner().getHexesWithPiece()) {
			CreatureStack c = t.getContents(getOwner().getName());
			for (int i = 0; i < c.getStack().size(); i++) {
				if (c.getStack().get(i).getType().equals("Creature")) {	
					if (((Creature)c.getStack().get(i)).isMagic())
						return true;
				}
			}
		}
		return false;
	}

	/*
	 * Check if the hex you wish to change is within the range of one of your magic creatures, 
	 * i.e. within a distance smaller than your magic creature's combat value.
	 *
	 * If the hex you wish to change is your own, you may look through the tile deck and pick any tile you wish.
	 * If the hex you wish to change is not your own, shuffle the tile deck and pick the top tile. If the drawn tile is the same type as the one you are 
	 * trying to change, draw again until you draw a hex that is a different type.
	 *
	 * Terrain-dependent special incomes are lost when the hex changes (they get returned to the cup).
	 *
	 * The owner of the switched hex can place unused counters from his rack in the hex.
	 *
	 * If the replacement hex is a sea hex, all creatures in the hex can move to an adjacent friendly hex.If none exists, the creatures/other pieces are returned to
	 * the cup.
	 *
	 * Big JuJu cannot be played on a hex with a Citadel.
	 */
	@Override
	public void performAbility() {
		Creature c;
		int comVal = 0;
		Terrain magicTerrain = null;
		Iterator<Coord> keySetIterator = Board.getTerrains().keySet().iterator();
		for (Terrain t : getOwner().getHexesWithPiece()) {
			CreatureStack cs = t.getContents(getOwner().getName());
			for (int i = 0; i < cs.filterCreatures(cs.getStack()).size(); i++) {	
				if (cs.filterCreatures(cs.getStack()).get(i).isMagic()) {
					c = cs.filterCreatures(cs.getStack()).get(i);
					magicTerrain = t;
					comVal = c.getCombatValue();
					break;
				}
			}
		}
		
		while (keySetIterator.hasNext()) {
			Coord key = keySetIterator.next();
			if (Board.getTerrains().get(key).compareTo(magicTerrain.getCoords()) > comVal)
				Board.getTerrains().get(key).cover();
		}

		Terrain clicked = ClickObserver.getInstance().getClickedTerrain();
		Coord clickedCoord = clicked.getCoords();

		Terrain newTerrain = TileDeck.getInstance().getTopTile();
		System.out.println(newTerrain);
		System.out.println(clicked);
		newTerrain.setCoords(clickedCoord);
		Board.getTerrains().remove(clickedCoord);
		Board.getTerrains().put(clickedCoord, newTerrain);
		System.out.println(newTerrain);

		GameLoop.getInstance().unPause();
	}
}