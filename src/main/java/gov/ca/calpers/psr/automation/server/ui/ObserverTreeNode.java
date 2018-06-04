/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The Class ObserverTreeNode.
 *
 * @author burban
 */
public class ObserverTreeNode extends DefaultMutableTreeNode implements Observer{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new observer tree node.
	 */
	public ObserverTreeNode()
	{
		super();
	}
	
	/**
	 * Instantiates a new observer tree node.
	 *
	 * @param userObject the user object
	 */
	public ObserverTreeNode(Observable userObject)
	{
		super(userObject);
		userObject.addObserver(this);
	}
	
	/**
	 * Instantiates a new observer tree node.
	 *
	 * @param userObject the user object
	 * @param allowsChildren the allows children
	 */
	public ObserverTreeNode(Observable userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);
		userObject.addObserver(this);
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		synchronized(ObserverTreeNode.this)
		{
			ObserverTreeNode.this.notifyAll();
		}
	}

}
