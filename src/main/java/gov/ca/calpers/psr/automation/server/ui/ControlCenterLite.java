/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.TestSetRunStatus;
import javax.swing.JLabel;

/**
 * The Class ControlCenterLite.
 *
 * @author burban
 */
public class ControlCenterLite extends JPanel implements Observer{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The test set. */
	private AutomationTestSet theTestSet;
	
	/** The center panel. */
	private JSplitPane centerPanel;
	
	/** The table view. */
	private ScenariosViewLite tableView;
	
	/** The test tree. */
	private RoadMapLite testTree;
	
	/** The bottom panel. */
	private JPanel bottomPanel;
	
	/** The lbl test set status value. */
	private JLabel lblTestSetStatusValue;
	
	/** The status ready to run. */
	private final String statusReadyToRun = "<html><b><font color=\"#0000FF\">Ready to Run</font></b></html>";    
    
    /** The status running. */
    private final String statusRunning = "<html><b><font color=\"#FFA500\">Running</font></b></html>";    
    
    /** The status finished. */
    private final String statusFinished = "<html><b><font color=\"#008000\">Finished</font></b></html>";
	
	/**
	 * 	 
	 *
	 * @param testSet the test set
	 */
	public ControlCenterLite(AutomationTestSet testSet) 
	{
		super();
		theTestSet=testSet;		
		theTestSet.addObserver(this);
		setLayout(new BorderLayout(0,0));
		centerPanel = new JSplitPane();
        centerPanel.setOneTouchExpandable(true);
        tableView = new ScenariosViewLite(theTestSet, theTestSet.getTestsAsList());
        testTree = new RoadMapLite(new CopyOnWriteArrayList<AutomationFunctionalGroup>(), theTestSet, tableView);        
        testTree.setTestSet(theTestSet);        
        centerPanel.setLeftComponent(new JScrollPane(testTree));        
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        centerPanel.setRightComponent(new JScrollPane(tableView));
        centerPanel.setLeftComponent(new JScrollPane(testTree));
        add(centerPanel, BorderLayout.CENTER);
        centerPanel.setDividerLocation(300);
        
        SpringLayout sl_bottomPanel = new SpringLayout();
        bottomPanel = new JPanel(sl_bottomPanel);
        bottomPanel.setPreferredSize(new Dimension(800, 30));
        add(bottomPanel, BorderLayout.SOUTH);
        
        JLabel lblTestSetStatus = new JLabel("Test Set Status:");
        sl_bottomPanel.putConstraint(SpringLayout.NORTH, lblTestSetStatus, 7, SpringLayout.NORTH, bottomPanel);
        sl_bottomPanel.putConstraint(SpringLayout.WEST, lblTestSetStatus, 10, SpringLayout.WEST, bottomPanel);
        bottomPanel.add(lblTestSetStatus);
        
        lblTestSetStatusValue = new JLabel("");
        sl_bottomPanel.putConstraint(SpringLayout.NORTH, lblTestSetStatusValue, 0, SpringLayout.NORTH, lblTestSetStatus);
        sl_bottomPanel.putConstraint(SpringLayout.WEST, lblTestSetStatusValue, 6, SpringLayout.EAST, lblTestSetStatus);
        bottomPanel.add(lblTestSetStatusValue);
        
        if(theTestSet.getRunStatus().equals(TestSetRunStatus.READY_TO_RUN.toString()))
		{
			lblTestSetStatusValue.setText(statusReadyToRun);
		}else if(theTestSet.getRunStatus().equals(TestSetRunStatus.RUNNING.toString()))
		{
			lblTestSetStatusValue.setText(statusRunning);
		}else if(theTestSet.getRunStatus().equals(TestSetRunStatus.COMPLETE.toString()))
		{
			lblTestSetStatusValue.setText(statusFinished);
		}else
		{
			lblTestSetStatusValue.setText(theTestSet.getRunStatus());
		}
		
		
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if(theTestSet.getRunStatus().equals(TestSetRunStatus.READY_TO_RUN.toString()))
		{
			lblTestSetStatusValue.setText(statusReadyToRun);
		}else if(theTestSet.getRunStatus().equals(TestSetRunStatus.RUNNING.toString()))
		{
			lblTestSetStatusValue.setText(statusRunning);
		}else if(theTestSet.getRunStatus().equals(TestSetRunStatus.COMPLETE.toString()))
		{
			lblTestSetStatusValue.setText(statusFinished);
		}else
		{
			lblTestSetStatusValue.setText(theTestSet.getRunStatus());
		}
		
	}
	

}
