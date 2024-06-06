import java.util.*;
import java.io.*;

public class IterativePlayer extends Player {

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

    public IterativePlayer(Board b) {
        super(b);
    }

    private Set<Location> getBlockingPositions() throws IOException, InterruptedException {
        NonBlockingPlayerBDD p = new NonBlockingPlayerBDD(this.getBoard().eyesOnPrize());
        return p.getBlockingPositions();
    }

    public String losingCondition() throws IOException, InterruptedException {
        StringBuilder str = new StringBuilder();
        str.append("!(()");

        Iterator<Location> iter = getBlockingPositions().iterator();
        while (iter.hasNext()) {
            Location cur = iter.next();
            if (iter.hasNext()) {
                str.append(Board.boardHas(cur, Board.floor) + " | ");
            } else {
                str.append(Board.boardHas(cur, Board.floor) + " ) U ( ");
            }
        }
        iter = this.getBoard().getGoalPositions().iterator();

        while (iter.hasNext()) {
            Location cur = iter.next();
            if (iter.hasNext()) {
                str.append(Board.boardHas(cur, Board.boxOnGoal) + " | ");
            } else {
                str.append(Board.boardHas(cur, Board.boxOnGoal) + " )); ");
            }
        }
        return str.toString();
    }
}