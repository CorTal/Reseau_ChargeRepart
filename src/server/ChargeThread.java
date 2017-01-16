package server;

/**
 * The thread used by servers to calculate their charges.
 * Basically, the thread will sleep for 60 seconds, before it will
 * increment the charge, after it will decrement it, so we know in 
 * real time the charge of the server.
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class ChargeThread extends Thread {
	
	/**
	 * The server which this thread is linked.
	 */
	private Server server;
	
	/**
	 * Constructor of the class.
	 * Call the Thread constructor and link the thread to server.
	 * @param serv The server which this thread is linked
	 */
	ChargeThread(Server serv){
		super();					//Thread constructor
		server = serv;				//Server attribution
	}
	
	/**
	 * The run method of the thread.
	 * Sleep for 60 seconds, decrement the charge before and increment after.
	 */
	public void run(){
		try {
			server.more();					//Increment
			System.out.println("Charge du serveur : " + server.getNbRequetes());
			sleep(60*1000);					//1 minute sleep
			server.less();					//Decrement
			System.out.println("Charge du serveur : " + server.getNbRequetes());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
