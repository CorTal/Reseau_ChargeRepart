package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * The Server class, used to represent a server on which a client will connect
 * to register and use a service.
 * I considered the server as a thread itself.
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class Server extends Thread{
	
	/**
	 * The ServerSocket.
	 */
	ServerSocket socket;
	
	/**
	 * The map of all the services registered on the server.
	 */
	Map<String,String> mapServices;
	
	/**
	 * The charge of the server.
	 */
	int nbRequetes;
	
	
	/**
	 * The port on which the server is connected.
	 */
	int port;
	
	/**
	 * All the client placed in a queue.
	 */
	PriorityQueue<Socket> requetes;
	
	/**
	 * All the parameters placed in a queue.
	 */
	PriorityQueue<Object> objets;
	
	/**
	 * All the wanted services in a queue
	 */
	PriorityQueue<String> Services;
	
	/**
	 * The thread on the server that will caught the connections
	 */
	WatcherThread wt;
	
	/**
	 * The first object used to lock resources for threads
	 */
	private Object lock = new Object();
	
	/**
	 * The second object used to lock resources for threads
	 */
	private Object lock2 = new Object();
	

	/**
	 * The constructor of the class.
	 * Initialize the attributes, launch the ServerSocket and register on the proxy.
	 * @param pt the port on which to launch the server
	 * @throws Exception
	 */
	public Server(int pt) throws Exception {
		
		nbRequetes = 0;																//No charge for the moment
		socket = new ServerSocket(pt);												//Server launched
		System.out.println("Serveur lancé sur le port : "+pt);
		Socket toProxy = new Socket("localhost",3000);								//Connection to Proxy, the ip and port has to be known by the class
		DataOutputStream intOut = new DataOutputStream(toProxy.getOutputStream());	//The out stream of the proxy
		intOut.writeInt(0);															//Register
		intOut.writeInt(pt);														//Send port
		toProxy.close();															//Close the connection
		port = pt;
		mapServices = new HashMap<String,String>();
		requetes = new PriorityQueue<Socket>();
		objets = new PriorityQueue<Object>();
		Services = new PriorityQueue<String>();
		wt = new WatcherThread(this);
	}
	
	/**
	 * The run method of the thread.
	 * In an infinite loop, wait for a notify from a UseThread indicating that a request has been sent and the server
	 * can process it.
	 */
	public void run(){
		wt.start();																	//Watcher started
		while(true) {
			try {
				synchronized(lock){
					lock.wait();													//Wait for a notify that a service is executable
				}
				while(!empty()){
					synchronized(lock2){
						useService(Services.poll(),objets.poll(),requetes.poll());	//Take the latest request and process it
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
				

	public static Iterable<JavaSourceFromString> getJavaSourceFromString(
			String fileName, String code) {
		return Collections.singletonList(new JavaSourceFromString(fileName,
				code));
	}
	/**
	 * The method using the service the client requested and then answer back.
	 * @param serviceName The service wanted
	 * @param p The parameters
	 * @param cli The client
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	public void useService(String serviceName, Object p, Socket cli) throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String program = "import java.io.*; import common.Parameters; "
		  + "public class " + serviceName + " implements Runnable {"
		  + "   protected Parameters parameters; "
		  + "   protected Object result; "
		  + "   public " + serviceName + "() { } "
		  + "   public void setParameters(Parameters p) { this.parameters = p; }"
		  + "   public void run (){ "
		  + mapServices.get(serviceName)
		  + "   }"
		  + "   public Object getResult() {"
		  + "     return result; "
		  + "   } "
		  + "}";
		
		  Iterable<? extends SimpleJavaFileObject> fileObjects = getJavaSourceFromString(serviceName, program);
		   
		  MyJavaFileManager fileManager = new MyJavaFileManager(compiler
		      .getStandardFileManager(null, null, null));
		 
		  compiler.getTask(null, fileManager, null, null, null, fileObjects)
		    .call();
		  ClassLoader loader = fileManager.getClassLoader();
		 
		  Class<?> clazz = Class.forName(serviceName, true, loader);
		 
		  Object t = clazz.newInstance();
		  Runnable r = Runnable.class.cast(t);
		  Method methods[] = clazz.getMethods();
		  for (int i=0; i<methods.length; ++i) {
		    if (methods[i].getName().equals("setParameters")) {
		      methods[i].invoke(t, new Object[] { p });  
		    }
		  } 
		  r.run();
		  Object result = null;
		  for (int i=0; i<methods.length; ++i) {
		    if (methods[i].getName().equals("getResult")) {
		      result = methods[i].invoke(t, new Object[] { });  
		     
		    }
		  } 
		  ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
		  out.writeObject(result);
		  out.flush();

	}
	

	/**
	 * Main method
	 * Launch the server.
	 * @param args 0 the port of the server
	 */
	public static void main(String[] args) {
		try {
			Server cu1 = new Server(Integer.parseInt(args[0]));
			cu1.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Increase charge
	 */
	public void more() {
		nbRequetes++;
		
	}
	
	/**
	 * Decrease charge
	 */
	public void less(){
		nbRequetes--;
	}
	
	/**
	 * Made to let the WatcherThread to accept a client.
	 * @return The client socket
	 * @throws IOException
	 */
	public Socket accept() throws IOException{
		return socket.accept();
	}
	
	/**
	 * Accessor
	 * @return The charge of the server
	 */
	public int getNbRequetes() {
		return nbRequetes;
	}

	/**
	 * Add a new service
	 * @param string The service name
	 * @param code The code of the service
	 */
	public void put(String string, String code) {
		mapServices.put(string, code);
	}

	/**
	 * Add a new request to the queue of the server
	 * @param string The service requested
	 * @param p The parameters of the service
	 * @param client The client to send a response
	 */
	public void addUseService(String string, Object p, Socket client) {
		Services.add(string);
		objets.add(p);
		requetes.add(client);
		
	}
	
	/**
	 * Check if the server has no request to process
	 * @return true if there is no request to process, false otherwise
	 */
	private boolean empty(){
		return requetes.isEmpty() && Services.isEmpty() && objets.isEmpty();
	}
	
	/**
	 * Return the first lock object
	 * @return The lock object
	 */
	public  Object lock(){
		return lock;
	}
	
	/**
	 * Return the code of the specified service
	 * @param method The name of the service
	 * @return The string containing the code
	 */
	public String getCode(String method){
		return mapServices.get(method);
	}
	
	/**
	 * Check if a service is already registered or not
	 * @param service The service to check
	 * @return True if the service is already registered, false otherwise
	 */
	public boolean serviceRegistered(String service){
		return mapServices.containsKey(service);
	}
	
	
	/**
	 * Return the server port
	 * @return The server port
	 */
	public int getPort(){
		return port;
	}
}
