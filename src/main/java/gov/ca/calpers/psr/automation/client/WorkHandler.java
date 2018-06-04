package gov.ca.calpers.psr.automation.client;

import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.pojo.Instruction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;

/**
 * The Class WorkHandler.
 */
public class WorkHandler implements Runnable {
    
    /** The temp dir. */
    private static String tempDir = System.getProperty("java.io.tmpdir");
    
    /** The tasks killer. */
    private static String tasksKiller = null;
    
    /** The config file template. */
    private static String configFileTemplate = null;
    
    /** The default config file template. */
    private static String defaultConfigFileTemplate = null;
    
    /** The status. */
    private ExecutionStatus status = null;
    
    /** The user name. */
    private String userName;
    
    /** The password. */
    private String password;
    
    /** The instruction. */
    private Instruction instruction;
    
    /** The halt execution. */
    private boolean haltExecution = false;
    
    /** The total run count. */
    private int totalRunCount = 0; 

    /** The lines. */
    private ConcurrentLinkedQueue<String> lines = new ConcurrentLinkedQueue<String>();
    
    private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(WorkHandler.class);
    
    static {
        URL resource = Instruction.class.getResource("/gov/ca/calpers/psr/automation/pojo/taskstokilltemplate");

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
            tasksKiller = fileData.toString();
        } catch (IOException e) {
            // NULL            		
        }


        resource = Instruction.class.getResource("/gov/ca/calpers/psr/automation/pojo/Custom_Environment_default.ini");

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
            defaultConfigFileTemplate = fileData.toString();
        } catch (IOException e) {
        	//Null
        }
    }

    /**
     * Instantiates a new work handler.
     *
     * @param instruction the instruction
     */
    public WorkHandler(Instruction instruction) {
    	this.userName = instruction.getUserName();
        this.password = instruction.getPassword();
        this.instruction = instruction;
        this.totalRunCount = instruction.getRunCount();
        ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Client_Worker_Processing");
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
        // create file to kill all ALM related process first
    	log.debug("Starting WorkHandler Thread.");
    	ExecutionStatus currStatus = null;
    	log.debug("Running killAllProcesses()");
	    killAllProcess();
	
        // write file that we call to execute the test
        String fileContent = instruction.getCommand(userName);
        String fileName = instruction.getTestName().replaceAll(" ", "_");
        File fileToExecute;
        try {
            fileToExecute = File.createTempFile(fileName, ".vbs");
            fileToExecute.deleteOnExit();
            FileWriter writer = new FileWriter(fileToExecute);
            writer.write(fileContent);
            writer.flush();
            writer.close();

            log.debug("Starting process to run vbs script to execute test.");
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "c:\\windows\\syswow64\\cscript.exe " + fileToExecute.getAbsolutePath(),
                    new String(instruction.getPassword()));
            final Process p = pb.start();
            final InputStreamReader inputSR;
            final InputStreamReader errorSR;
            InputStream is = p.getInputStream();
            inputSR = new InputStreamReader(is);
            final BufferedReader bReader = new BufferedReader(inputSR);
            InputStream es = p.getErrorStream();
            errorSR = new InputStreamReader(es);
            final BufferedReader bErrorReader = new BufferedReader(errorSR);
            ExecutorService readerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Script Reader");
                }
            });
            Future<?> outPutReader = readerExecutor.submit(new Runnable() {

                @Override
                public void run() {
                    String line;
                    try {
                        while ((line = bReader.readLine()) != null) {
                            lines.add(line);
                            System.out.println("Output Reader: " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            ExecutorService errorExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Script Error Reader");
                }
            });
            Future<?> errorReader = errorExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    String line;

                    try {
                        while ((line = bErrorReader.readLine()) != null) {
                            lines.add(line);
                            System.out.println("Error Reader: " + line);
                        }
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            
            try {
            	log.debug("Starting wait on thread for input/output queues to finish.");
                p.waitFor();
                log.debug("Ending wait on thread for input/output queues to finish.");
               
                System.out.println("Deleting temp vbs file.");
                fileToExecute.delete();
                System.out.println("Deleting temp directory.");
                deleteTempDir();
            } catch (InterruptedException e) {
            		e.printStackTrace();
            	}	
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	        // figure out the status.	        
	        ExecutionStatus tempStatus = ExecutionStatus.NOT_RUN;
	        log.debug("Output from running VBS file:");
	        int counter = 0;
	        for (String line : lines) {
	        	log.debug(line);
	            String[] vals = line.split(":");	            
	            if (vals.length == 3) {
	                if (vals[2].trim().toUpperCase().equalsIgnoreCase("PASSED")) {
	                	log.debug("Found the status line. Setting script to 'PASSED'.");
	                    tempStatus = ExecutionStatus.PASSED;
	                } else {	                	
	                	log.debug("Found the status line. Setting script to 'FAILED'.");
	                    tempStatus = ExecutionStatus.FAILED;
	                }	                
	            }
	            counter++;
	        }
	        currStatus = tempStatus;
                
        killAllProcess();
        if (currStatus == null) {
            currStatus = ExecutionStatus.NOT_RUN;
        }                
        updateResult(currStatus);
        log.debug("Finished process of running vbs script to execute test.");
   }
   
    /**
     * Helper method to delete the garbage generated by QTP in temp dir.
     */

    private static void deleteTempDir() {
        File tempDirectory = new File(tempDir);
        File[] filesToDelete = tempDirectory.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.matches("^\\{([0-9ABCDEF\\-])*\\}$") || name.matches("^tsr([0-9ABCDEF]){3}\\.tmp$")) {
                    return true;
                }
                return false;
            }
        });
        for (File fileToDelete : filesToDelete) {
            if (fileToDelete.isDirectory()) {
                for (File d : fileToDelete.listFiles()) {
                    try {
                        d.delete();
                    } catch (Throwable e) {
                        // null
                    }
                }
                try {
                    fileToDelete.delete();
                } catch (Throwable e) {
                    // null
                }
            }
        }
    }

    /**
     * Helper method to delete process generated by QTP.
     */
    private void killAllProcess() {
        try {
            final File killerFile = File.createTempFile("AutomationKiller", ".bat");
            killerFile.deleteOnExit();
            FileWriter writer = new FileWriter(killerFile);
            writer.write(tasksKiller);
            writer.flush();
            writer.close();
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", killerFile.getAbsolutePath());

            Process p = pb.start();
            try {
                InputStream is = p.getInputStream();
                final BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
                InputStream es = p.getErrorStream();
                final BufferedReader bErrorReader = new BufferedReader(new InputStreamReader(es));
                ExecutorService errorThread = Executors.newSingleThreadExecutor();
                errorThread.execute(new Runnable() {

                    @Override
                    public void run() {
                        String line;
                        try {
                            while ((line = bErrorReader.readLine()) != null) {
                                System.err.println(line);
                            }
                        } catch (IOException e) {
                            // print the stack trace. Should never occurred.
                            e.printStackTrace();           
                        }
                    }
                });

                ExecutorService outThread = Executors.newSingleThreadExecutor();
                outThread.execute(new Runnable() {

                    @Override
                    public void run() {
                        String line;
                        try {
                            while ((line = bReader.readLine()) != null) {
                                System.err.println(line);
                            }
                        } catch (IOException e) {
                            // print the stack trace. Should never occurred.
                            e.printStackTrace();
                        }
                    }
                });
                log.debug("Starting wait for killAllProcess() to complete.");
                p.waitFor();
                log.debug("Finished wait for killAllProcess() to complete.");
                killerFile.delete();
            } catch (InterruptedException e) {
                // null since we don't care that much
            }

        } catch (IOException e1) {
            // null since we don't care that much
        }
    }

    /**
     * Update result.
     *
     * @param result the result
     */
    private synchronized void updateResult(ExecutionStatus result) {
        status = result;
        this.notifyAll();
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public synchronized ExecutionStatus getStatus() {
        while (status == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return status;
    }
    
    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public synchronized void setStatus(ExecutionStatus status)
    {
    	this.status = status;
    }

	/**
	 * Checks if is halted.
	 *
	 * @return the haltExecution
	 */
	public boolean isHalted() {
		return haltExecution;
	}

	/**
	 * Halt execution.
	 */
	public void haltExecution() {
		if(!haltExecution)
		{
			haltExecution=true;
		}		
	}

	/**
	 * Gets the total run count.
	 *
	 * @return the totalRunCount
	 */
	public int getTotalRunCount() {
		return totalRunCount;
	}

	/**
	 * Sets the total run count.
	 *
	 * @param totalRunCount the totalRunCount to set
	 */
	public void setTotalRunCount(int totalRunCount) {
		this.totalRunCount = totalRunCount;
	}
}
