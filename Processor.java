/*


Falta:
	Comprobar que el usuario exista
	Poner jugadores de ajedrez famosos en ls BD
	Hacer concurrente esta clase
	estados back to enum
	Semáforos para las mesas
	int para los códigos de mesas


*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import chess.*;

public class Processor extends Thread {

	static HashMap<String,String> users = new HashMap<String,String>();
	static ArrayList<Integer> gameCodes = new ArrayList<Integer>();

	// Boards for all of the games initiated by the clients
	// that a currently being played
	static int initialCapacity;
	static Board [] games = null;

	// Board code to access existing games
	int gameCode;
	

	private Socket socketServicio;

	private BufferedReader inReader;

	private PrintWriter outPrinter;

	int serverState;
	int color;

	String userLogin = "";

	final int 
		START = 0, UNAUTHENTICATED = 1, AUTHENTICATED = 2,
		WHITES = 3, BLACKS = 4, LOGOUT = 5,
		WHITE = 0, BLACK = 1;
	
	// Constructor
	public Processor( Socket socketServicio  ) {
		this.socketServicio = socketServicio;

		serverState = START;
		users.put("A","A");

		initialCapacity = 10;
		games = new Board[ initialCapacity ];

		for ( int i = 0; i < initialCapacity; i++ )
			games[i] = new Board();
	}
	
        @Override
	public void run(){
		
		String datosRecibidos;
		String answer;
		String peticion;

		datosRecibidos = "";
		answer = "";
		peticion = "";

		try {
			// Get IO flux
			inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
				
			while (serverState != LOGOUT) {
			
				// Read client's request
				datosRecibidos = inReader.readLine();
				peticion = datosRecibidos.toUpperCase();

				peticion += " . . . . . . . . . . ";

				// Check if user is leaving
				if ( peticion.contains( "EXIT" ) ){
					answer = "Leaving now. =(\n";
					serverState = LOGOUT;
				}

				System.out.println( "Thread id " + Thread.currentThread().getId() + " at server state: " + serverState);

				switch ( serverState ){

					case START:

						if ( peticion.contains( "CONNECT") ){
							answer = "Greetings, my fearless chess player. You may now introduce yourslef.\n";
							serverState = 1;
						}
						else answer = "Writte \'connect\' to connect\n";
						break;

					case UNAUTHENTICATED:

						boolean authenticated = checkUser( peticion );

						if ( authenticated ){
							answer = "=) Hello, " + userLogin + "! Do yo want to join or to create a game?\n";
							serverState = AUTHENTICATED;
						}
						else answer = "Error 0000001. Wrong username or password.\n";
						break;

					case AUTHENTICATED:

						int code = -1;
						boolean failedParse = false;

						// Get number typed by player
						try {
							code = Integer.parseInt( peticion.split( " " )[1] );
						} catch ( Exception e ){
							failedParse = true;
						}	//NO SE QUE HACE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
System.out.println( failedParse );

						// Check for valid code
						if ( failedParse || code < 0 || code > games.length )
							answer = peticion.split( " " )[1] + " is not a valid code. Type in a number between 0 and " + games.length + " with the following syntax: \n\t(JOIN/CREATE) <code>\n";

						else {

							// Process request for joining an existing game
							if ( peticion.split( " " )[0].equals( "JOIN" ) ){

								if ( gameCodes.contains( code ) ){
									answer = "Joining the game.\n";
									// Remove it from games waiting for an adversary
									// so that no third party can come into the game
									gameCodes.remove( code );
									serverState = WHITES;
									color = BLACK;
								}
								else answer = "Error 404: game not found. Type in another game code.\n";
							}

							// Process request for starting a game
							else if ( peticion.split( " " )[0].equals( "CREATE" ) ){

								if ( ! gameCodes.contains( code ) ){
									answer = "Game created. Waiting for your opponent.\n";
									gameCodes.add( code );
									games[code] = new Board();
									serverState = WHITES;
									color = WHITE;
								}
								else answer = "Error 405: game found. Enter a non-existing game code.\n";
							}
						}
						break;

					case WHITES:

						// Whites move
						if ( color == WHITE ){

							if ( this.checkMove( peticion ) ){
                                                         
                               					if ( games[gameCode].getCheck( WHITE ) ){
                                					answer = games[gameCode].toString() + "\nWhites win the game!\n";
                                					serverState = AUTHENTICATED;
                                				}
                                				else {
	                                				serverState = BLACKS;
	                                				answer = games[gameCode].toString() + "\nBlacks move now.\n";
	                            				}
                            				}
                            				else answer = games[gameCode].toString() + "\nThat was an illegal movement!\nPlease specify a movement with the following syntax: MOV <source> TO <destination>.\n";
						}

						// Blacks don't move here!
						else answer = games[gameCode].toString() + "\nNot your turn yet.\n";
						break;

					case BLACKS:

						// Blacks move
						if ( color == BLACK ){

							if ( this.checkMove( peticion ) ){
                                                            
                                				if ( games[gameCode].getCheck( BLACK ) ){
                                					answer = games[gameCode].toString() + "\nBlacks win the game!";
                                					serverState = AUTHENTICATED;
                                				}
                                				else {
                                					serverState = WHITES;
                                					answer = games[gameCode].toString() + "\nWhites move now.";
                                				}
                            				}
                            				else answer = games[gameCode].toString() + "\nThat was an illegal movement!";
						}

						// Whites don't move here!
						else answer = games[gameCode].toString() + "\nNot your turn yet.";
						break;

					default:

						System.out.println( "You've done something wrong, this message should never appear. Muahaha." );

						break;
				}//End of switch

				// Send to client
				if (answer != "\n") outPrinter.println(answer);
			}//End of while
				
		} catch ( IOException e ) {
			System.err.println( "Error al obtener los flujos de entrada/salida." );
		}
	}
        

	public boolean checkUser( String query ){


		if ( query.contains( "LOGIN" ) && query.contains( "PASSWD" ) ){

			// Find user
			userLogin = query.split( " " )[1];

			if ( users.containsKey( userLogin ) ){

				// Find password

				String password = query.split( " " )[3];

				if ( users.get( userLogin ).equals( password ) )
					return true;
			}
		}

		return false;
	}
        

        public boolean checkMove( String query ){
            

            if ( "MOV".equals( query.split( " " )[0] ) && "TO".equals( query.split( " " )[2] ) ){
                
                String initPos = query.split( " " )[1];
                String goalPos = query.split( " " )[3];
                
                return games[gameCode].move( initPos + " " + goalPos );
            }
            
            return false;
        }
}
