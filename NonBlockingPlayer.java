import java.util.*;

public class NonBlockingPlayer extends Player {
    String name;
    public static void main(String[] args) {
        System.out.println("hello");
    }

    public NonBlockingPlayer(Board b, String name) {
        super(b);
        this.name = name;
    }

    public Set<Location> getBlockingPositions() {
        HashSet<Location> ret = new HashSet<Location>();
        Board eyesOnPrize = this.getBoard().eyesOnPrize();
        Set<Location> floorLocations = eyesOnPrize.getFloorLocations();
        for (Location loc : floorLocations) {
            Board singleBox = eyesOnPrize.copy();
            singleBox.setCell(loc, Board.box);
            NonBlockingPlayer oneBoxPlay = new NonBlockingPlayer(singleBox, this.name + loc);

        }
        return ret;
    }

    public String losingCondition() {
        return atLeastABoxOnGoal();
    }

    private String atLeastABoxOnGoal() {
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME atLeastOneBoxOnGoal := G!()");
        /* for (Location loc : getBoard().getGoalPositions()) */
        Iterator<Location> iter = getBoard().getGoalPositions().iterator();
        while (iter.hasNext()) {
            Location curGoal = iter.next();
            str.append(Board.boardHas(curGoal, Board.boxOnGoal));
            if (iter.hasNext()) {
                str.append(" | ");
            } else {
                str.append(");");
            }
        }
        return str.toString();
    }
}