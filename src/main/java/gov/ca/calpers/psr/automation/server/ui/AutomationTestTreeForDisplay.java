package gov.ca.calpers.psr.automation.server.ui;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;

/**
 * The Class AutomationTestTreeForDisplay.
 */
public class AutomationTestTreeForDisplay extends JXTree {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The root. */
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		
	
	/** The default tree model. */
	private SortTreeModel defaultTreeModel = new SortTreeModel(root, new TreeStringComparator());
	
	/** The func groups. */
	private List<AutomationFunctionalGroup> funcGroups;
	
	/** The all auto functional groups. */
	private List<AutomationFunctionalGroup> allAutoFunctionalGroups;
	
	/**
	 * Instantiates a new automation test tree for display.
	 *
	 * @param funcGroups the func groups
	 * @param rootObj the root obj
	 */
	public AutomationTestTreeForDisplay(List<AutomationFunctionalGroup> funcGroups, Object rootObj)
	{
		super();		
		expandRow(0);
		setRootVisible(false);
		setShowsRootHandles(true);				
		root = new DefaultMutableTreeNode(rootObj);		
		defaultTreeModel = new SortTreeModel(root, new TreeStringComparator());
		this.setModel(defaultTreeModel);
		this.funcGroups = funcGroups;
		this.allAutoFunctionalGroups = AutomationFunctionalGroup.getAll();
		createFunctionalGroupTree();		
	}
	
	/**
	 * Creates the functional group tree.
	 */
	private void createFunctionalGroupTree()
	{
		for(AutomationFunctionalGroup funcGroup : funcGroups)
		{
			if(funcGroup.getTests().isEmpty())
			{
				funcGroups.remove(funcGroup);
			}else
			{
				DefaultMutableTreeNode currNode = new DefaultMutableTreeNode(funcGroup);
				for(AutomationTest test : funcGroup.getTests())
				{
					ObserverTreeNode testNode = new ObserverTreeNode(test);
					addNodeToDefaultTreeModel(defaultTreeModel, currNode, testNode);
				}
				addNodeToDefaultTreeModel(defaultTreeModel, root, currNode);
				defaultTreeModel.nodeStructureChanged(  root  );
			}
		}
	}
	
	/**
	 * Adds the node to default tree model.
	 *
	 * @param treeModel the tree model
	 * @param parentNode the parent node
	 * @param node the node
	 */
	private static synchronized void addNodeToDefaultTreeModel( DefaultTreeModel treeModel, DefaultMutableTreeNode parentNode, DefaultMutableTreeNode node ) {
		
		treeModel.insertNodeInto(  node, parentNode, parentNode.getChildCount()  );		
		
		if (  parentNode == ((SortTreeModel)treeModel).getRoot()  ) {
			treeModel.nodeStructureChanged(  (TreeNode) ((SortTreeModel)treeModel).getRoot()  );
		}
	}
	
	/**
	 * Removes the node from default tree model.
	 *
	 * @param treeModel the tree model
	 * @param parentNode the parent node
	 * @param node the node
	 */
	private static synchronized void removeNodeFromDefaultTreeModel(DefaultTreeModel treeModel, DefaultMutableTreeNode parentNode, DefaultMutableTreeNode node){
		if(parentNode == ((SortTreeModel)treeModel).getRoot())
		{
			treeModel.removeNodeFromParent(node);
			treeModel.nodeStructureChanged(  (TreeNode) ((SortTreeModel)treeModel).getRoot()  );
		}else
		{			
			parentNode.remove(node);
			treeModel.nodeStructureChanged(parentNode);					
		}
	}
	
	/**
	 * Removes the test from tree.
	 *
	 * @param test the test
	 */
	public synchronized void removeTestFromTree(AutomationTest test)
	{
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) defaultTreeModel.getRoot();		
		for(int i = 0; i < rootNode.getChildCount(); i++)
		{
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			for(int j = 0; j < parentNode.getChildCount(); j++)
			{
				ObserverTreeNode childNode = (ObserverTreeNode) parentNode.getChildAt(j);
				AutomationTest nodeTest = (AutomationTest) childNode.getUserObject();
				if(test.compareTo(nodeTest) == 0)					
				{
					removeNodeFromDefaultTreeModel(defaultTreeModel, parentNode, childNode);					
					if(parentNode.getUserObject() instanceof AutomationFunctionalGroup && parentNode.isLeaf())
					{
						AutomationFunctionalGroup group = (AutomationFunctionalGroup) parentNode.getUserObject();
						funcGroups.remove(group);
						removeNodeFromDefaultTreeModel(defaultTreeModel, root, parentNode);
					}
				}				
			}			
		}
		if(rootNode.getChildCount()==0)
		{
			this.setRootVisible(false);
		}
	}
	
	/**
	 * Removes the all tests from tree.
	 */
	public synchronized void removeAllTestsFromTree()
	{
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) defaultTreeModel.getRoot();		
		for(int i = 0; i < rootNode.getChildCount(); i++)
		{
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			for(int j = 0; j < parentNode.getChildCount(); j++)
			{
				ObserverTreeNode childNode = (ObserverTreeNode) parentNode.getChildAt(j);				
				removeNodeFromDefaultTreeModel(defaultTreeModel, parentNode, childNode);					
				if(parentNode.getUserObject() instanceof AutomationFunctionalGroup && parentNode.isLeaf())
				{
					AutomationFunctionalGroup group = (AutomationFunctionalGroup) parentNode.getUserObject();
					funcGroups.remove(group);
					removeNodeFromDefaultTreeModel(defaultTreeModel, root, parentNode);
				}
								
			}			
		}
	}
	
	/**
	 * Adds the test to tree.
	 *
	 * @param test the test
	 * @return the default mutable tree node
	 */
	public synchronized DefaultMutableTreeNode addTestToTree(AutomationTest test)
	{
		if(!this.hasTest(test))
		{
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) defaultTreeModel.getRoot();
			if(rootNode.getChildCount()==0)
			{
				this.setRootVisible(true);
			}
			for(int i = 0; i < rootNode.getChildCount(); i++)
			{
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
				AutomationFunctionalGroup funcGroup = (AutomationFunctionalGroup) parentNode.getUserObject(); 
				if(funcGroup.getAutoFunctionalGroupCode().equals(test.getAutoFunctionalGroupCode()))
				{
					ObserverTreeNode childNode = new ObserverTreeNode(test);
					funcGroup.addTest(test);
					addNodeToDefaultTreeModel(defaultTreeModel, parentNode, childNode);
					return parentNode;
				}					
			}
			AutomationFunctionalGroup newGroup = null;
			boolean foundGroup = false;
			for(AutomationFunctionalGroup group : allAutoFunctionalGroups)
			{
				if(group.getAutoFunctionalGroupCode().equals(test.getAutoFunctionalGroupCode()))
				{
					newGroup = group;
					foundGroup = true;
					break;
				}
			}
			
			if(!foundGroup)
			{
				newGroup = AutomationFunctionalGroup.getGroup(test.getAutoFunctionalGroupCode()); 
			}
			DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(newGroup);
			ObserverTreeNode childNode = new ObserverTreeNode(test);
			newGroup.addTest(test);
			addNodeToDefaultTreeModel(defaultTreeModel, root, parentNode);
			addNodeToDefaultTreeModel(defaultTreeModel,parentNode,childNode);			
			return parentNode;
		}
		return null;
		
	}

	/**
	 * Sets the default tree model.
	 *
	 * @param defaultTreeModel the new default tree model
	 */
	public void setDefaultTreeModel(SortTreeModel defaultTreeModel) {
		this.defaultTreeModel = defaultTreeModel;
	}

	/**
	 * Gets the functional groups.
	 *
	 * @return the functional groups
	 */
	public List<AutomationFunctionalGroup> getFunctionalGroups() {
		return funcGroups;
	}

	/**
	 * Sets the functional groups.
	 *
	 * @param funcGroups the new functional groups
	 */
	public void setFunctionalGroups(List<AutomationFunctionalGroup> funcGroups) {
		this.funcGroups = funcGroups;
	}
	
	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public synchronized DefaultMutableTreeNode getRoot() {
		return root;
	}

	/**
	 * Sets the root.
	 *
	 * @param root the new root
	 */
	public synchronized void setRoot(DefaultMutableTreeNode root) {
		this.root = root;
	}

	/**
	 * Adds the functional group.
	 *
	 * @param funcGroup the func group
	 */
	public synchronized void addFunctionalGroup(AutomationFunctionalGroup funcGroup)
	{
		if(!hasGroup(funcGroup.getAutoFunctionalGroupCode()))
		{
			funcGroups.add(funcGroup);
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(funcGroup);
			for(AutomationTest test : funcGroup.getTests())
			{
				ObserverTreeNode testNode = new ObserverTreeNode(test);
				addNodeToDefaultTreeModel(defaultTreeModel, groupNode, testNode);
			}
			addNodeToDefaultTreeModel(defaultTreeModel, root, groupNode);
			defaultTreeModel.nodeStructureChanged(  root  );
		}
	}
	
	/**
	 * Checks for group.
	 *
	 * @param funcGroupCode the func group code
	 * @return true, if successful
	 */
	public synchronized boolean hasGroup(String funcGroupCode)
	{		
		for (int i = 0; i < root.getChildCount(); i++) {
			ObserverTreeNode node = (ObserverTreeNode) root
					.getChildAt(i);

			if (!node.isLeaf()
					&& node.getUserObject() instanceof AutomationFunctionalGroup) {
				AutomationFunctionalGroup grp = (AutomationFunctionalGroup) node.getUserObject();
				if(grp.getAutoFunctionalGroupCode().equals(funcGroupCode))
				{
					return true;
				}				
			}
		}
		return false;
	}
	
	/**
	 * Checks for test.
	 *
	 * @param test the test
	 * @return true, if successful
	 */
	public synchronized boolean hasTest(AutomationTest test)
	{		
	
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) root
					.getChildAt(i);

			if (!node.isLeaf()
					&& node.getUserObject() instanceof AutomationFunctionalGroup) {
				for (int j = 0; j < node.getChildCount(); j++) {
					DefaultMutableTreeNode testNode = (ObserverTreeNode) node
							.getChildAt(j);
					if (testNode.getUserObject() instanceof AutomationTest) {
						if (test.compareTo((AutomationTest) testNode
								.getUserObject()) == 0) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
	
	
	/**
	 * Sort automation test tree.
	 *
	 * @param parentNode the parent node
	 */
	public synchronized void sortAutomationTestTree(DefaultMutableTreeNode parentNode)
	{
		defaultTreeModel.nodeStructureChanged(parentNode);
	}
	
	/**
	 * Sort automation functional group tree.
	 */
	public synchronized void sortAutomationFunctionalGroupTree()
	{
		defaultTreeModel.nodeStructureChanged(root);
	}
	
	/**
	 * Expand automation functional groups.
	 *
	 * @param expandedRows the expanded rows
	 */
	public synchronized void expandAutomationFunctionalGroups(Enumeration expandedRows)
	{
		while(expandedRows.hasMoreElements())
		{
			this.expandPath((TreePath) expandedRows.nextElement());
			
		}
	}
	
	/**
	 * Gets the all tests.
	 *
	 * @return the all tests
	 */
	public synchronized CopyOnWriteArrayList<AutomationTest> getAllTests()
	{
		CopyOnWriteArrayList<AutomationTest> list = new CopyOnWriteArrayList<AutomationTest>();
		for(int i = 0; i < root.getChildCount(); i++)
		{
			DefaultMutableTreeNode groupNode = (DefaultMutableTreeNode) root.getChildAt(i);
			for(int j = 0; j < groupNode.getChildCount(); j++)
			{
				ObserverTreeNode testNode = (ObserverTreeNode) groupNode.getChildAt(j);
				list.add((AutomationTest) testNode.getUserObject());
			}
		}
		return list;
	}
	
	/**
	 * Gets the all auto functional group tests.
	 *
	 * @param autoFunctionalGroupCode the auto functional group code
	 * @return the all auto functional group tests
	 */
	public synchronized CopyOnWriteArrayList<AutomationTest> getAllAutoFunctionalGroupTests(String autoFunctionalGroupCode)
	{
		CopyOnWriteArrayList<AutomationTest> autoFunctGroupTests = new CopyOnWriteArrayList<AutomationTest>();
		for(int i = 0; i < root.getChildCount(); i++)
		{
			DefaultMutableTreeNode groupNode = (DefaultMutableTreeNode) root.getChildAt(i);
			AutomationFunctionalGroup functGroup = (AutomationFunctionalGroup) groupNode.getUserObject();
			if(functGroup.getAutoFunctionalGroupCode().equals(autoFunctionalGroupCode))
			{
				for(int j = 0; j < groupNode.getChildCount(); j++)
				{
					ObserverTreeNode testNode = (ObserverTreeNode) groupNode.getChildAt(j);
					CollectionsUtil.addInOrder(autoFunctGroupTests, (AutomationTest)testNode.getUserObject());
				}
			}
		}
		return autoFunctGroupTests;
		
	}

	/**
	 * Gets the all functional group tests.
	 *
	 * @param functionalGroupCode the functional group code
	 * @return the all functional group tests
	 */
	public synchronized CopyOnWriteArrayList<AutomationTest> getAllFunctionalGroupTests(String functionalGroupCode)
	{
		CopyOnWriteArrayList<AutomationTest> functGroupTests = new CopyOnWriteArrayList<AutomationTest>();
		for(int i = 0; i < root.getChildCount(); i++)
		{
			DefaultMutableTreeNode groupNode = (DefaultMutableTreeNode) root.getChildAt(i);			
			for(int j = 0; j < groupNode.getChildCount(); j++)
			{
				ObserverTreeNode testNode = (ObserverTreeNode) groupNode.getChildAt(j);
				AutomationTest test = (AutomationTest) testNode.getUserObject();
				if(test.getFunctionalGroupCode().equals(functionalGroupCode))
				{
					CollectionsUtil.addInOrder(functGroupTests, (AutomationTest)testNode.getUserObject());
				}
			}			
		}
		return functGroupTests;
		
	}

	/**
	 * The Class SortTreeModel.
	 */
	class SortTreeModel extends DefaultTreeModel {
		  
  		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		
		/** The comparator. */
		private Comparator<TreeNode> comparator;

		  /**
  		 * Instantiates a new sort tree model.
  		 *
  		 * @param node the node
  		 * @param c the c
  		 */
  		public SortTreeModel(TreeNode node, Comparator c) {
		    super(node);
		    comparator = c;
		  }

		  /**
  		 * Instantiates a new sort tree model.
  		 *
  		 * @param node the node
  		 * @param asksAllowsChildren the asks allows children
  		 * @param c the c
  		 */
  		public SortTreeModel(TreeNode node, boolean asksAllowsChildren, Comparator c) {
		    super(node, asksAllowsChildren);
		    comparator = c;
		  }

		  /**
  		 * Insert node into.
  		 *
  		 * @param child the child
  		 * @param parent the parent
  		 */
  		public void insertNodeInto(MutableTreeNode child, MutableTreeNode parent) {
		    int index = findIndexFor(child, parent);
		    super.insertNodeInto(child, parent, index);
		  }

		  /* (non-Javadoc)
  		 * @see javax.swing.tree.DefaultTreeModel#insertNodeInto(javax.swing.tree.MutableTreeNode, javax.swing.tree.MutableTreeNode, int)
  		 */
  		@Override
		public void insertNodeInto(MutableTreeNode child, MutableTreeNode par, int i) {
		    // The index is useless in this model, so just ignore it.
		    insertNodeInto(child, par);
		  }

		  // Perform a recursive binary search on the children to find the right
		  /**
  		 * Find index for.
  		 *
  		 * @param child the child
  		 * @param parent the parent
  		 * @return the int
  		 */
  		// insertion point for the next node.
		  private int findIndexFor(MutableTreeNode child, MutableTreeNode parent) {
		    int cc = parent.getChildCount();
		    if (cc == 0) {
		      return 0;
		    }
		    if (cc == 1) {
		      return comparator.compare(child, parent.getChildAt(0)) <= 0 ? 0 : 1;
		    }
		    return findIndexFor(child, parent, 0, cc - 1); // First & last index
		  }

		  /**
  		 * Find index for.
  		 *
  		 * @param child the child
  		 * @param parent the parent
  		 * @param i1 the i1
  		 * @param i2 the i2
  		 * @return the int
  		 */
  		private int findIndexFor(MutableTreeNode child, MutableTreeNode parent,
		      int i1, int i2) {
		    if (i1 == i2) {
		      return comparator.compare(child, parent.getChildAt(i1)) <= 0 ? i1
		          : i1 + 1;
		    }
		    int half = (i1 + i2) / 2;
		    if (comparator.compare(child, parent.getChildAt(half)) <= 0) {
		      return findIndexFor(child, parent, i1, half);
		    }
		    return findIndexFor(child, parent, half + 1, i2);
		  }

	
		}

		//TreeStringComparator.java
		//This class compares the contents of the userObject as strings.
		//It's case-insensitive.
		//

		/**
		 * The Class TreeStringComparator.
		 */
		class TreeStringComparator implements Comparator {
		  
  		/* (non-Javadoc)
  		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
  		 */
  		@Override
		public int compare(Object o1, Object o2) {
		    if (!(o1 instanceof DefaultMutableTreeNode && o2 instanceof DefaultMutableTreeNode)) {
		      throw new IllegalArgumentException(
		          "Can only compare ObserverTreeNode objects");
		    }
		    String s1 = ((DefaultMutableTreeNode) o1).getUserObject().toString();
		    String s2 = ((DefaultMutableTreeNode) o2).getUserObject().toString();
		    return s1.compareToIgnoreCase(s2);
		  }
		}
	

}
