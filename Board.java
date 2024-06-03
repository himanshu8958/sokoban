import java.io.*;
import java.util.*;

public class Board{
	char[][] cell;
	File boardFile;
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

	public File getBoardFile() {
		return this.boardFile;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				str.append(cell[r][c]);
			}
			str.append('\n');
		}
		return str.toString();
	}
	
	int hashcode = -1;

	public int hashCode() {
		int ans = 0;
		if (this.hashcode != -1)
			return this.hashcode;
		for (int r = 0; r < this.rows; r++) {
			for (int c = 0; c < this.cols; c++) {
				ans *= 11;
				ans += getCell(new Location(r, c)).hashCode();
			}
		}
		this.hashcode = ans;
		return ans;
	}

	public boolean equals(Object b) {
		if (!(b instanceof Board))
			return false;
		Board otherBoard = (Board) b;
		if (this.rows != otherBoard.rows || this.cols != otherBoard.cols) {
			return false;
		}

		for (int r = 0; r < this.rows; r++) {
			for (int c = 0; c < this.cols; c++) {
				if (this.cell[r][c] != otherBoard.cell[r][c])
					return false;
			}
		}
		return true;
	}

	public Board(char[][] cell, int rows, int cols, File boardFile) {
		this.cell = cell;
		this.rows = rows;
		this.cols = cols;
		this.boardFile = boardFile;
	}

	public Board() {
		this.cell = null;
		this.rows = 0;
		this.cols = 0;
	}
	
	public Board copy(){
		char [][] nuBoard = new char[this.rows] [this.cols];
		for(int r = 0; r < this.rows; r++) 
			for(int c = 0; c < this.cols; c++){
				nuBoard[r][c] = this.cell[r][c];
			}
		return new Board(nuBoard, this.rows, this.cols, this.boardFile);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Board b = Board.readBoard("board3");

		b.printBoard();
	}

	public String getCell(Location l) {
		return cellToString(this.cell[l.x][l.y]);
	}

	public void setCell(Location l, String s) {
		this.cell[l.x][l.y] = s.charAt(1);
		this.hashcode = -1;
	}

	public Location getKeeperLocation(){
		for ( int r = 0; r < rows; r++){
			for ( int c = 0 ; c<cols; c++){
				if(cell[r][c] == '@' | cell[r][c] == '+')
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
	public static String cellToString(char a) {
		return "\"" + a + "\"";
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


		for(int r = b.rows -1 ; r >= 0; r--) {
			String line = file.nextLine();
			for(int c = 0; c < b.cols; c++){
				b.cell[r][c] = line.charAt(c);
			}
		}
		file.close();
		return b;
    }

	public Set<Location> getGoalPositions(){
		Set<Location> goals = new HashSet<Location>();
		for(int r = 0; r < this.rows; r++){
			for (int c = 0 ; c < this.cols; c++){
				if(cellToString(this.cell[r][c]).equals(Board.goal) || 
					cellToString(cell[r][c]).equals(Board.boxOnGoal) || 
					cellToString(cell[r][c]).equals(Board.keeperOnGoal)) {
					goals.add(new Location(r,c));
				}
				
			}
		}
		return goals;
	}

	public Board getPartialBoard(Set<Location> currentBoxes, Set<Location> currentGoals){
		if(currentBoxes.size() != currentGoals.size())
			System.out.println("Same # of boxes and goals should be killed");
		Board newBoard = new Board(this.cell.clone(), this.rows, this.cols, this.boardFile);
		Set<Location> boxesToKill = this.getBoxPositions();
		boxesToKill.removeAll(currentBoxes);
		Set<Location> goalsToKill = this.getGoalPositions();
		goalsToKill.removeAll(currentGoals);

		for(Location deadBox : boxesToKill){
			newBoard.cell[deadBox.x][deadBox.y] = '#';
		}
		for(Location deadGoal : goalsToKill) {
			if(deadGoal.equals(getKeeperLocation())){
				newBoard.cell[deadGoal.x][deadGoal.y] = '@';
			} else {
				newBoard.cell[deadGoal.x][deadGoal.y] = '_';
			}
		}
		return newBoard;		
	}

	public Set<Location> getBoxPositions(){
		Set<Location> goals = new HashSet<Location>();
		for(int r = 0; r < this.rows; r++){
			for (int c = 0 ; c < this.cols; c++){
				if(cellToString(this.cell[r][c]).equals(Board.box) || 
					cellToString(cell[r][c]).equals(Board.boxOnGoal)){
					goals.add(new Location(r,c));
				}
				
			}
		}
		return goals;
	}
	

	public StringBuilder winningCondition() {
		StringBuilder str = new StringBuilder();
		List<Location> goals = new ArrayList<Location>();
		for (int r = 0; r < this.rows; r++) {
			for (int c = 0; c < this.cols; c++) {
				if (cellToString(this.cell[r][c]).equals(Board.goal) ||
						cellToString(cell[r][c]).equals(Board.boxOnGoal) ||
						cellToString(cell[r][c]).equals(Board.keeperOnGoal)) {
					goals.add(new Location(r, c));
				}

			}
		}
		/* System.out.println(goals.size()); */
		for (int ctr = 0; ctr < (goals.size() - 1); ctr++) {
			str.append(Board.boardHas(goals.get(ctr), Board.boxOnGoal));
			str.append(" & ");
		}
		str.append(Board.boardHas(goals.get(goals.size() - 1), Board.boxOnGoal) + ";\n");
		return str;
	}

	/* returns current board with just the goals and walls */
	public Board eyesOnPrize() {
		Board ret = this.copy();
		for (int r = 0; r < ret.rows; r++) {
			for (int c = 0; c < ret.cols; c++) {
				Location curLocation = new Location(r, c);
				if (ret.getCell(curLocation).equals(Board.box)) {
					ret.setCell(curLocation, Board.floor);
				} else if (ret.getCell(curLocation).equals(Board.boxOnGoal) ||
						ret.getCell(curLocation).equals(Board.keeperOnGoal) ||
						ret.getCell(curLocation).equals(Board.goal)) {
					ret.setCell(curLocation, Board.goal);
				}
			}
		}
		return ret;
	}

	public boolean isCorner(Location loc) {
		Location above = Location.transpose(loc, Location.above);
		Location below = Location.transpose(loc, Location.below);
		Location left = Location.transpose(loc, Location.left);
		Location right = Location.transpose(loc, Location.right);

		boolean aWall = !exists(above) | this.getCell(above).equals(Board.wall);
		boolean bWall = !exists(below) | this.getCell(below).equals(Board.wall);
		boolean lWall = !exists(left) | this.getCell(left).equals(Board.wall);
		boolean rWall = !exists(right) | this.getCell(right).equals(Board.wall);

		if (aWall & lWall)
			return true;
		if (aWall & rWall)
			return true;
		if (bWall & lWall)
			return true;
		if (bWall & rWall)
			return true;
		return false;
	}

	public Set<Location> getFloorLocations() {
		HashSet<Location> ans = new HashSet<Location>();
		for (int r = 0; r < this.rows; r++) {
			for (int c = 0; c < this.cols; c++) {
				Location curLocation = new Location(r, c);
				if (this.getCell(curLocation).equals(Board.floor)) {
					ans.add(curLocation);
				}
			}
		}
		return ans;
	}

}
