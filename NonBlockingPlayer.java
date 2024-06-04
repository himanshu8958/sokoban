import java.util.*;
import java.io.*;

public class NonBlockingPlayer extends Player {
    Location boxLocation;

    public static void main(String[] args) throws IOException, InterruptedException {
        Board aBoard = Board.readBoard(new File("Boards/board3"));
        /* aBoard.printBoard(); */
        Board clearBoard = aBoard.eyesOnPrize();
        System.out.println();
        /* clearBoard.printBoard(); */

        NonBlockingPlayer nbPlayer = new NonBlockingPlayer(aBoard);

        Set<Location> blockigLocations = nbPlayer.getBlockingPositions();
        nbPlayer.printBlockingPostions(blockigLocations);
        System.out.println();
        aBoard.printBoard();
    }

    public void printBlockingPostions(Set<Location> bPos) {
        for (int r = this.getBoard().rows - 1; r >= 0; r--) {
            for (int c = 0; c < this.getBoard().cols; c++) {
                Location cur = new Location(r, c);
                if (bPos.contains(cur)) {
                    System.out.print(1);
                } else {
                    if (this.getBoard().getCell(cur).equals(Board.wall)) {
                        System.out.print("#");
                    } else {
                        System.out.print(0);
                    }
                }
            }
            System.out.println();

        }
    }

    public Location getBoxLocation() {
        return this.boxLocation;
    }

    public NonBlockingPlayer(Board b) {
        super(b);
    }

    public NonBlockingPlayer(Board b, Location boxLocation) {
        super(b);
        this.boxLocation = boxLocation;
    }

    public Set<Location> getBlockingPositions() throws IOException, InterruptedException {
        HashSet<Location> blockingPositions = new HashSet<Location>();
        Board eyesOnPrize = this.getBoard().eyesOnPrize();
        Set<Location> floorLocations = eyesOnPrize.getFloorLocations();
        Set<NonBlockingPlayer> pottentialBlockingPlayers = new HashSet<NonBlockingPlayer>();
        for (Location loc : floorLocations) {
            if (this.getBoard().getGoalPositions().contains(loc)) {
                continue; // a goal cannot be a blocking position
            }

            if (this.getBoard().isCorner(loc)) {
                blockingPositions.add(loc);
            } else {
                Board singleBox = eyesOnPrize.copy();
                singleBox.setCell(loc, Board.box);
                File singleBoxFile = new File(singleBox.getBoardFile().getPath() + File.pathSeparator + loc);
                singleBox.setBoardFile(singleBoxFile);
                NonBlockingPlayer oneBoxPlay = new NonBlockingPlayer(singleBox, loc);
                pottentialBlockingPlayers.add(oneBoxPlay);
            }
        }

        for (NonBlockingPlayer p : pottentialBlockingPlayers) {
            File smvFile = new File(p.getBoard().getBoardFile().getPath() + ".smv");
            File outFile = new File(p.getBoard().getBoardFile().getPath() + ".out");
            File errFile = new File(p.getBoard().getBoardFile().getPath() + ".err");
            System.out.println(smvFile.getPath());
            p.getBoard().printBoard();
            System.out.println();

            p.writeSmv(smvFile);
            ModelChecker.checkInteractive(smvFile, p.getBound(), outFile, errFile);
            Play curPlay = Play.readTrace(p.getBoard(), outFile);
            if (!curPlay.isWin()) {
                blockingPositions.add(p.getBoxLocation());
                System.out.println("blocking position");
            }
            else {
                System.out.println("not a blocking position");
            }
            System.out.println("..................................................");
        }
        return blockingPositions;
    }


    // This method will decide the aggresiveness of the starategy, how far will yo
    // go to find all the blocking positions?
    public int getBound() {
        return (int) ((this.getBoard().rows + this.getBoard().cols));
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