import java.io.*;
import java.util.*;
public class Play {
    Map<Integer, State> states = new HashMap<Integer, State>();
    Boolean isWinningPlay;
    Board initialBoard;
    State initialState;
    File trace;

    public boolean isWin() {
        return isWinningPlay;
    }

    public StringBuilder LURD() {
        StringBuilder str = new StringBuilder();
        for (int ctr = 0; ctr < states.size(); ctr++) {
            State curState = states.get(ctr + 1);
            str.append(curState.direction);
            if (ctr < states.size() - 1)
                str.append(", ");
            else
                str.append('\n');
        }
        return str;
    }
    public void playThePlay(){
        System.out.println("States : " + states.size());
        for(int ctr = 0; ctr < states.size(); ctr++ ){
            System.out.println("State : " + ctr );
            State curState = states.get(ctr + 1);
            System.out.println(curState);
        }
    }
    
    private Play(Board initialBoard, File trace) {
        this.initialBoard = initialBoard;
        this.initialState = new State(initialBoard);
        this.trace = trace;
    }

    public static Play readTrace(Board initialBoard, File trace) throws IOException {
        Play thisPlay = new Play(initialBoard, trace);
        String line  = null;
        Scanner br = new Scanner(trace);
        State currentState = thisPlay.initialState;
        currentState = new State(initialBoard);
        currentState.board = initialBoard;

        while(br.hasNextLine()) {
            line = br.nextLine();
            line = line.trim();

            if(line.startsWith("-- specification")) {
                if(line.contains("is true")) {
                    thisPlay.isWinningPlay = false;
                } else if(line.contains("is false")) {
                    thisPlay.isWinningPlay = true;
                }
            } else if(line.startsWith("-> State:")){
                /* if(currentState == thisPlay.initialState){
                    continue;
                }
                thisPlay.states.put(thisPlay.states.size() + 1, currentState);
                currentState = new State(currentState); */
                if(thisPlay.states.size() != 0 ) {
                    currentState = new State(currentState);
                } 
                thisPlay.states.put(thisPlay.states.size() + 1, currentState);

            } else if (line.contains("board[")) {
                String[] parts = line.split("=");
                String key = parts[0].trim();
                String value = parts[1].trim().replace("\"", "");

                String[] indices = parts[0].split("]");
                
                String rowS = indices[0].substring(indices[0].indexOf('[') + 1);
                String colS = indices[1].substring(indices[1].indexOf('[') + 1);
                
                currentState.board.cell[Integer.valueOf(rowS)][Integer.valueOf(colS)] = value.charAt(0);
            } else if( line.contains("row =")){
                currentState.row = Integer.parseInt(line.split("=")[1].trim());
            } else if (line.contains("col =")) {
                    currentState.col = Integer.parseInt(line.split("=")[1].trim());
            } else if (line.contains("won =")) {
                currentState.won = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("direction =")) {
                currentState.direction = line.split("=")[1].trim();
            } else if (line.contains("goalsCovered =")) {
                currentState.goalsCovered = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("left =")) {
                currentState.left = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("right =")) {
                currentState.right = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("down =")) {
                currentState.down = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("up =")) {
                currentState.up = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("boxUp =")) {
                currentState.boxUp = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("boxDown =")) {
                currentState.boxDown = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("boxLeft =")) {
                currentState.boxLeft = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if (line.contains("boxRight =")) {
                currentState.boxRight = Boolean.parseBoolean(line.split("=")[1].trim());
            } else if(line.contains("-- Loop starts")) {
                currentState.loopStarted = true;
            }
        }
        br.close();
        return thisPlay;
    }
    

    public static void main(String[] args) throws IOException{
        File boardFile = new File("Boards/board1");
        Board b = Board.readBoard(boardFile);
        
        Play aPlay = Play.readTrace(b, new File(boardFile + ".out"));
        aPlay.playThePlay();
    }
    
}