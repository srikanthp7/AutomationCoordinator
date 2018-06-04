package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

public final class AutomationTest
  extends Observable
  implements Comparable<AutomationTest>, Serializable
{
  private static final long serialVersionUID = -3421075261186029330L;
  private long testId;
  private String testName;
  private Date createDate;
  private Date updateDate;
  private String scriptTypeCode;
  private String scriptStatusCode;
  private String autoFunctionalGroupCode;
  private String functionalGroupCode;
  private String distributeFormInd;
  private String queryIndicator;
  private String updatedBy;
  private RollIndicatorEnum rollIndicator;
  private int qcTestId;
  private Set<TestDependency> testDependencies;
  private TestResult testResult;
  private boolean retrievedDependencies = false;
  
  public AutomationTest()
  {
    this.testName = "NO_NAME";
    this.testResult = new TestResult();
    this.testResult.setTest(this);
    setRunCount(0);
  }
  
  public AutomationTest(String scriptName)
  {
    setTestName(scriptName);
  }
  
  public void setTestName(String testName)
  {
    this.testName = testName;
    dataHasChanged();
  }
  
  public String getTestName()
  {
    return this.testName;
  }
  
  public boolean hasCompleted()
  {
    return (this.testResult.getExecutionStatus().equals(ExecutionStatus.PASSED)) || (this.testResult.getExecutionStatus().equals(ExecutionStatus.MANUALLY_PASSED)) || (this.testResult.getExecutionStatus().equals(ExecutionStatus.FAILED)) || (this.testResult.getExecutionStatus().equals(ExecutionStatus.BLOCKED));
  }
  
  public final AutomationTest copy()
  {
    AutomationTest newTest = new AutomationTest();
    newTest.setRollIndicator(this.rollIndicator);
    newTest.setExecutionStatus(getExecutionStatus());
    newTest.setTestName(getTestName().toString());
    newTest.setAutoFunctionalGroupCode(this.autoFunctionalGroupCode);
    newTest.setFunctionalGroupCode(this.functionalGroupCode);
    newTest.setCreateDate(this.createDate);
    newTest.setQueryIndicator(this.queryIndicator);
    newTest.setScriptStatusCode(this.scriptStatusCode);
    newTest.setScriptTypeCode(this.scriptTypeCode);
    newTest.setTestId(this.testId);
    newTest.setUpdateDate(this.updateDate);
    newTest.setUpdatedBy(this.updatedBy);
    newTest.setTestDependencies(this.testDependencies);
    newTest.setTestResult(this.testResult.copy());
    newTest.setQcTestId(this.qcTestId);
    newTest.setTestDependencies(this.testDependencies);
    newTest.setDistributeFormInd(this.distributeFormInd);
    
    return newTest;
  }
  
  public int compareTo(AutomationTest o)
  {
    return getTestName().compareTo(o.getTestName());
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    
    result = 31 * result + this.qcTestId;
    
    result = 31 * result + (int)(this.testId ^ this.testId >>> 32);
    result = 31 * result + (this.testName == null ? 0 : this.testName.hashCode());
    
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AutomationTest)) {
      return false;
    }
    AutomationTest other = (AutomationTest)obj;
    if (this.qcTestId != other.qcTestId) {
      return false;
    }
    if (this.testId != other.testId) {
      return false;
    }
    if (this.testName == null)
    {
      if (other.testName != null) {
        return false;
      }
    }
    else if (!this.testName.equals(other.testName)) {
      return false;
    }
    return true;
  }
  
  public long getTestId()
  {
    return this.testId;
  }
  
  public void setTestId(long id)
  {
    this.testId = id;
  }
  
  public Date getCreateDate()
  {
    return this.createDate;
  }
  
  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }
  
  public Date getUpdateDate()
  {
    return this.updateDate;
  }
  
  public void setUpdateDate(Date updateDate)
  {
    this.updateDate = updateDate;
  }
  
  public String getScriptTypeCode()
  {
    return this.scriptTypeCode;
  }
  
  public void setScriptTypeCode(String scriptTypeCode)
  {
    this.scriptTypeCode = scriptTypeCode;
  }
  
  public String getScriptStatusCode()
  {
    return this.scriptStatusCode;
  }
  
  public void setScriptStatusCode(String scriptStatusCode)
  {
    this.scriptStatusCode = scriptStatusCode;
  }
  
  public String getAutoFunctionalGroupCode()
  {
    return this.autoFunctionalGroupCode;
  }
  
  public void setAutoFunctionalGroupCode(String autoFunctionalGroupCode)
  {
    this.autoFunctionalGroupCode = autoFunctionalGroupCode;
  }
  
  public String getQueryIndicator()
  {
    return this.queryIndicator;
  }
  
  public void setQueryIndicator(String queryIndicator)
  {
    this.queryIndicator = queryIndicator;
  }
  
  public String getUpdatedBy()
  {
    return this.updatedBy;
  }
  
  public void setUpdatedBy(String updatedBy)
  {
    this.updatedBy = updatedBy;
  }
  
  public String getFunctionalGroupCode()
  {
    return this.functionalGroupCode;
  }
  
  public void setFunctionalGroupCode(String functionalGroupCode)
  {
    this.functionalGroupCode = functionalGroupCode;
  }
  
  public int getQcTestId()
  {
    return this.qcTestId;
  }
  
  public void setQcTestId(int qcTestId)
  {
    this.qcTestId = qcTestId;
  }
  
  public ExecutionStatus getExecutionStatus()
  {
    return this.testResult.getExecutionStatus();
  }
  
  public void setExecutionStatus(ExecutionStatus executionStatus)
  {
    this.testResult.setExecutionStatus(executionStatus);
    dataHasChanged();
  }
  
  public int getRunCount()
  {
    return this.testResult.getRunCount();
  }
  
  public void setRunCount(int runCount)
  {
    this.testResult.setRunCount(runCount);
    dataHasChanged();
  }
  
  public Set<TestDependency> getTestDependencies()
  {
    if (!this.retrievedDependencies)
    {
      System.out.println("Getting dependencies with test id: " + this.testId);
      this.testDependencies = TestDependency.getTestDependenciesByChildId(this.testId);
      this.retrievedDependencies = true;
      if (this.testDependencies == null) {
        this.testDependencies = new HashSet();
      }
    }
    return this.testDependencies;
  }
  
  public void setTestDependencies(Set<TestDependency> testDependencies)
  {
    this.testDependencies = testDependencies;
    dataHasChanged();
  }
  
  public RollIndicatorEnum getRollIndicator()
  {
    return this.rollIndicator;
  }
  
  public void setRollIndicator(RollIndicatorEnum rollIndicator)
  {
    this.rollIndicator = rollIndicator;
  }
  
  public TestResult getTestResult()
  {
    return this.testResult;
  }
  
  public void setTestResult(TestResult testResult)
  {
    this.testResult = testResult;
    this.testResult.setTest(this);
    this.testResult.setRunCount(testResult.getRunCount());
    dataHasChanged();
  }
  
  public void addTestDependency(TestDependency aDep)
  {
    this.testDependencies.add(aDep);
    dataHasChanged();
  }
  
  public void removeTestDependency(TestDependency aDep)
  {
    this.testDependencies.remove(aDep);
    dataHasChanged();
  }
  
  public String toString()
  {
    return this.testName;
  }
  
  public String getExecutionStatusHTML()
  {
    StringBuilder st = new StringBuilder();
    st.append("<html><font color='");
    switch (this.testResult.getExecutionStatus())
    {
    case NOT_RUN: 
      st.append("#778899");
      break;
    case IN_PROGRESS: 
      st.append("#0000FF");
      break;
    case PASSED: 
    case MANUALLY_PASSED: 
      st.append("#228B22");
      break;
    case FAILED: 
      st.append("#B22222");
      break;
    case BLOCKED: 
      st.append("#FFA500");
    }
    st.append("'>").append(this.testResult.getExecutionStatus());
    st.append("</color></font></html>");
    return st.toString();
  }
  
  private synchronized void dataHasChanged()
  {
    setChanged();
    synchronized (this)
    {
      notifyObservers();
    }
  }
  
  public long getStartTime()
  {
    return this.testResult.getStartTime();
  }
  
  public void setStartTime(long timeInMillis)
  {
    this.testResult.setStartTime(timeInMillis);
  }
  
  public long getFinalDuration()
  {
    return this.testResult.getFinalDuration();
  }
  
  public void setFinalDuration(long timeInMillis)
  {
    this.testResult.setFinalDuration(timeInMillis);
  }
  
  public void setClient(String clientName)
  {
    this.testResult.setClient(clientName);
    dataHasChanged();
  }
  
  public String getClient()
  {
    return this.testResult.getClient();
  }
  
  public String getDistributeFormInd()
  {
    return this.distributeFormInd;
  }
  
  public void setDistributeFormInd(String distributeFormInd)
  {
    this.distributeFormInd = distributeFormInd;
  }
  
  public static String getDistributeFormIndOfTest(String testScriptName)
  {
    Session session = HibernateUtil.getSesssion();
    AutomationTest dFormindicator = null;
    if (session != null)
    {
      session.setDefaultReadOnly(true);
      
      Transaction tx = session.beginTransaction();
      
      Query query = session.createSQLQuery("SELECT * FROM AUTOMATION_SCRIPT WHERE SCRIPT_NAME =:testName").addEntity(AutomationTest.class);
      
      query.setParameter("testName", testScriptName);
      List<AutomationTest> list = query.list();
      dFormindicator = (AutomationTest)list.get(0);
      
      System.out.println("Query.toString= " + query.toString());
      tx.commit();
      session.close();
    }
    return dFormindicator.getDistributeFormInd();
  }
}
