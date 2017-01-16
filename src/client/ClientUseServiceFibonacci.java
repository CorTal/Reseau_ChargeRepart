package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import common.Parameters;

/**
 * 
 * The class used for a client to use the Fibonacci service on a server.
 * The client send the parameters used by Fibonacci, and then receive the result
 * and print it.
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class ClientUseServiceFibonacci {
	
	/**
	 * The out Stream to write to the server.
	 */
	PrintWriter out;
	
	/**
	 * The socket that will connect to the wanted server
	 */
	Socket socket;
	
	/**
	 * Constructor of the class.
	 * Initialize the connection to the server and the out stream.
	 * @param ip the ip of the server
	 * @param port the port of the server
	 * @throws UnknownHostException if the host cannot be reached
	 * @throws IOException if the out stream can't be created
	 */
	public ClientUseServiceFibonacci(String ip,int port) throws UnknownHostException, IOException {
		socket = new Socket(ip, port);
		out = new PrintWriter(socket.getOutputStream());
	}
	
	
	/**
	 * The method called to use the service on the server and get the result back.
	 * You first send your USE request on the server, then it answers you if
	 * the server is has to much requests or not, if it doesn't, it will be the server
	 * that will send back the result, if it does, the client will receive from the server
	 * the informations of another server to connect to to get the result.
	 *  
	 * @param fibo the integer we want to know the fibonacci value
	 * @throws Exception
	 */
	public void useService(int fibo) throws Exception {
		Parameters params = new Parameters(1);  											//The object that will send the int fibo the the server.
		params.set(0, new Integer(fibo));													//The integer in the parameters.
		
		System.out.println("Appel du service Fibonacci pour calculer Fibonacci("+fibo+")");	
		
		out.println("USE Fibonacci");														//A USE request is sent.
		out.flush();																		//Stream flush.
		DataInputStream dOut = new DataInputStream(socket.getInputStream());				//In stream to receive server response.
		
		//The server send an int back, so we know its state
		if(dOut.readInt() == 1){															//If 1 is returned, the server is overcharged.
			String adr = dOut.readUTF();													//The ip of the new server.
			int port = dOut.readInt();														//The port of the new server.
			socket = new Socket(adr,port);													//Connection to the new server.
		}
		ObjectOutputStream obj_stream = new ObjectOutputStream(socket.getOutputStream());	//An out object stream to send parameters.
		obj_stream.writeObject(params);														//Object sent.
		
		
		ObjectInputStream o = new ObjectInputStream(socket.getInputStream());				//An in object stream to receive the result
		Object p = o.readObject();															//The object is received.
		
		
		System.out.println("Fib("+fibo+")=" + p);											//Result printed.

		socket.close();																		//Connection closed
	}
	
	/**
	 * The main method.
	 * Create the object and send the use request
	 * @param args 0 IP 1 Port 2 Fibonacci int
	 */
	public static void main(String[] args) {
		try {
		    
			ClientUseServiceFibonacci cu1 = new ClientUseServiceFibonacci(args[0],Integer.parseInt(args[1]));	//Object created with ip and port.
			cu1.useService(Integer.parseInt(args[2]));															//Use request sent with fibonnaci int
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
