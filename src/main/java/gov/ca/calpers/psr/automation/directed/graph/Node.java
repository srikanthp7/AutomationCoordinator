/**
 * 
 */
package gov.ca.calpers.psr.automation.directed.graph;

/**
 * @author burban
 *
 */

import java.io.Serializable;
import java.util.HashSet;


/**
 * The Class Node.
 */
public class Node implements Serializable
{       
    
    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The out edges. */
	private HashSet<Edge> outEdges;
    
    /** The in edges. */
    private HashSet<Edge> inEdges;
    
    /** The user object. */
    private final Object userObject;
    
    /** The name. */
    private String name;
    
    /** The depth. */
    private int depth;

    /**
     * Instantiates a new node.
     *
     * @param obj the obj
     * @param name the name
     */
    public Node(final Object obj, String name) {
        this.name = name; 
        userObject = obj;
        outEdges = new HashSet<Edge>();
        inEdges = new HashSet<Edge>();
    }
    
    /**
     * Adds the edge.
     *
     * @param node the node
     * @param edgeType the edge type
     * @return the node
     */
    public Node addEdge(final Node node, EdgeTypeEnum edgeType){
    	final Edge e = new Edge(this, node, edgeType);
    	for(Edge edge : outEdges)
    	{
    		if(edge.getChildNode()==this && edge.getParentNode() == node && edge.getEdgeType().equals(edgeType))
    		{
    			System.out.println("Edge already exists, not adding to node's outEdges.");    			 
    			if(!node.hasInEdge(e))
    			{
    				System.out.println("Child node does not have in-edge. Adding in-edge.");
    		        node.addInEdge(e);    		        
    			}else
    			{
    				System.out.println("Child node already has in-edge." + " Child Node: " + node.getName() + ", ParentNode: " + this.getName());
    			}
    			return this;
    		}
    	}
    	System.out.println("Adding out-edge from " + this.name + " to " + node.getName());
        outEdges.add(e);
        node.addInEdge(e);
        return this;
    }
    
    /**
     * Adds the in edge.
     *
     * @param newEdge the new edge
     */
    public void addInEdge(final Edge newEdge)
    {
    	if(!inEdges.contains(newEdge))
    	{
	    	if(newEdge.getParentNode().getUserObject().equals(this.userObject) && !inEdges.contains(newEdge))
	    	{
	    		System.out.println("(Inside Node) Adding in-edge.");
	    		inEdges.add(newEdge);
	    	}else
	    	{
	    		System.out.println("(Inside Node) Not adding in-edge. This node is equal to edge's parent node.");
	    	}
    	}else
    	{
    		System.out.println("(Inside Node) Not adding in-edge. In-edge already exists.");
    	}
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Copy.
     *
     * @param aNode the a node
     * @return the node
     */
    //Used to copy a given node
    public final Node copy(final Node aNode)
    {
    	final Node node = new Node(aNode.getUserObject(), aNode.getName()); 
        return node;    	
    }
    
//    public boolean equals(final Node aNode)
//    {   
//    	System.out.println("Checking if current node (" + this.getName() + ") is equal to node: " + aNode.getName());
//    	if(this.name.equals(aNode.getName()) && this.userObject == aNode.getUserObject())
//    	{   		
//    		return true;
//    	}else
//    	{
//    		return false;
//    	}
//    }

	/**
 * Gets the out edges.
 *
 * @return the out edges
 */
public HashSet<Edge> getOutEdges() {
		return outEdges;
	}

	/**
	 * Sets the out edges.
	 *
	 * @param outEdges the new out edges
	 */
	public void setOutEdges(HashSet<Edge> outEdges) {
		this.outEdges = outEdges;
	}
	
	/**
	 * Gets the in edges.
	 *
	 * @return the in edges
	 */
	public HashSet<Edge> getInEdges() {
		// TODO Auto-generated method stub
		return inEdges;
	}  

	/**
	 * Gets the user object.
	 *
	 * @return the user object
	 */
	public Object getUserObject() {
		return userObject;
	}
	
	/**
	 * Checks for in edge.
	 *
	 * @param edge the edge
	 * @return true, if successful
	 */
	public boolean hasInEdge(Edge edge)
	{
		for(Edge anEdge : inEdges)
		{
			if(edge.getChildNode() == anEdge.getChildNode() && edge.getEdgeType().equals(anEdge.getEdgeType()))
			{
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		result = prime * result
//				+ ((outEdges == null) ? 0 : outEdges.hashCode());
		result = prime * result
				+ ((userObject == null) ? 0 : userObject.hashCode());
//		result = prime * result
//				+ ((inEdges == null) ? 0 : inEdges.hashCode());
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
		if (!(obj instanceof Node)) {
			return false;
		}
		Node other = (Node) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (userObject == null) {
			if (other.userObject != null) {
				return false;
			}
		} else if (!userObject.equals(other.userObject)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the depth.
	 *
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Sets the depth.
	 *
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	
    
}
