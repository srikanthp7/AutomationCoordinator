package gov.ca.calpers.psr.automation.server.ui;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;

import java.awt.Dimension;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Class TestSelectionDialog.
 */
public class TestSelectionDialog extends JDialog implements ActionListener,TreeSelectionListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The scroll pane. */
	private JScrollPane scrollPane;
	
	/** The bottom panel. */
	private JPanel bottomPanel;
	
	/** The all tests tree. */
	private AutomationTestTreeForDisplay allTestsTree;
	
	/** The all functional groups list. */
	private CopyOnWriteArrayList<AutomationFunctionalGroup> allFunctionalGroups;
	
	/** The all tests list. */
	private CopyOnWriteArrayList<AutomationTest> allTests;
	
	/** The child test. */
	private AutomationTest childTest;
	
	/** The btn select test. */
	private JButton btnSelectTest;
		
	/** The selected test. */
	private AutomationTest selectedTest = null;
	
	/**
	 * Instantiates a new test selection dialog.
	 *
	 * @param test the test
	 * @param allTests the all tests
	 */
	public TestSelectionDialog(AutomationTest test, CopyOnWriteArrayList<AutomationTest> allTests) {
		setSize(new Dimension(400, 400));
		setPreferredSize(new Dimension(400, 400));
		setModal(true);
		
		
		bottomPanel = new JPanel();
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		
		Component horizontalGlue = Box.createHorizontalGlue();
		bottomPanel.add(horizontalGlue);		
		
		btnSelectTest = new JButton("Select Test");
		btnSelectTest.setActionCommand("Select_Test");
		btnSelectTest.addActionListener(this);
		bottomPanel.add(btnSelectTest);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("Cancel");
		btnCancel.addActionListener(this);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setPreferredSize(new Dimension(5, 0));
		horizontalStrut.setMaximumSize(new Dimension(5, 32767));
		horizontalStrut.setMinimumSize(new Dimension(5, 0));
		bottomPanel.add(horizontalStrut);
		bottomPanel.add(btnCancel);
		
		childTest = test;
		this.allTests = allTests;
		this.buildAutoFunctionalGroupTestLists();		
		allTestsTree = new AutomationTestTreeForDisplay(allFunctionalGroups,"All Tests");
		allTestsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		allTestsTree.removeTestFromTree(childTest);
		
		scrollPane = new JScrollPane(allTestsTree);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		
	}
	
	/**
	 * Builds the auto functional group test lists.
	 */
	private void buildAutoFunctionalGroupTestLists()
	{
		allFunctionalGroups = (CopyOnWriteArrayList<AutomationFunctionalGroup>) AutomationFunctionalGroup.getAll();
		for(AutomationFunctionalGroup grp : allFunctionalGroups)
		{
			for(AutomationTest test : allTests)
			{
				if(test.getAutoFunctionalGroupCode().equals(grp.getAutoFunctionalGroupCode()))
				{					
					grp.addTest(test);
				}
			}
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Cancel"))
		{
			dispose();
		}else if(e.getActionCommand().equals("Select_Test"))
		{
			if(!allTestsTree.isSelectionEmpty())
			{
				TreePath path = allTestsTree.getSelectionPath();
				DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				if(lastNode.getUserObject() instanceof AutomationTest)
				{
					selectedTest = (AutomationTest) lastNode.getUserObject();
					setVisible(false);
					dispose();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(!allTestsTree.isSelectionEmpty())
		{
			TreePath path = allTestsTree.getSelectionPath();
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if(lastNode.getUserObject() instanceof AutomationTest)
			{
				btnSelectTest.setEnabled(true);
			}
		}else
		{
			btnSelectTest.setEnabled(false);
		}		
	}
	
	/**
	 * Show dialog.
	 *
	 * @return the automation test
	 */
	public AutomationTest showDialog() {
	    
		setVisible(true);
	    return selectedTest;
	}

}
