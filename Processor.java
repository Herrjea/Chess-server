/*


Falta:
	Comprobar que el usuario exista
	Poner jugadores de ajedrez famosos en ls BD
	Hacer concurrente esta clase
	estados back to enum
	Semáforos para las mesas
	int para los códigos de mesas


*/






import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import chess.*;


//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class Processor extends Thread {

	static HashMap<String,String> users = new HashMap<String,String>();
	static ArrayList<Integer> gameCodes = new ArrayList<Integer>();

	// Boards for all of the games initiated by the clients
	// that a currently being played
	static int initialCapacity;
	static Board [] games = null;

	// Board code to access existing games
	int gameCode;
	
	// Referencia a un socket para enviar/recibir las peticiones/answers
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;
	
	// Para que la answer sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;

	int serverState/*State.START*/;
	int color;

	String userLogin = "";

	final int START = 0, UNAUTHENTICATED = 1, AUTHENTICATED = 2, WHITES = 8, BLACKS = 9, LOGOUT = 15, WHITE = 20, BLACK = 21;
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public Processor( Socket socketServicio  ) {
		this.socketServicio = socketServicio;
		random = new Random();  //Creo que esto ya no sirve

		serverState = START;
		users.put("A","A");

		initialCapacity = 10;
		games = new Board[ initialCapacity ];
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
        @Override
	public void run(){
		
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		byte [] datosRecibidos = new byte[1024];
		int bytesRecibidos = 0;
		
		// Array de bytes para enviar la answer. Podemos reservar memoria cuando vayamos a enviarka:
		byte [] datosEnviar;

		String answer = "";
		
		while ( serverState != LOGOUT/*State.LOGOUT*/ ){

			try {
				// Obtiene los flujos de escritura/lectura
				inputStream = socketServicio.getInputStream();
				outputStream = socketServicio.getOutputStream();
				
				// Lee la petición del cliente
				////////////////////////////////////////////////////////
				bytesRecibidos = inputStream.read( datosRecibidos );
				////////////////////////////////////////////////////////
				
				// Creamos un String a partir de un array de bytes de tamaño "bytesRecibidos":
				String peticion = new String( datosRecibidos, 0, bytesRecibidos );
				peticion = peticion.toLowerCase();


				switch ( serverState ){

					case START:

						if ( peticion.contains( "conexion") ){

							answer = "Greetings, my fearless chess player. You may now introduce yourslef.";

							serverState = 1;
						}

						break;

					case UNAUTHENTICATED:

						boolean authenticated = checkUser( peticion );

						if ( authenticated ){

							answer = "=) Hello, " + userLogin + "! Do yo want to join or to create a game?";

							serverState = AUTHENTICATED;
						}
						else {

							answer = "Error 0000001. Wrong username or password.";
						}

						break;

					case AUTHENTICATED:

						int code = -1;
						boolean failedParse = false;

						// Get number typed by player
						try {
							code = Integer.parseInt( peticion.split( " " )[1] );
						} catch ( NumberFormatException nfe ){
							failedParse = true;
						}

						// Check for valid code
						if ( failedParse || code < 0 || code > games.length ){

							System.out.println(
								peticion.split( " " )[1] + " is not a valid code. Type in a number between 0 and " + games.length +
								" with the following syntax: \n\t(JOIN/CREATE) <code>" );
						}

						else {

							// Process request for joining an existing game
							if ( peticion.split( " " )[0].equals( "JOIN" ) ){

								if ( gameCodes.contains( code ) ){
									answer = "Joining the game.";
									// Remove it from games waiting for an adversary
									// so that no third party can come into the game
									gameCodes.remove( code );
									serverState = WHITES;
									color = BLACK;
								}
								else {
									answer = "Error 404: game not found. Type in another game code.";
								}
							}

							// Process request for starting a game
							else if ( peticion.split( " " )[0].equals( "CREATE" ) ){

								if ( ! gameCodes.contains( code ) ){
									answer = "Game created. Waiting for your opponent.";
									gameCodes.add( code );
									games[code] = new Board();
									serverState = WHITES;
									color = WHITE;
								}
								else {
									answer = "Error 405: game found. Enter a non-existing game code.";
								}
							}
						}

						break;

					case WHITES:

						// Whites move
						if ( color == WHITE ){

							if ( this.checkMove( peticion ) ){
                                                            
                                                            serverState = BLACKS;
                                                            answer = "Blacks move now.";
                                                        }
                                                        else answer = "That was an illegal movement!";
						}

						// Blacks don't move here!
						else {
                                                    
                                                    answer = "Not your turn yet.";
						}

						// Print current board
                                                //Esto da igual que lo imprimas lo ejecuta el servidor
                                                //Cuando lo ejecute el cliente no lo ver�
                                                //Cuando le encies el mensaje de no te toca o el moviento
                                                //Tienes que enviarlo con el tablero
						//System.out.println( games[gameCode].toString() );

						break;

					case BLACKS:

						// Blacks move
						if ( color == BLACK ){

							if ( this.checkMove( peticion ) ){
                                                            
                                                            serverState = WHITES;
                                                            answer = "Whites move now.";
                                                        }
                                                        else answer = "That was an illegal movement!";
						}

						// Blacks don't move here!
						else {
                                                    
                                                    answer = "Not your turn yet.";
						}

						// Print current board
						//System.out.println( games[gameCode].toString() );

						break;
                                }



				// Convertimos el String de answer en una array de bytes:
				datosEnviar = answer.getBytes();
				
				// Enviamos la traducción de Yoda:
				////////////////////////////////////////////////////////
				outputStream.write( datosEnviar, 0, datosEnviar.length );
				////////////////////////////////////////////////////////
				
				
				
			} catch ( IOException e ) {
				System.err.println( "Error al obtener los flujso de entrada/salida." );
			}
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