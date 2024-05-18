public class Location{
    int x;
    int y;
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Location transpose(Location a, Location b  ) {
        return new Location(a.x + b.x, a.y + b.y);
    }
}