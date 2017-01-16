package common;

import java.io.Serializable;

/**
 * 
 * The class representing Parameters sent to  server.
 * @author Théo Voillemin & Corentin Talarmain
 *
 */
public class Parameters implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The tab containing all the parameters.
	 */
	Object tab[];
	
	/**
	 * Constructor.
	 * Initialize the tab with a size of the number of parameters
	 * @param nbr_params the number of parameters
	 */
	public Parameters(int nbr_params) {
		tab = new Object[nbr_params];
	}
	
	/**
	 * Modifier of a parameter.
	 * @param n The index of the parameter
	 * @param obj The new object
	 */
	public void set(int n, Object obj) {
		tab[n]=obj;
	}
	
	/**
	 * Method used to access to the object at the index n.
	 * @param n The index of the object.
	 * @return The object at the specified index.
	 */
	public Object get(int n) {
		return tab[n];
	}
	
	
	
	
}
