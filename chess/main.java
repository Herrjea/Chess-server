
package chess;
import java.util.Scanner;


class MainChess{

	public static void main( String[] args ){

		Scanner in = new Scanner (System.in);
		String command;
		Board board = new Board( 8, 6 );

		System.out.print( board.toString() + "\n" +
			( board.getTurn() == 0 ? "Blancas" : "Negras" ) +
			": " );
		command = in.nextLine();
		while ( !command.equals("e") && !command.equals("exit") ){
			board.move( command );
			System.out.print( board.toString() +
				( board.getTurn() == 0 ? "Blancas" : "Negras" ) +
				": " );
			command = in.nextLine();
		}

		System.out.println( board.toString() );
	}
};
