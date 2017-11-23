import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


//
// José María Gómez García
// Manuel Herrera Ojea
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


