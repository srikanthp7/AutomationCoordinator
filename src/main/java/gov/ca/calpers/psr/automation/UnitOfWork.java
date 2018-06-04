package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.directed.graph.Edge;
import gov.ca.calpers.psr.automation.directed.graph.Node;
import gov.ca.calpers.psr.automation.interfaces.Completable;
import gov.ca.calpers.psr.automation.interfaces.NodeValueChanged;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

/**
 * The Class UnitOfWork.
 */
public class UnitOfWork implements Serializable, Completable{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The max re run count. */
    private int maxReRunCount;    
    
    /** The test result. */
    private TestResult testResult;
    
    /** The auto test. */
    private AutomationTest autoTest;
    
    /** The test set. */
    private AutomationTestSet testSet;
    
    /** The server config. */
    private ALMServerConfig serverConfig;
    
    /** The node. */
    private Node node;    
    
    /** The is complete. */
    private boolean isComplete = false;
    
    /** The Constant log. */
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(UnitOfWork.class);
    // A hack to callback to tell the tree to rerender once the value of the
    /** The callback. */
    // nodes changed.
    protected Map<NodeValueChanged, Object> callback = new HashMap<NodeValueChanged, Object>();

    /**
     * Instantiates a new unit of work.
     */
    public UnitOfWork() {
        // TODO do we really need to reload the data from the database?
        // scenarioId = ++scenarioIdCounter;
        //testResult = new TestResult();        
    }
    
    /**
     * Instantiates a new unit of work.
     *
     * @param node the node
     * @param aTestSet the a test set
     * @param serverConf the server conf
     */
    public UnitOfWork(Node node, AutomationTestSet aTestSet, ALMServerConfig serverConf)
    {
    	this.node = node;
    	autoTest = (AutomationTest)node.getUserObject();
    	testSet = aTestSet;
    	setServerConfig(serverConf);    	
    	maxReRunCount = testSet.getRetryLimit();
    	testResult = autoTest.getTestResult();
    	testResult.setTest(autoTest);
    	testResult.setTestSet(testSet);
    }

//    public UnitOfWork(int scenarioId, String testName) {
//        testResult = new TestResult();        
//        //test.setTestName(testName);
//    }

//    public UnitOfWork(int scenarioId, String testName, boolean checkInFileCritical) {
//        testResult = new TestResult(autoTest);        
//        //test.setTestName(testName);
//        this.checkInFileCritical = checkInFileCritical;
//    }

    /**
 * Gets the test name.
 *
 * @return the test name
 */
public String getTestName() {
        if(autoTest != null && autoTest.getTestName() != null)
        {
        	return autoTest.getTestName();
        }
        return null;
    }

    /**
     * Gets the final duration.
     *
     * @return the final duration
     */
    public long getFinalDuration() {
        return testResult.getFinalDuration();
    }

    /**
     * Sets the execution status.
     *
     * @param status the new execution status
     */
    public synchronized void setExecutionStatus(ExecutionStatus status) {
    	updateStatus(status);
//    	testResult.setExecutionStatus(status);
//        if (testResult != null) {
//        	testResult = testResult.update();
//        }
//        if(autoTest != null)
//        {
//        	autoTest.setTestResult(testResult);
//        }

    }

    /**
     * Update status.
     *
     * @param status the status
     */
    public synchronized void updateStatus(ExecutionStatus status) {
    	testResult.setExecutionStatus(status);
    	autoTest.setExecutionStatus(status);
//        if (testResult.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS)) {
//        	testResult.incrementRunCount();
//        	this.incrementRunCount();
//        }
//        if (testResult.getExecutionStatus().equals(ExecutionStatus.FAILED)) {
//            if (testResult.getRunCount() < maxReRunCount) {
//            	testResult.setExecutionStatus(ExecutionStatus.NOT_RUN);
//            	testResult.setStartTime(System.currentTimeMillis());
//            }
//        }
    	
        if (autoTest.getExecutionStatus().equals(ExecutionStatus.FAILED)
                || autoTest.getExecutionStatus().equals(ExecutionStatus.PASSED)) {
        	
    		if(autoTest.getStartTime()!=0)
    		{
    			autoTest.setFinalDuration(System.currentTimeMillis() - autoTest.getStartTime());
    		}else
    		{
    			autoTest.setFinalDuration(0);
    		}
        	
        	isComplete = true;
        	System.out.println(autoTest.getTestName() + " is set to Complete.");
        }else if(autoTest.getExecutionStatus().equals(ExecutionStatus.MANUALLY_PASSED))
        {
        	if(autoTest.getFinalDuration()!=0)
        	{
        		if(autoTest.getStartTime()!=0)
        		{
        			autoTest.setFinalDuration(System.currentTimeMillis() - autoTest.getStartTime());
        		}else
        		{
        			autoTest.setFinalDuration(0);
        		}
        	}
        	isComplete=true;
        	System.out.println(autoTest.getTestName() + " is set to Complete.");
        }else if(autoTest.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
        {
        	autoTest.setStartTime(0);
        	autoTest.setFinalDuration(0);
        	this.setRunCount(0);
        	isComplete = true;
        	System.out.println(autoTest.getTestName() + " is set to Complete.");
        }else if(autoTest.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
        {
        	autoTest.setFinalDuration(0);
        	autoTest.setStartTime(0);        	
        	this.setRunCount(0);
        	isComplete = false;
        	System.out.println(autoTest.getTestName() + " is set to Incomplete.");
		}else
        {
        	isComplete = false;
        	System.out.println(autoTest.getTestName() + " is set to Incomplete.");
        }
        
        try{
        	TestResult result = autoTest.getTestResult().save();
        	autoTest.getTestResult().setId(result.getId());
        }catch(Exception e)
        {
        	for(StackTraceElement stackElem : e.getStackTrace())
    		{
    			log.error(stackElem.toString());
    		}
    		e.printStackTrace();
        }
        
        
        for (NodeValueChanged listener : callback.keySet()) {
            listener.notifyChanged(callback.get(listener));
        }
    }

    /**
     * Gets the work.
     *
     * @return the work
     */
    public UnitOfWork getWork() {
    	//Changed from testResult to autoTest
        if (autoTest.getExecutionStatus().equals(ExecutionStatus.NOT_RUN)) {
            return this;
        }
        return null;
    }

    /**
     * Evaluate state.
     */
    public synchronized void evaluateState() {
        for (NodeValueChanged listener : callback.keySet()) {
            listener.notifyChanged(callback.get(listener));
        }
    }

    /**
     * Gets the client.
     *
     * @return the client
     */
    public String getClient() {
        return autoTest.getClient();
    }

    /**
     * Sets the client.
     *
     * @param client the new client
     */
    public void setClient(String client) {
    	autoTest.setClient(client);
        for (NodeValueChanged listener : callback.keySet()) {
            listener.notifyChanged(callback.get(listener));
        }
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return autoTest.getTestResult().getStartTime();
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(long startTime) {
    	autoTest.getTestResult().setStartTime(startTime);
        for (NodeValueChanged listener : callback.keySet()) {
            listener.notifyChanged(callback.get(listener));
        }

    }

    /**
     * Increment run count.
     */
    protected void incrementRunCount() {
    	autoTest.getTestResult().incrementRunCount();
    }
    
    /**
     * Sets the run count.
     *
     * @param runCount the new run count
     */
    public void setRunCount(int runCount)
    {
    	//testResult.setRunCount(runCount);
    	autoTest.setRunCount(runCount);
    }

    /**
     * Gets the run count.
     *
     * @return the run count
     */
    public int getRunCount() {
        return autoTest.getTestResult().getRunCount();
    }

    /**
     * Sets the max re run count.
     *
     * @param value the new max re run count
     */
    public void setMaxReRunCount(int value) {
        this.maxReRunCount = value;
    }

    /**
     * Gets the max re run count.
     *
     * @return the max re run count
     */
    public int getMaxReRunCount() {
        return maxReRunCount;
    }

    /**
     * To string.
     *
     * @param prefix the prefix
     * @return the string
     */
    public String toString(String prefix) {
        StringBuilder st = new StringBuilder();
        st.append(prefix);
        if(autoTest != null && autoTest.getTestName() != null)
        {
        	st.append(autoTest.getTestName());
        	if(autoTest.getExecutionStatus() != null)
        	{
        		st.append(" - ");
        		st.append(autoTest.getExecutionStatus());
        	}
        }        
        return st.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        st.append("<html><font color='");
        switch (testResult.getExecutionStatus()) {
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
        case BLOCKED:
        	st.append("#FEDE14");
        	break;
        case FAILED:
            st.append("#B22222");
            break;
        }
        st.append("'>");
        if (autoTest.getTestResult().getClient() != null) {
            st.append(" - ").append(autoTest.getTestResult().getClient().replace(".calpers.ca.gov", ""));
            st.append(" - ");
            if (autoTest.getTestResult().getFinalDuration() > 0) {
                st.append(autoTest.getTestResult().getFinalDuration() / (1000 * 60)).append(" min(s)");
            } else {
                long duration = System.currentTimeMillis() - autoTest.getTestResult().getStartTime();
                st.append(duration / (1000 * 60)).append(" min(s)");
            }
        }
        st.append("</color></font></html>");
        return st.toString();

    }

    /**
     * Sets the callback.
     *
     * @param callback the callback
     * @param callbackValue the callback value
     */
    public void setCallback(NodeValueChanged callback, Object callbackValue) {
        this.callback.put(callback, callbackValue);
    }

    /**
     * Gets the execution status.
     *
     * @return the execution status
     */
    public ExecutionStatus getExecutionStatus() {
    	if(autoTest == null)
    	{
    		System.out.println("AutoTest is null for work.");
    		log.debug("UnitOfWork.getExecutionStatus: AutoTest is null for work.");
    	}else if(autoTest.getExecutionStatus()==null)
    	{
    		System.out.println("Execution Status is null for test: " + this.getTestName());
    		log.debug("UnitOfWork.getExecutionStatus: Execution Status is null for test: " + this.getTestName());
    	}
    	
    	return autoTest.getExecutionStatus();    	
    }

    /**
     * Gets the execution status html.
     *
     * @return the execution status html
     */
    public String getExecutionStatusHTML() {
        StringBuilder st = new StringBuilder();
        st.append("<html><font color='");
        switch (testResult.getExecutionStatus()) {
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
        case BLOCKED:
        	st.append("#FEDE14");
        	break;
        case FAILED:
            st.append("#B22222");
            break;
        }
        st.append("'>").append(testResult.getExecutionStatus());
        st.append("</color></font></html>");
        return st.toString();
    }

    /**
     * Save test result.
     *
     * @return the test result
     */
    public TestResult saveTestResult() {
        // test = test.save();
    	TestResult result;
    	try{
    		result = testResult.save();
    		testResult.setId(result.getId());
    	}catch (Exception e)
    	{
    		for(StackTraceElement stackElem : e.getStackTrace())
    		{
    			log.error(stackElem.toString());
    		}
    		e.printStackTrace();
    	}
        return testResult;
    }

    /**
     * Gets the test result.
     *
     * @return the test result
     */
    public TestResult getTestResult() {
        return autoTest.getTestResult();
    }

    /**
     * Gets the auto test.
     *
     * @return the autoTest
     */
	public AutomationTest getAutoTest() {
		return autoTest;
	}

	/**
	 * Sets the auto test.
	 *
	 * @param autoTest the autoTest to set
	 */
	public void setAutoTest(AutomationTest autoTest) {
		this.autoTest = autoTest;
	}

	/**
	 * Gets the test set.
	 *
	 * @return the testSet
	 */
	public AutomationTestSet getTestSet() {
		return testSet;
	}

	/**
	 * Sets the test set.
	 *
	 * @param testSet the testSet to set
	 */
	public void setTestSet(AutomationTestSet testSet) {
		this.testSet = testSet;
	}

	/**
	 * Gets the server config.
	 *
	 * @return the serverConfig
	 */
	public ALMServerConfig getServerConfig() {
		return serverConfig;
	}

	/**
	 * Sets the server config.
	 *
	 * @param serverConfig the serverConfig to set
	 */
	public void setServerConfig(ALMServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	/**
	 * Gets the out edges.
	 *
	 * @return the out edges
	 */
	public HashSet<Edge> getOutEdges()
	{
		return node.getOutEdges();
	}
	
	/**
	 * Gets the in edges.
	 *
	 * @return the in edges
	 */
	public HashSet<Edge> getInEdges()
	{
		return node.getInEdges();
	}

	/**
	 * Checks if is complete.
	 *
	 * @return the isCompleted
	 */
	@Override
	public boolean isComplete() {
		return (autoTest.getExecutionStatus().equals(ExecutionStatus.BLOCKED) || autoTest.getExecutionStatus().equals(ExecutionStatus.PASSED) || autoTest.getExecutionStatus().equals(ExecutionStatus.MANUALLY_PASSED) || autoTest.getExecutionStatus().equals(ExecutionStatus.FAILED));
	}


	/**
	 * Update test result.
	 *
	 * @return the test result
	 */
	public TestResult updateTestResult() {
		TestResult result;
    	try{
    		result = testResult.save();
    		testResult.setId(result.getId());
    	}catch (Exception e)
    	{
    		for(StackTraceElement stackElem : e.getStackTrace())
    		{
    			log.error(stackElem.toString());
    		}
    		e.printStackTrace();
    	}
        return testResult;
    }

	/**
	 * Populate test result.
	 *
	 * @param result the result
	 */
	//Deprecated
    public void populateTestResult(TestResult result) {
    	//testResult = result;
    }

}
