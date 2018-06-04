package gov.ca.calpers.psr.automation.pojo;

import gov.ca.calpers.psr.automation.ALMServerConfig;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.UnitOfWork;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;

/**
 * The Class Instruction.
 */
public class Instruction implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The counter. */
    private static int counter = 0;
    
    /** The id. */
    private final int id;
    
    /** The host id. */
    private String hostId;    
    
    /** The test. */
    private AutomationTest test;
    
    /** The test set. */
    private AutomationTestSet testSet;
    
    /** The test name. */
    private String testName;
    
    /** The status. */
    private ExecutionStatus status;

    
    /** The commands. */
    private String commands;

    /** The end time. */
    private long endTime;
    
    /** The server. */
    private final String server;
    
    /** The domain. */
    private final String domain;
    
    /** The project. */
    private final String project;
 
    /** The server config. */
    private final ALMServerConfig serverConfig;
    
    /** The max failed retries. */
    private final int maxFailedRetries;
    
    /** The template. */
    private static String template = null;
    
    /** The work. */
    private final UnitOfWork work;
    
    /** The log. */
    private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(Instruction.class);
  
    static {
        URL resource = Instruction.class.getResource("/gov/ca/calpers/psr/automation/pojo/vbscripttemplate");

        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(resource.openStream()));
            char[] buf = new char[1024];
            int numRead = 0;
            StringBuilder fileData = new StringBuilder();
            while ((numRead = in.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            in.close();
            template = fileData.toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }

    }

    /**
     * Instantiates a new instruction.
     *
     * @param work the work
     * @param serverConf the server conf
     * @param maxFailedRetries the max failed retries
     */
    public Instruction(UnitOfWork work, ALMServerConfig serverConf, int maxFailedRetries) {
        this.work = work;
    	id = ++counter;
        try {
            hostId = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // null
        }
        test = work.getAutoTest();
        testSet = work.getTestSet();
        serverConfig = serverConf;
        this.maxFailedRetries= maxFailedRetries;
        //allowableDuraionInMinutes = work.getTestResult().getMaxAllowedTimeInMinutes();
        //System.out.println("************************** " + allowableDuraionInMinutes + " **************");
        testName = work.getTestName();
        //startTime = work.getStartTime();
        status = work.getExecutionStatus();
        server = serverConfig.getURL();
        domain = serverConfig.getAlmDomain();
        project = serverConfig.getAlmProject();            
        
    }

    /**
     * Gets the command.
     *
     * @param userName the user name
     * @return the command
     */
    public String getCommand(String userName) {
        String theTestPath = testSet.getAlmPath();
        String theTestName = test.getTestName();
        //System.out.println("Template: " + template);        
        System.out.println("Server: " + server);
        System.out.println("Domain: " + domain);
        System.out.println("Project: " + project);
        System.out.println("Username: " + userName);
        System.out.println("The Test Path: " + theTestPath);
        System.out.println("The Test Name: " + theTestName);
        System.out.println("testName: " + testName);
        
        return String.format(template, server, domain, project, userName, theTestPath, testSet.getTestSetName(), "\"\""
                + testName + "\"\"");
    }

    /**
     * Gets the host id.
     *
     * @return the host id
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Gets the test name.
     *
     * @return the test name
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Sets the test name.
     *
     * @param testName the new test name
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Gets the commands.
     *
     * @return the commands
     */
    public String getCommands() {
        return commands;
    }

    /**
     * Sets the commands.
     *
     * @param commands the new commands
     */
    public void setCommands(String commands) {
        this.commands = commands;
    }
    
    /**
     * Sets the start time.
     *
     * @param time the new start time
     */
    public void setStartTime(long time)
    {    	
    	work.setStartTime(time);    	
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return work.getStartTime();
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime the new end time
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
        work.getAutoTest().getTestResult().setFinalDuration(endTime - this.getStartTime());
        test.getTestResult().setFinalDuration(endTime - this.getStartTime());
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public ExecutionStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(ExecutionStatus status) {
        this.status = status;
        work.setExecutionStatus(status);
    }

    
    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword()
    {
    	return serverConfig.getDecryptedCredential();
    }
    
    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName()
    {
    	return serverConfig.getUsername();
    }

//    public long getAllowableDuraionInMinutes() {
//        return allowableDuraionInMinutes;
//    }

    /**
 * Gets the run count.
 *
 * @return the run count
 */
public int getRunCount()
    {
    	return work.getRunCount();
    }
    
    /**
     * Sets the run count.
     *
     * @param runCount the new run count
     */
    public void setRunCount(int runCount)
    {
    	work.setRunCount(runCount);
    }
    
    /**
     * Gets the work.
     *
     * @return the work
     */
    public UnitOfWork getWork()
    {
    	return work;
    }
    
    /**
     * Gets the max failed retries.
     *
     * @return the maxFailedRetries
     */
	public int getMaxFailedRetries() {
		return maxFailedRetries;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
    public int hashCode() {
        return id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Instruction) {
            if (((Instruction) obj).id == id) {
                return true;
            }
        }
        return false;
    }

}
