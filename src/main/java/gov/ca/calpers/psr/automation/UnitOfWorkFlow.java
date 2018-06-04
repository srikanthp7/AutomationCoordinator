package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.directed.graph.Edge;
import gov.ca.calpers.psr.automation.directed.graph.EdgeTypeEnum;
import gov.ca.calpers.psr.automation.directed.graph.Node;
import gov.ca.calpers.psr.automation.logger.LoggerPane;


import gov.ca.calpers.psr.automation.server.socket.AutomationCoordinatorServer;
import gov.ca.calpers.psr.automation.server.ui.ServerStatus;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;

/**
 * The Class UnitOfWorkFlow.
 */
public class UnitOfWorkFlow {
    
    /** The root. */
    private UnitOfWork root = null;
    
    /** The test set. */
    private AutomationTestSet testSet = null;
    
    /** The instance. */
    private static UnitOfWorkFlow instance;
    
    /** The server config. */
    private ALMServerConfig serverConfig;    
    
    /** The independent tests. */
    private CopyOnWriteArrayListWithStatus<UnitOfWork> independentTests;
    
    /** The independent pre roll tests. */
    private CopyOnWriteArrayListWithStatus<UnitOfWork> independentPreRollTests;
    
    /** The independent post roll tests. */
    private CopyOnWriteArrayListWithStatus<UnitOfWork> independentPostRollTests;
    
    /** The non roll trees. */
    private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>> nonRollTrees;
    
    /** The pre roll trees. */
    private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>> preRollTrees;
    
    /** The roll trees. */
    private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>> rollTrees;
    
    /** The post roll trees. */
    private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>> postRollTrees;   
    
    /** The all work. */
    private CopyOnWriteArrayList<UnitOfWork> allWork;
    
    /** The work queue. */
    private ConcurrentLinkedQueue<UnitOfWork> workQueue;
    
    /** The override queue. */
    private ConcurrentLinkedQueue<UnitOfWork> overrideQueue;
    
    /** The work added to queue. */
    private CopyOnWriteArrayList<UnitOfWork> workAddedToQueue;
    
    /** The work manager. */
    private WorkManager workManager;
    
    /** The hard stop in effect. */
    private boolean hardStopInEffect = false;
    
    /** The built work lists. */
    private boolean builtWorkLists = false;
    
    /** The server. */
    private AutomationCoordinatorServer server;
    
    /** The log. */
    private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(UnitOfWorkFlow.class);
    
    /**
     * Gets the single instance of UnitOfWorkFlow.
     *
     * @param testSet the test set
     * @param workManager the work manager
     * @param server the server
     * @return single instance of UnitOfWorkFlow
     */
    public static synchronized UnitOfWorkFlow getInstance(AutomationTestSet testSet, WorkManager workManager, AutomationCoordinatorServer server)
    {    	
    	if(instance==null)
    	{
    		instance = new UnitOfWorkFlow(testSet, workManager, server);
    	}
    	return instance;
    }   
    
 	/**
	  * Instantiates a new unit of work flow.
	  *
	  * @param testSet the test set
	  * @param workManager the work manager
	  * @param server the server
	  */
	 private UnitOfWorkFlow(AutomationTestSet testSet, WorkManager workManager, AutomationCoordinatorServer server) {
    	this.setWorkManager(workManager);
    	this.server = server;
		this.testSet = testSet;
    	serverConfig = ALMServerConfig.getServerDetails();
    	workQueue = new ConcurrentLinkedQueue<UnitOfWork>();
    	overrideQueue = new ConcurrentLinkedQueue<UnitOfWork>();
    	workAddedToQueue = new CopyOnWriteArrayList<UnitOfWork>();
    	allWork = new CopyOnWriteArrayList<UnitOfWork>();
    	independentTests = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
    	independentPreRollTests = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
    	independentPostRollTests = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
    	nonRollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();
    	preRollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();
    	rollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();
    	postRollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();   	
    	buildUnitOfWorkLists();    	
    }
	
	/**
	 * Builds the unit of work lists.
	 */
	public void buildUnitOfWorkLists()
	{
		if(!builtWorkLists)
		{
			workQueue = new ConcurrentLinkedQueue<UnitOfWork>();
	    	workAddedToQueue = new CopyOnWriteArrayList<UnitOfWork>();
	    	allWork = new CopyOnWriteArrayList<UnitOfWork>();
	    	independentTests = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
	    	independentPreRollTests = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
	    	independentPostRollTests = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
	    	nonRollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();
	    	preRollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();
	    	rollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>();
	    	postRollTrees = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<UnitOfWork>>(); 
			CopyOnWriteArrayList<UnitOfWork> workList = new CopyOnWriteArrayList<UnitOfWork>();
			DependencyWorkBucket workBucket = workManager.getDependencyWorkBucket(); 
			for(CopyOnWriteArrayListWithStatus<Node> preRollTree: workBucket.getPreRollTreeList())
			{
				CopyOnWriteArrayListWithStatus<UnitOfWork> preRollUnitOfWorkList = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
				for(Node node : preRollTree)
				{
					UnitOfWork work = new UnitOfWork(node, testSet, serverConfig);
					preRollUnitOfWorkList.add(work);
					workList.add(work);
				}
				preRollTrees.add(preRollUnitOfWorkList);
			}
			
			for(CopyOnWriteArrayListWithStatus<Node> rollTree: workBucket.getRollTreeList())
			{
				CopyOnWriteArrayListWithStatus<UnitOfWork> rollUnitOfWorkList = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
				for(Node node : rollTree)
				{
					UnitOfWork work = new UnitOfWork(node, testSet, serverConfig);
					rollUnitOfWorkList.add(work);
					workList.add(work);
				}
				rollTrees.add(rollUnitOfWorkList);
			}
			
			for(CopyOnWriteArrayListWithStatus<Node> postRollTree: workBucket.getPostRollTreeList())
			{
				CopyOnWriteArrayListWithStatus<UnitOfWork> postRollUnitOfWorkList = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
				for(Node node : postRollTree)
				{
					UnitOfWork work = new UnitOfWork(node, testSet, serverConfig);
					postRollUnitOfWorkList.add(work);
					workList.add(work);
				}
				postRollTrees.add(postRollUnitOfWorkList);
			}
			
			for(CopyOnWriteArrayListWithStatus<Node> nonRollTree: workBucket.getNonRollTreeList())
			{
				CopyOnWriteArrayListWithStatus<UnitOfWork> nonRollUnitOfWorkList = new CopyOnWriteArrayListWithStatus<UnitOfWork>();
				for(Node node : nonRollTree)
				{
					UnitOfWork work = new UnitOfWork(node, testSet, serverConfig);
					nonRollUnitOfWorkList.add(work);
					workList.add(work);
				}
				nonRollTrees.add(nonRollUnitOfWorkList);
			}
			
			for(Node test: workBucket.getIndependentPreRollTests())
			{
				UnitOfWork work = new UnitOfWork(test, testSet, serverConfig);
				independentPreRollTests.add(work);
				workList.add(work);
			}
			
			for(Node test: workBucket.getIndependentPostRollTests())
			{
				UnitOfWork work = new UnitOfWork(test, testSet, serverConfig);
				independentPostRollTests.add(work);	
				workList.add(work);
			}
			
			for(AutomationTest test: workManager.getIndependentTestList())
			{
				Node node = new Node(test, test.getTestName());
				UnitOfWork work = new UnitOfWork(node, testSet, serverConfig);
				independentTests.add(work);
				workList.add(work);
			}
			
			UnitOfWork parallelBucket = new ParallelBucket(workList);
			allWork = workList;
	    	root = parallelBucket;
	    	for(UnitOfWork work : workList)
			{
				if(work.getExecutionStatus().equals(ExecutionStatus.FAILED) || work.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
				{
					reportWorkStatus(work, work.getExecutionStatus(), true);
				}
			}
	    	builtWorkLists = true;
		}
	}
    
    /**
     * Sets the test set.
     *
     * @param testSet the new test set
     */
    public void setTestSet(AutomationTestSet testSet)
    {    	
    	this.testSet = testSet;
    }
    
    /**
     * Gets the test set.
     *
     * @return the test set
     */
    public AutomationTestSet getTestSet()
    {
    	return testSet;
    }

    /**
     * Gets the root node.
     *
     * @return the root node
     */
    public UnitOfWork getRootNode() {
        return root;
    }

    /**
     * Gets the work.
     *
     * @param client the client
     * @return the work
     */
    public synchronized UnitOfWork getWork(String clientName) {
    	log.debug("UnitOfWorkFlow: Getting work for " + clientName);
    	UnitOfWork work;
    	if(!overrideQueue.isEmpty())
        {
        	work = overrideQueue.poll();
        }else
        {
        	work = workQueue.poll();
        }    	
    	
    	// if there is no work, we can sit here and wait until work becomes
        // available. Once the worker response with the status for the work,
        // we need to wake up all threads that is waiting to get the work.
        if(work != null)
        {
//        	boolean lookingForWork = true;
//        	while(lookingForWork)
//        	{
        		System.out.println("Work status: " + work.getExecutionStatus());
        		log.debug("UnitOfWorkFlow.getWork: Work Status: " + work.getExecutionStatus());
        		if(work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
        		{
		        	boolean canRunLater = false;
		        	boolean isBlocked = false;        	
		        	for(Edge edge : work.getInEdges())
		        	{
		        		if(!edge.isDisabled())
		        		{
			        		if(edge.getEdgeType().equals(EdgeTypeEnum.MUST_RUN))
			        		{
			        			AutomationTest childTest = (AutomationTest) edge.getChildNode().getUserObject();
			        			for(UnitOfWork someWork : allWork)
			        			{
			        				if(someWork.getAutoTest()==childTest)
			        				{
			        					if(!someWork.isComplete())
			        					{
			        						System.out.println(work.getTestName() + " can not be executed current because parent work has not completed yet. Parent work: " + someWork.getTestName()  + " with status: " + someWork.getExecutionStatus() );
			        						log.debug(work.getTestName() + " can not be executed current because parent work has not completed yet. Parent work: " + someWork.getTestName()  + " with status: " + someWork.getExecutionStatus());
			        						canRunLater = true;
			        						break;
			        					}
			        				}
			        			}
			        			if(canRunLater)
			        			{
			        				break;
			        			}
			        		}else
			        		{
			        			AutomationTest parentTest = (AutomationTest) edge.getChildNode().getUserObject();
			        			for(UnitOfWork someWork : allWork)
			        			{
			        				if(someWork.getAutoTest()==parentTest)
			        				{
			        					if(someWork.getExecutionStatus().equals(ExecutionStatus.FAILED) || someWork.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
			        					{
			        						System.out.println("Work is blocked by: " + someWork.getTestName()  + " with status: " + someWork.getExecutionStatus() );
			        						log.debug("Work is blocked by: " + someWork.getTestName()  + " with status: " + someWork.getExecutionStatus() );
			        						isBlocked = true;
			        						break;
			        					}else if(!someWork.isComplete())
			        					{
			        						System.out.println(work.getTestName() + " can not be executed current because parent work has not completed yet. Parent work: " + someWork.getTestName()  + " with status: " + someWork.getExecutionStatus() );
			        						log.debug(work.getTestName() + " can not be executed current because parent work has not completed yet. Parent work: " + someWork.getTestName()  + " with status: " + someWork.getExecutionStatus());
			        						canRunLater = true;
			        						break;
			        					}
			        				}
			        			}
			        			if(canRunLater)
			        			{
			        				break;
			        			}
			        		}
		        		}
		        	}
		        	if(isBlocked)
		        	{
		        		work.setExecutionStatus(ExecutionStatus.BLOCKED);
		        		reportWorkStatus(work, ExecutionStatus.BLOCKED, true);
		        		work = getWork(clientName);		        		
//		        		lookingForWork=false;
		        		if(work != null)
		        		{
		        			work.setClient(clientName);
		        			workInProgress(work);
		        		}
//		        		work = workQueue.poll();
//		        		if(work==null)
//		        		{
//		        			return work;
//		        		}
		        	}else if(canRunLater)
		        	{
		        		work = getWork(clientName);		        		
//		        		lookingForWork=false;
		        		if(work != null)
		        		{
			        		work.setClient(clientName);
			        		workInProgress(work);
		        		}
//		        		workQueue.poll();
//		        		if(work==null)
//		        		{
//		        			return work;
//		        		}
		        	}else
		        	{
//		        		lookingForWork=false;
		        		work.setClient(clientName);
		        		workInProgress(work);
		        	}
        		}else
        		{
        			System.out.println("**** Warning! Warning! work that was not in status NOT_RUN found in queue.");
        			log.debug("**** Warning! Warning! work that was not in status NOT_RUN found in queue.");
        			work = getWork(clientName);
        			//work = workQueue.poll();
        		}
        	}
        

        return work;
    }
    
    /**
     * Propagate blocked status.
     *
     * @param work the work
     */
    public synchronized void propagateBlockedStatus(UnitOfWork work)
    {
    	for(Edge inEdge : work.getOutEdges())
		{
    		if(!inEdge.isDisabled())
    		{
				AutomationTest parentTest = (AutomationTest) inEdge.getParentNode().getUserObject();
				for(UnitOfWork someWork : allWork)
				{
					if(someWork.getAutoTest() == parentTest)
					{
						if(work.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
						{
							if(inEdge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
							{
								if(someWork.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
								{
									someWork.setExecutionStatus(ExecutionStatus.BLOCKED);
									someWork.getTestResult().setFinalDuration(0);
									someWork.setRunCount(0);
									someWork.getAutoTest().getTestResult().setFinalDuration(0);
									reportWorkStatus(someWork, ExecutionStatus.BLOCKED, true);						
								}
							}
						}else if(work.getExecutionStatus().equals(ExecutionStatus.FAILED))
						{
							if(inEdge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
							{
								if(someWork.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
								{
									someWork.setExecutionStatus(ExecutionStatus.BLOCKED);
									someWork.getTestResult().setFinalDuration(0);
									someWork.setRunCount(0);
									someWork.getAutoTest().getTestResult().setFinalDuration(0);
									reportWorkStatus(someWork, ExecutionStatus.BLOCKED, true);						
								}
							}else
							{
								if(someWork.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
								{
									boolean isBlockedByOtherTest = false;
									for(Edge edge : someWork.getInEdges())
									{							
										AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
										if(edge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
										{
											if(test.getExecutionStatus().equals(ExecutionStatus.BLOCKED) || test.getExecutionStatus().equals(ExecutionStatus.FAILED))
											{
												isBlockedByOtherTest = true;
												break;
											}
										}										
									}
									if(isBlockedByOtherTest)
									{
										break;
									}						
									someWork.getTestResult().setFinalDuration(0);
									someWork.setRunCount(0);
									someWork.getAutoTest().getTestResult().setFinalDuration(0);						
									reportWorkStatus(someWork, ExecutionStatus.NOT_RUN, true);							
								}
							}
						}						
					}
				}
    		}
		}
    }
    
    /**
     * Propagate un blocked status.
     *
     * @param work the work
     */
    public synchronized void propagateUnBlockedStatus(UnitOfWork work)
    {
    	for(Edge inEdge : work.getOutEdges())
		{
			AutomationTest parentTest = (AutomationTest) inEdge.getParentNode().getUserObject();
			for(UnitOfWork someWork : allWork)
			{
				if(someWork.getAutoTest() == parentTest)
				{					
					if(someWork.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
					{
						boolean isBlockedByOtherTest = false;
						for(Edge edge : someWork.getInEdges())
						{							
								
							AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
							if(edge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
							{
								if(test.getExecutionStatus().equals(ExecutionStatus.BLOCKED) || test.getExecutionStatus().equals(ExecutionStatus.FAILED))
								{
									isBlockedByOtherTest = true;
									break;
								}
							}						
						}
						if(isBlockedByOtherTest)
						{
							break;
						}						
						someWork.getTestResult().setFinalDuration(0);
						someWork.setRunCount(0);
						someWork.getAutoTest().getTestResult().setFinalDuration(0);						
						reportWorkStatus(someWork, ExecutionStatus.NOT_RUN, true);						
					}
				}
			}
		}
    }

    /**
     * Gets the work manager.
     *
     * @return the workManager
     */
	public WorkManager getWorkManager() {
		return workManager;
	}

	/**
	 * Sets the work manager.
	 *
	 * @param workManager the workManager to set
	 */
	public void setWorkManager(WorkManager workManager) {
		this.workManager = workManager;
	}

	/**
	 * Gets the all work.
	 *
	 * @return the allWork
	 */
	public CopyOnWriteArrayList<UnitOfWork> getAllWork() {
		return allWork;
	}

	/**
	 * Checks if is hard stop in effect.
	 *
	 * @return the hardStopInEffect
	 */
	public boolean isHardStopInEffect() {
		return hardStopInEffect;
	}


	/**
	 * Work in progress.
	 *
	 * @param work the work
	 */
	private synchronized void workInProgress(UnitOfWork work) {
        work.updateStatus(ExecutionStatus.IN_PROGRESS);
        reportWorkStatus(work,work.getExecutionStatus(), false);
    }
	
	/**
	 * Process work queues.
	 */
	public synchronized void processWorkQueues() {
		Thread processQueues = new Thread(new Runnable() {
			@Override
			public void run() {
				// Start with preroll dependencies				
				while(!isWorkComplete())
				{			
					if(!hasRollFailed())
					{							
						if(isPreRollComplete() && testSet.getHardStopRoll()=='Y')
						{
							System.out.println("Pre-Roll scripts are complete. Hard Stop in effect.");
							hardStopInEffect = true;
							server.setServerStatus(ServerStatus.HARD_STOP);
						}else
						{
							if(server.getServerStatus().equals(ServerStatus.HARD_STOP))
							{
								server.setServerStatus(ServerStatus.EXECUTING_TEST_SET);
							}else if(server.getServerStatus().equals(ServerStatus.EXECUTING_ROLL_FAILED))
							{
								server.setServerStatus(ServerStatus.EXECUTING_TEST_SET);
							}
							hardStopInEffect = false;
							 if (!independentPreRollTests.isComplete()) {
								for (UnitOfWork indPreRollWork : independentPreRollTests) {
									if (!indPreRollWork.isComplete()) {
										if (processUnitOfWork(indPreRollWork, true)) {
											break;
										}
			
									} else {
										if (indPreRollWork.getExecutionStatus().equals(
												ExecutionStatus.FAILED)) {
											for (Edge edge : indPreRollWork.getOutEdges()) {
												if (edge.getEdgeType().equals(
														EdgeTypeEnum.MUST_PASS)) {
													for (UnitOfWork unitWork : independentPreRollTests) {
														if (unitWork.getAutoTest().equals(
																edge
																		.getChildNode()
																		.getUserObject())
																&& unitWork
																		.getExecutionStatus()
																		.equals(ExecutionStatus.NOT_RUN)) {
															unitWork.setExecutionStatus(ExecutionStatus.BLOCKED);
														}
													}
												}
											}
										}
									}
								}
							 }
							if (!preRollTrees.isComplete()) {
								for (CopyOnWriteArrayListWithStatus<UnitOfWork> preRollList : preRollTrees) {
									if (!preRollList.isComplete()) {
										for (UnitOfWork preRollWork : preRollList) {
											if (!preRollWork.isComplete()) {
												if (processUnitOfWork(preRollWork, false)) {
													break;
												}
			
											} else {
												if (preRollWork.getExecutionStatus()
														.equals(ExecutionStatus.FAILED)) {
													for (Edge edge : preRollWork
															.getOutEdges()) {
														if (edge.getEdgeType().equals(
																EdgeTypeEnum.MUST_PASS)) {
															for (UnitOfWork unitWork : preRollList) {
																if (unitWork
																		.getAutoTest()
																		.equals(edge
																				.getChildNode()
																				.getUserObject())
																		&& unitWork
																				.getExecutionStatus()
																				.equals(ExecutionStatus.NOT_RUN)) {
																	unitWork.setExecutionStatus(ExecutionStatus.BLOCKED);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}else if (isPreRollComplete() && !rollTrees.isComplete() && testSet.getHardStopRoll()=='N') {
								for (CopyOnWriteArrayListWithStatus<UnitOfWork> rollList : rollTrees) {
									if (!rollList.isComplete()) {
										for (UnitOfWork rollWork : rollList) {
											if (!rollWork.isComplete()) {
												if (processUnitOfWork(rollWork, false)) {
													break;
												}
			
											} else {
												if (rollWork.getExecutionStatus().equals(
														ExecutionStatus.FAILED)) {
													for (Edge edge : rollWork.getOutEdges()) {
														if (edge.getEdgeType().equals(
																EdgeTypeEnum.MUST_PASS)) {
															for (UnitOfWork unitWork : rollList) {
																if (unitWork
																		.getAutoTest()
																		.equals(edge
																				.getChildNode()
																				.getUserObject())
																		&& unitWork
																				.getExecutionStatus()
																				.equals(ExecutionStatus.NOT_RUN)) {
																	unitWork.setExecutionStatus(ExecutionStatus.BLOCKED);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							} else{
								if (isPreRollComplete() && !postRollTrees.isComplete() && testSet.getHardStopRoll()=='N' && !hasRollFailed()) {
								for (CopyOnWriteArrayListWithStatus<UnitOfWork> postRollList : postRollTrees) {
									if (!postRollList.isComplete()) {
										for (UnitOfWork postRollWork : postRollList) {
											if (!postRollWork.isComplete()) {
												if (processUnitOfWork(postRollWork, false)) {
													break;
												}
			
											} else {
												if (postRollWork.getExecutionStatus()
														.equals(ExecutionStatus.FAILED)) {
													for (Edge edge : postRollWork
															.getOutEdges()) {
														if (edge.getEdgeType().equals(
																EdgeTypeEnum.MUST_PASS)) {
															for (UnitOfWork unitWork : postRollList) {
																if (unitWork
																		.getAutoTest()
																		.equals(edge
																				.getChildNode()
																				.getUserObject())
																		&& unitWork
																				.getExecutionStatus()
																				.equals(ExecutionStatus.NOT_RUN)) {
																	unitWork.setExecutionStatus(ExecutionStatus.BLOCKED);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							} 
								if (isPreRollComplete() && !independentPostRollTests.isComplete() && testSet.getHardStopRoll()=='N' && !hasRollFailed()) {
								for (UnitOfWork indPostRollWork : independentPostRollTests) {
									if (!indPostRollWork.isComplete()) {
										if (processUnitOfWork(indPostRollWork, true)) {
											break;
										}
			
									} else {
										if (indPostRollWork.getExecutionStatus().equals(
												ExecutionStatus.FAILED)) {
											for (Edge edge : indPostRollWork.getOutEdges()) {
												if (edge.getEdgeType().equals(
														EdgeTypeEnum.MUST_PASS)) {
													for (UnitOfWork unitWork : independentPostRollTests) {
														if (unitWork.getAutoTest().equals(
																edge
																		.getChildNode()
																		.getUserObject())
																&& unitWork
																		.getExecutionStatus()
																		.equals(ExecutionStatus.NOT_RUN)) {
															unitWork.setExecutionStatus(ExecutionStatus.BLOCKED);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}else
				{
					//If roll failed, do what? Block all post-roll? Complete the Test Set Execution?
					server.setServerStatus(ServerStatus.EXECUTING_ROLL_FAILED);
				}
					
					if(!nonRollTrees.isComplete())
					{
						for (CopyOnWriteArrayListWithStatus<UnitOfWork> nonRollList : nonRollTrees) {
							if (!nonRollList.isComplete()) {
								for (UnitOfWork nonRollWork : nonRollList) {
									if (!nonRollWork.isComplete()) {
										if (processUnitOfWork(nonRollWork, false)) {
											break;
										}
	
									} else {
										if (nonRollWork.getExecutionStatus()
												.equals(ExecutionStatus.FAILED)) {
											for (Edge edge : nonRollWork
													.getOutEdges()) {
												if (edge.getEdgeType().equals(
														EdgeTypeEnum.MUST_PASS)) {
													for (UnitOfWork unitWork : nonRollList) {
														if (unitWork
																.getAutoTest()
																.equals(edge
																		.getChildNode()
																		.getUserObject())
																&& unitWork
																		.getExecutionStatus()
																		.equals(ExecutionStatus.NOT_RUN)) {
															unitWork.setExecutionStatus(ExecutionStatus.BLOCKED);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}else
					{
						System.out.println("Non-Roll Trees Complete.");
					}
	
					if (!independentTests.isComplete()) {
						for (UnitOfWork indWork : independentTests) {
							if (!indWork.isComplete()) {
								if (processUnitOfWork(indWork, true)) {
									break;
								}
							}
						}
					}else
					{
						System.out.println("Independent Tests Complete.");
					}
					
					try{
						Thread.sleep(10000);
					}catch (InterruptedException e) {
	                    e.printStackTrace();                        
	                }
				}
				LoggerPane.info("Test Set execution has completed.");
				testSet.setExecutionComplete(true);
			}
		});
		System.out.println("Starting to process the Work Queue");
		processQueues.start();

	}
	
	/**
	 * Adds the work to override queue.
	 *
	 * @param work the work
	 */
	public synchronized void addWorkToOverrideQueue(UnitOfWork work)
	{
		overrideQueue.add(work);
	}
	
	/**
	 * Checks if is work complete.
	 *
	 * @return true, if is work complete
	 */
	private boolean isWorkComplete()
	{
		return (preRollTrees.isComplete() && rollTrees.isComplete() && postRollTrees.isComplete() && nonRollTrees.isComplete() && independentTests.isComplete() && independentPreRollTests.isComplete() && independentPostRollTests.isComplete());		
	}
	
	/**
	 * Checks if is pre roll complete.
	 *
	 * @return true, if is pre roll complete
	 */
	private boolean isPreRollComplete()
	{
		return (preRollTrees.isComplete() && independentPreRollTests.isComplete());
	}
	
	/**
	 * Process unit of work.
	 *
	 * @param work the work
	 * @param notATree the not a tree
	 * @return true, if successful
	 */
	private synchronized boolean processUnitOfWork(UnitOfWork work, boolean notATree)
	{
		if(notATree)
		{
			if(work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
			{
				if(!workQueue.contains(work))
				{				
					workQueue.add(work);
					workAddedToQueue.add(work);
					System.out.println("Adding work to queue: " + work.getTestName());
				}else
				{
					System.out.println(work.getTestName() + " already exists in the work queue.");
				}
				return true;
			}
		}else
		{
			if(work.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
			{
				if(!workQueue.contains(work))
				{				
					workQueue.add(work);
					workAddedToQueue.add(work);
					System.out.println("Adding work to queue: " + work.getTestName());
				}else
				{
					System.out.println(work.getTestName() + " already exists in the work queue.");
				}
				return true;
			}else if(work.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS))
			{
				return true;
			}
		}
			
		return false;
	}

    /**
     * Report work status.
     *
     * @param work the work
     * @param status the status
     * @param propagateIfBlocked the propagate if blocked
     */
    public synchronized void reportWorkStatus(UnitOfWork work, ExecutionStatus status, boolean propagateIfBlocked) {
    	System.out.println("Reporting Status on " + work.getTestName() + ": " + status.toString());
    	log.debug("Reporting Status on " + work.getTestName() + ": " + status.toString());
    	for(UnitOfWork someWork :  ((ParallelBucket)root).getAllWorks())
    	{
    		if(someWork.getTestName().equals(work.getTestName()))
    		{ 			
    			someWork.setRunCount(work.getRunCount());
    			someWork.setStartTime(work.getStartTime());    		
    			someWork.getTestResult().setFinalDuration(work.getFinalDuration());    			
    			someWork.updateStatus(status);
    			
    			 if(propagateIfBlocked)
    		      {
    		    	  if(status.equals(ExecutionStatus.BLOCKED))
    		    	  {
    		    		  for(Edge edge : someWork.getOutEdges())
    		    		  {
    		    			  if(edge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
    		    			  {
    		    				  AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
    		    				  for(UnitOfWork moreWork :  ((ParallelBucket)root).getAllWorks())
    		    				  {
    		    					  if(moreWork.getAutoTest().getTestName().equals(test.getTestName()))
    		    					  {
    		    						  log.debug("UnitOfWorkFlow.reportWorkStatus: Propagating BLOCKED status. (1)");
    		    						  propagateBlockedStatus(moreWork); 
    		    					  }
    		    				  }
    		    			  }
    		    		  }    		    		  
    		    	  }else if(status.equals(ExecutionStatus.FAILED))
    		    	  {
    		    		  for(Edge edge : someWork.getOutEdges())
    		    		  {
    		    			  if(edge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
    		    			  {
    		    				  AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
    		    				  for(UnitOfWork moreWork :  ((ParallelBucket)root).getAllWorks())
    		    				  {
    		    					  if(moreWork.getAutoTest().getTestName().equals(test.getTestName()))
    		    					  {
    		    						  log.debug("UnitOfWorkFlow.reportWorkStatus: Propagating BLOCKED status. (2)");
    		    						  propagateBlockedStatus(moreWork); 
    		    					  }
    		    				  }    		    				  
    		    			  }else
    		    			  {
    		    				  AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
    		    				  for(UnitOfWork moreWork :  ((ParallelBucket)root).getAllWorks())
    		    				  {
    		    					  if(moreWork.getAutoTest().getTestName().equals(test.getTestName()))
    		    					  {
    		    						  log.debug("UnitOfWorkFlow.reportWorkStatus: Propagating Un-BLOCKED status. (1)");
    		    						  propagateUnBlockedStatus(moreWork); 
    		    					  }
    		    				  }
    		    			  }
    		    		  }
    		    	  }else if(status.equals(ExecutionStatus.MANUALLY_PASSED))
    		    	  {
    		    		  for(Edge edge : someWork.getOutEdges())
    		    		  {
		    			  	  AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
		    				  for(UnitOfWork moreWork :  ((ParallelBucket)root).getAllWorks())
		    				  {
		    					  if(moreWork.getAutoTest().getTestName().equals(test.getTestName()))
		    					  {
		    						  log.debug("UnitOfWorkFlow.reportWorkStatus: Propagating Un-BLOCKED status. (1)");
		    						  propagateUnBlockedStatus(moreWork); 
		    					  }
		    				  }    		    				  
    		    		  }    		    		  
    		    	  }else if(status.equals(ExecutionStatus.NOT_RUN))
    		    	  {
    		    		  for(Edge edge : someWork.getOutEdges())
    		    		  {
		    			  	  AutomationTest test = (AutomationTest) edge.getChildNode().getUserObject();
		    				  for(UnitOfWork moreWork :  ((ParallelBucket)root).getAllWorks())
		    				  {
		    					  if(moreWork.getAutoTest().getTestName().equals(test.getTestName()))
		    					  {
		    						  log.debug("UnitOfWorkFlow.reportWorkStatus: Propagating Un-BLOCKED status. (1)");
		    						  propagateUnBlockedStatus(moreWork); 
		    					  }
		    				  }    		    				  
    		    		  }
    		    	  }
    		      }
    			 break;
    		}
    	}       

//    	for(AutomationTest test: testSet.getTests())
//    	{
//    		if(test.getTestName().equals(work.getTestName()))
//    		{		
//    			test.setExecutionStatus(work.getExecutionStatus());
//    			test.setStartTime(work.getStartTime());
//    			test.setFinalDuration(work.getFinalDuration());
//    			test.setRunCount(work.getRunCount());
//    			test.setClient(work.getClient());
//    			test.getTestResult().save();
//    		}
//    	}      
      //this.notifyAll();     
    }
    
    public boolean hasRollFailed()
    {
    	for(CopyOnWriteArrayList<UnitOfWork> rollList : rollTrees)
    	{
    		for(UnitOfWork work : rollList)
    		{
    			if(work.getTestResult().getExecutionStatus().equals(ExecutionStatus.FAILED) || work.getTestResult().getExecutionStatus().equals(ExecutionStatus.BLOCKED))
    			{
    				log.debug("Roll Script: " + work.getTestName() + " is failed.  Post-Roll Scripts will not be executed unless this script is MANUALLY_PASSED");
    				return true;
    			}
    		}
    	}
    	return false;
    }

    /**
     * Work updated.
     */
    public synchronized void workUpdated() {
        this.notifyAll();
    }

}
