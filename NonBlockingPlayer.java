import java.util.*;
import java.io.*;

public class NonBlockingPlayer extends Player {
    String name;
    Location boxLocation;
    public static void main(String[] args) {
        System.out.println("hello");
    }

    public String getName() {
        return name;
    }

    public Location getBoxLocation() {
        return this.boxLocation;
    }

    public NonBlockingPlayer(Board b, String name, Location boxLocation) {
        super(b);
        this.name = name;
        this.boxLocation = boxLocation;
    }

    public Set<Location> getBlockingPositions() throws IOException, InterruptedException {
        HashSet<Location> blockingPositions = new HashSet<Location>();
        Board eyesOnPrize = this.getBoard().eyesOnPrize();
        Set<Location> floorLocations = eyesOnPrize.getFloorLocations();
        Set<NonBlockingPlayer> blockCandidatePlayer = new HashSet<NonBlockingPlayer>();
        for (Location loc : floorLocations) {
            if (this.getBoard().isCorner(loc)) {
                blockingPositions.add(loc);
            } else {
                Board singleBox = eyesOnPrize.copy();
                singleBox.setCell(loc, Board.box);
                NonBlockingPlayer oneBoxPlay = new NonBlockingPlayer(singleBox, this.name + loc, loc);
                blockCandidatePlayer.add(oneBoxPlay);
            }
        }

        for (NonBlockingPlayer p : blockCandidatePlayer) {
            File smvFile = new File(p.getName() + ".smv");
            File outFile = new File(p.getName() + ".out");
            p.writeSmv(smvFile);
            ModelChecker.checkInteractive(smvFile, p.getBound(), outFile);
            Play curPlay = Play.readTrace(p.getBoard(), outFile);
            if (!curPlay.isWin()) {
                blockingPositions.add(p.getBoxLocation());
            }
        }
        return blockingPositions;
    }

    // This method will decide the aggresiveness of the starategy, how far will yo
    // go to find all the blocking positions?
    public int getBound() {
        return (int) ((this.getBoard().rows + this.getBoard().cols) * 3);
    }

    public String losingCondition() {
        return atLeastABoxOnGoal();
    }

    private String atLeastABoxOnGoal() {
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME atLeastOneBoxOnGoal := G!(");
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