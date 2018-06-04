package gov.ca.calpers.psr.automation.server.socket;

import gov.ca.calpers.psr.automation.ALMServerConfig;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.WorkManager;
import gov.ca.calpers.psr.automation.logger.LoggerPane;
import gov.ca.calpers.psr.automation.server.ui.ServerStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;

/**
 * The Class AutomationCoordinatorServer.
 */
public class AutomationCoordinatorServer extends Observable implements Observer {
    
    /** The clients. */
    private final LinkedBlockingQueue<Socket> clients = new LinkedBlockingQueue<Socket>();
    
    /** The a live. */
    private boolean aLive = true;
    
    /** The server. */
    private ServerSocket server;
    
    /** The server status. */
    private ServerStatus serverStatus;
    
    /** The client handlers. */
    private CopyOnWriteArrayList<ClientHandler> clientHandlers;
    
    /** The server config. */
    private ALMServerConfig serverConfig;
    
    /** The auto server. */
    private AutomationCoordinatorServer autoServer;
    
    /** The port. */
    private int port = 4000;
    
    /**
     * Gets the client handlers.
     *
     * @return the clientHandlers
     */
	public CopyOnWriteArrayList<ClientHandler> getClientHandlers() {
		return clientHandlers;
	}

	/** The client id. */
	private long clientId = 0;
    
    /** The log. */
    private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(AutomationCoordinatorServer.class);

    /**
     * Instantiates a new automation coordinator server.
     *
     * @param status the status
     * @param testSet the test set
     * @param serverConf the server conf
     * @param workManager the work manager
     */
    public AutomationCoordinatorServer(ServerStatus status, final AutomationTestSet testSet, ALMServerConfig serverConf, final WorkManager workManager) {
    	this.serverStatus=status;
    	autoServer = this;
    	clientHandlers = new CopyOnWriteArrayList<ClientHandler>();
    	serverConfig = serverConf;
        // start a thread to handle client connection one at a time.
        ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Server_Worker_Dispatcher");
                return thread;
            }
        });
        dispatcherExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (aLive) {
                    try {                    	
                        Socket theClientSocket = clients.take();
                        ++clientId;
                        ClientHandler client = new ClientHandler(theClientSocket, serverStatus,testSet,serverConfig, clientId, workManager, autoServer);
                        client.addObserver(autoServer);
                        clientHandlers.add(client);
                        setChanged();
                        notifyObservers();
                    } catch (InterruptedException e) {
                        log.error("Error (Interrupted): ", e);
                    }
                }
            }
        });
        try {
            int socketId = port;
            LoggerPane.info("Starting server on socket " + socketId);
            server = new ServerSocket(socketId);
            ExecutorService serverMonitor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Server_Monitor");
                    return thread;
                }
            });
            serverMonitor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (aLive) {
                            LoggerPane.debug("Server waiting for client to connect...");
                            Socket clientConnected = server.accept();
                            clients.add(clientConnected);
                            LoggerPane.debug("A new client connect.");
                        }

                    } catch (IOException e) {
                        if (e.getMessage().equals("socket closed")) {
                            LoggerPane.info("Server is closed.");
                            log.info("Server is closed.");
                        } else {
                            log.info("Error (Unknown): ", e);
                        }

                    }
                }
            });

        } catch (IOException e) {
        	if(e instanceof SocketException)
        	{
        		JOptionPane.showMessageDialog(null,
        				"Another server is running or another application is using port "+ port + ".\nPlease close any other servers that are running or any application that is currently using port " + port + ".\n\nServer is shutting down now.",
        		        "Error binding to port " + port + ".",
        		        JOptionPane.ERROR_MESSAGE);
        		    System.exit(1);
        	}else
        	{
        		log.error("Error (IO): ", e);
        	}            
        }
    }

    /**
     * Gets the server status.
     *
     * @return the serverStatus
     */
	public synchronized ServerStatus getServerStatus() {
		return serverStatus;
	}

	/**
	 * Sets the server status.
	 *
	 * @param serverStatus the serverStatus to set
	 */
	public synchronized void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
		for(ClientHandler client: clientHandlers)
		{
			client.setServerStatus(serverStatus);
		}
		dataHasChanged();
		
	}

	/**
	 * Shut down server.
	 */
	public void shutDownServer() {
        aLive = false;
        try {
        	for(ClientHandler client: clientHandlers)
        	{
        		client.shutdownHandler();
        	}
            server.close();
        } catch (IOException e) {            
        	log.error("Error (IO): ", e);
        }
    }
	
	/**
	 * All client are disconnected.
	 *
	 * @return true, if successful
	 */
	public boolean allClientAreDisconnected()
	{
		for(ClientHandler client : clientHandlers)
		{
			if(!client.isShutdown())
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * All client are finished.
	 *
	 * @return true, if successful
	 */
	public boolean allClientAreFinished()
	{
		for(ClientHandler client : clientHandlers)
		{
			if(!client.isFinished())
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Data has changed.
	 */
	private synchronized void dataHasChanged() {		
		setChanged();
		notifyObservers();

	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		
		if(o instanceof ClientHandler)
		{
			ClientHandler client = (ClientHandler) o;
			for(ClientHandler currClient : clientHandlers)
			{
				if(currClient.compareTo(client)==0)
				{
					clientHandlers.remove(currClient);
					dataHasChanged();
				}
			}
		}
		
	}
}
