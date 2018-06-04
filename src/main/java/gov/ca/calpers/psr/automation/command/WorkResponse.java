package gov.ca.calpers.psr.automation.command;

import gov.ca.calpers.psr.automation.pojo.Instruction;

import java.io.Serializable;

/**
 * The Class WorkResponse.
 */
public class WorkResponse implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The work. */
    // work to be process
    private Instruction work;

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

}
