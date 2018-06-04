package gov.ca.calpers.psr.automation.command;

import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.pojo.Instruction;

import java.io.Serializable;

/**
 * The Class WorkResult.
 */
public class WorkResult implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The work. */
    private Instruction work;
    
    /**
     * Instantiates a new work result.
     */
    public WorkResult()
    {
    	
    }

    /**
     * Gets the work.
     *
     * @return the work
     */
    public Instruction getWork() {
        return work;
    }

    /**
     * Sets the work.
     *
     * @param work the new work
     */
    public void setWork(Instruction work) {
        this.work = work;       
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public ExecutionStatus getStatus() {
        return work.getStatus();
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(ExecutionStatus status) {        
        work.setStatus(status);        
    }

	/**
	 * Gets the failed retry count.
	 *
	 * @return the failedRetryCount
	 */
	public int getFailedRetryCount() {
		return work.getRunCount();
	}

	/**
	 * Sets the failed retry count.
	 *
	 * @param failedRetryCount the failedRetryCount to set
	 */
	public void setFailedRetryCount(int failedRetryCount) {		
		work.setRunCount(failedRetryCount);
	}

}
