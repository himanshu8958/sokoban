import java.io.*;
import java.util.*;

public class Board{
    char[][] cell;
    int rows = 0;
    int cols = 0;
	public static final String keeper = "\"@\"";
	public static final String keeperOnGoal = "\"+\"";
	public static final String box = "\"$\"";
	public static final String boxOnGoal = "\"*\"";
	public static final String wall = "\"#\"";
	public static final String goal = "\".\"";
	public static final String floor = "\"_\"";

	public static final String down = "d";
	public static final String right = "r";
	public static final String left = "l";
	public static final String up = "u";

	public static final String downFeasible =  "down ";
	public static final String rightFeasible = "right";
	public static final String leftFeasible =  "left ";
	public static final String upFeasible =    "up   ";

	public static final String boxDown =  "boxDown ";
	public static final String boxLeft =  "boxLeft ";
	public static final String boxRight = "boxRight";
	public static final String boxUp =    "boxUp   ";
	

	public static void main (String[] args) throws FileNotFoundException{
		Board b = Board.readBoard("board3");
		
		b.printBoard();
	}

	public Location keeperLocation(){
		for ( int r = 0; r < rows; r++){
			for ( int c = 0 ; c<cols; c++){
				if(cell[r][c] == '@')
				return(new Location(r, c));
			}
		}
		return null;
	}
	public static String getBoard(Location loc) {
		return "board[" + loc.x + "][" + loc.y + "]";
	}
	
	public static String getLocation(Location loc) {
		return "row = " + loc.x + " & col = " + loc.y;
	}

	public static String nextDirection(String a) {
        return "next(direction) = " + a;
    }

    public static String boardHas(Location loc, String s) {
        return Board.getBoard(loc) + " = " + s;
    }

	public boolean exists(Location loc) {
		if( 0 <= loc.x & loc.x <= rows &
		 0 <= loc.y & loc.y <= cols)
			return true;
		return false;
	}

	public void printBoard() {
		for ( int r = 0; r < rows; r++){
			for( int c = 0; c < cols; c++) {
				System.out.print(cell[r][c]);
			}
			System.out.println();
		}
	}

    public static Board readBoard (String path) throws FileNotFoundException {
		Scanner file = new Scanner( new File ( path));
		Board b = new Board();
		while(file.hasNextLine()) {
			b.rows++;
			b.cols = file.nextLine().length();
		}
		file = new Scanner(new File(path));
		b.cell = new char[b.rows][b.cols];

		int row = 0; 
		int col = 0;
		for(int r = b.rows -1 ; r >= 0; r--) {
			String line = file.nextLine();
			for(int c = 0; c < b.cols; c++){
				b.cell[r][c] = line.charAt(c);
			}
		}
		return b;
    }

}
