package KAT;

public interface Movable {
	
	public void toggleAboutToMove();
	public int movesLeft();
	public boolean doneMoving();
	public void resetMoves();
	public void move(Terrain t);
	public boolean canMoveTo(Terrain from, Terrain to);
	public boolean isAboutToMove();
	
}
