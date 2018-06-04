/**
 * 
 */
package gov.ca.calpers.psr.automation.directed.graph;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestManager;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.CopyOnWriteArrayListWithStatus;
import gov.ca.calpers.psr.automation.DependencyWorkBucket;
import gov.ca.calpers.psr.automation.RollIndicatorEnum;
import gov.ca.calpers.psr.automation.TestDependency;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;

/**
 * The Class TreeManager.
 *
 * @author burban
 */
public class TreeManager implements Observer{

	/** The test tree map. */
	private ConcurrentHashMap<AutomationTest, DirectedGraph> testTreeMap;
	
	/** The tree list map. */
	private ConcurrentHashMap<DirectedGraph, CopyOnWriteArrayListWithStatus<DirectedGraph>> treeListMap;
	
	/** The tree list. */
	private CopyOnWriteArrayListWithStatus<DirectedGraph> treeList;
	
	/** The work bucket. */
	private DependencyWorkBucket workBucket;
	
	/** The pre roll tree list. */
	private CopyOnWriteArrayListWithStatus<DirectedGraph> preRollTreeList;
	
	/** The roll tree list. */
	private CopyOnWriteArrayListWithStatus<DirectedGraph> rollTreeList;
	
	/** The post roll tree list. */
	private CopyOnWriteArrayListWithStatus<DirectedGraph> postRollTreeList;
	
	/** The non roll tree list. */
	private CopyOnWriteArrayListWithStatus<DirectedGraph> nonRollTreeList;
	
	/** The topological sorted list. */
	private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> topologicalSortedList;
	
	/** The test list. */
	private CopyOnWriteArrayListWithStatus<AutomationTest> testList;
	
	/** The test set. */
	private final AutomationTestSet testSet;
	
	/** The overall digraph of all tests in testset */
	
	private DirectedGraph overallGraph;
	
	private ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustRunTests = new ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>>();
	
	private ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustPassTests = new ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>>();
	
	 /** The log. */
 	private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(TreeManager.class);
	
	/**
	 * Instantiates a new tree manager.
	 *
	 * @param testSet the test set
	 */
	public TreeManager(final AutomationTestSet testSet)
	{
		this.testSet = testSet;
		overallGraph = new DirectedGraph();
		//this.testSet.addObserver(this);
		testTreeMap = new ConcurrentHashMap<AutomationTest, DirectedGraph>();
		treeListMap = new ConcurrentHashMap<DirectedGraph, CopyOnWriteArrayListWithStatus<DirectedGraph>>();
		treeList = new CopyOnWriteArrayListWithStatus<DirectedGraph>();
		testList = new CopyOnWriteArrayListWithStatus<AutomationTest>();
		topologicalSortedList = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();
		workBucket = new DependencyWorkBucket();
		preRollTreeList = new CopyOnWriteArrayListWithStatus<DirectedGraph>();
		rollTreeList = new CopyOnWriteArrayListWithStatus<DirectedGraph>();
		postRollTreeList = new CopyOnWriteArrayListWithStatus<DirectedGraph>();
		nonRollTreeList = new CopyOnWriteArrayListWithStatus<DirectedGraph>();
	}
	
	/**
	 * Adds the tests.
	 *
	 * @param tests the tests
	 * @return the concurrent hash map
	 */
	public boolean addTests(CopyOnWriteArrayList<AutomationTest> tests) throws IllegalArgumentException
	{
		ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingTests = new ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>>();
		int counter = 0;
		boolean hasMissingTests=false;	
		for(AutomationTest test: tests)
		{
			boolean hasMissingDependencyTest = false;
			log.debug("Currently adding test " + test.getTestName() + " to the Test Tree Manager (" + counter + ").");
			System.out.println("Currently adding test " + test.getTestName() + " to the Test Tree Manager (" + counter + ").");			
			try {
				hasMissingDependencyTest = this.addTest(test);
				if(hasMissingDependencyTest)
				{
					hasMissingTests=true;
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);				
			}			
			counter++;
		}
		
		
		return hasMissingTests;
	}
	
	private boolean preCheckForCycles(AutomationTest test)
	{
		boolean result = false;
		Node node = new Node(test, test.getTestName());
		
		overallGraph.addNode(node);
		for(TestDependency dep : test.getTestDependencies())
		{
			final AutomationTest parent = testSet.getTestById(dep.getParentTestId());
			if(parent!=null)
			{
				boolean parentExists = false;
				Node parentNode = null;
				Iterator<Node> iter = overallGraph.iterator();
				while(iter.hasNext())
				{				
					Node aNode = iter.next();
					if(aNode.getName().equals(parent.getTestName()))
					{
						parentNode = aNode;
						parentExists = true;
					}
				}
				
				if(parentExists && parentNode != null)
				{
					overallGraph.addEdge(parentNode, node);
				}else
				{
					Node newNode = new Node(parent, parent.getTestName());
					overallGraph.addNode(newNode);
					overallGraph.addEdge(newNode, node);
				}
			}
		}
		
		try {
			TopologicalSort.sort(overallGraph);
			result = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			result = false;
		}		
		
		return result;
	}
	
	/**
	 * Adds the test.
	 *
	 * @param test the test
	 * @return the automation test
	 */
	//Recursive 
	private boolean addTest(final AutomationTest test) throws IllegalArgumentException
	{
		System.out.println("\n\n\nCurrently adding test " + test.getTestName());		
		DirectedGraph graph;
		final Node node;
		boolean hasMissingTest = false;
		if(!preCheckForCycles(test))
		{
			throw new IllegalArgumentException("Error: Cycle detected in dependency tree when adding test: " + test.getTestName());
		}
		
		if(testTreeMap.containsKey(test))
		{
			// Add recursive call for dependencies.. 
			// but Dependencies should have already 
			// been processed if the test already exists in the map
			System.out.println("Test already exists in the map.");
			graph = testTreeMap.get(test);
			node = graph.retrieveNodeByTest(test);
		}else
		{
			System.out.println("Test does not exist in the map. Creating new tree. ***********");			
			graph = new DirectedGraph();
			node = new Node(test, test.getTestName());			
			System.out.println("Adding node to graph.");
			graph.addNode(node);
			System.out.println("Node added to graph.");
			System.out.println("Adding test and graph to map.");
			testTreeMap.put(test, graph);
			System.out.println("Test and graph added to map.");
			if(test.getRollIndicator() == null || test.getRollIndicator().equals(RollIndicatorEnum.NONE))
			{
				System.out.println("Adding graph to nonRollTreeList. (1)");
				nonRollTreeList.add(graph);
				treeListMap.put(graph, nonRollTreeList);
			}else if(test.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
			{
				System.out.println("Adding graph to preRollTreeList. (1)");
				preRollTreeList.add(graph);
				treeListMap.put(graph, preRollTreeList);
			}else if(test.getRollIndicator().equals(RollIndicatorEnum.ROLL))
			{
				System.out.println("Adding graph to rollTreeList. (1)");
				rollTreeList.add(graph);
				treeListMap.put(graph, rollTreeList);
			}else if(test.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
			{
				System.out.println("Adding graph to postRollTreeList. (1)");
				postRollTreeList.add(graph);
				treeListMap.put(graph, postRollTreeList);
			}else
			{
				System.out.println("Error! Error! Roll Indicator value not in expected results.");
			}
			System.out.println("ADDED NEW TREE TO TREE LIST (1)");
			treeList.add(graph);
		}		
		
		for(TestDependency dep : test.getTestDependencies())
		{
			//This may or may not get the correct test object
			final AutomationTest parent = testSet.getTestById(dep.getParentTestId());
			if(testSet.contains(parent))
			{					
				System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				System.out.println("Processing dependent Test (Parent Test of " + test.getTestName() +"): " + parent.getTestName());
				if(testTreeMap.containsKey(parent))
				{
					System.out.println("Parent test exists in a tree.");					
					final Node parentNode;
					if(graph.equals(testTreeMap.get(parent)))
					{
						parentNode = graph.retrieveNodeByTest(parent);
						System.out.println("Child and Parent nodes EXIST in the same graph.");
						parentNode.addEdge(node, dep.getDependencyType());
						graph.addEdge(parentNode, node);
					}else
					{
						DirectedGraph parentGraph = testTreeMap.get(parent);
						parentNode = parentGraph.retrieveNodeByTest(parent);						
						System.out.println("Child and Parent nodes DO NOT EXIST in the same graph.");
						if(test.getRollIndicator()!=null)
						{
							if(test.getRollIndicator().equals(parent.getRollIndicator()))
							{	
								System.out.println("Child and Parent node have the same roll indicator. Adding to same tree.");
								this.mergeTrees(graph, parentGraph);							
								parentNode.addEdge(node, dep.getDependencyType());
								graph.addEdge(parentNode, node);
							}else
							{
								System.out.println("Child and Parent node have different roll indicators.");
								parentNode.addEdge(node, dep.getDependencyType());
								final Edge e = new Edge(parentNode, node, dep.getDependencyType());
								node.addInEdge(e);
							}
						}else
						{
							if(parent.getRollIndicator()!=null)
							{
								//Test has null roll indicator. Parent has non-null indicator
								System.out.println("Child and Parent node have different roll indicators (Child is null).");								
								parentNode.addEdge(node, dep.getDependencyType());
							}else
							{
								//both test and parent test have a null roll indicator = merge trees
								System.out.println("Child and Parent node have no roll indicator (null). Adding to same tree (non-roll tree).");
								this.mergeTrees(graph, parentGraph);							
								parentNode.addEdge(node, dep.getDependencyType());
								graph.addEdge(parentNode, node);
							}
						}
					}				
				}else
				{
					System.out.println("Parent test does not exist in tree map.");
					final Node parentNode = new Node(parent, parent.getTestName());
					if(test.getRollIndicator()!=null)
					{
						if(test.getRollIndicator().equals(parent.getRollIndicator()))
						{	
							//Both tests have same roll indicators. Add parent to test's graph
							System.out.println("Adding parent node to graph.");
							graph.addNode(parentNode);
							System.out.println("Parent node added to graph.");
							System.out.println("Adding edge to parent node in child node.");
							parentNode.addEdge(node, dep.getDependencyType());
							System.out.println("Edge to parent node added in child node.");
							System.out.println("Adding edge from child node to parent node in graph.");
							graph.addEdge(parentNode, node);
							System.out.println("Edge from child node to parent node added in graph.");
							if(testTreeMap.get(parent)!=graph)
							{
								testTreeMap.remove(parent);
								testTreeMap.put(parent, graph);
							}						
						}else
						{
							//Tests have different roll indicators
							//Create new graph for parent, create node edges only, no graph edges.
							DirectedGraph parentGraph = new DirectedGraph();
							Node newNode = new Node(parent, parent.getTestName());
							newNode.addEdge(node, dep.getDependencyType());
							parentGraph.addNode(newNode);
							if(parent.getRollIndicator()==null)
							{
								System.out.println("Adding graph to nonRollTreeList. (2)");
								nonRollTreeList.add(parentGraph);
								treeListMap.put(parentGraph, nonRollTreeList);
							}else if(parent.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
							{
								System.out.println("Adding graph to preRollTreeList. (2)");
								preRollTreeList.add(parentGraph);
								treeListMap.put(parentGraph, preRollTreeList);
							}else if(parent.getRollIndicator().equals(RollIndicatorEnum.ROLL))
							{
								System.out.println("Adding graph to rollTreeList. (2)");
								rollTreeList.add(parentGraph);
								treeListMap.put(parentGraph, rollTreeList);
							}else if(parent.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
							{
								System.out.println("Adding graph to postRollTreeList. (2)");
								postRollTreeList.add(parentGraph);
								treeListMap.put(parentGraph, postRollTreeList);
							}else
							{
								System.out.println("Error! Error! Roll Indicator value not in expected results.");
							}
							System.out.println("ADDED NEW TREE TO TREE LIST (2)");
							treeList.add(parentGraph);
							if(testTreeMap.get(parent)!=parentGraph)
							{
								testTreeMap.remove(parent);
								testTreeMap.put(parent, parentGraph);
							}
						}
					}else
					{
						if(parent.getRollIndicator()!=null)
						{
							//Test roll indicator is null, Parent roll indicator is not null.
							//Create new graph for parent, create node edges only, no graph edges.
							DirectedGraph parentGraph = new DirectedGraph();
							Node newNode = new Node(parent, parent.getTestName());
							newNode.addEdge(node, dep.getDependencyType());
							parentGraph.addNode(newNode);
							if(parent.getRollIndicator()==null)
							{
								nonRollTreeList.add(parentGraph);
							}else if(parent.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
							{
								preRollTreeList.add(parentGraph);
							}else if(parent.getRollIndicator().equals(RollIndicatorEnum.ROLL))
							{
								rollTreeList.add(parentGraph);
							}else if(parent.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
							{
								postRollTreeList.add(parentGraph);
							}else
							{
								System.out.println("Error! Error! Roll Indicator value not in expected results.");
							}
							System.out.println("ADDED NEW TREE TO TREE LIST (3)");
							treeList.add(parentGraph);
							if(testTreeMap.get(parent)!=parentGraph)
							{
								testTreeMap.remove(parent);
								testTreeMap.put(parent, parentGraph);
							}
						}else
						{
							//Both tests have null roll indicators. Add parent to test's graph
							System.out.println("Adding parent node to graph.");
							graph.addNode(parentNode);
							System.out.println("Parent node added to graph.");
							System.out.println("Adding edge to parent node in child node.");
							parentNode.addEdge(node, dep.getDependencyType());
							System.out.println("Edge to parent node added in child node.");
							System.out.println("Adding edge from child node to parent node in graph.");
							graph.addEdge(parentNode, node);
							System.out.println("Edge from child node to parent node added in graph.");
							if(testTreeMap.get(parent)!=graph)
							{
								testTreeMap.remove(parent);
								testTreeMap.put(parent, graph);
							}						
						}
					}
				}	
			//Srikanth 03/24/2017 Checking if the tree already has the parent node
				if (!this.testExistsInATree(parent)){ 
								this.addTest(parent);
				}
			}else
			{
				AutomationTest parentTest = AutomationTestManager.getAutomationTestManager().getTestById(dep.getParentTestId());
				if(dep.getDependencyType().equals(EdgeTypeEnum.MUST_RUN))
				{					
					CopyOnWriteArrayList<AutomationTest> list = missingMustRunTests.get(test);
					if(list==null)
					{
						list = new CopyOnWriteArrayList<AutomationTest>();
						list.add(parentTest);
						missingMustRunTests.put(test, list);
					}else
					{
						if(!list.contains(parentTest))
						{
							list.add(parentTest);
						}
						
					}
				}else
				{
					CopyOnWriteArrayList<AutomationTest> list = missingMustPassTests.get(test);
					if(list==null)
					{
						list = new CopyOnWriteArrayList<AutomationTest>();
						list.add(parentTest);
						missingMustPassTests.put(test, list);
					}else
					{
						if(!list.contains(parentTest))
						{
							list.add(parentTest);
						}
					}									
				}
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				System.out.println(test.getTestName() + " is dependent on Test with id: " + dep.getParentTestId() + ". Test with id: " + dep.getParentTestId() + "must be added to the test set.");
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				hasMissingTest = true;
			}
			
		}
		return hasMissingTest;
	}	
	

	/**
	 * Gets the tree list.
	 *
	 * @return the treeList
	 */
	public CopyOnWriteArrayListWithStatus<DirectedGraph> getTreeList() {
		return treeList;
	}

	/**
	 * Sets the tree list.
	 *
	 * @param treeList the treeList to set
	 */
	public void setTreeList(CopyOnWriteArrayListWithStatus<DirectedGraph> treeList) {
		this.treeList = treeList;
	}
	
	/**
	 * @return the missingMustRunTests
	 */
	public ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> getMissingMustRunTests() {
		return missingMustRunTests;
	}

	/**
	 * @param missingMustRunTests the missingMustRunTests to set
	 */
	public void setMissingMustRunTests(
			ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustRunTests) {
		this.missingMustRunTests = missingMustRunTests;
	}

	/**
	 * @return the missingMustPassTests
	 */
	public ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> getMissingMustPassTests() {
		return missingMustPassTests;
	}

	/**
	 * @param missingMustPassTests the missingMustPassTests to set
	 */
	public void setMissingMustPassTests(
			ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustPassTests) {
		this.missingMustPassTests = missingMustPassTests;
	}

	/**
	 * Test exists in a tree.
	 *
	 * @param test the test
	 * @return true, if successful
	 */
	public boolean testExistsInATree(final AutomationTest test)
	{
		return testTreeMap.containsKey(test);
	}
	
	/**
	 * Merge trees.
	 *
	 * @param treeOne the tree one
	 * @param treeTwo the tree two
	 */
	private void mergeTrees(DirectedGraph treeOne, DirectedGraph treeTwo)
	{
		System.out.println("Number of tree's before merge: " + treeList.size());
		DirectedGraph mergedGraph = treeOne.mergeTree(treeTwo);
		Iterator<Node> iter = mergedGraph.iterator();
		while(iter.hasNext())			
		{
			final Node node = iter.next();
			final AutomationTest test = (AutomationTest)node.getUserObject();
			testTreeMap.remove(test);
			testTreeMap.put(test, mergedGraph);			
		}
		CopyOnWriteArrayListWithStatus<DirectedGraph>  oldGraphList1 = treeListMap.get(treeOne);
		if(oldGraphList1 != null)
		{
			oldGraphList1.remove(treeOne);
		}
		CopyOnWriteArrayListWithStatus<DirectedGraph>  oldGraphList2 = treeListMap.get(treeTwo);
		if(oldGraphList2 != null)
		{
			oldGraphList2.remove(treeTwo);			
		}
		if(oldGraphList1==oldGraphList2)
		{
			oldGraphList1.add(mergedGraph);
		}else
		{
			Iterator<Node> iterator = mergedGraph.iterator();
			Node node = iterator.next();
			AutomationTest test = (AutomationTest) node.getUserObject();
			if(test.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
			{
				preRollTreeList.add(mergedGraph);
			}else if(test.getRollIndicator().equals(RollIndicatorEnum.ROLL))
			{
				rollTreeList.add(mergedGraph);
			}else if(test.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
			{
				postRollTreeList.add(mergedGraph);
			}else
			{
				nonRollTreeList.add(mergedGraph);
			}			
		}
		
		treeList.remove(treeOne);
		treeList.remove(treeTwo);
		treeList.add(mergedGraph);
		System.out.println("Number of tree's after merge: " + treeList.size());
		
	}
	
	/**
	 * Gets the topological sorted list.
	 *
	 * @return the topological sorted list
	 * @throws Throwable the throwable
	 */
	public CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> getTopologicalSortedList() throws IllegalArgumentException
	{
		if(treeList.isEmpty())
		{
			return null;
		}else
		{
			System.out.println("Number of trees to sort: " + treeList.size());
			CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> list = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();
			try{
				list = sortLists(treeList);
			}catch(IllegalArgumentException t)
			{
				throw new IllegalArgumentException("Error: " + t.getMessage(), t);
			}
			
			return null;
		}		
	}

	/**
	 * Gets the work.
	 *
	 * @return the independentPostRollTests
	 * @throws Throwable the throwable
	 */
//	public CopyOnWriteArrayList<AutomationTest> getIndependentPostRollTests() {
//		for(DirectedGraph graph: postRollTreeList)
//		{
//			if(graph.size()==1)
//			{
//				AutomationTest test = (AutomationTest) graph.iterator().next().getUserObject();
//				independentPostRollTests.add(test);
//				postRollTreeList.remove(graph);
//			}
//		}
//		return independentPostRollTests;
//	}
	
	public DependencyWorkBucket getWork() throws IllegalArgumentException
	{		
		CopyOnWriteArrayListWithStatus<Node> independentPreRollTests = new CopyOnWriteArrayListWithStatus<Node>();
		CopyOnWriteArrayListWithStatus<Node> independentPostRollTests = new CopyOnWriteArrayListWithStatus<Node>();
		
		for(DirectedGraph graph: preRollTreeList)
		{
			if(graph.size()==1)
			{
				//AutomationTest test = ;
				independentPreRollTests.add(graph.iterator().next());
				preRollTreeList.remove(graph);
			}
		}		
		
		for(DirectedGraph graph: postRollTreeList)
		{
			if(graph.size()==1)
			{
				//AutomationTest test = (AutomationTest) graph.iterator().next();
				independentPostRollTests.add(graph.iterator().next());
				postRollTreeList.remove(graph);
			}
		}
		
		workBucket.setIndependentPreRollTests(independentPreRollTests);
		workBucket.setIndependentPostRollTests(independentPostRollTests);
		try{
			workBucket.setPreRollTreeList(this.sortLists(preRollTreeList));
			workBucket.setRollTreeList(this.sortLists(rollTreeList));
			workBucket.setPostRollTreeList(this.sortLists(postRollTreeList));
			workBucket.setNonRollTreeList(this.sortLists(nonRollTreeList));
		}catch(IllegalArgumentException t)
		{
			throw new IllegalArgumentException("Error: " + t.getMessage(), t);
		}
		
		return workBucket;
	}
	
	/**
	 * Sort lists.
	 *
	 * @param list the list
	 * @return the copy on write array list with status
	 * @throws Throwable the throwable
	 */
	private final CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> sortLists(CopyOnWriteArrayListWithStatus<DirectedGraph> list) throws IllegalArgumentException
	{
		CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> sortedList = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();			
		try{
			for(DirectedGraph graph : list)			
			{
				sortedList.add(TopologicalSort.sort(graph));
			}
		}catch(IllegalArgumentException t)		
		{
			
			throw new IllegalArgumentException("Error: " + t.getMessage(), t);			
		}
		
		return sortedList;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nonRollTreeList == null) ? 0 : nonRollTreeList.hashCode());
		result = prime
				* result
				+ ((postRollTreeList == null) ? 0 : postRollTreeList.hashCode());
		result = prime * result
				+ ((preRollTreeList == null) ? 0 : preRollTreeList.hashCode());
		result = prime * result
				+ ((rollTreeList == null) ? 0 : rollTreeList.hashCode());
		result = prime * result
				+ ((testList == null) ? 0 : testList.hashCode());
		result = prime * result + ((testSet == null) ? 0 : testSet.hashCode());
		result = prime * result
				+ ((testTreeMap == null) ? 0 : testTreeMap.hashCode());
		result = prime
				* result
				+ ((topologicalSortedList == null) ? 0 : topologicalSortedList
						.hashCode());
		result = prime * result
				+ ((treeList == null) ? 0 : treeList.hashCode());
		result = prime * result
				+ ((workBucket == null) ? 0 : workBucket.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TreeManager)) {
			return false;
		}
		TreeManager other = (TreeManager) obj;
		if (nonRollTreeList == null) {
			if (other.nonRollTreeList != null) {
				return false;
			}
		} else if (!nonRollTreeList.equals(other.nonRollTreeList)) {
			return false;
		}
		if (postRollTreeList == null) {
			if (other.postRollTreeList != null) {
				return false;
			}
		} else if (!postRollTreeList.equals(other.postRollTreeList)) {
			return false;
		}
		if (preRollTreeList == null) {
			if (other.preRollTreeList != null) {
				return false;
			}
		} else if (!preRollTreeList.equals(other.preRollTreeList)) {
			return false;
		}
		if (rollTreeList == null) {
			if (other.rollTreeList != null) {
				return false;
			}
		} else if (!rollTreeList.equals(other.rollTreeList)) {
			return false;
		}
		if (testList == null) {
			if (other.testList != null) {
				return false;
			}
		} else if (!testList.equals(other.testList)) {
			return false;
		}
		if (testSet == null) {
			if (other.testSet != null) {
				return false;
			}
		} else if (!testSet.equals(other.testSet)) {
			return false;
		}
		if (testTreeMap == null) {
			if (other.testTreeMap != null) {
				return false;
			}
		} else if (!testTreeMap.equals(other.testTreeMap)) {
			return false;
		}
		if (topologicalSortedList == null) {
			if (other.topologicalSortedList != null) {
				return false;
			}
		} else if (!topologicalSortedList.equals(other.topologicalSortedList)) {
			return false;
		}
		if (treeList == null) {
			if (other.treeList != null) {
				return false;
			}
		} else if (!treeList.equals(other.treeList)) {
			return false;
		}
		if (workBucket == null) {
			if (other.workBucket != null) {
				return false;
			}
		} else if (!workBucket.equals(other.workBucket)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	

}
