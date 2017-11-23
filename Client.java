//
// José María Gómez García
// Manuel Herrera Ojea
//

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main( String[] args ) {
		
		byte [] buferEnvio;
		byte [] buferRecepcion = new byte[20000];
		int bytesLeidos = 0;

		Scanner in = new Scanner( System.in );

		String host = "localhost";
		int port = 9898;
		Socket socketServicio = null;

		String teclado = " ";

		try {
			socketServicio = new Socket( host, port );

			InputStream inputStream = socketServicio.getInputStream();
			OutputStream outputStream = socketServicio.getOutputStream();
			
			while ( ! teclado.toUpperCase().contains( "EXIT" ) ) {

				System.out.print( "> " );
				teclado = in.nextLine() + " .";
				buferEnvio = teclado.getBytes();

				outputStream.write(buferEnvio,0,buferEnvio.length);
				outputStream.flush();

				// Read answer
				bytesLeidos = inputStream.read(buferRecepcion);

				teclado = new String( buferRecepcion, 0, bytesLeidos );
				
				System.out.println( teclado );
				
			}

			socketServicio.close();
			
			// Excepciones:
		} catch ( UnknownHostException e ) {
			System.err.println( "Error: Nombre de host no encontrado." );
		} catch ( IOException e ) {
			System.err.println( "Error de entrada/salida al abrir el socket." );
		}
	}
}
