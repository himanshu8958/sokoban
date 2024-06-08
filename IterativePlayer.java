import java.util.*;
import java.io.*;

public class IterativePlayer extends Player {
    SortedSet<Location> goalsReached = new TreeSet<Location>();
    File initialBoardFile = null;

    public File getInitialBoardFile() {
        return this.initialBoardFile;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File boardFile = new File(args[0]);
        File smvFile = new File(boardFile.getPath() + ".smv");
        File ouFile = new File(boardFile.getPath() + ".out");
        File errFile = new File(boardFile.getPath() + ".err");
        Board aBoard = Board.readBoard(boardFile);
        /* aBoard.printBoard(); */

        IterativePlayer p = new IterativePlayer(aBoard, boardFile);
        aBoard.printBoard();
        p.writeSmv(smvFile);
        ModelChecker.checkBdd(smvFile, ouFile, errFile);
        Play thisPlay = Play.readTrace(aBoard, ouFile);
        System.out.println("The  winning state");
        thisPlay.getWinnState().getBoard().printBoard();
    }

    public IterativePlayer(Board b, File initBoardFile) {
        super(b);
        this.initialBoardFile = initBoardFile;
    }

    public static File getFileMentioningReached(Board board, File boardFile) {
        StringBuilder str = new StringBuilder();
        str.append(boardFile.getPath());
        for (Location loc : board.getReachedGoals()) {
            str.append(loc.toString());
        }
        return new File(str.toString());
    }

    public static Board oneIteration(Board curBoard, File initBoardFile) throws IOException, InterruptedException {
        File smvFile = new File(IterativePlayer.getFileMentioningReached(curBoard, initBoardFile).getPath() + ".smv");
        File outFile = new File(IterativePlayer.getFileMentioningReached(curBoard, initBoardFile).getPath() + ".out");
        File errFile = new File(IterativePlayer.getFileMentioningReached(curBoard, initBoardFile).getPath() + ".err");

        IterativePlayer plyr = new IterativePlayer(curBoard, initBoardFile);
        ModelChecker.checkBdd(smvFile, outFile, errFile);
        Play thisPlay = Play.readTrace(curBoard, outFile);
        return thisPlay.getWinnState().getBoard();
    }

    private Set<Location> getBlockingPositions() throws IOException, InterruptedException {
        NonBlockingPlayerBDD p = new NonBlockingPlayerBDD(this.getBoard().eyesOnPrize());
        return p.getBlockingPositions();
    }

    public String losingCondition() throws IOException, InterruptedException {
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME oneMoreGoal := !((");
        if (this.getBlockingPositions().size() != 0) {
            Iterator<Location> iter = getBlockingPositions().iterator();
            // do not go to the blocking positions
            while (iter.hasNext()) {
                Location cur = iter.next();
                if (iter.hasNext()) {
                    str.append(Board.boardHas(cur, Board.floor) + " | ");
                } else {
                    str.append(Board.boardHas(cur, Board.floor) + " ) U ( ");
                }
            }
        } else {
            str.append(" TRUE ) U ( ");
        }

        // must keep the goals that were achieved
        if (this.getBoard().getReachedGoals().size() != 0) {
            Iterator<Location> rGoalIter = this.getBoard().getReachedGoals().iterator();
            str.append("(");
            if (rGoalIter.hasNext()) {

                while (rGoalIter.hasNext()) {
                    Location cur = rGoalIter.next();
                    if (rGoalIter.hasNext()) {
                        str.append(Board.boardHas(cur, Board.boxOnGoal) + " & ");
                    } else {
                        str.append(Board.boardHas(cur, Board.boxOnGoal) + " ) & ( ");
                    }
                }
            }
        } else {
            str.append("TRUE & (");
        }

        if (this.getBoard().getRemainingGoals().size() != 0) {
            Iterator<Location> nGoalIter = this.getBoard().getRemainingGoals().iterator();
            System.out.println("remaining golas : " + this.getBoard().getRemainingGoals().size());

            while (nGoalIter.hasNext()) {
                Location cur = nGoalIter.next();
                if (nGoalIter.hasNext()) {
                    str.append(Board.boardHas(cur, Board.boxOnGoal) + " | ");
                } else {
                    str.append(Board.boardHas(cur, Board.boxOnGoal) + " ))); ");
                }
            }
            return str.toString();
        } else {
            str.append("TRUE ));");
        }
        return str.toString();
    }
}