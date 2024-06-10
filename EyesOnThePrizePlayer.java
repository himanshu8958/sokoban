import java.io.*;

public class EyesOnThePrizePlayer extends Player {
    Board b = null;

    public EyesOnThePrizePlayer(Board b) {
        super(b);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String curBoard = args[0];
        File boardFile = new File(curBoard);
        Board board = Board.readBoard(boardFile);
        File smvFile = new File(curBoard + ".smv");
        Player p = new Player(board);
        FileWriter out = new FileWriter(smvFile);
        /* System.out.println(p.smvWriter()); */
        out.write(p.smvCreator());
        out.close();
    }

    public String smvCreator() throws IOException, InterruptedException {
        StringBuilder out = new StringBuilder();
        out.append("MODULE main\n\n");
        out.append("DEFINE\n");
        out.append("rows := " + b.rows + ";\n");
        out.append("cols := " + b.cols + ";\n");
        out.append("goalsCovered := " + b.winningCondition() + "\n");
        out.append("VAR\n");
        /*
         * out.append("row : 0.."+ (b.rows - 1) +";\n");
         * out.append("col : 0.."+ (b.cols - 1) +";\n");
         */
        out.append("row : 0.. (rows - 1);\n");
        out.append("col : 0.. (cols - 1);\n");
        out.append("won : boolean;\n");
        out.append("direction : {l, r, u, d};\n");
        out.append(
                "board:array 0 .. (rows - 1) of array 0 .. (cols - 1) of {\"#\", \"+\", \"$\", \"*\", \"@\", \".\", \"_\"};\n");
        out.append("DEFINE\n");
        out.append("boxRight := ((col + 1) < cols) & (board[row][col + 1] = \"$\" | board[row][col + 1] = \"*\");\n");
        out.append("boxLeft  := ((col - 1) > -1)   & (board[row][col - 1] = \"$\" | board[row][col - 1] = \"*\");\n");
        out.append("boxDown  := ((row - 1) > -1)   & (board[row - 1][col] = \"$\" | board[row - 1][col] = \"*\");\n");
        out.append("boxUp    := ((row + 1) < rows) & (board[row + 1][col] = \"$\" | board[row + 1][col] = \"*\");\n\n");
        out.append("up := (row + 1 < rows &\n" +
                "\t(board[row + 1][ col] = \"_\" | board[row + 1 ][col]  = \".\" | \n" +
                "\t(boxUp & row + 2 < rows & (board[row + 2][col] = \"_\" | board[row + 2][col] = \".\"))));\n");
        out.append("down := (row - 1 >= 0 &\n" +
                "\t(board[row - 1][ col] = \"_\" | board[row - 1 ][col]  = \".\" |\n" +
                "\t(boxDown & row - 2 >= 0 & (board[row - 2][col] = \"_\" | board[row - 2][col] = \".\"))));\n");
        out.append("right := (col + 1 < cols &\n" +
                "\t(board[row][col + 1] = \"_\" | board[row][col + 1]  = \".\" | \n" +
                "\t(boxRight & col + 2 < cols & (board[row][col + 2] = \"_\" | board[row][col + 2] = \".\"))));\n");
        out.append("left := (col - 1 >= 0 &\n" +
                "\t(board[row][col - 1] = \"_\" | board[row][col - 1]  = \".\" | \n" +
                "\t(boxLeft & col - 2 >= 0 & (board[row][col - 2] = \"_\" | board[row][col - 2] = \".\"))));\n");

        out.append("ASSIGN\n");
        for (int r = b.rows - 1; r >= 0; r--) {
            for (int c = 0; c < b.cols; c++) {
                out.append("init(board[" + r + "][" + c + "]) :=\"" + b.cell[r][c] + "\";");
            }
            out.append("\n");
        }
        Location keeper = this.b.getKeeperLocation();
        out.append("\ninit(row) := " + keeper.x + ";");
        out.append("\ninit(col) := " + keeper.y + ";");
        out.append("\ninit(won) := FALSE;");

        out.append("\nnext(won) := \n" +
                "case\n " +
                "\tgoalsCovered & !won    : TRUE;\n" +
                "\tTRUE                   : won;\n" +
                "esac;\n");
        out.append("\nnext(direction) :=\n" +
                "case\n" +

                "\tleft & right & up & down		: {l, r, u, d};\n\n" +

                "\tleft & right & up & !down               : {l, r, u};\n" +
                "\tleft & right & !up & down 		: {l, r, d};\n" +
                "\tleft & !right & up & down 		: {l, u, d};\n" +
                "\t!left & right & up & down 		: {u, r, d};\n\n" +

                "\t!left & !right & up & down		: {u, d};\n" +
                "\t!left & right & !up & down		: {r, d};\n" +
                "\t!left & right & up & !down		: {r, u};\n" +
                "\tleft & !right & !up & down		: {l, d};\n" +
                "\tleft & !right & up & !down		: {l, u};\n" +
                "\tleft & right & !up & !down		: {l, r};\n\n" +

                "\tleft & !right & !up & !down		: {l};\n" +
                "\t!left & right & !up & !down		: {r};\n" +
                "\t!left & !right & up & !down		: {u};\n" +
                "\t!left & !right & !up & down		: {d};\n" +
                /* should be unreachable case */
                "\t--should be unreachable\n" +
                "\tTRUE                    : {l, r, u, d};\n" +
                "esac;\n");

        out.append("next (col) :=\n" +
                "case\n" +
                "\tnext(direction) = l & left : col - 1;\n" +
                "\tnext(direction) = r & right : col + 1;\n" +
                "\tTRUE : col;\n" +
                "esac;\n" +

                "next (row) :=\n" +
                "case" +
                "\tnext(direction) = d & down : row - 1;\n" +
                "\tnext(direction) = u & up : row + 1;\n" +
                "\tTRUE : row; \n" +
                "esac;\n");

        for (int r = 0; r < b.rows; r++) {
            for (int c = 0; c < b.cols; c++) {
                Location curLocation = new Location(r, c);

                out.append("\nnext (" + Board.getBoard(curLocation) + ") :=\n");
                out.append("case\n");
                out.append("\t" + Board.boardHas(curLocation, Board.wall) + " : " + Board.wall + ";\n");

                /* Keeper leaving this cell */
                out.append(tab + "-- Keeper leaving this cell\n");
                out.append(and(tab + Board.getLocation(curLocation),
                        Board.boardHas(curLocation, Board.keeper) + " : " + Board.floor));
                out.append(and(tab + Board.getLocation(curLocation),
                        Board.boardHas(curLocation, Board.keeperOnGoal) + " : " + Board.goal));

                /* Welcome keeper */
                out.append(tab + "-- Welcome Keeper\n");
                /* keeper moves down, Thus he must be above */
                out.append(incomingKeeper(Board.down, Board.downFeasible,
                        Location.transpose(curLocation, Location.above), curLocation));
                out.append(incomingKeeper(Board.up, Board.upFeasible, Location.transpose(curLocation, Location.below),
                        curLocation));
                out.append(incomingKeeper(Board.left, Board.leftFeasible,
                        Location.transpose(curLocation, Location.right), curLocation));
                out.append(incomingKeeper(Board.right, Board.rightFeasible,
                        Location.transpose(curLocation, Location.left), curLocation));

                /* Incoming box */
                out.append(tab + "-- Incoming box\n");
                out.append(incomingBox(Board.downFeasible, Board.down, Location.transpose(curLocation, Location.above),
                        Location.transpose(curLocation, Location.above2), curLocation));
                out.append(incomingBox(Board.upFeasible, Board.up, Location.transpose(curLocation, Location.below),
                        Location.transpose(curLocation, Location.below2), curLocation));
                out.append(incomingBox(Board.leftFeasible, Board.left, Location.transpose(curLocation, Location.right),
                        Location.transpose(curLocation, Location.right2), curLocation));
                out.append(incomingBox(Board.rightFeasible, Board.right, Location.transpose(curLocation, Location.left),
                        Location.transpose(curLocation, Location.left2), curLocation));

                /* Outgoing box */
                out.append(tab + "-- Outgoing box\n");
                out.append(outgoingBox(Board.downFeasible, Board.down, curLocation,
                        Location.transpose(curLocation, Location.above)));
                out.append(outgoingBox(Board.upFeasible, Board.up, curLocation,
                        Location.transpose(curLocation, Location.below)));
                out.append(outgoingBox(Board.leftFeasible, Board.left, curLocation,
                        Location.transpose(curLocation, Location.right)));
                out.append(outgoingBox(Board.rightFeasible, Board.right, curLocation,
                        Location.transpose(curLocation, Location.left)));

                /* Default case: nothing happened */
                out.append(tab + "TRUE : " + Board.getBoard(curLocation) + ";\n");

                /* time to close the case */
                out.append(and("esac"));
            }
        }
        /* out.append(negWinningCondition()); */
        /* System.out.println(moreGoalsLessBoxes()); */
        /* System.out.println(lessGoalsMoreBoxex()); */
        /* out.append(moreGoalsLessBoxes()); */
        /* out.append(GFnoBoxesWithoutGoals()); */
        /* out.append("LTLSPEC NAME iNeverWin := G(!won);\n"); */
        out.append(this.losingCondition());
        /* out.append(goalsAreCovered()); */
        return out.toString();
    }

    /* This method must be overridden inorder to change the winning condition */
    public String losingCondition() throws IOException, InterruptedException {
        return ("LTLSPEC NAME iNeverWin := G(!won);\n");
    }

    public StringBuilder negWinningCondition() {
        StringBuilder str = new StringBuilder();
        return str;
    }

    /* reached all goals and same number of boxes */
    public StringBuilder allGoalsSameBoxex() {
        StringBuilder str = new StringBuilder();
        return str;
    }

    public StringBuilder moreGoalsLessBoxes() {
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME moreGoalsLessBoxes := !F(");
        for (int r = 0; r < b.rows; r++) {
            for (int c = 0; c < b.cols; c++) {
                Location curloc = new Location(r, c);
                if (curloc.x == (b.rows - 1) & curloc.y == (b.cols - 1))
                    str.append(Board.getBoard(curloc) + " != " + Board.box + ");");
                else
                    str.append(Board.getBoard(curloc) + " != " + Board.box + " | ");
            }
        }
        return str;
    }
}
