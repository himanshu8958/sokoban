import java.util.*;
import java.io.*;

public class IterativePlayer extends Player {
    SortedSet<Location> goalsReached = new TreeSet<Location>();
    File initialBoardFile = null;

    public File getInitialBoardFile() {
        return this.initialBoardFile;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File inputBoardFile = new File(args[0]);
        Board input = Board.readBoard(inputBoardFile);
        Board result = input;
        int ctr = 1;
        Board perviousResult = input;
        do {
            perviousResult = result;
            result = IterativePlayer.oneIteration(result, inputBoardFile);
            System.out.println("after Iteration : " + ctr++ + ": ");
            result.printBoard();

        } while (!result.equals(perviousResult) & result.getRemainingGoals().size() > 0);
        if (result.getRemainingGoals().size() > 0) {
            System.out.println("All goals cnnot reach goals, pointless to move ahead");
        } else {

            System.out.println("all goals covered");
            System.out.println("reached goals : " + result.getReachedGoals().size());
        }

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
        plyr.writeSmv(smvFile);
        ModelChecker.checkBdd(smvFile, outFile, errFile);
        Play thisPlay = Play.readTrace(curBoard, outFile);
        return thisPlay.getWinnState().getBoard();
    }

    private static Set<Location> blockingPositions = null;

    private void resetBlockingPositions() {
        IterativePlayer.blockingPositions = null;
        IterativePlayer.ctr = 0;
    }

    public int getBlockingPositionRecalcFrequency() {
        return 1;
    }

    public static int ctr = 0;

    private Set<Location> getBlockingPositions() throws IOException, InterruptedException {

        if (ctr % (2 * this.getBlockingPositionRecalcFrequency()) == 0) {
            this.resetBlockingPositions();
        }
        if (IterativePlayer.blockingPositions == null) {
            NonBlockingPlayerBDD p = new NonBlockingPlayerBDD(this.getBoard().eyesOnPrize());
            IterativePlayer.blockingPositions = p.getBlockingPositions();
            if (ctr == 0) {
                /*
                 * System.out.println("Current Blocking Positions");
                 * p.printBlockingPostions(IterativePlayer.blockingPositions);
                 */
            }
        }
        ctr++;
        return IterativePlayer.blockingPositions;
    }

    public String losingCondition() throws IOException, InterruptedException {
        return this.way2();
    }

    public String way2() throws IOException, InterruptedException {
        StringBuilder str = new StringBuilder();
        str.append("--Iterative Player; way 2\n");
        str.append("-- Blocking Positions : ");
        Set<Location> blockingPos = this.getBlockingPositions();
        for (Location l : blockingPos) {
            str.append(l);
            str.append(", ");
        }
        str.append('\n');
        str.append("-- Reached Goals : ");
        for (Location l : this.getBoard().getReachedGoals()) {
            str.append(l);
            str.append(", ");
        }
        str.append('\n');
        str.append("-- Remaining Goals : ");
        for (Location l : this.getBoard().getRemainingGoals()) {
            str.append(l);
            str.append(", ");
        }
        str.append('\n');
        str.append("LTLSPEC NAME oneMoreGoal := !(");
        if (blockingPos.size() > 0) {
            str.append("G{");
            Iterator<Location> iter = getBlockingPositions().iterator();
            // do not go to the blocking positions
            while (iter.hasNext()) {
                Location cur = iter.next();
                str.append("(");
                str.append(Board.boardHas(cur, Board.floor) + " | ");
                str.append(Board.boardHas(cur, Board.keeper) + " )");
                if (iter.hasNext()) {
                    str.append(" & ");
                } else {
                    str.append("}");
                }
            }
            str.append(" & F(");
        } else {
            str.append("F(");
        }

        // must keep the goals that were achieved
        if (this.getBoard().getReachedGoals().size() > 0) {
            Iterator<Location> rGoalIter = this.getBoard().getReachedGoals().iterator();
            if (rGoalIter.hasNext()) {
                str.append("(");
                while (rGoalIter.hasNext()) {
                    Location cur = rGoalIter.next();
                    if (rGoalIter.hasNext()) {
                        str.append(Board.boardHas(cur, Board.boxOnGoal) + " & ");
                    } else {
                        str.append(Board.boardHas(cur, Board.boxOnGoal) + " ) & ");
                    }
                }
                /* str.append(")"); */
            }
        }

        if (this.getBoard().getRemainingGoals().size() > 0) {
            Iterator<Location> nGoalIter = this.getBoard().getRemainingGoals().iterator();
            System.out.println("remaining goals : " + this.getBoard().getRemainingGoals().size());

            while (nGoalIter.hasNext()) {
                Location cur = nGoalIter.next();
                if (nGoalIter.hasNext()) {
                    str.append(Board.boardHas(cur, Board.boxOnGoal) + " | ");
                } else {
                    str.append(Board.boardHas(cur, Board.boxOnGoal) + " ));");
                }
            }
        }
        return str.toString();
    }

    public String way1() throws IOException, InterruptedException {
        StringBuilder str = new StringBuilder();
        str.append("--Iterative Player\n");
        str.append("-- Blocking Positions : ");
        for (Location l : this.getBlockingPositions()) {
            str.append(l);
        }
        str.append('\n');
        str.append("-- Reached Goals : ");
        for (Location l : this.getBoard().getReachedGoals()) {
            str.append(l);
        }
        str.append('\n');
        str.append("-- Remaining Goals : ");
        for (Location l : this.getBoard().getRemainingGoals()) {
            str.append(l);
        }
        str.append('\n');
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