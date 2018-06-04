package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.interfaces.NodeValueChanged;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ParallelBucket.
 */
public class ParallelBucket extends WorkerBucket {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new parallel bucket.
     *
     * @param works the works
     */
    public ParallelBucket(List<UnitOfWork> works) {
        super(works);
        setMaxReRunCount(1);
    }

    /* (non-Javadoc)
     * @see gov.ca.calpers.psr.automation.WorkerBucket#getWork()
     */
    @Override
    public UnitOfWork getWork() {
        // this is for parallel bucket.
        // get next bucket to be processed.
        // we just get the next bucket that needed to be run.
        for (UnitOfWork work : works) {
            UnitOfWork returnVal = null;
            if (work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN)
                    || work.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS)) {
                returnVal = work.getWork();
            }
            if (returnVal != null) {
                return returnVal;
            }

        }
        return null;
    }

    /* (non-Javadoc)
     * @see gov.ca.calpers.psr.automation.WorkerBucket#evaluateState()
     */
    @Override
    public void evaluateState() {
        for (UnitOfWork work : works) {
            work.evaluateState();
        }
        boolean atLeastOneIsNotRun = false;
        boolean atLeastOneIsInProgress = false;
        boolean atLeastOneIsFailed = false;
        for (UnitOfWork work : works) {
            if (work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN)) {
                atLeastOneIsNotRun = true;
            }
            if (work.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS)) {
                atLeastOneIsInProgress = true;
            }
            if (work.getExecutionStatus().equals(ExecutionStatus.FAILED)) {
                atLeastOneIsFailed = true;
            }
        }
        if (atLeastOneIsInProgress) {
            setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        } else if (atLeastOneIsNotRun) {
            setExecutionStatus(ExecutionStatus.NOT_RUN);
        } else if (atLeastOneIsFailed) {
            setExecutionStatus(ExecutionStatus.FAILED);
        } else {
            setExecutionStatus(ExecutionStatus.PASSED);
        }
        for (NodeValueChanged listener : callback.keySet()) {
            listener.notifyChanged(callback.get(listener));
        }
    }

}
