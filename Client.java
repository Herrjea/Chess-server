//
// YodafyServidorIterativo
// (CC) jjramos, 2012
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
		byte [] buferRecepcion = new byte[256];
		int bytesLeidos = 0;

		Scanner in = new Scanner( System.in );

		String host = "localhost";
		int port = 8989;
		Socket socketServicio = null;

		String teclado = " ";

		try {
			socketServicio = new Socket( host, port );

			InputStream inputStream = socketServicio.getInputStream();
			OutputStream outputStream = socketServicio.getOutputStream();
			
			while (teclado.toUpperCase() != "EXIT") {

				if (teclado.toUpperCase() != "EXIT") System.out.print("holaaaa");

				System.out.print( "> " );
				teclado = in.nextLine();
				buferEnvio = teclado.getBytes();

				outputStream.write(buferEnvio,0,buferEnvio.length);
				outputStream.flush();

				// Read answer
				bytesLeidos = inputStream.read(buferRecepcion);
				
				for(int i=0;i<bytesLeidos;i++){
					System.out.print((char)buferRecepcion[i]);
				}
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
