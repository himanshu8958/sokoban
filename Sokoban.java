import java.io.*;

public class Sokoban {
    public static void main(String[] args) throws IOException, InterruptedException {
        for (int ctr = 0; ctr < args.length; ctr++) {
            System.out.println(ctr + " " + args[ctr]);
        }
        switch (args.length) {
            case 0:
                System.out.println(help());
                break;
            case 1:
                System.out.println(help());
                break;
            case 2:
                File boardFile = new File(args[1]);
                File smvFile = new File(args[1] + ".smv");
                File traceFile = new File(args[1] + ".out");
                Board board = Board.readBoard(boardFile);

                if (args[0].equals("-os") || args[0].equals("-oneShot")) {
                    /* Q2 */
                    Player player = new Player(board);
                    player.writeSmv(smvFile);
                    ModelChecker.checkBdd(smvFile, traceFile);
                    Play thisPlay = Play.readTrace(board, traceFile);
                    if (thisPlay.isWin()) {
                        System.out.println("There exists a way to solve this board");
                        System.out.println(thisPlay.LURD());
                    } else {
                        System.out.println("This board is unsolvable");
                    }

                } else if (args[0].equals("-osn") || args[0].equals("-oneShotNonBlocking")) {
                    /* Q2 but nonBlocking */
                } else if (args[0].equals("-itr") || args[0].equals("-iterative")) {
                    /* Q4 */
                }
            default:
                break;
        }
    }

    private static String help() {
        return "Sokoban \n\n" +
                "-os or -oneShot : does the Q2 in the simplest way. \n\n" +
                "-osn or -oneShotNonBlocking : Q2 but we find the blocking locations in the board and mark it for the solver\n\n"
                +
                "-itr or -iterative: Q4 ";
    }
}