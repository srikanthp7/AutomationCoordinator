/**
 * 
 */
package gov.ca.calpers.psr.automation.directed.graph;


/*****************************************************************************
 * File: DirectedGraph.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing a directed graph.  Internally, the class is represented
 * by an adjacency list.
 */
import java.util.*; // For HashMap, HashSet
import java.util.concurrent.ConcurrentHashMap;

import gov.ca.calpers.psr.automation.AutomationTest;

/**
 * The Class DirectedGraph.
 */
public final class DirectedGraph implements Iterable<Node> {
    
    /** The m graph. */
    /* A map from nodes in the graph to sets of outgoing edges.  Each
     * set of edges is represented by a map from edges to doubles.
     */
    private final ConcurrentHashMap<Node, Set<Node>> mGraph = new ConcurrentHashMap<Node, Set<Node>>();

    /**
     * Adds a new node to the graph.  If the node already exists, this
     * function is a no-op.
     *
     * @param node The node to add.
     * @return Whether or not the node was added.
     */
    public boolean addNode(Node node) {
        /* If the node already exists, don't do anything. */
        if (mGraph.containsKey(node))
            return false;

        /* Otherwise, add the node with an empty set of outgoing edges. */
        mGraph.put(node, new HashSet<Node>());
        return true;
    }

    /**
     * Given a start node, and a destination, adds an arc from the start node 
     * to the destination.  If an arc already exists, this operation is a 
     * no-op.  If either endpoint does not exist in the graph, throws a 
     * NoSuchElementException.
     *
     * @param start The start node.
     * @param dest The destination node.
     * @throws NoSuchElementException If either the start or destination nodes
     *                                do not exist.
     */
    public void addEdge(Node start, Node dest) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(start) || !mGraph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        /* Add the edge. */  
        if(!this.edgeExists(start, dest))
        {
        	 mGraph.get(start).add(dest);
        }       
    }

    /**
     * Removes the edge from start to dest from the graph.  If the edge does
     * not exist, this operation is a no-op.  If either endpoint does not
     * exist, this throws a NoSuchElementException.
     *
     * @param start The start node.
     * @param dest The destination node.
     * @throws NoSuchElementException If either node is not in the graph.
     */
    public void removeEdge(Node start, Node dest) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(start) || !mGraph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        mGraph.get(start).remove(dest);
    }

    /**
     * Given two nodes in the graph, returns whether there is an edge from the
     * first node to the second node.  If either node does not exist in the
     * graph, throws a NoSuchElementException.
     *
     * @param start The start node.
     * @param end The destination node.
     * @return Whether there is an edge from start to end.
     * @throws NoSuchElementException If either endpoint does not exist.
     */
    public boolean edgeExists(Node start, Node end) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(start) || !mGraph.containsKey(end))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        return mGraph.get(start).contains(end);
    }

    /**
     * Given a node in the graph, returns an immutable view of the edges
     * leaving that node as a set of endpoints.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NoSuchElementException If the node does not exist.
     */
    public Set<Node> edgesFrom(Node node) {
        /* Check that the node exists. */
        Set<Node> arcs = mGraph.get(node);
        if (arcs == null)
            throw new NoSuchElementException("Source node does not exist.");

        return Collections.unmodifiableSet(arcs);
    }

    /**
     * Returns an iterator that can traverse the nodes in the graph.
     *
     * @return An iterator that traverses the nodes in the graph.
     */
    @Override
	public Iterator<Node> iterator() {
        return mGraph.keySet().iterator();
    }

    /**
     * Returns the number of nodes in the graph.
     *
     * @return The number of nodes in the graph.
     */
    public int size() {
        return mGraph.size();
    }

    /**
     * Returns whether the graph is empty.
     *
     * @return Whether the graph is empty.
     */
    public boolean isEmpty() {
        return mGraph.isEmpty();
    }
    
    /**
     * Sets the depth starting from node.
     *
     * @param node the node
     * @param startingDepth the starting depth
     * @return the int
     */
    public int setDepthStartingFromNode(Node node, int startingDepth)
    {
    	int maxDepth = node.getDepth();    	
    	if(startingDepth > maxDepth)
    	{
    		maxDepth = startingDepth;
    	}
    	node.setDepth(maxDepth);
    	
    	if(node.getOutEdges().isEmpty())
    	{
    		return maxDepth;
    	}else
    	{
    		for(Edge edge: node.getOutEdges())
    		{
    			int returnedDepth = setDepthStartingFromNode(edge.getChildNode(), startingDepth + 1); 
    			if(returnedDepth > maxDepth)
    			{
    				maxDepth = returnedDepth;
    			}
    		}
    	}
    		
    	return maxDepth;
    }
    
    /**
     * Merge tree.
     *
     * @param aTree the a tree
     * @return the directed graph
     */
    public DirectedGraph mergeTree(DirectedGraph aTree)
    {
    	Iterator<Node> treeIterator = aTree.iterator();
    	while(treeIterator.hasNext())
    	{
    		Node aNode = treeIterator.next();
    		this.addNode(aNode);    		
    	}
    	treeIterator = aTree.iterator();
    	while(treeIterator.hasNext())
    	{
    		Node aNode = treeIterator.next();
    		for(Edge edge : aNode.getOutEdges())
    		{
    			this.addEdge(edge.getChildNode(), edge.getParentNode());
    		}
    	}
    	return this;    		
    }
    
    /**
     * Retrieve node by test.
     *
     * @param test the test
     * @return the node
     */
    public Node retrieveNodeByTest(AutomationTest test)
    {    	
    	for(Node node : mGraph.keySet())
    	{
    		AutomationTest aTest = (AutomationTest) node.getUserObject();
    		if(test.equals(aTest))
    		{
    			return node;
    		}
    	}
    	return null;
    }
    
    
}