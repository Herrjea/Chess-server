
package chess;


/***
	*	@side	0 if whites, 1 if blacks
	*/
abstract class Piece{

	public static Board board;
	protected String type;
	protected int side;
	protected int leap_x;
	protected int leap_y;
	protected boolean rider;
	protected int hopper;

	public Piece( String type, int color ){

		this.type = type;
		side = color;
	}

	public Piece( String type, int color, int leap_x, int leap_y, boolean rider ){

		this.type = type;
		side = color;
		this.leap_x = leap_x;
		this.leap_y = leap_y;
		this.rider = rider;
		hopper = 0;
	}

	public String getType(){

		return type;
	}

	public int getSide(){

		return side;
	}

	public static boolean something( Position orig, Position dest ){

		return true;
	}

	public boolean validMove( Position orig, Position dest ){

		if ( board.free( dest.x, dest.y ) || 
			  board.at( dest.x, dest.y ).getSide() != side ){

			if ( rider ){

				int inc_i = 0;
				int inc_j = 0;

				// Use specified vector
				if ( (float) Math.abs( dest.x - orig.x ) /
					Math.abs( dest.y - orig.y )
					==
					(float) leap_x / leap_y ){
					inc_i = leap_x;
					inc_j = leap_y;
				// Revert vector components
				}else if( (float) Math.abs( dest.x - orig.x ) /
					Math.abs( dest.y - orig.y )
					==
					(float) leap_y / leap_x ){
					inc_i = leap_y;
					inc_j = leap_x;
				// Non reachable tile
				}else
					return false;

				// Rotate new vector if necessary
				if ( dest.x < orig.x )
					inc_i = -inc_i;
				if ( dest.y < orig.y )
					inc_j = -inc_j;

				for ( int i = orig.x + inc_i,
						j = orig.y + inc_j;
						i != dest.x; 
						i += inc_i, j += inc_j )
					if ( !board.free(i,j) )
						return false;

				return true;

			}else

				return
					( Math.abs( orig.x - dest.x) == leap_x &&
						Math.abs( orig.y - dest.y ) == leap_y ) ||
					( Math.abs( orig.x - dest.x) == leap_y &&
						Math.abs( orig.y - dest.y ) == leap_x );
		}

		return false;
	}

	public abstract char letter();
};


// Peón

class Pawn extends Piece{

	Pawn( int color ){

		super( "pawn", color );
	}

	public boolean validMove( Position orig, Position dest ){

		return
			orig.x - dest.x == ( side == 0 ? 1 : -1 ) &&
			( board.free( dest.x, dest.y ) ?
				dest.y == orig.y :
				(	board.at( dest.x, dest.y ).getSide() != side &&
					Math.abs( orig.y - dest.y ) == 1 ) );
	}

	public char letter(){

		return side == 1 ? '♙' : '♟';
	}
};


// Caballo

class Knight extends Piece{

	Knight( int color ){

		super( "knight", color, 2, 1, false );
	}

/*	public boolean validMove( Position orig, Position dest ){

		return
			( board.free( dest.x, dest.y ) ||
			  board.at( dest.x, dest.y ).getSide() != side ) &&
			( ( Math.abs( orig.x - dest.x) == 2 &&
				 Math.abs( orig.y - dest.y ) == 1 ) ||
			  ( Math.abs( orig.x - dest.x) == 1 &&
				 Math.abs( orig.y - dest.y ) == 2 ) );
	}*/

	public char letter(){

		return side == 1 ? '♘' : '♞';
	}
};


// Torre

class Rook extends Piece{

	Rook( int color ){

		super( "rook", color, 1, 0, true );
	}

/*	public boolean validMove( Position orig, Position dest ){

		if ( ( board.free( dest.x, dest.y ) || 
					board.at( dest.x, dest.y ).getSide() != side ) &&
			  ( orig.x == dest.x ^ orig.y == dest.y ) ){

			// Movimiento vertical
			if ( orig.y == dest.y ){

				for ( int i = Math.min( orig.x, dest.x) + 1;
					i < Math.max( orig.x, dest.x ); i++ )
					if ( !board.free( i, dest.y ) )
						return false;

			// Movimiento horizontal
			}else{

				for ( int i = Math.min( orig.y, dest.y ) + 1;
					i < Math.max( orig.y, dest.y ); i++ )
					if ( !board.free( dest.x, i ) )
						return false;
			}

			return true;
		}

		return false;
	}*/

	public char letter(){

		return side == 1 ? '♖' : '♜';
	}
};

// Alfil

class Bishop extends Piece{

	Bishop( int color ){

		super( "bishop", color, 1, 1, true );
	}

/*	public boolean validMove( Position orig, Position dest ){

		if ( ( board.free( dest.x, dest.y ) || 
					board.at( dest.x, dest.y ).getSide() != side ) &&
			  Math.abs( orig.x - dest.x) == Math.abs( orig.y - dest.y ) ){
			int inc_i = orig.x < dest.x ? 1 : -1;
			int inc_j = orig.y < dest.y ? 1 : -1;

				for ( int i = orig.x + inc_i, j = orig.y + inc_j;
					i != dest.x; i += inc_i, j += inc_j ){
System.out.println( "checking " + i + "," + j );
					if ( !board.free(i,j) )
						return false;}

			return true;
		}

		return false;
	}*/

	public char letter(){

		return side == 1 ? '♗' : '♝';
	}
};


// Reina

class Queen extends Piece{

	Queen( int color ){

		super( "queen", color );
	}

	public boolean validMove( Position orig, Position dest ){

		if ( board.free( dest.x, dest.y ) || 
			  board.at( dest.x, dest.y ).getSide() != side )

			// Movimiento en equis
			if ( Math.abs( orig.x - dest.x) == Math.abs( orig.y - dest.y ) ){
				int inc_i = orig.x < dest.x ? 1 : -1;
				int inc_j = orig.y < dest.y ? 1 : -1;

					for ( int i = orig.x + inc_i, j = orig.y + inc_j;
						i != dest.x; i += inc_i, j += inc_j ){
	System.out.println( "checking " + i + "," + j );
						if ( !board.free(i,j) )
							return false;}

			return true;

			// Movimiento en cruz
			}else if ( orig.x == dest.x ^ orig.y == dest.y ){

				// Movimiento vertical
				if ( orig.y == dest.y ){

					for ( int i = Math.min( orig.x, dest.x) + 1;
						i < Math.max( orig.x, dest.x ); i++ )
						if ( !board.free( i, dest.y ) )
							return false;

				// Movimiento horizontal
				}else{

					for ( int i = Math.min( orig.y, dest.y ) + 1;
						i < Math.max( orig.y, dest.y ); i++ )
						if ( !board.free( dest.x, i ) )
							return false;
				}

				return true;
			}

		return false;
	}

	public char letter(){

		return side == 1 ? '♕' : '♛';
	}
};

// Rey

class King extends Piece{

	King( int color ){

		super( "king", color );
	}

	public boolean validMove( Position orig, Position dest ){

		return
			( board.free( dest.x, dest.y ) || 
			  board.at( dest.x, dest.y ).getSide() != side ) &&
			( dest.x >= orig.x - 1 &&
			  dest.x <= orig.x + 1 &&
			  dest.y >= orig.y - 1 &&
			  dest.y <= orig.y + 1 );
	}

	public char letter(){

		return side == 1 ? '♔' : '♚';
	}
};

class Test extends Piece{

	public Test( int color, int leap_x, int leap_y, boolean rider ){

		super( "test", color, leap_x, leap_y, rider );
	}

	public boolean validMove( Position orig, Position dest ){

		if ( board.free( dest.x, dest.y ) || 
			  board.at( dest.x, dest.y ).getSide() != side ){

			int inc_i = 0;
			int inc_j = 0;

			// Use specified vector
			if ( (float) Math.abs( dest.x - orig.x ) /
				Math.abs( dest.y - orig.y )
				==
				(float) leap_x / leap_y ){
				inc_i = leap_x;
				inc_j = leap_y;
			// Revert vector components
			}else if( (float) Math.abs( dest.x - orig.x ) /
				Math.abs( dest.y - orig.y )
				==
				(float) leap_y / leap_x ){
				inc_i = leap_y;
				inc_j = leap_x;
			// Non reachable tile
			}else
				return false;

			// Rotate new vector if necessary
			if ( dest.x < orig.x )
				inc_i = -inc_i;
			if ( dest.y < orig.y )
				inc_j = -inc_j;

			for ( int i = orig.x + inc_i,
					j = orig.y + inc_j;
					i != dest.x; 
					i += inc_i, j += inc_j )
				if ( !board.free(i,j) )
					return false;

			return true;
		}

		return false;
	}

	public char letter(){

		return side == 0 ? 't' : 'T';
	}
};





/*
System.out.println(  );

System.out.println( "rook::" + orig.x + "," + orig.y + " " + dest.x + "," + dest.y );

♔♕♖♗♘♙
♚♛♜♝♞♟
†
ত
🐉🐲
*/
