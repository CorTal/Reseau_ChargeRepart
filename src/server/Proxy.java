package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * Proxy is the class that knows every server existing, its use is 
 * to help overcharged server by asking to server if there is one that's not overcharged,
 * and then make them communicate.
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class Proxy {
	
	/**
	 * The ServerSocket
	 */
	ServerSocket socket;
	
	/**
	 * The list of ip of the different servers
	 */
	private ArrayList<InetAddress> servs;
	
	/**
	 * The list of ports used by the differents servers
	 */
	private ArrayList<Integer> ports;
	
	/**
	 * The class constructor, where everything happens.
	 * Inside an infinite loop, the goal is to receive a
	 * connection from a server, to list the server or to
	 * help it if it is overcharged.
	 * If the server is overcharged, the proxy iterate on its lists,
	 * it connects to each server and then ask for their charges.
	 * If the server is undercharged, the iteration is over, and the
	 * proxy send the ip and the port of the undercharged server to the overcharged server.
	 * If there is no undercharged server, then the initial overcharged server has to take care
	 * of the request.
	 * @throws IOException
	 */
	public Proxy() throws IOException{
		socket = new ServerSocket(3000);													//Server on port 3000.
		System.out.println("Proxy lancé sur port 3000");
		servs = new ArrayList<InetAddress>();												//IPs list.
		ports = new ArrayList<Integer>();													//Ports list.
		while(true){
			Socket serverCli = socket.accept();												//Waiting for a server to connect.
			DataOutputStream helpOut = new DataOutputStream(serverCli.getOutputStream());	//The out stream of the server.
			helpOut.flush();
			DataInputStream helpIn = new DataInputStream(serverCli.getInputStream());		//The in stream of the server.
			int op = helpIn.readInt();														//The type of operation the server wants
			int port = helpIn.readInt();													//The server indicates its port
			switch(op){
				case 0:																		//Registrating server on proxy.
					servs.add(serverCli.getInetAddress());									//The ip of the server.
					ports.add(port);														//The port of the server.
					break;
				case 1:
					boolean found = false;													//Undercharged server found
					int i = 0;
					while(i < servs.size() && !found){										//Entire list iterated or undercharged server found
						if(!(servs.get(i).equals(serverCli.getInetAddress())) || (ports.get(i) != port)){	//We won't ask the undercharged server
							System.out.println("Asking to Server : " + servs.get(i).getHostAddress() + "|" + ports.get(i));
							Socket toServ = new Socket(servs.get(i),ports.get(i));			//The connection to the checked server
							PrintWriter out = new PrintWriter(toServ.getOutputStream());	//Out stream of the checked server
							out.flush();
							out.println("PROXY_GETINFO");									//The proxy indicates he want to get infos
							out.flush();
							DataInputStream toServIn = new DataInputStream(toServ.getInputStream());	//In stream of the checked server
							
							int nbrRe = toServIn.readInt();									//The charge of the checked server
							
							toServ.close();													//Close the connection to the checked server
							
							if(nbrRe < 4){													//Check the server state
								ObjectOutputStream objhelpOut = new ObjectOutputStream(serverCli.getOutputStream());	//The out stream of the server asking for help
								objhelpOut.writeObject((Object)servs.get(i));				//The ip of the undercharged server
								objhelpOut.writeObject((Object)ports.get(i));				//The port of the undercharged server
								found = true;												//The proxy have found a new server
							}
							i++;
						}
					}
					if(!found){																//If no server have been found
						ObjectOutputStream objhelpOut = new ObjectOutputStream(serverCli.getOutputStream());	//The outstream of the server asking for help
						objhelpOut.writeObject(new String("NO"));							//No server found
					}
					break;
			}
		}
	}
	
	/**
	 * The main method launching the proxy
	 * @param args Empty
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		try {
			Proxy pr = new Proxy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
