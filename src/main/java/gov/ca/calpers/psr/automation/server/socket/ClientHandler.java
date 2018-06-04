package gov.ca.calpers.psr.automation.server.socket;

import gov.ca.calpers.psr.automation.ALMServerConfig;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.UnitOfWork;
import gov.ca.calpers.psr.automation.UnitOfWorkFlow;
import gov.ca.calpers.psr.automation.WorkManager;
import gov.ca.calpers.psr.automation.command.Ack;
import gov.ca.calpers.psr.automation.command.ExecutingServerStatus;
import gov.ca.calpers.psr.automation.command.FinishedServerStatus;
import gov.ca.calpers.psr.automation.command.IdleServerStatus;
import gov.ca.calpers.psr.automation.command.PausedServerStatus;
import gov.ca.calpers.psr.automation.command.ServerStatusRequest;
import gov.ca.calpers.psr.automation.command.StatusUpdate;
import gov.ca.calpers.psr.automation.command.StoppedServerStatus;
import gov.ca.calpers.psr.automation.command.WorkRequest;
import gov.ca.calpers.psr.automation.command.WorkResponse;
import gov.ca.calpers.psr.automation.command.WorkResult;
import gov.ca.calpers.psr.automation.logger.LoggerPane;
import gov.ca.calpers.psr.automation.pojo.Instruction;
import gov.ca.calpers.psr.automation.server.ui.ServerStatus;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;

/**
 * The Class ClientHandler.
 */
public class ClientHandler extends Observable implements Runnable, Comparable<ClientHandler> {
    
    /** The socket. */
    private final Socket socket;
    
    /** The work. */
    private UnitOfWork work = null;
    
    /** The server status. */
    private ServerStatus serverStatus;
    
    /** The work flow. */
    private UnitOfWorkFlow workFlow;    
    
    /** The test set. */
    private AutomationTestSet testSet;
    
    /** The server config. */
    private ALMServerConfig serverConfig;
    
    /** The is shutdown. */
    private boolean isShutdown = false;
    
    /** The is finished. */
    private boolean isFinished = false;
    
    /** The name. */
    private String name;
    
    /** The id. */
    private long id;
    
    /** The work manager. */
    private WorkManager workManager;
    
    /** The server. */
    private AutomationCoordinatorServer server;
    
    /** The log. */
    private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(ClientHandler.class);
    
    
    /**
     * Instantiates a new client handler.
     *
     * @param theSocket the the socket
     * @param status the status
     * @param testSet the test set
     * @param serverConf the server conf
     * @param clientID the client id
     * @param workManager the work manager
     * @param server the server
     */
    public ClientHandler(Socket theSocket, ServerStatus status, AutomationTestSet testSet, ALMServerConfig serverConf, long clientID, WorkManager workManager, AutomationCoordinatorServer server) {    	
    	this.server = server;
    	serverStatus=status;
        this.socket = theSocket;
        serverConfig = serverConf;
        id=clientID;
        this.testSet = testSet;
        this.workManager = workManager;
        //workFlow = UnitOfWorkFlow.getInstance(testSet, env, workManager);
        String fullHostName = socket.getInetAddress().getHostName();
        String[] vals = fullHostName.split("\\.");
        name= vals[0].toUpperCase();
        LoggerPane.info("Creating new handler for " + name + ".");
        log.info("Creating new handler for " + name + ".");
        
        ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(name + "_Monitor");                
                return thread;
            }
        });
        dispatcherExecutor.execute(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
    	
        try {
        	LoggerPane.debug("Monitor socket connection from " + name + ".");
        	log.debug("Monitor socket connection from " + name + ".");
        	ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
        	outStream.flush();
        	ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        	boolean gotRequestWhileIdle = false;        	
        	boolean gotRequestWhileFinished = false;
        	boolean gotRequestWhilePaused = false;
        	boolean gotRequestWhileStopped = false;
        	boolean gotRequestWhileExecuting = false;
        	boolean displayedServerStatusRequestReceived = false;
        	boolean clientIsMisbehaving = false;
            while (!clientIsMisbehaving) {
            	if(!serverStatus.equals(ServerStatus.FINISHED))
            	{            		
            		isFinished=true;
            	}else
            	{
            		isFinished = false;
            	}
            		log.debug("Waiting for request from client: " + this.name);
            		Object object = inputStream.readObject();                //inputStream.close();               
	                if(object instanceof ServerStatusRequest)
	                {
	                	if(!displayedServerStatusRequestReceived)
	                	{
	                		LoggerPane.info("Received a ServerStatusRequest from " + name + ".");
	                		log.info("Received a ServerStatusRequest from " + name + ".");
	                		//displayedServerStatusRequestReceived=true;	                		
	                	}
	                	if(serverStatus.equals(ServerStatus.IDLE))
	                	{                		
	                		IdleServerStatus idleStatus= new IdleServerStatus();
	                    	if(!gotRequestWhileIdle)
	                    	{
	                    		LoggerPane.info("Sending Idle Status to " + name + ".");
	                    		log.info("Sending Idle Status to " + name + ".");
	                    	}
	                    	gotRequestWhileIdle=true;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(idleStatus);
	                        outStream.flush(); 
	                	}else if(serverStatus.equals(ServerStatus.EXECUTING_TEST_SET))
	                	{
	                		ExecutingServerStatus execStatus= new ExecutingServerStatus();
	                    	if(!gotRequestWhileExecuting)
	                    	{
	                    		LoggerPane.info("Sending Executing Status to " + name + ".");
	                    		log.info("Sending Executing Status to " + name + ".");
	                    	}	                    	
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=true;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(execStatus);
	                        outStream.flush();
	                	}else if(serverStatus.equals(ServerStatus.EXECUTING_ROLL_FAILED))
	                	{
	                		ExecutingServerStatus execStatus= new ExecutingServerStatus();
	                    	if(!gotRequestWhileExecuting)
	                    	{
	                    		LoggerPane.info("Sending Executing Status to " + name + ".");
	                    		log.info("Sending Executing Status to " + name + ".");
	                    	}	                    	
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=true;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(execStatus);
	                        outStream.flush();
	                	}else if(serverStatus.equals(ServerStatus.PAUSED))
	                	{
	                		PausedServerStatus pausedStatus= new PausedServerStatus();
	                    	if(!gotRequestWhilePaused)
	                    	{
	                    		LoggerPane.info("Sending Paused Status to " + name + ".");
	                    		log.info("Sending Paused Status to " + name + ".");
	                    	}	                    	
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=true;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(pausedStatus);
	                        outStream.flush();
	                	}else if(serverStatus.equals(ServerStatus.STOPPED))
	                	{
	                		StoppedServerStatus stoppedStatus= new StoppedServerStatus();
	                    	if(!gotRequestWhileStopped)
	                    	{
	                    		LoggerPane.info("Sending Stopped Status to " + name + ".");
	                    		log.info("Sending Paused Stopped to " + name + ".");
	                    	}	                    	
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=true;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(stoppedStatus);
	                        outStream.flush();
	                	}else if(serverStatus.equals(ServerStatus.FINISHED))
	                	{
	                		FinishedServerStatus finishedStatus= new FinishedServerStatus();
	                    	if(!gotRequestWhileFinished)
	                    	{
	                    		LoggerPane.info("Sending Finished Status to " + name + ".");
	                    		log.info("Sending Finished Stopped to " + name + ".");
	                    	}	                    	
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=true;
	                    	outStream.reset();
	                        outStream.writeObject(finishedStatus);
	                        outStream.flush();
	                	}
	                }else if (object instanceof WorkRequest) {
	                    // client request work
	                	if(!gotRequestWhileIdle && !gotRequestWhileFinished && !gotRequestWhileFinished && !gotRequestWhilePaused && !gotRequestWhileStopped)
	                	{
	                		LoggerPane.info("Getting request from " + name + " for new work.");
	                		log.info("Getting request from " + name + " for new work.");
	                	}
	                		
	                	if(serverStatus.equals(ServerStatus.IDLE))
	                    {
	                    	IdleServerStatus idleWork= new IdleServerStatus();
	                    	if(!gotRequestWhileIdle)
	                    	{
	                    		LoggerPane.info("Sending Idle Work Response to " + name + ".");
	                    		log.info("Sending Idle Work Response to " + name + ".");
	                    	}
	                    	gotRequestWhileIdle=true;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(idleWork);
	                        outStream.flush();                        
	                    }else if(serverStatus.equals(ServerStatus.STOPPED))
	                    {
	                    	StoppedServerStatus stoppedWork= new StoppedServerStatus();
	                    	if(!gotRequestWhileStopped)
	                    	{
	                    		LoggerPane.info("Sending Stopped Work Response to " + name + ".");
	                    		log.info("Sending Stopped Work Response to " + name + ".");
	                    	}
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=true;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(stoppedWork);
	                        outStream.flush();         
	                    }else if(serverStatus.equals(ServerStatus.PAUSED))
	                    {
	                    	PausedServerStatus pausedWork= new PausedServerStatus();
	                    	if(!gotRequestWhilePaused)
	                    	{
	                    		LoggerPane.info("Sending Paused Work Response to " + name + ".");
	                    		log.info("Sending Paused Work Response to " + name + ".");
	                    	}
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=true;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=false;
	                    	outStream.reset();
	                        outStream.writeObject(pausedWork);
	                        outStream.flush();         
	                    }
	                    else if(serverStatus.equals(ServerStatus.FINISHED))
	                    {
	                    	FinishedServerStatus finishedWork = new FinishedServerStatus();
	                    	if(!gotRequestWhileFinished)
	                    	{
	                    		LoggerPane.info("Sending Finished Work Response to " + name + ".");
	                    		log.info("Sending Finished Work Response to " + name + ".");
	                    	}
	                    	gotRequestWhileIdle=false;
	                    	gotRequestWhileExecuting=false;
	                    	gotRequestWhilePaused=false;
	                    	gotRequestWhileStopped=false;
	                    	gotRequestWhileFinished=true;
	                    	outStream.reset();
	                    	outStream.writeObject(finishedWork);
	                        outStream.flush();
	                    }
	                    else if(serverStatus.equals(ServerStatus.EXECUTING_TEST_SET) || serverStatus.equals(ServerStatus.HARD_STOP) || serverStatus.equals(ServerStatus.EXECUTING_ROLL_FAILED))                    	
	                    {	                    	
	                    	if(workFlow==null)
	                    	{
	                    		workFlow = UnitOfWorkFlow.getInstance(testSet, workManager, server);
	                    	}	                    	
	                    	
	                    	gotRequestWhileIdle = false;
	                    	gotRequestWhilePaused = false;
                    		gotRequestWhileStopped = false;
	                    	work = workFlow.getWork(this.getName());
		                    if (work == null) {                    				
                				LoggerPane.info("Sending null to " + name + ".");
                				log.info("Sending null to " + name + ".");                  				
                				outStream.reset();
                				outStream.writeObject(null);
                				outStream.flush();                    			
		                    } else {
		                        //work.setClient(name);		                       
		                        LoggerPane.info("Sending " + work.getTestName() + " to "
		                                + name + ".");
		                        log.info("Sending " + work.getTestName() + " to "
		                                + name + ".");
		                        
		                        WorkResponse resp = new WorkResponse();		                    	
		                        
		                        Instruction inst = new Instruction(work, serverConfig, testSet.getRetryLimit());
		                        resp.setWork(inst);
		                        outStream.reset();
		                        outStream.writeObject(resp);
		                        outStream.flush();
		                    }
                    	
	                    }
	                } else if(object instanceof StatusUpdate)
	                {
	                	 // client send back status update.                	
	                    LoggerPane.debug("Getting status update from " + name + ".");
	                    log.debug("Getting status update from " + name + ".");
	                    StatusUpdate status = (StatusUpdate)object;
	                    Instruction inst = status.getWork();
	                    UnitOfWork returnedWork = inst.getWork();
	                    LoggerPane.debug("Unit of Work returned in StatusUpdate is: " + returnedWork.getTestName());
	                    log.debug("Unit of Work returned in StatusUpdate is: " + returnedWork.getTestName());                    
	                    LoggerPane.debug("Unit of Work status returned in UnitOfWork is: " + returnedWork.getExecutionStatus());
	                    LoggerPane.debug("Unit of Work status returned in StatusUpdate is: " + status.getStatus().toString());
	                    log.debug("Unit of Work status returned in UnitOfWork is: " + returnedWork.getAutoTest().getTestResult().getExecutionStatus().toString());
	                    log.debug("Unit of Work status returned in StatusUpdate is: " + status.getStatus().toString());
	                    returnedWork.getAutoTest().getTestResult().setFinalDuration(inst.getEndTime()-inst.getStartTime());
	                    log.debug("Unit of Work status returned in AutomationTest is: " + returnedWork.getAutoTest().getTestResult().getExecutionStatus().toString());
	                    workFlow.reportWorkStatus(returnedWork, status.getStatus(), false);
	                    
	                    if(serverStatus.equals(ServerStatus.PAUSED))
	                    {
	                    	outStream.reset();
	                    	outStream.writeObject(new PausedServerStatus());
	                    	LoggerPane.debug("Sending PausedWorkResponse to " + name + ".");
	                        log.info("Sending PausedWorkResponse to " + name + ".");
	                    }else if(serverStatus.equals(ServerStatus.STOPPED))
	                    {
	                    	outStream.reset();
	                    	outStream.writeObject(new StoppedServerStatus());
	                    	outStream.flush();
	                    	LoggerPane.debug("Sending StoppedWorkResponse to " + name + ".");
	                        log.info("Sending StoppedWorkResponse to " + name + ".");
	                    }else if(serverStatus.equals(ServerStatus.FINISHED))
	                    {
	                    	outStream.reset();
	                    	outStream.writeObject(new FinishedServerStatus());
	                    	outStream.flush();
	                    	LoggerPane.debug("Sending FinishedWorkResponse to " + name + ".");
	                        log.info("Sending FinishedWorkResponse to " + name + ".");
	                    }else
	                    {
	                    	outStream.reset();
	                    	outStream.writeObject(new Ack());
	                    	outStream.flush();
	                    	LoggerPane.debug("Sending Ack to " + name + ".");
		                    log.info("Sending Ack to " + name + ".");
	                    }
	                    
	                    
	                } else if (object instanceof WorkResult) {
	                    // client send back result.                	
	                    LoggerPane.debug("Getting work result from " + name + ".");
	                    log.debug("Getting work result from " + name + ".");
	                    WorkResult result = (WorkResult)object;
	                    Instruction inst = result.getWork();
	                    UnitOfWork returnedWork = inst.getWork();
	                    LoggerPane.debug("Unit of Work returned in WorkResult is: " + returnedWork.getTestName());
	                    log.debug("Unit of Work returned in WorkResult is: " + returnedWork.getTestName());                    
	                    LoggerPane.debug("Unit of Work status returned in UnitOfWork is: " + returnedWork.getExecutionStatus());
	                    LoggerPane.debug("Unit of Work status returned in WorkResult is: " + result.getStatus().toString());
	                    log.debug("Unit of Work status returned in UnitOfWork is: " + returnedWork.getAutoTest().getTestResult().getExecutionStatus().toString());
	                    log.debug("Unit of Work status returned in WorkResult is: " + result.getStatus().toString());
	                    returnedWork.getAutoTest().getTestResult().setFinalDuration(inst.getEndTime()-inst.getStartTime());
	                    log.debug("Unit of Work status returned in AutomationTest is: " + returnedWork.getAutoTest().getTestResult().getExecutionStatus().toString());
	                    // If result is IN_PROGRESS or NOT_RUN, something is wrong with the client so set work back to as if it hasn't run at all yet.
	                    if(result.getStatus().equals(ExecutionStatus.IN_PROGRESS) || result.getStatus().equals(ExecutionStatus.NOT_RUN))
	                    {
	                    	returnedWork.setRunCount(0);	                    	
	                    	result.setFailedRetryCount(0);	                    	
	                    	if(result.getStatus().equals(ExecutionStatus.IN_PROGRESS))	                    	
	                    	{
	                    		returnedWork.setExecutionStatus(ExecutionStatus.NOT_RUN);
	                    		result.setStatus(ExecutionStatus.NOT_RUN);
	                    		clientIsMisbehaving = true;
	                    	}
	                    }
	                    log.debug("Reporting status for Test: " + work.getTestName() + " (Client: " + this.getName() + " )");
	                    workFlow.reportWorkStatus(returnedWork, result.getStatus(), true);
	                    work = null;
	                    outStream.reset();
	                    outStream.writeObject(new Ack());
	                    outStream.flush();
	                    LoggerPane.debug("Sending Ack to " + name + ".");
	                    log.debug("Sending Ack to " + name + ".");	                
	                }
            }
            try {            	
            	socket.shutdownInput();
            	socket.shutdownOutput();
                socket.close();
                setChanged();
                notifyObservers();
                isShutdown = true;
            } catch (IOException e1) {
                // null
            	log.error("Error (IO): ", e1);            	
            }
            
        } catch(EOFException e){        	
        	log.error("Error (EOF): ", e);
        }catch (Exception e) {
            e.printStackTrace();
            if (work != null) {
                LoggerPane.error(name + " disconnected.  Reset test to NOT_RUN.");
                log.error(name + " disconnected.  Reset test to NOT_RUN.", e);
                workFlow.reportWorkStatus(work, ExecutionStatus.NOT_RUN, false);
            }else
            {
            	 LoggerPane.error(name + " disconnected with no work in progress.");
            	 log.error(name + " disconnected with no work in progress.", e);
            }
            try {            	
            	socket.shutdownInput();
            	socket.shutdownOutput();
                socket.close();
                setChanged();
                notifyObservers();
            } catch (IOException e1) {
                // null
            	log.error("Error (IO): ", e1);
            	//Logger.error(e1.getMessage());
            }
        }finally
        {
        	try {            	
            	socket.shutdownInput();
            	socket.shutdownOutput();
                socket.close();
                setChanged();
                notifyObservers();
                isShutdown = true;
            } catch (IOException e1) {
                // null
            	log.error("Error (IO): ", e1);            	
            }
        }
    }
    
    /**
     * Sets the server status.
     *
     * @param status the new server status
     */
    public synchronized void setServerStatus(ServerStatus status)
    {
    	serverStatus = status;
    }
    
    /**
     * Gets the id.
     *
     * @return the id
     */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}	
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name)
	{
		this.name=name;
	}

	/**
	 * Shutdown handler.
	 */
	public synchronized void shutdownHandler()
    {    	
    	try {
    		if(socket != null && !socket.isClosed() && socket.isConnected())
    		{
    			socket.shutdownInput();
            	socket.shutdownOutput();
                socket.close();
                setChanged();
                notifyObservers();
    		}        	
            isShutdown = true;
        } catch (IOException e1) {
            // null
        	log.error("Error (IO): ", e1);        	
        }

    }
	
	/**
	 * Checks if is shutdown.
	 *
	 * @return true, if is shutdown
	 */
	public boolean isShutdown()
	{
		return isShutdown;
	}
	
	/**
	 * Checks if is finished.
	 *
	 * @return true, if is finished
	 */
	public boolean isFinished()
	{
		return isFinished;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ClientHandler o) {
		// TODO Auto-generated method stub
		 if (this.id < o.getId())
		        return -1;
		    if (this.id==o.getId())
		        return 0;

		    return 1;
	}

}
