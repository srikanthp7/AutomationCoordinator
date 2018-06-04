package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.Session;

/**
 * The Class TestResult.
 */
public class TestResult implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    private long id = 0;

    /** The test set. */
    private AutomationTestSet testSet;    
    
    /** The test. */
    private AutomationTest test;

    /** The execution status. */
    private ExecutionStatus executionStatus = ExecutionStatus.NOT_RUN;

    /** The client. */
    private String client = "";

    /** The start time. */
    private long startTime = 0;

    /** The run count. */
    private int runCount = 0;

    /** The final duration. */
    private long finalDuration = 0;
  
 
    
    /**
     * Instantiates a new test result.
     */
    public TestResult()
    {
    	
    }

    /**
     * Instantiates a new test result.
     *
     * @param test the test
     */
    public TestResult(AutomationTest test)
    {
    	//this.setExecutionStatus(ExecutionStatus.NOT_RUN);
    	this.test = test;    	
    }

    /**
     * Gets the test.
     *
     * @return the test
     */
	public AutomationTest getTest() {
		return test;
	}

	/**
	 * Sets the test.
	 *
	 * @param test the test to set
	 */
	public void setTest(AutomationTest test) {
		this.test = test;	
		//executionStatus = test.getExecutionStatus().getValue();
	}
	
    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the test set.
     *
     * @return the test set
     */
    public AutomationTestSet getTestSet() {
        return testSet;
    }

    /**
     * Sets the test set.
     *
     * @param testSet the new test set
     */
    public void setTestSet(AutomationTestSet testSet) {
        this.testSet = testSet;
    }

    /**
     * Gets the test name.
     *
     * @return the test name
     */
    public String getTestName() {
        return test.getTestName();
    }

//    public void setTestName(String testName) {
//        if (testName != null && !testName.isEmpty()) {
//            this.testName = TestName.getTestFromName(testName);
//        }
//    }

//    public long getMaxAllowedTimeInMinutes() {
//        return testName.get3StdDevDurationInMinute();
//    }

    /**
 * Gets the execution status.
 *
 * @return the execution status
 */
public ExecutionStatus getExecutionStatus() {
        return this.executionStatus;
    }

    /**
     * Sets the execution status.
     *
     * @param executionStatus the new execution status
     */
    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    /**
     * Gets the client.
     *
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets the client.
     *
     * @param client the new client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Gets the run count.
     *
     * @return the run count
     */
    public int getRunCount() {
        return runCount;
    }

    /**
     * Increment run count.
     */
    public void incrementRunCount() {
        ++this.runCount;
    }
    
    /**
     * Sets the run count.
     *
     * @param runCount the new run count
     */
    public void setRunCount(int runCount)
    {
    	this.runCount = runCount;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the final duration.
     *
     * @return the final duration
     */
    public long getFinalDuration() {
        return finalDuration;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
            }

    /**
     * Set final duration is milliseconds. Should be in seconds but oh well.
     *
     * @param finalDuration the new final duration
     */
    public void setFinalDuration(long finalDuration) {
        this.finalDuration = finalDuration;
    }
    

    /**
     * Save.
     *
     * @return the test result
     * @throws Exception the exception
     */
    public synchronized TestResult save() throws Exception{
       
        	if (id == 0) {
    			try {    				
    				return create(this);
    			} catch (Exception e) {
    				throw e;
    			}
    		} else {
    			try {
    				return update(this);
    			} catch (Exception e) {
    				throw e;
    			}
    		}        
       
    }



    /**
     * Copy.
     *
     * @return the test result
     */
    public TestResult copy() {
        TestResult copy = new TestResult(test);        
        copy.setTest(test);
        copy.setTestSet(testSet);
        copy.setId(id);
        copy.setStartTime(startTime);;
        copy.setExecutionStatus(executionStatus);
        copy.setClient(client);
        copy.setRunCount(runCount);
        copy.setFinalDuration(finalDuration);
      
        return copy;
    }

    /**
     * Save test result details into the database.
     * 
     * @param value
     *            The business details.
     * @return The saved business details.
     */
    private static TestResult create(TestResult value) {
        Session session = HibernateUtil.getSesssion();
        session.beginTransaction();
        long id = (Long) session.save(value);
        value.setId(id);
        session.getTransaction().commit();
        return value;
    }

    /**
     * Update business details.
     *
     * @param result - The details to be updated.
     * @return The updated business details.
     * @throws Exception the exception
     */
    private static TestResult update(TestResult result) throws Exception{
    	Session session = HibernateUtil.getSesssion();
		try {
			session.beginTransaction();
			session.update(result);
			session.getTransaction().commit();
			session.close();		
		} catch (Exception e) {
			session.close();
			throw e;
		}

		return result;
    }
}
