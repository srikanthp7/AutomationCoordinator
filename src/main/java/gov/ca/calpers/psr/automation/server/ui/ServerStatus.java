/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

/**
 * The Enum ServerStatus.
 *
 * @author burban
 */
public enum ServerStatus{
	
	/** The idle. */
	IDLE, 
	/** The shut down. */
	SHUT_DOWN, 
	/** The executing test set. */
	EXECUTING_TEST_SET, 
	/** The paused. */
	PAUSED, 
	/** The hard stop. */
	HARD_STOP, 
	/** The finished. */
	FINISHED, 
	/** The stopped. */
	STOPPED,	
	EXECUTING_ROLL_FAILED	
}
