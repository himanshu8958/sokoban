import java.io.*;
import java.util.*;

public class NonBlockingPlayerBDD extends NonBlockingPlayer {
    public NonBlockingPlayerBDD(Board b) {
        super(b);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Board aBoard = Board.readBoard(new File("Boards/board7"));
        /* aBoard.printBoard(); */
        Board clearBoard = aBoard.eyesOnPrize();
        aBoard.printBoard();

        System.out.println();
        clearBoard.printBoard();

        NonBlockingPlayerBDD nbPlayer = new NonBlockingPlayerBDD(clearBoard);

        Set<Location> blockingLocations = nbPlayer.getBlockingPositions();
        nbPlayer.printBlockingPostions(blockingLocations);
        System.out.println();
        aBoard.printBoard();

    }

    public Set<Location> getBlockingPositions() throws IOException, InterruptedException {
        TreeSet<Location> blockingPositions = new TreeSet<Location>();
        Board eyesOnPrize = this.getBoard().eyesOnPrize();
        boolean change = true;
        Set<Location> pottentialBlocks = super.getBlockingPositions();
        System.out.println("================================================================");
        System.out.println("BMC could not solve : " + pottentialBlocks.size());
        while (change) {
            System.out.println("blocked " + pottentialBlocks.size());
            Set<Location> nuFloorLocations = super.getBlockingPositions();
            if (nuFloorLocations.size() == pottentialBlocks.size()) {
                change = false;

            }
            pottentialBlocks = nuFloorLocations;
        }
        Set<NonBlockingPlayer> pottentialBlockingPlayers = new TreeSet<NonBlockingPlayer>();
        for (Location loc : pottentialBlocks) {
            if (this.getBoard().getGoals().contains(loc)) {
                continue; // a goal cannot be a blocking position
            }

            if (this.getBoard().isCorner(loc)) {
                blockingPositions.add(loc);
                continue;
            } else {
                Board singleBox = eyesOnPrize.copy();
                singleBox.setCell(loc, Board.box);
                File singleBoxFile = new File(singleBox.getBoardFile().getPath() + loc);
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
            ModelChecker.checkBdd(smvFile, outFile, errFile);
            Play curPlay = Play.readTrace(p.getBoard(), outFile);
            if (!curPlay.isWin()) {
                blockingPositions.add(p.getBoxLocation());
                System.out.println("blocking position for sure!");
            } else {
                System.out.println("not a blocking position");
                this.addPositionReachesGoal(p.getBoxLocation());

            }
            System.out.println("..................................................");
        }
        return blockingPositions;
    }

    private String atLeastABoxOnGoal() {
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME atLeastOneBoxOnGoal := G!(");
        Iterator<Location> iter = this.getGoalPositions().iterator();
        while (iter.hasNext()) {
            Location curGoal = iter.next();
            if (this.getBoard().getGoals().contains(curGoal))
                str.append(Board.boardHas(curGoal, Board.boxOnGoal));
            else if (this.getBoard().getFloorLocations().contains(curGoal))
                str.append(Board.boardHas(curGoal, Board.box));
            if (iter.hasNext()) {
                str.append(" | ");
            } else {
                str.append(");");
            }
        }
        return str.toString();
    }

}