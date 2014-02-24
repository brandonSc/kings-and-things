package KAT;

public class Coord implements Comparable<Coord> {
	
	private int x, y, z;
	
	public Coord (int x, int y, int z) {
		this.x = x;
		this.y = y; 
		this.z = z;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getZ() { return z; }
	
	@Override
	public int compareTo(Coord c) {
		int diffX = this.x - c.getX();
		int diffY = this.y - c.getY();
		int diffZ = this.z - c.getZ();
		int distance = (int)Math.sqrt(Math.pow(diffX,2)+Math.pow(diffY,2)+Math.pow(diffZ,2));
		return distance;
	}
	
	public boolean isEqual(Coord c) {
		if (x == c.getX() && y == c.getY() && z == c.getZ())
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		return "x: " + x + "\ny: " + y + "\nz:" + z;
	}
}
