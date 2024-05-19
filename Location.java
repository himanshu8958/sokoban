public class Location{
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
    
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Location transpose(Location a, Location b  ) {
        return new Location(a.x + b.x, a.y + b.y);
    }
}