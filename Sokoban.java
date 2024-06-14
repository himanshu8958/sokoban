import java.io.*;

public class Sokoban {
    public static void main(String[] args) throws IOException, InterruptedException {
        /*
         * for (int ctr = 0; ctr < args.length; ctr++) {
         * System.out.println(ctr + " " + args[ctr]);
         * }
         */
        long startTime = System.nanoTime();
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
                File errFile = new File(args[1] + ".err");

                Board board = Board.readBoard(boardFile);

                if (args[0].equals("-os") || args[0].equals("-oneShot")) {
                    /* Q2 */
                    Player player = new Player(board);
                    player.writeSmv(smvFile);
                    ModelChecker.checkBdd(smvFile, traceFile, errFile);
                    Play thisPlay = Play.readTrace(board, traceFile);

                    if (thisPlay.isWin()) {
                        System.out.println("There exists a way to solve this board");
                        System.out.println("Moves : " + thisPlay.LURD());
                        /* thisPlay.playThePlay(); */
                    } else {
                        System.out.println("This board is unsolvable");
                    }

                } else if (args[0].equals("-itr") || args[0].equals("-iterative")) {
                    /* Q4 */
                    Board input = Board.readBoard(boardFile);
                    Board result = input;
                    int ctr = 1;
                    Board perviousResult = input;
                    do {
                        perviousResult = result;
                        result = IterativePlayer.oneIteration(result, boardFile);
                        System.out.println("after Iteration : " + ctr++ + ": ");
                        result.printBoard();

                    } while (!result.equals(perviousResult) & result.getRemainingGoals().size() > 0);
                    if (result.getRemainingGoals().size() > 0) {
                        System.out.println("All goals cannot reach goals, pointless to move ahead");
                    } else {

                        System.out.println("all goals covered");
                        System.out.println("reached goals : " + result.getReachedGoals().size());
                    }
                }
            default:
                break;
        }
        System.out.println("Total time taken : " + (System.nanoTime() - startTime) / 1000000 + " ms");
    }

    private static String help() {
        return "Sokoban \n\n" +
                "-os or -oneShot : does the Q2 in the simplest way. \n\n" +
                "-itr or -iterative: Q4 ";
    }
}