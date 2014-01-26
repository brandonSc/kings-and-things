package KAT;

import java.util.ArrayList;

public class TileDeck {

	private ArrayList<Terrain> deck;
	
	public TileDeck() {
		
		
		//Creates arraylist with every tile piece minus the 4 sea tiles set aside
		deck = new ArrayList<Terrain>();
		for (int i = 0; i < 7; i++)
			deck.add(new Terrain("Desert"));
		for (int i = 0; i < 7; i++)
			deck.add(new Terrain("Forest"));
		for (int i = 0; i < 6; i++)
			deck.add(new Terrain("FrozenWaste"));
		for (int i = 0; i < 6; i++)
			deck.add(new Terrain("Jungle"));
		for (int i = 0; i < 7; i++)
			deck.add(new Terrain("Mountains"));
		for (int i = 0; i < 7; i++)
			deck.add(new Terrain("Plains"));
		for (int i = 0; i < 5; i++)
			deck.add(new Terrain("Sea"));
		for (int i = 0; i < 7; i++)
			deck.add(new Terrain("Swamp"));
		
	}
	
	public Terrain getRandomTile() {
		
		int r = (int) (Math.random() * deck.size());
		return deck.remove(r);
	}
	
	
}
