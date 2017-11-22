
package chess;


public class Board{

	private Piece[][] board;
	private int width;
	private int height;
	private int turn;	// 0 for whites, 1 for blacks. Pretty messy
	private boolean[] check = new boolean[2];

	public Board(){

		this( 8, 8 );
	}

	public Board( int x, int y ){

		width = x;
		height = y;
		board = new Piece[height][width];
		turn = 0;
		Piece.board = this;

		//board[2][0] = new Test( 0, 1, 1, true );
		//board[3][5] = new Test( 1, 2, 1, false );
		// Piezas iniciales
		// Negras
		for ( int i = 0; i < width; i++ )
			board[1][i] = new Pawn( 1 );
		board[0][0] = new Rook( 1 );
		board[0][width-1] = new Rook( 1 );
		board[0][1] = new Knight( 1 );
		board[0][width-2] = new Knight( 1 );
		board[0][2] = new Bishop( 1 );
		board[0][width-3] = new Bishop( 1 );
		board[0][3] = new King( 1 );
		board[0][4] = new Queen( 1 );
		// Blancas
		for ( int i = 0; i < width; i++ )
			board[height-2][i] = new Pawn( 0 );
		board[height-1][0] = new Rook( 0 );
		board[height-1][width-1] = new Rook( 0 );
		board[height-1][1] = new Knight( 0 );
		board[height-1][width-2] = new Knight( 0 );
		board[height-1][2] = new Bishop( 0 );
		board[height-1][width-3] = new Bishop( 0 );
		board[height-1][3] = new Queen( 0 );
		board[height-1][4] = new King( 0 );
	}

	public int getWidth(){

		return width;
	}

	public int getHeight(){

		return height;
	}

	public int getTurn(){

		return turn;
	}

	public boolean getCheck( int side ){

		if ( side > 1 ) System.out.println( "Side = " + side + ". Qué te ocurre en la cabesa, muyayo." );
		return check[side];
	}

	public Piece at( int x, int y ){

		return board[x][y];
	}

	private boolean move( Position orig, Position dest ){

//System.out.println( "movepos::" + orig.x + "," + orig.y + " " + dest.x + "," + dest.y ); ///////////////

		if ( inside( orig ) && inside( dest ) &&
			  board[orig.x][orig.y] != null &&
			  turn == board[orig.x][orig.y].getSide() &&
			  board[orig.x][orig.y].validMove( orig, dest ) ){

			board[dest.x][dest.y] = board[orig.x][orig.y];
			board[orig.x][orig.y] = null;

			// Comprobar jaque
			check[1-turn] = checkCheck() ? true : false;
			// Change turn
			turn = 1 - turn; 
			return true;
		}

		return false;
	}

	public boolean move( int orig_x, int orig_y, int dest_x, int dest_y ){

		return move( new Position( orig_y, orig_x ),
						 new Position( dest_y, dest_x ) );
	}

	public boolean move( String command ){

		int orig_x = 0, orig_y = 0;
		int dest_x = 0, dest_y = 0;
		int i = 0, fin = command.length();
		char actual;
		command = command.toUpperCase();
		// phase indica la finalización correcta 
		// del estado actual del análisis
		boolean phase;	

		// Letra de origen
		phase = false;
		while ( i < fin && ! phase ){
			actual = command.charAt( i++ );
			if ( actual >= 'A' && actual <= 'Z' ){
				orig_x = actual - 'A';
				phase = true;
			}
		}
		if ( ! phase ) return false;

		// Dígito de origen
		phase = false;
		while ( i < fin && ! phase ){
			actual = command.charAt( i++ );
			if ( actual >= '1' && actual <= '9' ){
				orig_y = actual - '1';
				phase = true;
			}
		}
		if ( ! phase ) return false;

		// Letra de destino
		phase = false;
		while ( i < fin && ! phase ){
			actual = command.charAt( i++ );
			if ( actual >= 'A' && actual <= 'Z' ){
				dest_x = actual - 'A';
				phase = true;
			}
		}
		if ( ! phase ) return false;

		// Dígito de destino
		phase = false;
		while ( i < fin && ! phase ){
			actual = command.charAt( i++ );
			if ( actual >= '1' && actual <= '9' ){
				dest_y = actual - '1';
				phase = true;
			}
		}
		if ( ! phase ) return false;

//System.out.println( "movecom::" + orig_y + "," + orig_x + " " + dest_y + "," + dest_x );
		return move( new Position( orig_y, orig_x ),
						 new Position( dest_y, dest_x ) );
	}

	public boolean free( int x, int y ){

		return board[x][y] == null;
	}

	public boolean inside( Position tile ){

		return tile.x >= 0 && tile.x < height &&
				 tile.y >= 0 && tile.y < width;
	}

	private boolean checkCheck(){

		Position king = null;
		// Buscar la posición del rey pertinente
		for ( int i = 0; i < height && king == null; i++ )
			for ( int j = 0; j < width && king == null; j++ )
				if ( !free(i,j) &&
					  at(i,j).getType().equals( "king" ) &&
					  at(i,j).getSide() != turn )
					king = new Position( i, j );

		for ( int i = 0; i < height; i++ )
			for ( int j = 0; j < width; j++ )
				if ( !free(i,j) &&
					  at(i,j).validMove( new Position(i,j), king ) )
					return true;

		return false;
	}

	public String toString(){

		String text = "";
		char inicio = 'A';

		// Letras del marco superior
		text += "\n     ";
		for ( int i = 0; i < width * 8; i++ )
			text += i % 8 == 4 ? inicio++ : " ";
		text += "\n\n";

		for ( int i = 0; i <= height * 4; i++ ){

			// Números del marco izquierdo
			text += "  " + ( i % 4 == 2 ? i / 4 + 1 : " " ) + "  ";

			// Tablero
			for ( int j = 0; j <= width * 8; j++ ){

				if ( i % 4 == 0 || j % 8 == 0 )
					text += "#";
				else if ( i % 4 == 2 && j % 8 == 4 ){
					int x = ( i - 2 ) / 4;
					int y = ( j - 4 ) / 8;
					if ( board[x][y] == null )
						text += " ";
					else
						text += board[x][y].letter();
				}else
					text += " ";
			}

			// Números del marco derecho
			text += "  " + ( i % 4 == 2 ? i / 4 + 1 : " " ) + "  ";

			text += "\n";
		}

		// Letras del marco inferior
		text += "\n     ";
		inicio = 'A';
		for ( int i = 0; i < width * 8; i++ )
			text += i % 8 == 4 ? inicio++ : " ";
		text += "\n\n";

		// Estado de jaque
		if ( check[turn] )
			text += "†!\n";

		return text;
	}
};


/*
System.out.println(  );
*/
