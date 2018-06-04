/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.interfaces.NodeValueChanged;

/**
 * The Class RoadMapLite.
 *
 * @author burban
 */
public class RoadMapLite extends AutomationTestTreeForDisplay implements NodeValueChanged, Observer, MouseListener{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The test set. */
	private AutomationTestSet theTestSet;
	
	/** The view. */
	private ScenariosViewLite view;

	/**
	 * Instantiates a new road map lite.
	 *
	 * @param funcGroups the func groups
	 * @param testSet the test set
	 * @param view the view
	 */
	public RoadMapLite(List<AutomationFunctionalGroup> funcGroups, AutomationTestSet testSet, ScenariosViewLite view)
	{
		super(funcGroups, testSet);
		theTestSet=testSet;
		this.view=view;
		this.getRoot().setUserObject(theTestSet);
        theTestSet.addObserver(this);
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        for(AutomationTest test: theTestSet.getTestsAsList())
        {        	
        	this.addTestToTree(test);
        }
        this.addMouseListener(this);       
	}
	
	/**
	 * Sets the test set.
	 *
	 * @param testSet the new test set
	 */
	public void setTestSet(AutomationTestSet testSet)
	{
		theTestSet = testSet;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)){
            int selRow = this.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = this.getPathForLocation(e.getX(), e.getY());
                    this.setSelectionPath(selPath); 
                    if (selRow>-1){
                       this.setSelectionRow(selRow); 
                    }
        }
			
		 TreePath[] selectionPaths = getSelectionPaths();
         if(selectionPaths.length!=0)
         {
             TreePath selectionPath = selectionPaths[0];
         	DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
			CopyOnWriteArrayList<AutomationTest> selectedTests = new CopyOnWriteArrayList<AutomationTest>();
			if(lastNode.getUserObject() instanceof  AutomationFunctionalGroup)
			{
				AutomationFunctionalGroup currGroup = (AutomationFunctionalGroup) lastNode.getUserObject();
									
				for(AutomationTest test: theTestSet.getTestsAsList())
				{	
					if(test.getAutoFunctionalGroupCode().equals(currGroup.getAutoFunctionalGroupCode()))
					{
						selectedTests.add(test);
					}
					
				}
				List<AutomationTest> list = new ArrayList<AutomationTest>(selectedTests);
				Collections.sort(list);
			}else if(lastNode.getUserObject() instanceof AutomationTestSet)
			{
				AutomationTestSet testSet = (AutomationTestSet) lastNode.getUserObject();					
				for(AutomationTest test: testSet.getTestsAsList())
				{
					selectedTests.add(test);
				}    									
			}
			if(!selectedTests.isEmpty())
			{
				List<AutomationTest> list = new ArrayList<AutomationTest>(selectedTests);
				Collections.sort(list);
				CopyOnWriteArrayList<AutomationTest> returnList = new CopyOnWriteArrayList<AutomationTest>(list);
				view.setTestsList(returnList);
			}
         }
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see gov.ca.calpers.psr.automation.interfaces.NodeValueChanged#notifyChanged(java.lang.Object)
	 */
	@Override
	public void notifyChanged(Object associatedNode) {
		// TODO Auto-generated method stub
		
	}

}
