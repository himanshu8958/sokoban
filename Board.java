import java.io.*;
import java.util.*;

public class Board {
    char[][] cell;
    int rows = 0;
    int cols = 0;
	
	public boolean exists(int x, int y) {
		if( 0 <= x & x <= rows &
		 0 <= y & y <= cols)
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
		while(file.hasNextLine()) {
			String line = file.nextLine();
			for(int ctr = 0; ctr < line.length(); ctr++) {
				b.cell[row][col] = line.charAt(col);
				col++;
			}
			row++;
		}
		return b;
    }

}
