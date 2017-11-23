import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
public class TCPServer {


	public static void main( String[] args ) {
	

		ServerSocket serverSocket = null;
		Socket socketServicio = null;

		Client servicio = null;
		boolean salir = false;
		boolean error = false;


		// Puerto de escucha
		int port = 9898;
		// array de bytes auxiliar para recibir o enviar datos.
		byte [] buffer = new byte[256];
		// Número de bytes leídos
		int bytesLeidos = 0;
		
		try {
			// Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			serverSocket = new ServerSocket( port );
			
			// Mientras ... siempre!
			do {

				// Aceptamos una nueva conexión con accept()
				try {
					System.out.println( "Waiting to accept player." );
					socketServicio = serverSocket.accept();
				} catch ( IOException e ){
					System.out.println( "Error: no se pudo aceptar la conexión solicitada." );
					error = true;
				}
				
				System.out.println( "Player accepted." );
			
				
				// Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
				// argumento el nuevo socket, para que realice el procesamiento
				// Este esquema permite que se puedan usar hebras más fácilmente.
				Processor procesador = new Processor( socketServicio );
				procesador.start();
				
			} while ( ! salir );
			
		} catch ( IOException e ) {
			System.err.println( "Error al escuchar en el puerto " + port );
		}

	}

}


/*
try {
			// Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			serverSocket = new ServerSocket( port );
			
			// Mientras ... siempre!
			do {

				// Aceptamos una nueva conexión con accept()
				try {
					System.out.println( "Server: Waiting for players to accept." );
					socketServicio = serverSocket.accept();
				} catch ( IOException e ){
					System.out.println( "Error: Unable to accept connection." );
					error = true;
				}
					System.out.println( "Server: Player accepted. Branching to new thread." );
			
				
				// Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
				// argumento el nuevo socket, para que realice el procesamiento
				// Este esquema permite que se puedan usar hebras más fácilmente.
				Processor procesador = new Processor( socketServicio );
				procesador.start();
				
			} while ( ! salir );
			
		} catch ( IOException e ) {
			System.err.println( "Error trying to listen in port " + port );
		}
		To summarize my personality, I find great fulfillment in helping others, especially if it's my code that's helping. And if
		Where there's lack of knowledge, there's a chan
		I really enjoy being able to learn, helping others and watching me growing every day.
		Being able to grow every day is what really gets me going, and that makes every conversation and every project a delight.

		Code is how we tell our colleagues how we feel about them
		A good code should read like a story, not like a puzzle
		Prefer clear code over clever code
		Programs must be written for people to read, and only inciddentally for machines to execute. Abelson and Sussman
		Simplicity, brevity, clarity, humanity.
		Code for humans to read a story, not for machines to execute a puzzle.
		comunicacion@ongranada.com
		*/