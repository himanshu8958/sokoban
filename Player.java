import java.io.*;
import java.util.*;
public class Player {
    Board b = null;
    
    public Player(Board b){
        this.b = b;
    }

    public static void main(String[] args)throws IOException{
        String curBoard = args[0];
        Board board = Board.readBoard(curBoard);
        File smvFile = new File(curBoard + ".smv");
        Player p = new Player(board);
        FileWriter out = new FileWriter(smvFile);
        /* System.out.println(p.smvWriter()); */
        out.write(p.smvWriter());
        out.close();
    }

    public StringBuilder and(String... args) {
        StringBuilder str = new  StringBuilder();
        for (int ctr = 0; ctr < args.length -1; ctr ++){
            str.append(args[ctr] + " & ");
        }
        str.append(args[args.length - 1] + ";\n");
        return str;
    }

    public StringBuilder incomingBox(String dirFeasible, String nextDirection, Location boxLoc, Location keeperLoc, Location curLocation){
        StringBuilder str = new StringBuilder();
        if(!b.exists(curLocation) | !b.exists(keeperLoc) | !b.exists(boxLoc)) 
            return str;
        str.append(and(tab + dirFeasible, Board.nextDirection(nextDirection), Board.boardHas(boxLoc,Board.box), 
            Board.getLocation(keeperLoc), Board.boardHas(curLocation, Board.floor) + " : " + Board.box));
        str.append(and(tab + dirFeasible, Board.nextDirection(nextDirection), Board.boardHas(boxLoc,Board.box), 
            Board.getLocation(keeperLoc), Board.boardHas(curLocation, Board.goal) + " : " + Board.boxOnGoal));
        str.append(and(tab + dirFeasible, Board.nextDirection(nextDirection), Board.boardHas(boxLoc,Board.boxOnGoal), 
            Board.getLocation(keeperLoc), Board.boardHas(curLocation, Board.floor) + " : " + Board.box));
        str.append(and(tab + dirFeasible, Board.nextDirection(nextDirection), Board.boardHas(boxLoc,Board.boxOnGoal), 
            Board.getLocation(keeperLoc), Board.boardHas(curLocation, Board.goal) + " : " + Board.boxOnGoal));

        return str;
    }

    /* curLocation is box location */
    public StringBuilder outgoingBox(String dirFeasible, String nextDirection, Location curLocation, Location keeperLoc){
        StringBuilder str = new StringBuilder();
        if(!b.exists(curLocation) | !b.exists(keeperLoc)) 
            return str;
        str.append(and(tab + dirFeasible, Board.nextDirection(nextDirection),
            Board.getLocation(keeperLoc),
            Board.boardHas(curLocation, Board.box) + " : " + Board.floor));
        str.append(and(tab + dirFeasible, Board.nextDirection(nextDirection),
            Board.getLocation(keeperLoc),
            Board.boardHas(curLocation, Board.boxOnGoal) + " : " + Board.goal));
        
        return str;
    }

    public StringBuilder incomingKeeper(String dir, String dirFeasible, Location keeperLoc, Location curLocation){
        StringBuilder out = new StringBuilder();
        if(!b.exists(keeperLoc) | !b.exists(curLocation)) /* taking care of the corner cases */
            return out;
        out.append(and(tab+dirFeasible, Board.getLocation(keeperLoc), Board.nextDirection(dir), Board.boardHas(curLocation, Board.box) + " : " + Board.keeper));
        out.append(and(tab+dirFeasible, Board.getLocation(keeperLoc), Board.nextDirection(dir), Board.boardHas(curLocation, Board.boxOnGoal) + " : " + Board.keeperOnGoal));
        out.append(and(tab+dirFeasible, Board.getLocation(keeperLoc), Board.nextDirection(dir), Board.boardHas(curLocation, Board.goal) + " : " + Board.keeperOnGoal));
        out.append(and(tab+dirFeasible, Board.getLocation(keeperLoc), Board.nextDirection(dir), Board.boardHas(curLocation, Board.floor) + " : " + Board.keeper));        
        return out;
    }

    String tab = "\t";
    String newLine = ";\n";
    String and = " & ";
    public String smvWriter(){
        StringBuilder out = new StringBuilder();
        out.append("MODULE main\n\n");
        out.append("DEFINE\n");
        out.append("rows := " + b.rows + ";\n");
        out.append("cols := " + b.cols + ";\n");
        out.append("goalsCovered := "  + b.winningCondition()+"\n");
        out.append("VAR\n");
        /* out.append("row : 0.."+ (b.rows - 1) +";\n");
        out.append("col : 0.."+ (b.cols - 1) +";\n"); */
        out.append("row : 0.. (rows - 1);\n");
        out.append("col : 0.. (cols - 1);\n");
        out.append("won : boolean;\n");
        out.append("direction : {l, r, u, d};\n");
        out.append("board:array 0 .. (rows - 1) of array 0 .. (cols - 1) of {\"#\", \"+\", \"$\", \"*\", \"@\", \".\", \"_\"};\n");
        out.append("DEFINE\n");
        out.append("boxRight := ((col + 1) < cols) & (board[row][col + 1] = \"$\" | board[row][col + 1] = \"*\");\n");
        out.append("boxLeft  := ((col - 1) > -1)   & (board[row][col - 1] = \"$\" | board[row][col - 1] = \"*\");\n");
        out.append("boxDown  := ((row - 1) > -1)   & (board[row - 1][col] = \"$\" | board[row - 1][col] = \"*\");\n");
        out.append("boxUp    := ((row + 1) < rows) & (board[row + 1][col] = \"$\" | board[row + 1][col] = \"*\");\n\n");
        out.append("up := (row + 1 < rows &\n"+
		   "\t(board[row + 1][ col] = \"_\" | board[row + 1 ][col]  = \".\" | \n"+
		   "\t(boxUp & row + 2 < rows & (board[row + 2][col] = \"_\" | board[row + 2][col] = \".\"))));\n");
        out.append("down := (row - 1 >= 0 &\n"+
		   "\t(board[row - 1][ col] = \"_\" | board[row - 1 ][col]  = \".\" |\n" +
		   "\t(boxDown & row - 2 >= 0 & (board[row - 2][col] = \"_\" | board[row - 2][col] = \".\"))));\n");
        out.append("right := (col + 1 < cols &\n"+
		   "\t(board[row][col + 1] = \"_\" | board[row][col + 1]  = \".\" | \n"+
		   "\t(boxRight & col + 2 < cols & (board[row][col + 2] = \"_\" | board[row][col + 2] = \".\"))));\n");
    	out.append("left := (col - 1 >= 0 &\n"+
		   "\t(board[row][col - 1] = \"_\" | board[row][col - 1]  = \".\" | \n"+
		   "\t(boxLeft & col - 2 >= 0 & (board[row][col - 2] = \"_\" | board[row][col - 2] = \".\"))));\n");
        
        out.append("ASSIGN\n");
        for (int r = b.rows -1 ; r >= 0 ; r--){
            for(int c = 0; c < b.cols; c++){
                out.append("init(board[" + r  + "][" + c+"]) :=\"" + b.cell[r][c] + "\";");
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
                "\tTRUE                   : won;\n"+
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
                "\t--should be unreachable\n"+
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

    System.out.println("rows : " + b.rows + " cols : " + b.cols);
    for (int r = 0; r < b.rows; r++) {
        for ( int c = 0 ; c < b.cols; c ++) { 
            Location curLocation = new Location(r, c);
            
            out.append("\nnext (" + Board.getBoard(curLocation) +") :=\n");
            out.append("case\n");
            out.append("\t" + Board.boardHas(curLocation,Board.wall) + " : " + Board.wall + ";\n");
            
            /* Keeper leaving this cell */
            out.append(tab +"-- Keeper leaving this cell\n");
            out.append(and(tab + Board.getLocation(curLocation), Board.boardHas(curLocation, Board.keeper) + " : " + Board.floor ));
            out.append(and(tab + Board.getLocation(curLocation), Board.boardHas(curLocation, Board.keeperOnGoal)+ " : " + Board.goal));
            
            /* Welcome keeper */
            out.append(tab +"-- Welcome Keeper\n");
            /* keeper moves down, Thus he must be above */
            out.append(incomingKeeper(Board.down, Board.downFeasible, Location.transpose(curLocation, Location.above), curLocation));
            out.append(incomingKeeper(Board.up, Board.upFeasible, Location.transpose(curLocation, Location.below), curLocation));
            out.append(incomingKeeper(Board.left, Board.leftFeasible, Location.transpose(curLocation, Location.right), curLocation));
            out.append(incomingKeeper(Board.right, Board.rightFeasible, Location.transpose(curLocation, Location.left), curLocation));

            /* Incoming box */
            out.append(tab + "-- Incoming box\n");
            out.append(incomingBox(Board.downFeasible, Board.down, Location.transpose(curLocation, Location.above),
             Location.transpose(curLocation, Location.above2), curLocation));
            out.append(incomingBox(Board.upFeasible, Board.up, Location.transpose(curLocation, Location.below),
             Location.transpose(curLocation, Location.below2), curLocation));
            out.append(incomingBox(Board.leftFeasible, Board.left,Location.transpose(curLocation, Location.right),
             Location.transpose(curLocation, Location.right2), curLocation));
            out.append(incomingBox(Board.rightFeasible, Board.right,Location.transpose(curLocation, Location.left),
             Location.transpose(curLocation, Location.left2), curLocation));

            /* Outgoing box */
            out.append(tab + "-- Outgoing box\n");
            out.append(outgoingBox(Board.downFeasible, Board.down, curLocation, Location.transpose(curLocation, Location.above)));
            out.append(outgoingBox(Board.upFeasible, Board.up, curLocation, Location.transpose(curLocation, Location.below)));
            out.append(outgoingBox(Board.leftFeasible, Board.left, curLocation, Location.transpose(curLocation, Location.right)));
            out.append(outgoingBox(Board.rightFeasible, Board.right, curLocation, Location.transpose(curLocation, Location.left)));

            /* Default case:  nothing happened */
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
        out.append("LTLSPEC NAME iNeverWin := G(!won);\n");
        /* out.append(goalsAreCovered()); */
        return out.toString();
    }
    
    public StringBuilder negWinningCondition(){
        StringBuilder str = new StringBuilder();
        return str;
    }
    
    /* reached all goals and same number of boxes */
    public StringBuilder allGoalsSameBoxex(){
        StringBuilder str = new StringBuilder();
        return str;
    }

    public StringBuilder moreGoalsLessBoxes(){
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME moreGoalsLessBoxes := !F(");
        for(int r = 0; r < b.rows; r ++) {
            for (int c = 0; c < b.cols; c ++){
                Location curloc = new Location(r,c);
                if(curloc.x == (b.rows - 1) & curloc.y == (b.cols - 1 ) )
                    str.append(Board.getBoard(curloc) + " != " + Board.box + ");");
                else
                str.append(Board.getBoard(curloc) + " != " + Board.box  + " | "); 
            }
        }
        return str;
    }

    /* public StringBuilder goalsAreCovered(){
        Set<Location> goals = b.getGoalPositions();
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME goalsCovered :=G(" );
        Iterator<Location> iter = goals.iterator();
        while(iter.hasNext()) {
            Location curLocation  = iter.next();
            str.append("("+
                Board.boardHas(curLocation, Board.goal) + 
                " | "  +
                Board.boardHas(curLocation, Board.keeperOnGoal) +
                 " ) ");
            if(iter.hasNext()){
                str.append(" & ");
            } else {
                str.append(");\n");
            }
        }
        return str;
    }
    public StringBuilder GFnoBoxesWithoutGoals(){
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME GFnoBoxesWithoutGoals := GF(");
        for(int r = 0; r < b.rows; r ++) {
            for (int c = 0; c < b.cols; c ++){
                Location curloc = new Location(r,c);
                if(curloc.x == (b.rows - 1) & curloc.y == (b.cols - 1 ) )
                    str.append(Board.getBoard(curloc) + " != " + Board.box + ");");
                else
                str.append(Board.getBoard(curloc) + " != " + Board.box  + " & "); 
            }
        }
        return str;
    }

    
    /* public StringBuilder lessGoalsMoreBoxex(){
        StringBuilder str = new StringBuilder();
        str.append("LTLSPEC NAME moreGoalsLessBoxes := G(");
        for(int r = 0; r < b.rows; r ++) {
            for (int c = 0; c < b.cols; c ++){
                Location curloc = new Location(r,c);
                if(curloc.x == (b.rows - 1) & curloc.y == (b.cols - 1 ) )
                    str.append(Board.getBoard(curloc) + " != " + Board.goal + ");");
                else
                str.append(Board.getBoard(curloc) + " != " + Board.goal  + " & "); 
            }
        }
        return str;
    } */
}
