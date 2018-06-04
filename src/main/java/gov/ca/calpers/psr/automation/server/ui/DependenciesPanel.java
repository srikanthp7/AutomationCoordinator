package gov.ca.calpers.psr.automation.server.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.TestDependency;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.ListSelectionModel;

/**
 * The Class DependenciesPanel.
 */
public class DependenciesPanel extends JPanel implements ActionListener, TreeSelectionListener{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The split pane. */
	private JSplitPane splitPane;
	
	/** The all tests panel. */
	private JPanel allTestsPanel;
	
	/** The main test panel. */
	private JPanel mainTestPanel;
	
	/** The test details panel. */
	private TestDetailsPanel testDetailsPanel;
	
	/** The test dependencies panel. */
	private JPanel testDependenciesPanel;
	
	/** The bottom panel. */
	private JPanel bottomPanel;
	
	/** The all tests list. */
	private JList allTestsList;

	/** The add button. */
	private JButton addButton;
	
	/** The medium button dimensions. */
	private Dimension mediumButtonDimensions = new Dimension(75,30);
	
	/** The selected test. */
	private AutomationTest selectedTest;
	
	/** The all tests. */
	private CopyOnWriteArrayList<AutomationTest> allTests;
	
	/** The all functional groups. */
	private CopyOnWriteArrayList<AutomationFunctionalGroup> allFunctionalGroups;
	
	/** The test tree. */
	private AutomationTestTreeForDisplay testTree;
	
	/** The scroll pane. */
	private JScrollPane scrollPane;
	
	/** The scroll pane panel. */
	private ScrollablePanel scrollPanePanel;
	
	/** The main frame. */
	private JFrame mainFrame;	

	/**
	 * Instantiates a new dependencies panel.
	 *
	 * @param allTests the all tests
	 * @param mainFrame the main frame
	 */
	public DependenciesPanel(CopyOnWriteArrayList<AutomationTest> allTests, JFrame mainFrame)
	{
		setPreferredSize(new Dimension(800, 500));
		setLayout(new BorderLayout(0,0));
		splitPane = new JSplitPane();
		this.allTests = allTests;
		this.mainFrame = mainFrame;
		setupAllTests();		
		createAllTestsPanel();
		createMainTestPanel();	
		splitPane.setDividerLocation(0.3);
		splitPane.setResizeWeight(0.3);
		splitPane.setDoubleBuffered(true);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(allTestsPanel);
		splitPane.setRightComponent(mainTestPanel);
		this.add(splitPane, BorderLayout.CENTER);				
	}
	
	/**
	 * Creates the all tests panel.
	 */
	private void createAllTestsPanel()
	{
		allTestsPanel = new JPanel();
		allTestsPanel.setBorder(new EmptyBorder(2, 2, 2, 1));
		allTestsPanel.setPreferredSize(new Dimension(300, 500));
		allTestsList = new JList();
		allTestsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		allTestsList.setBorder(new EmptyBorder(2, 2, 2, 2));
		testTree = new AutomationTestTreeForDisplay(allFunctionalGroups, "All Tests");
		testTree.addTreeSelectionListener(this);
		testTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		testTree.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		allTestsPanel.setLayout(new BorderLayout(0,0));
		JScrollPane scroll = new JScrollPane(testTree);		
		allTestsPanel.add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * Creates the main test panel.
	 */
	private void createMainTestPanel()
	{
		mainTestPanel = new JPanel(new BorderLayout(0,0));
		mainTestPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
		createTestDetailsPanel();
		mainTestPanel.add(testDetailsPanel, BorderLayout.NORTH);
		createTestDependenciesPanel();
		mainTestPanel.add(testDependenciesPanel, BorderLayout.CENTER);
		createBottomPanel();
		mainTestPanel.add(bottomPanel, BorderLayout.SOUTH);		
	}
	
	/**
	 * Creates the test details panel.
	 */
	private void createTestDetailsPanel()
	{
		testDetailsPanel = new TestDetailsPanel();
		testDetailsPanel.setBorder(new TitledBorder("Test Details"));
		
		
	}
	
	/**
	 * Creates the test dependencies panel.
	 */
	private void createTestDependenciesPanel()
	{
		testDependenciesPanel = new JPanel();
		testDependenciesPanel.setBorder(new TitledBorder("Parent Test Dependencies"));
		GridLayout gridLayout = new GridLayout(1,1);
		testDependenciesPanel.setLayout(gridLayout);
		scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(0,0));
		scrollPanePanel = new ScrollablePanel();
		scrollPanePanel.setPreferredSize(new Dimension(500,300));
		scrollPanePanel.setMinimumSize(new Dimension(10,10));
		BoxLayout box = new BoxLayout(scrollPanePanel, BoxLayout.Y_AXIS);
		scrollPanePanel.setLayout(box);
		scrollPane.setViewportView(scrollPanePanel);		
		testDependenciesPanel.add(scrollPane);		
	}
	
	/**
	 * Creates the bottom panel.
	 */
	private void createBottomPanel()
	{
		bottomPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(bottomPanel, BoxLayout.X_AXIS);
		bottomPanel.setLayout(boxLayout);		
		addButton = new JButton("Add");
		addButton.setActionCommand(ApplicationCommands.ADD.toString());
		addButton.addActionListener(this);
		addButton.setEnabled(false);		
		addButton.setPreferredSize(mediumButtonDimensions);
		addButton.setMargin(new Insets(1, 0, 1, 0));
		bottomPanel.setBorder(new EmptyBorder(0, 0, 0, 4));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(addButton);		
	}
	
	/**
	 * Populate main test panel.
	 */
	private synchronized void populateMainTestPanel()
	{
		int totalHeight = 0;
		
		if(selectedTest != null)
		{
			testDetailsPanel.setAutomationTest(selectedTest);			
			scrollPanePanel.removeAll();
			scrollPanePanel.revalidate();
			scrollPane.revalidate();
			scrollPanePanel.repaint();
			scrollPane.repaint();
			for(TestDependency testDep : selectedTest.getTestDependencies())
			{				
				DependencyConfigPanel config = new DependencyConfigPanel(mainFrame, this);
				config.setChildTest(selectedTest);
				AutomationTest parentTest = null;
				for(AutomationTest test: allTests)
				{
					if(test.getTestId()==testDep.getParentTestId())
					{
						parentTest = test;
					}
				}
				if(parentTest!=null)
				{
					config.setTestDependency(testDep, parentTest);			
					scrollPanePanel.add(config);
					totalHeight = totalHeight + config.getHeight();
				}else
				{
					System.out.println("Could not find parent test of dependency using test id: " + testDep.getParentTestId());
				}
				
			}
			if(scrollPanePanel.getComponents().length == 0)
			{
				JPanel temp = new JPanel();				
				BoxLayout box = new BoxLayout(temp, BoxLayout.X_AXIS);
				temp.setLayout(box);				
				temp.add(Box.createHorizontalGlue());
				temp.add(new JLabel("No Dependencies"));				
				temp.add(Box.createHorizontalGlue());
				scrollPanePanel.add(temp);
			}			
			scrollPanePanel.add(Box.createVerticalGlue());
			if(totalHeight < 90)
			{
				totalHeight = 90;
			}
			scrollPanePanel.setSize(scrollPanePanel.getWidth(), totalHeight);
			scrollPanePanel.setPreferredSize(scrollPanePanel.getSize());
			scrollPanePanel.revalidate();
			scrollPane.revalidate();
		}
		
	}
	
	/**
	 * Setup all tests.
	 */
	private void setupAllTests()
	{		
		buildAutoFunctionalGroupTestLists();		
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
					Set<TestDependency> dependencies = test.getTestDependencies();					
					grp.addTest(test);
				}
			}
		}
	}
	
	/**
	 * Removes the dependency config panel.
	 *
	 * @param panel the panel
	 */
	public void removeDependencyConfigPanel(DependencyConfigPanel panel)	
	{
		scrollPanePanel.remove(panel);
		mainFrame.pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="ADD")
		{			
			TestSelectionDialog dialog = new TestSelectionDialog(selectedTest, allTests);
			dialog.setLocationRelativeTo(null);
			dialog.setTitle("Select Parent Test For Dependency");
			AutomationTest parentTest=dialog.showDialog();
			if(parentTest !=null)
			{				
				TestDependency newDep = new TestDependency();
				newDep.setAutomationScriptId(selectedTest.getTestId());
				newDep.setParentTestId(parentTest.getTestId());
				newDep.save();
				selectedTest.addTestDependency(newDep);
				populateMainTestPanel();
			}			
		}
		if(e.getActionCommand().contains("Save_Dependency"))
		{
			//Save/update Dependency to DB.
			long id = Long.parseLong((e.getActionCommand().substring(e.getActionCommand().lastIndexOf("_") + 1)));
			for(TestDependency dep : selectedTest.getTestDependencies())
			{
				if(dep.getDependencyId() == id)
				{
					TestDependency temp = dep.save();
					dep.setDependencyId(temp.getDependencyId());
					break;					
				}				
				populateMainTestPanel();				
			}
			((JButton)e.getSource()).setEnabled(false);			
		}else if(e.getActionCommand().equals("Change_Dependency_Type"))
		{
			//Save/update Dependency to DB.
			long id = Long.parseLong((e.getActionCommand().substring(e.getActionCommand().lastIndexOf("_") + 1)));
			for(TestDependency dep : selectedTest.getTestDependencies())
			{
				if(dep.getDependencyId() == id)
				{
					//selectedTest.removeTestDependency(dep);
					TestDependency temp = dep.save();
					dep.setDependencyId(temp.getDependencyId());
					break;					
				}
				populateMainTestPanel();				
			}
			
		}else if(e.getActionCommand().contains("Remove_Dependency"))
		{
			long id = Long.parseLong((e.getActionCommand().substring(e.getActionCommand().lastIndexOf("_") + 1)));
			//Delete Dependency
			for(TestDependency dep : selectedTest.getTestDependencies())
			{
				if(dep.getDependencyId() == id)
				{
					selectedTest.removeTestDependency(dep);
					TestDependency.delete(dep);
					break;					
				}
			}
			populateMainTestPanel();
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		 DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                 testTree.getLastSelectedPathComponent();
		 /* if nothing is selected */		 
	     if (node == null) return;
	    /* retrieve the node that was selected */ 
	     Object nodeObject = node.getUserObject();
	     if(nodeObject instanceof AutomationTest)
	     {
	    	 setSelectedTest((AutomationTest) nodeObject);
	    	 addButton.setEnabled(true);
	     }else
	     {
	    	 addButton.setEnabled(false);
	    	 return;
	     }	     
	     populateMainTestPanel();
	    /* React to the node selection. */		 
	}
	
	/**
	 * Sets the selected test.
	 *
	 * @param test the new selected test
	 */
	private synchronized void setSelectedTest(AutomationTest test)
	{
		selectedTest = test;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	@Override
	public void repaint()
	{
		populateMainTestPanel();
		super.repaint();
	}

}
