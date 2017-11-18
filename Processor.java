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


//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class Processor extends Thread {

	static HashMap<String,String> users = new HashMap<String,String>();
	static ArrayList<String> games = new ArrayList<String>();

	
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
		random = new Random();

		serverState = START;
		users.put("A","A");
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
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
				peticion.toLowerCase();


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

						String code = peticion.split( " " )[1];

						if ( peticion.split( " " )[0].equals( "JOIN" ) ){

							if ( games.contains( code ) ){
								answer = "Joining the game.";
								games.remove( code );
								serverState = WHITES;
								color = BLACK;
							}
							else {
								answer = "Error 404: game not found. Type in another game code.";
							}
						}

						else if ( peticion.split( " " )[0].equals( "CREATE" ) ){

							if ( ! games.contains( code ) ){
								answer = "Game created. Waiting for your opponent.";
								serverState = WHITES;
								color = WHITE;
							}
							else {
								answer = "Error 405: game found. Enter a non-existing game code.";
							}
						}

						break;

					case WHITES:

						// Whites move
						if ( color == WHITE ){

							
						}

						// Blacks don't move here!
						else {

						}

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

	// Yoda interpreta una frase y la devuelve en su "dialecto":
	private String yodaDo( String peticion ) {
		// Desordenamos las palabras:
		String[] s = peticion.split( " " );
		String resultado = "";
		
		for ( int i = 0; i < s.length; i++ ){
			int j = random.nextInt( s.length );
			int k = random.nextInt( s.length );
			String tmp = s[j];
			
			s[j] = s[k];
			s[k] = tmp;
		}
		
		resultado = s[0];
		for ( int i = 1; i < s.length; i++ ){
		  resultado += " " + s[i];
		}
		
		return resultado /* Ejercicio 2 */ + "\n";
	}

	public boolean checkUser( String query ){

		if ( query.contains( "LOGIN" ) && query.contains( "PASSWD" ) ){

			// Buscar al usuario
			userLogin = query.split( " " )[1];
			if ( users.containsKey( userLogin ) ){
			// Buscar la contraseña
				String password = query.split( " " )[3];
				if ( users.get( userLogin ).equals( password ) )
					return true;
			}
		}

		return false;
	}
}