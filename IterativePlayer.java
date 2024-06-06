import java.util.*;
import java.io.*;

public class IterativePlayer extends Player {

    public static void main(String[] args) throws IOException, InterruptedException {
        File boardFile = new File("Boards/board7");
        File smvFile = new File(boardFile.getPath() + ".smv");
        File ouFile = new File(boardFile.getPath() + ".out");
        File errFile = new File(boardFile.getPath() + ".err");
        Board aBoard = Board.readBoard(boardFile);
        /* aBoard.printBoard(); */

        IterativePlayer p = new IterativePlayer(aBoard);
        p.writeSmv(smvFile);
    }

    public IterativePlayer(Board b) {
        super(b);
    }

    private Set<Location> getBlockingPositions() throws IOException, InterruptedException {
        NonBlockingPlayerBDD p = new NonBlockingPlayerBDD(this.getBoard().eyesOnPrize());
        return p.getBlockingPositions();
    }

    public String losingCondition() throws IOException, InterruptedException {
        StringBuilder str = new StringBuilder();
        str.append("!((");

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

        // must keep the goals that were achieved
        Iterator<Location> rGoalIter = this.getBoard().getReachedGoals().iterator();
        if (rGoalIter.hasNext()) {
            str.append("(");
            while (rGoalIter.hasNext()) {
                Location cur = rGoalIter.next();
                if (rGoalIter.hasNext()) {
                    str.append(Board.boardHas(cur, Board.boxOnGoal) + " & ");
                } else {
                    str.append(Board.boardHas(cur, Board.boxOnGoal) + " )   ");
                }
            }
        }

        Iterator<Location> nGoalIter = this.getBoard().getRemainingGoals().iterator();
        while (nGoalIter.hasNext()) {
            Location cur = nGoalIter.next();
            if (nGoalIter.hasNext()) {
                str.append(Board.boardHas(cur, Board.boxOnGoal) + " | ");
            } else {
                str.append(Board.boardHas(cur, Board.boxOnGoal) + " )); ");
            }
        }
        return str.toString();
    }
}