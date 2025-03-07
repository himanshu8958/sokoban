public class Location implements Comparable {
    int x;
    int y;
    public static final Location above = new Location(1, 0);
    public static final Location below = new Location(-1, 0);
    public static final Location left = new Location(0, -1);
    public static final Location right = new Location(0, 1);

    public static final Location above2 = new Location(2, 0);
    public static final Location below2 = new Location(-2, 0);
    public static final Location left2 = new Location(0, -2);
    public static final Location right2 = new Location(0, 2);

    public int compareTo(Object o) {
        if (!(o instanceof Location)) {
            return -1;
        }
        Location other = (Location) o;
        if (this.x != other.x)
            return this.x - other.x;
        else if (this.y != other.y)
            return this.y - other.y;
        else
            return 0;

    }

    public int getDistance(Location l) {
        return Math.abs(this.x - l.x) + Math.abs(this.y - l.y);
    }

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Location transpose(Location a, Location b  ) {
        return new Location(a.x + b.x, a.y + b.y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Object b) {
        if(b instanceof Location){
            Location bLoc = (Location) b;
            return this.x == bLoc.x & this.y == bLoc.y;
        }
        return false;
    }

    public int hashCode() {
        return Integer.valueOf(x).hashCode() + 13 * Integer.valueOf(y).hashCode();
    }
}