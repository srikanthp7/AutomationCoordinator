package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.interfaces.NodeValueChanged;
import java.util.List;
import java.util.Map;

public class WorkerBucket
  extends UnitOfWork
{
  private static final long serialVersionUID = 1L;
  protected final List<UnitOfWork> works;
  private boolean reRunWholeSet = false;
  
  public WorkerBucket(List<UnitOfWork> works)
  {
    this.works = works;
  }
  
  public WorkerBucket(List<UnitOfWork> works, boolean reRunWholeSet)
  {
    if (reRunWholeSet) {
      for (UnitOfWork work : works) {
        work.setMaxReRunCount(1);
      }
    }
    this.works = works;
    this.reRunWholeSet = reRunWholeSet;
  }
  
  public UnitOfWork getWork()
  {
    if ((getExecutionStatus().equals(ExecutionStatus.FAILED)) || (getExecutionStatus().equals(ExecutionStatus.PASSED))) {
      return null;
    }
    for (UnitOfWork work : this.works)
    {
      if (work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN)) {
        return work.getWork();
      }
      if (work.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS)) {
        return work.getWork();
      }
    }
    return null;
  }
  
  public List<UnitOfWork> getAllWorks()
  {
    return this.works;
  }
  
  public void addWork(UnitOfWork work)
  {
    this.works.add(work);
  }
  
  public void evaluateState()
  {
    for (UnitOfWork work : this.works) {
      work.evaluateState();
    }
    boolean atLeastOneIsNotRun = false;
    boolean atLeastOneIsInProgress = false;
    boolean atLeastOneIsFailed = false;
    for (UnitOfWork work : this.works)
    {
      if (work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN)) {
        atLeastOneIsNotRun = true;
      }
      if (work.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS))
      {
        atLeastOneIsInProgress = true;
        break;
      }
      if (work.getExecutionStatus().equals(ExecutionStatus.FAILED))
      {
        atLeastOneIsFailed = true;
        break;
      }
    }
    if (atLeastOneIsFailed) {
      updateStatus(ExecutionStatus.FAILED);
    } else if (atLeastOneIsInProgress) {
      updateStatus(ExecutionStatus.IN_PROGRESS);
    } else if (atLeastOneIsNotRun) {
      updateStatus(ExecutionStatus.NOT_RUN);
    } else {
      updateStatus(ExecutionStatus.PASSED);
    }
    for (NodeValueChanged listener : this.callback.keySet()) {
      listener.notifyChanged(this.callback.get(listener));
    }
  }
  
  public void updateStatus(ExecutionStatus status)
  {
    setExecutionStatus(status);
    if (this.reRunWholeSet)
    {
      if (status.equals(ExecutionStatus.FAILED))
      {
        int runCount = getRunCount();
        runCount++;
        if (runCount <= getMaxReRunCount()) {
          setExecutionStatus(ExecutionStatus.NOT_RUN);
        }
      }
      if ((status.equals(ExecutionStatus.FAILED)) && (getExecutionStatus().equals(ExecutionStatus.NOT_RUN))) {
        for (UnitOfWork work : this.works) {
          if ((work.getExecutionStatus().equals(ExecutionStatus.FAILED)) || (work.getExecutionStatus().equals(ExecutionStatus.PASSED)) || (work.getExecutionStatus().equals(ExecutionStatus.MANUALLY_PASSED))) {
            work.updateStatus(ExecutionStatus.NOT_RUN);
          }
        }
      }
    }
  }
  
  public String toString(String prefix)
  {
    StringBuilder st = new StringBuilder();
    st.append(prefix).append(getClass());
    st.append(" - ").append(getExecutionStatus());
    st.append("\n").append(prefix).append("[\n");
    
    int i = 0;
    for (UnitOfWork work : this.works)
    {
      st.append(prefix).append(++i).append(". ");
      st.append(work.toString(prefix + prefix));
      st.append("\n");
    }
    st.append(prefix + "]");
    return st.toString();
  }
}
