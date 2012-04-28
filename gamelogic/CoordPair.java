package gamelogic;

public class CoordPair {
    
    public int _x, _y;
    
    public CoordPair(int x, int y) {
		_x = x;
		_y = y;
    }
    
    public boolean equals(Object o) {
		CoordPair v = (CoordPair) o;
		return (v.getX() == _x && v.getY() == _y);
    }
    
    public int hashCode() {
		return (int) (13*_x + 31*_y);
    }

    public int getX() { return _x; }
    public int getY() { return _y; }
    
    public String toString() {
		return "(" + _x + "," + _y + ")";
    }
}
