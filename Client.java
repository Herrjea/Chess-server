//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main( String[] args ) {
		
		String buferEnvio;
		String buferRecepcion;

		Scanner in = new Scanner( System.in );

		String host = "localhost";
		int port = 8989;
		Socket socketServicio = null;
		
		buferEnvio = "";
		buferRecepcion = "";

		try {
			socketServicio = new Socket( host, port );

			BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
			
			while (buferEnvio.toUpperCase() != "EXIT") {

				System.out.print( "> " );
				buferEnvio = in.nextLine() + "\n";

				outPrinter.print(buferEnvio);
				outPrinter.flush();

				// Leer la respuesta del servidor
				buferRecepcion = inReader.readLine();
				System.out.println( buferRecepcion );
			}

			//socketServicio.close();
			
//System.out.println( "ccc" );
			// Excepciones:
		} catch ( UnknownHostException e ) {
			System.err.println( "Error: Nombre de host no encontrado." );
		} catch ( IOException e ) {
			System.err.println( "Error de entrada/salida al abrir el socket." );
		}
	}
}
