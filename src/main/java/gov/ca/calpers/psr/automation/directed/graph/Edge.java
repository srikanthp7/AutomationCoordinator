package gov.ca.calpers.psr.automation.directed.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.ExecutionStatus;

/**
 * The Class Edge.
 */
public class Edge implements Serializable, ActionListener
{
    
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The child node. */
	private Node childNode;

	/** The parent node. */
	private Node parentNode;
    
    /** The edge type. */
    private EdgeTypeEnum edgeType;
    
    /** The disabled. */
    private boolean disabled = false;
    
    /**
     * Instantiates a new edge.
     *
     * @param child the child
     * @param parent the parent
     * @param edgeType the edge type
     */
    public Edge(Node child, Node parent,EdgeTypeEnum edgeType) {
        this.childNode = child;
        this.parentNode = parent;
        this.edgeType = edgeType;
    }

    
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childNode == null) ? 0 : childNode.hashCode());
		result = prime * result
				+ ((edgeType == null) ? 0 : edgeType.hashCode());
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
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
		if (!(obj instanceof Edge)) {
			return false;
		}
		Edge other = (Edge) obj;
		if (childNode == null) {
			if (other.childNode != null) {
				return false;
			}
		} else if (!childNode.equals(other.childNode)) {
			return false;
		}
		if (edgeType != other.edgeType) {
			return false;
		}
		if (parentNode == null) {
			if (other.parentNode != null) {
				return false;
			}
		} else if (parentNode != other.getParentNode()) {
			return false;
		}
		return true;
	}



	/**
	 * Gets the edge type.
	 *
	 * @return the edgeType
	 */
	public EdgeTypeEnum getEdgeType() {
		return edgeType;
	}
	
	/**
	 * Sets the edge type.
	 *
	 * @param edgeType the edgeType to set
	 */
	public void setEdgeType(EdgeTypeEnum edgeType) {
		this.edgeType = edgeType;
	}
	
    /**
     * Gets the child node.
     *
     * @return the childNode
     */
	public Node getChildNode() {
		return childNode;
	}



	/**
	 * Sets the child node.
	 *
	 * @param childNode the childNode to set
	 */
	public void setChildNode(Node childNode) {
		this.childNode = childNode;
	}



	/**
	 * Gets the parent node.
	 *
	 * @return the parentNode
	 */
	public Node getParentNode() {
		return parentNode;
	}



	/**
	 * Sets the parent node.
	 *
	 * @param parentNode the parentNode to set
	 */
	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}



	/**
	 * Checks if is disabled.
	 *
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;	
	}



	/**
	 * Sets the disabled.
	 *
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}



	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("DISABLE"))
		{
			this.disabled = true;
			AutomationTest theTest = (AutomationTest) parentNode.getUserObject();
			if(theTest.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
			{	
				boolean isBlocked = false;
				for(Edge edge: parentNode.getInEdges())
				{
					if(edge != this && edge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS) && !edge.isDisabled())
					{
						AutomationTest parent = (AutomationTest)edge.getChildNode().getUserObject();
						if(parent.getExecutionStatus().equals(ExecutionStatus.FAILED) || parent.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
						{
							isBlocked = true;
							break;
						}
							
					}
						
				}
				if(!isBlocked)
				{
					theTest.setExecutionStatus(ExecutionStatus.NOT_RUN);
					propagateUnBlockedStatus(parentNode);
				}
			}
		}
		
		if(e.getActionCommand().equals("RE_ENABLE"))
		{
			this.disabled = false;
			AutomationTest theTest = (AutomationTest) parentNode.getUserObject();
			if(!theTest.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
			{	
				boolean isBlocked = false;
				for(Edge edge: parentNode.getInEdges())
				{
					if(edge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS) && !edge.isDisabled())
					{
						AutomationTest parent = (AutomationTest)edge.getChildNode().getUserObject();
						if(parent.getExecutionStatus().equals(ExecutionStatus.FAILED) || parent.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
						{
							isBlocked = true;
							break;
						}
							
					}
						
				}
				if(isBlocked)
				{
					theTest.setExecutionStatus(ExecutionStatus.BLOCKED);
					propagateBlockedStatus(parentNode);
				}
			}
		}
	}
	
	 /**
 	 * Propogate un blocked status.
 	 *
 	 * @param childNode the child node
 	 */
 	private void propagateUnBlockedStatus(Node childNode)
	 {
		boolean isBlockedByOtherTest = false;
    	for(Edge inEdge : childNode.getOutEdges())
    	{       		  			
			if(!inEdge.isDisabled())
			{
				AutomationTest test = (AutomationTest) inEdge.getChildNode().getUserObject();
				if(inEdge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
				{
					if(test.getExecutionStatus().equals(ExecutionStatus.BLOCKED) || test.getExecutionStatus().equals(ExecutionStatus.FAILED))
					{
						isBlockedByOtherTest = true;
						break;
					}
				}
			}
    		
    	}
    	if(isBlockedByOtherTest)
		{
    		((AutomationTest) childNode.getUserObject()).setExecutionStatus(ExecutionStatus.BLOCKED);			
		}else
		{
			AutomationTest currTest = (AutomationTest) childNode.getUserObject();
			currTest.setFinalDuration(0);
			currTest.setStartTime(0);
			currTest.setRunCount(0);
			currTest.setExecutionStatus(ExecutionStatus.NOT_RUN);
			
			for(Edge outEdge : childNode.getOutEdges())
			{
				propagateUnBlockedStatus(outEdge.getParentNode());
			}
		}	
	}		
	 
	 /**
 	 * Propogate blocked status.
 	 *
 	 * @param childNode the child node
 	 */
 	private void propagateBlockedStatus(Node childNode)
	 {		
    	for(Edge outEdge : childNode.getOutEdges())
    	{       		  			
			if(!outEdge.isDisabled())
			{				
				if(outEdge.getEdgeType().equals(EdgeTypeEnum.MUST_PASS))
				{
					AutomationTest test = (AutomationTest) outEdge.getParentNode().getUserObject();
					test.setFinalDuration(0);
					test.setStartTime(0);
					test.setRunCount(0);
					test.setExecutionStatus(ExecutionStatus.BLOCKED);
					propagateBlockedStatus(outEdge.getParentNode());
				}				
			}    		
    	}    	
	}		
	    
}
