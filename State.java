public class State{
    Board board;
    String direction;
    boolean goalsCovered;
    boolean left;
    boolean right;
    boolean up;
    boolean down;
    boolean boxUp;
    boolean boxDown;
    boolean boxLeft;
    boolean boxRight;
    boolean won; /* This will be true when the winning condition is reached */
    boolean loopStarted;

    /* position on the keeper */
    int row;
    int col;


    /* copy constructor */
    public State (State s) {
        this.board = s.board.copy();
        this.direction = s.direction;
        this.goalsCovered = s.goalsCovered;
        this.left = s.left;
        this.right = s.right;
        this.up = s.up;
        this.down = s.down;
        this.boxUp = s.boxUp;
        this.boxDown = s.boxDown;
        this.boxLeft = s.boxLeft;
        this.boxRight = s.boxRight;
        this.row = s.row;
        this.col = s.col;
        this.won = s.won;        
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(this.board.toString());
        str.append("won : " + won + '\n');
        str.append("feasible directions : ");
        if(left) str.append("l,");
        if(right) str.append("r,");
        if(down) str.append("d,");
        if(up) str.append("u,");
        str.append("\n boxes next to me : ");
        if(boxUp) str.append("u,");
        if(boxRight) str.append("r,");
        if(boxLeft) str.append("l,");
        if(boxDown) str.append("d,");
        str.append("\n");
        if(goalsCovered){
            str.append("goals covered \n");
        }
        if(loopStarted){
            str.append("looping now \n");
        }
        return str.toString();
    }

    public State (Board b) {
        this.board = b;
        this.direction = "";
        this.goalsCovered = false;
    }
}