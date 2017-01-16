package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * 
 * ClientRegisterServiceFibonacci is the class representing the Fibonacci service to be used
 * on a server by a client.
 * 
 * @author Théo Voillemin & Corentin Talarmain
 * 
 *
 */
public class ClientRegisterServiceFibonacci {
	
	/**
	 * The out Stream to write to the server.
	 */
	PrintWriter out;
	
	/**
	 * The socket that will connect to the wanted server
	 */
	Socket socket;
	
	/**
	 * The constructor of the class, used to initialize the connection to the server
	 * and to initialize the out stream.
	 * @param ip The IP adress of the wanted server.
	 * @param port the port on which the server is connected.
	 */
	public ClientRegisterServiceFibonacci(String ip,int port) {
		try {
			socket = new Socket(ip,port);						//Connection
			out = new PrintWriter(socket.getOutputStream());	//Out stream
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * The method used to send the service to register on the server.
	 * First the code has to be writted in a String object.
	 * Then it has to be sent to the server to get registered.
	 * 
	 */
	public void register_service() {
		String code = "try {"									//The code to register
			+ " int tab[] = new int[40];"						//Here it is Fibonacci
			+ " tab[0] = 0;"
			+ " tab[1] = 1;"
			+ " int n = (Integer) parameters.get(0);"
			+ " for (int i=2; i<=n; ++i) {"
			+ " 	tab[i] = tab[i-1] + tab[i-2];"
			+ " }"
			+ " result = (Object) tab[n];"
			+ "} catch(Exception e) {"
			+ "   e.printStackTrace();"
			+ "}";
		

		
		out.println("REGISTER Fibonacci");						//Let the server knows the client want to register Fibonacci
		out.println(code);										//Send the code
		out.println("END REGISTER");							//End of code register
		
		//And now we close the connection.
		
		out.close();		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * The code to get executed on call.
	 * Create a new ClientRegisterServiceFibonacci
	 * Then register the service.
	 * @param args Containing the IP and the port for the server.
	 */
	public static void main(String[] args) {
		ClientRegisterServiceFibonacci c = new ClientRegisterServiceFibonacci(args[0],Integer.parseInt(args[1]));	//Creating the ClientRegister
		try {
			c.register_service();								//Register the service
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
