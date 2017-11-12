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


public class YodafyServicio extends Thread {

	// Socket para la conexión TCP
	Socket socketServicio = null;
	
	// Ejercicio 2
	PrintWriter outPrinter;
	BufferedReader inReader;


	// Ejercicio 3
	public YodafyServicio( Socket socket_datos ){

		socketServicio = socket_datos;

		try {
			outPrinter = new PrintWriter( socketServicio.getOutputStream(), true );
			inReader = new BufferedReader( new InputStreamReader( socketServicio.getInputStream() ) );
		} catch ( IOException e ){
			System.err.println( this.getName() + " Error: no se pudo obtener un canal para los flujos." );
		}
	}


	// Ejercicio 3
	public void run(){

		String solicitud = "";
		String respuesta = "";

		try {
			solicitud = inReader.readLine();
		} catch ( IOException e ){
			System.err.println( this.getName() + " Error: no se pudo leer el mensaje." );
		}

		respuesta = procesar( solicitud );

		outPrinter.println( respuesta );

		try {
			inReader.close();
			outPrinter.close();
			socketServicio.close();
		} catch ( IOException e ){
			System.err.println( this.getName() + " Error: no se pudo cerrar la conexión." );
		}
	}


	// Ejercicio 3

	static String procesar( String mensaje ){

		return mensaje;
	}
}
