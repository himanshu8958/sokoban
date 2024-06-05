import java.util.*;
import java.io.*;

public class IterativePlayer extends NonBlockingPlayerBDD {

    public static void main(String[] args) {

    }

    public IterativePlayer(Board b) {
        super(b);
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