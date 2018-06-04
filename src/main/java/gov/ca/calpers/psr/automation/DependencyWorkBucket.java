package gov.ca.calpers.psr.automation;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import gov.ca.calpers.psr.automation.directed.graph.Node;


/**
 * The Class DependencyWorkBucket.
 */
public class DependencyWorkBucket implements Serializable{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The pre roll tree list. */
	private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> preRollTreeList;
	
	/** The roll tree list. */
	private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> rollTreeList;
	
	/** The post roll tree list. */
	private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> postRollTreeList;
	
	/** The non roll tree list. */
	private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> nonRollTreeList;
	
	/** The independent pre roll tests. */
	private CopyOnWriteArrayListWithStatus<Node> independentPreRollTests;
	
	/** The independent post roll tests. */
	private CopyOnWriteArrayListWithStatus<Node> independentPostRollTests;

	/**
	 * Instantiates a new dependency work bucket.
	 */
	public DependencyWorkBucket()
	{
		preRollTreeList = new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();
		rollTreeList =  new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();
		postRollTreeList =  new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();
		nonRollTreeList =  new CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>>();
		independentPreRollTests = new CopyOnWriteArrayListWithStatus<Node>();
		independentPostRollTests = new CopyOnWriteArrayListWithStatus<Node>();
	}

	/**
	 * Gets the pre roll tree list.
	 *
	 * @return the preRollTreeList
	 */
	public CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> getPreRollTreeList() {
		return preRollTreeList;
	}

	/**
	 * Sets the pre roll tree list.
	 *
	 * @param preRollTreeList the preRollTreeList to set
	 */
	public void setPreRollTreeList(
			CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> preRollTreeList) {
		this.preRollTreeList = preRollTreeList;
	}

	/**
	 * Gets the roll tree list.
	 *
	 * @return the rollTreeList
	 */
	public CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> getRollTreeList() {
		return rollTreeList;
	}

	/**
	 * Sets the roll tree list.
	 *
	 * @param rollTreeList the rollTreeList to set
	 */
	public void setRollTreeList(CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> rollTreeList) {
		this.rollTreeList = rollTreeList;
	}

	/**
	 * Gets the post roll tree list.
	 *
	 * @return the postRollTreeList
	 */
	public CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> getPostRollTreeList() {
		return postRollTreeList;
	}

	/**
	 * Sets the post roll tree list.
	 *
	 * @param postRollTreeList the postRollTreeList to set
	 */
	public void setPostRollTreeList(
			CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> postRollTreeList) {
		this.postRollTreeList = postRollTreeList;
	}

	/**
	 * Gets the independent pre roll tests.
	 *
	 * @return the independentPreRollTests
	 */
	public CopyOnWriteArrayListWithStatus<Node> getIndependentPreRollTests() {
		return independentPreRollTests;
	}

	/**
	 * Sets the independent pre roll tests.
	 *
	 * @param independentPreRollTests the independentPreRollTests to set
	 */
	public void setIndependentPreRollTests(
			CopyOnWriteArrayListWithStatus<Node> independentPreRollTests) {
		this.independentPreRollTests = independentPreRollTests;
	}
	
	/**
	 * Adds the independent pre roll tests.
	 *
	 * @param indPreRollTests the ind pre roll tests
	 */
	public void addIndependentPreRollTests(CopyOnWriteArrayList<AutomationTest> indPreRollTests)
	{		
		for(AutomationTest test: indPreRollTests)
		{
			boolean testExists = false;
			for(Node node : independentPreRollTests)
			{			
				AutomationTest nodeTest = (AutomationTest) node.getUserObject();
				if(test.getTestName().equals(nodeTest.getTestName()))
				{
					testExists = true;
					break;					
				}
			}
			if(!testExists)
			{
				Node node = new Node(test, test.getTestName());
				independentPreRollTests.add(node);
			}
		}
	}

	/**
	 * Gets the independent post roll tests.
	 *
	 * @return the independentPostRollTests
	 */
	public CopyOnWriteArrayListWithStatus<Node> getIndependentPostRollTests() {
		return independentPostRollTests;
	}

	/**
	 * Sets the independent post roll tests.
	 *
	 * @param independentPostRollTests the independentPostRollTests to set
	 */
	public void setIndependentPostRollTests(
			CopyOnWriteArrayListWithStatus<Node> independentPostRollTests) {
		this.independentPostRollTests = independentPostRollTests;
	}
	
	/**
	 * Adds the independent post roll tests.
	 *
	 * @param indPostRollTests the ind post roll tests
	 */
	public void addIndependentPostRollTests(CopyOnWriteArrayList<AutomationTest> indPostRollTests)
	{		
		for(AutomationTest test: indPostRollTests)
		{
			boolean testExists = false;
			for(Node node : independentPostRollTests)
			{			
				AutomationTest nodeTest = (AutomationTest) node.getUserObject();
				if(test.getTestName().equals(nodeTest.getTestName()))
				{
					testExists = true;
					break;					
				}
			}
			if(!testExists)
			{
				Node node = new Node(test, test.getTestName());
				independentPostRollTests.add(node);
			}
		}
	}

	/**
	 * Gets the non roll tree list.
	 *
	 * @return the nonRollTreeList
	 */
	public CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> getNonRollTreeList() {
		return nonRollTreeList;
	}

	/**
	 * Sets the non roll tree list.
	 *
	 * @param nonRollTreeList the nonRollTreeList to set
	 */
	public void setNonRollTreeList(CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> nonRollTreeList) {
		this.nonRollTreeList = nonRollTreeList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((independentPostRollTests == null) ? 0
						: independentPostRollTests.hashCode());
		result = prime
				* result
				+ ((independentPreRollTests == null) ? 0
						: independentPreRollTests.hashCode());
		result = prime * result
				+ ((nonRollTreeList == null) ? 0 : nonRollTreeList.hashCode());
		result = prime
				* result
				+ ((postRollTreeList == null) ? 0 : postRollTreeList.hashCode());
		result = prime * result
				+ ((preRollTreeList == null) ? 0 : preRollTreeList.hashCode());
		result = prime * result
				+ ((rollTreeList == null) ? 0 : rollTreeList.hashCode());
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
		if (!(obj instanceof DependencyWorkBucket)) {
			return false;
		}
		DependencyWorkBucket other = (DependencyWorkBucket) obj;
		if (independentPostRollTests == null) {
			if (other.independentPostRollTests != null) {
				return false;
			}
		} else if (!independentPostRollTests
				.equals(other.independentPostRollTests)) {
			return false;
		}
		if (independentPreRollTests == null) {
			if (other.independentPreRollTests != null) {
				return false;
			}
		} else if (!independentPreRollTests
				.equals(other.independentPreRollTests)) {
			return false;
		}
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
		return true;
	}
	
	
	
}
