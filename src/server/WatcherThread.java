package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * The class receiving the clients connecting to the server.
 * This thread takes care of all the requests other than USE :
 * -Register
 * -Give informations to proxy
 * -Receive a client
 * -Reach the proxy when server is overcharged
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class WatcherThread extends Thread {
	
	/**
	 * The server which the thread is linked to
	 */
	private Server server;
	
	/**
	 * The constructor, call the super constructor and initialize attributes
	 * @param serv The server to be linked to
	 */
	WatcherThread(Server serv){
		super();
		server = serv;
	}
	/**
	 * The run method of the thread, take care of client connection and request
	 */
	@SuppressWarnings("resource")
	public void run(){
		try {
			Socket client;
			while(true){
				client = server.accept();											//Waiting for a client
				synchronized(client){
					BufferedReader in = new BufferedReader(new InputStreamReader(	//The in stream of the client
							client.getInputStream())
					);
					String line = in.readLine();									//Read the first line sent by the client
					String[] command = line.split(" ");								//Split by the space character
					if(command[0].equals("REGISTER")) {								//If the client want to register a service
						if(!server.serviceRegistered(command[1])){					//Check if the service is already registered
							System.out.println("Enregistrement du service : " + command[1]);
							line = in.readLine();									
							String code = "";
							while (!line.equals("END REGISTER")) {					//Loop until all the code is received
								code += line;
								line = in.readLine();
							}
							synchronized(server){
								server.put(command[1],code);						//Put the service on the server
							}
						}else{
							System.out.println("Service "+command[1]+" déjà enregistré.");
						}
					}else if((command[0].equals("PROXY_GETINFO"))){					//If the client is the proxy and it wants informations
						System.out.println("Envoi d'informations au Proxy.");
						DataOutputStream cliOut = new DataOutputStream(client.getOutputStream());	//The out stream of the proxy
						cliOut.flush();
						cliOut.writeInt(server.getNbRequetes());					//Send the charge amount
						cliOut.close();
						
					}else if((command[0].equals("SERVER_GETCLIENT"))){				//If the client is an overcharged server which wants to give a client and request
						System.out.println("Transfert d'un client sur ce serveur");
						if(!server.serviceRegistered(command[1])){					//If the service is not registered, we have to register it
							System.out.println("Enregistrement du service : " + command[1]);
							String code = in.readLine();							//Read the code sent by the other server
							synchronized(server){
								server.put(command[1],code);						//Put the service on the server
							}
						}
						client = server.accept();									//Wait for the new client to connect to this server
						System.out.println("Client accepté.");
						String[] useCommand = {"USE",command[1]};					//Request to get used by the UseThread
						UseThread rt = new UseThread(server,client, useCommand);	//This server will take care of that request
						rt.start();

					}else if(command[0].equals("USE"))								//The request is a use request
					{
						if (server.getNbRequetes() > 4){							//Check if the server is overcharged
							
							Socket socket = new Socket("localhost",3000);			//Connection to the proxy
							System.out.println("Connexion au proxy");
							
							DataOutputStream outO = new DataOutputStream(socket.getOutputStream());	//The out stream of the proxy
							outO.flush();
							outO.writeInt(1);										//Overcharged request
							outO.writeInt(server.getPort());						//Send the server port
							
							try {
								ObjectInputStream inO = new ObjectInputStream(socket.getInputStream());	//The in stream of the proxy
								Object obj1 = inO.readObject();						//Read the proxy's answer
								if(obj1 instanceof InetAddress){					//If the object sent is a InetAddress, another server can take care of the request
									System.out.println("Transfert du client à un autre serveur.");
									InetAddress adr = (InetAddress) obj1;			//The address of the new server
									
									obj1 = inO.readObject();						//The port of the new server 
									Integer port = (Integer) obj1;
									
									socket.close();									//The proxy has given all the informations needed
									
									
									socket = new Socket(adr.getHostAddress(),port);	//Connection to the new server
									System.out.println("Connexion à l'autre serveur");
									PrintWriter out = new PrintWriter(socket.getOutputStream());	//The out stream of the new server
									out.flush();
									out.println("SERVER_GETCLIENT " + command[1] );	//Send a request to receive a client with the name of the service 
									out.println(server.getCode(command[1]));		//The code of the service is sent if ever the other server doesn't have it registered
									out.flush();
									
									System.out.println("Envoi des informations de l'autre serveur au client");
									DataOutputStream cliOut = new DataOutputStream(client.getOutputStream());	//The out stream of the client requesting a use
									cliOut.flush();
									cliOut.writeInt(1);								// 1 means for the client that he has to connect to another server 
									cliOut.writeUTF(socket.getInetAddress().getHostAddress());	//The ip of the new server
									cliOut.writeInt(port);							//The port of the new server
									cliOut.flush();
									
								}else if(obj1 instanceof String){					//If the object sent by proxy is a string, no server found
									DataOutputStream cliOut = new DataOutputStream(client.getOutputStream());
									cliOut.writeInt(0);								//The client doesn't have to change server
									UseThread rt = new UseThread(server,client, command);	//Processing of the use request
									rt.start();
								}
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}else{
							DataOutputStream cliOut = new DataOutputStream(client.getOutputStream());	//The out stream of the client
							cliOut.writeInt(0);										//This server will take care of the request
							UseThread rt = new UseThread(server,client, command);	//Processing of the request
							rt.start();
						}
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
