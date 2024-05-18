import java.io.*;
import java.util.*;
public class Player {
    Board b = null;
    public Player(Board b){
        this.b = b;
    }
    public static void main(String[] args)throws FileNotFoundException{
        
        Board board = Board.readBoard("board1");
        Player p = new Player(board);
        System.out.println(p.smvWriter());
    }
    public String smvWriter(){
        StringBuilder out = new StringBuilder();
        out.append("MODULE main\n\n");
        out.append("DEFINE\n");
        out.append("rows = " + b.rows + ";\n");
        out.append("cols = " + b.cols + ";\n\n");
        out.append("VAR\n");
        out.append("row : 0..rows-1;\n");
        out.append("col : 0..cols-1;\n");
        out.append("direction : {l, r, u, d, stop};\n");
        out.append("board:array 0 .. 8 of array 0 .. 7 of {\"@\", \"+\", \"$\", \"*\", \"#\", \".\", \"_\"};\n");
        out.append("DEFINE\n");
        out.append("boxRight := board[row][col + 1] = \"$\" | board[row][col + 1] = \"*\";\n");
        out.append("boxLeft  := board[row][col - 1] = \"$\" | board[row][col - 1] = \"*\";\n");
        out.append("boxDown  := board[row - 1][col] = \"$\" | board[row - 1][col] = \"*\";\n");
        out.append("boxUp    := board[row + 1][col] = \"$\" | board[row + 1][col] = \"*\";\n\n");
        out.append("up := (row + 1 < rows &\n"+
		   "\t(board[row + 1][ col] = \"_\" | board[row + 1 ][col]  = \".\" | \n"+
		   "\t(boxUp & row + 2 < rows & (board[row + 2][col] = \"_\" | board[row + 2][col] = \".\"))))\n");
        out.append("down := (row - 1 > 0 &\n"+
		   "\t(board[row - 1][ col] = \"_\" | board[row - 1 ][col]  = \".\" |\n" +
		   "\t(boxDown & row - 2 >= 0 & (board[row - 2][col] = \"_\" | board[row - 2][col] = \".\"))));\n");
        out.append("right := (col + 1 < cols &\n"+
		   "\t(board[row][col + 1] = \"_\" | board[row][col + 1]  = \".\" | \n"+
		   "\t(boxRight & col + 2 < cols & (board[row][col + 2] = \"_\" | board[row][col + 2] = \".\"))))\n");
    	out.append("left := (col - 1 > 0 &\n"+
		   "\t(board[row][col - 1] = \"_\" | board[row][col - 1]  = \".\" | \n"+
		   "\t(boxRight & col - 2 < cols & (board[row][col - 2] = \"_\" | board[row][col - 2] = \".\"))))\n");
        
        out.append("ASSIGN\n");
        for (int r = b.rows -1 ; r >= 0 ; r--){
            for(int c = 0; c < b.cols; c++){
                out.append("init(board[" + r  + "][" + c+"]) :=\"" + b.cell[r][c] + "\";");
            }
            out.append("\n");
        }
        Location keeper = this.b.keeperLocation();
        out.append("\ninit(row) : = " + keeper.x);
        out.append("\ninit(col) : = " + keeper.y);

        out.append("next(direction) :=\n" +
			"case\n" +
				
                "\tleft & right & up & down		: {l, r, u, d};\n\n" +

				"\tleft & right & up & !down   		: {l, r, u};\n" +
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
			"esac;\n");

            out.append("next (col) :=\n" +
	       	    "case\n" + 
			    "next(direction) = l & left : col - 1;\n" + 
			    "next(direction) = r & right : col + 1;\n" + 
			    "TRUE : col;\n" +
 		        "esac;\n" + 

		"next (row) :=\n" + 
	       	    "case" +
			"next(direction) = d & down : row - 1;\n" +
			"next(direction) = u & up : row + 1;\n" +
			"TRUE : row; \n" + 
		"esac;");

    	

        return out.toString();
    }
}
