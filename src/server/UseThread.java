package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * 
 * This class is used to take care of a use request and send it to the server.
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class UseThread extends Thread {
	
	/**
	 * The server which this thread is linked to
	 */
	private Server server;
	
	/**
	 * The client to send the response
	 */
	private Socket client;
	
	/**
	 * The request sent by the client
	 */
	private String[] command;
	
	/**
	 * The constructor of the class.
	 * Initialize the attributes
	 * @param serv The server
	 * @param cli The client
	 * @param comm The request
	 */
	UseThread(Server serv, Socket cli, String[] comm){
		super();
		server = serv;
		client = cli;
		command = comm;
	}
	
	/**
	 * The run method of the thread.
	 * Analyze the request to send it to the server
	 */
	public void run(){
		try {
			if(command[0].equals("USE")){								//Check if it's a USE request
				if(server.serviceRegistered(command[1])){				//Check if the wanted service is registered
					ChargeThread cht = new ChargeThread(server);		//Add a charge to the server
					cht.start();
					System.out.println("Utilisation du service : " + command[1]);
					ObjectInputStream o = new ObjectInputStream(client.getInputStream());	//In stream of the client
					Object p = o.readObject();												//Read the parameters
					server.addUseService(command[1], p, client);							//Send the informations to the server
					if(server.getState() == Thread.State.WAITING){							//If the server is waiting, notify it of the new request
						synchronized(server.lock()){
							server.lock().notify();
						}
					}
				}
			}
		} catch ( IOException | ClassNotFoundException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
}
