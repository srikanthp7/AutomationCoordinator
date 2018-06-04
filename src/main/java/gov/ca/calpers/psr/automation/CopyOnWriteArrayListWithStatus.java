/**
 * 
 */
package gov.ca.calpers.psr.automation;

import java.util.concurrent.CopyOnWriteArrayList;

import gov.ca.calpers.psr.automation.interfaces.Completable;

/**
 * The Class CopyOnWriteArrayListWithStatus.
 *
 * @author burban
 * @param <E> the element type
 */
public class CopyOnWriteArrayListWithStatus<E> extends CopyOnWriteArrayList<E> implements Completable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	

	/**
	 * Checks if is complete.
	 *
	 * @return true if all items in the list are "Complete", and false if any item is "Not Complete"
	 */
	@Override
	public boolean isComplete() {
		for(Object obj : this)
		{
			Completable comp = (Completable) obj;
			if(!comp.isComplete())
			{
				return false;
			}
		}
		return true;
	}
}
