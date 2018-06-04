package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.jcip.annotations.NotThreadSafe;

/**
 * The Class CollectionsUtil.
 */
@NotThreadSafe
public class CollectionsUtil {

  /**
   * Adds the in order.
   *
   * @param <T> the generic type
   * @param list the list
   * @param item the item
   * @return the int
   */
  public static <T extends Comparable<T>> int addInOrder(final List<T> list, final T item) {
    final int insertAt;
    // The index of the search key, if it is contained in the list; otherwise, (-(insertion point) - 1).
    final int index = Collections.binarySearch(list, item);
    if (index < 0) {
      insertAt = -(index + 1);
    } else {
      insertAt = index + 1;
    }

    list.add(insertAt, item);
    return insertAt;
  }
 
  /**
   * Sort automation functional group tree.
   *
   * @param parent the parent
   */
  public static synchronized void sortAutomationFunctionalGroupTree(DefaultMutableTreeNode parent) {
	  int n = parent.getChildCount();
	  List< AutomationFunctionalGroup> children = new ArrayList< AutomationFunctionalGroup>(n);
	  for (int i = 0; i < n; i++) {
		DefaultMutableTreeNode node =  (DefaultMutableTreeNode) parent.getChildAt(i);
		AutomationFunctionalGroup group = (AutomationFunctionalGroup) node.getUserObject();		
	    children.add(group);
	  }
	  Collections.sort(children); //iterative merge sort
	  parent.removeAllChildren();
	  for (AutomationFunctionalGroup group: children) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(group);
		for(AutomationTest test : group.getTests())
		{
			DefaultMutableTreeNode testNode = new DefaultMutableTreeNode(test);
			node.add(testNode);
		}
	    parent.add(node);
	  }
	}
  
  /**
   * Sort automation test tree.
   *
   * @param parent the parent
   */
  public static synchronized void sortAutomationTestTree(DefaultMutableTreeNode parent) {
	  int n = parent.getChildCount();
	  List< AutomationTest> children = new ArrayList< AutomationTest>(n);
	  for (int i = 0; i < n; i++) {
		DefaultMutableTreeNode node =  (DefaultMutableTreeNode) parent.getChildAt(i);
		AutomationTest test = (AutomationTest) node.getUserObject();
	    children.add(test);
	  }
	  Collections.sort(children); //iterative merge sort
	  parent.removeAllChildren();
	  for (AutomationTest test: children) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(test); 
	    parent.add(node);
	  }
	}

  /**
   * Instantiates a new collections util.
   */
  private CollectionsUtil() {
  }
  
}
