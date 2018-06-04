/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.TestDependency;
import gov.ca.calpers.psr.automation.directed.graph.EdgeTypeEnum;

/**
 * The Class DependencyConfigPanel.
 *
 * @author burban
 */
public class DependencyConfigPanel extends JPanel implements ItemListener{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The txt test name. */
	private JTextField txtTestName;
	
	/** The preferred height. */
	private final int preferredHeight = 90;
	
	/** The preferred width. */
	private final int preferredWidth = 538;
	
	/** The max height. */
	private final int maxHeight = preferredHeight;
	
	/** The max width. */
	private final int maxWidth = 32768;
	
	/** The static size. */
	private Dimension staticSize = new Dimension(preferredWidth, preferredHeight);
	
	/** The btn save. */
	private JButton btnSave;
	
	/** The btn remove. */
	private JButton btnRemove;
	
	/** The combo box. */
	private JComboBox comboBox;
	
	/** The original dependency type. */
	private String originalDependencyType;	
	
	/** The test dependency. */
	private TestDependency testDependency;
	
	/** The border. */
	private TitledBorder border;
	
	/** The child test. */
	private AutomationTest childTest;
	
	/** The main frame. */
	private JFrame mainFrame;
	
	/** The dependency panel. */
	private DependenciesPanel dependencyPanel;
	
	/** The action listener. */
	private ActionListener actionListener;
	
	/** The old item. */
	private Object oldItem = null;
	
	/** The new item. */
	private Object newItem = null;
	
	/**
	 * Instantiates a new dependency config panel.
	 *
	 * @param mainFrame the main frame
	 * @param dependencyPanel the dependency panel
	 */
	public DependencyConfigPanel(JFrame mainFrame, DependenciesPanel dependencyPanel) {
		this.dependencyPanel=dependencyPanel;		
		this.mainFrame = mainFrame;
		this.actionListener= dependencyPanel;
		setSize(staticSize);
		setMinimumSize(new Dimension(0,0));
		setPreferredSize(staticSize);
		setMaximumSize(new Dimension(maxWidth,maxHeight));
		border = new TitledBorder(null, "Dependency", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		setBorder(border);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
	
		JLabel lblDependsOnTest = new JLabel("Depends on Test:");
		springLayout.putConstraint(SpringLayout.NORTH, lblDependsOnTest, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblDependsOnTest, 10, SpringLayout.WEST, this);
		add(lblDependsOnTest);
		
		txtTestName = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtTestName, -3, SpringLayout.NORTH, lblDependsOnTest);
		springLayout.putConstraint(SpringLayout.EAST, txtTestName, -15, SpringLayout.EAST, this);
		add(txtTestName);
		txtTestName.setColumns(50);
		txtTestName.setEditable(false);
		
		JLabel lblDependencyType = new JLabel("Dependency Type:");
		springLayout.putConstraint(SpringLayout.NORTH, lblDependencyType, 16, SpringLayout.SOUTH, lblDependsOnTest);
		springLayout.putConstraint(SpringLayout.WEST, lblDependencyType, 0, SpringLayout.WEST, lblDependsOnTest);
		add(lblDependencyType);
		
		comboBox = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, txtTestName, 0, SpringLayout.WEST, comboBox);
		springLayout.putConstraint(SpringLayout.NORTH, comboBox, -3, SpringLayout.NORTH, lblDependencyType);
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 4, SpringLayout.EAST, lblDependencyType);
		add(comboBox);
		comboBox.addItem(EdgeTypeEnum.MUST_RUN.name());
		comboBox.addItem(EdgeTypeEnum.MUST_PASS.name());
		comboBox.setActionCommand("Change_Dependency_Type");
		comboBox.addItemListener(this);
		
		
		btnSave = new JButton("Save");		
		springLayout.putConstraint(SpringLayout.EAST, btnSave, -15, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, btnSave, -4, SpringLayout.NORTH, lblDependencyType);
		add(btnSave);
		btnSave.setActionCommand("Save_Dependency");
		btnSave.addActionListener(actionListener);
		btnSave.setEnabled(false);
		
		btnRemove = new JButton("Remove");
		btnRemove.setActionCommand("Remove_Dependency");
		btnRemove.addActionListener(actionListener);
		springLayout.putConstraint(SpringLayout.SOUTH, btnRemove, 0, SpringLayout.SOUTH, btnSave);
		springLayout.putConstraint(SpringLayout.EAST, btnRemove, -5, SpringLayout.WEST, btnSave);
		add(btnRemove);
	}

	
	/**
	 * Sets the dependency type.
	 *
	 * @param dependencyType the new dependency type
	 */
	public void setDependencyType(EdgeTypeEnum dependencyType)
	{
		comboBox.setSelectedItem(dependencyType.toString());
	}
	
	/**
	 * Sets the test name.
	 *
	 * @param testName the new test name
	 */
	public void setTestName(String testName)
	{
		txtTestName.setText(testName);
	}

	/**
	 * Gets the test dependency.
	 *
	 * @return the testDependency
	 */
	public TestDependency getTestDependency() {
		return testDependency;
	}

	/**
	 * Sets the test dependency.
	 *
	 * @param testDependency the testDependency to set
	 * @param parentTest the parent test
	 */
	public void setTestDependency(TestDependency testDependency, AutomationTest parentTest) {
		this.testDependency = testDependency;
		txtTestName.setText(parentTest.getTestName());
		border.setTitle("Dependency - " + testDependency.getDependencyId());
		comboBox.setSelectedItem(testDependency.getDependencyType().name());
		comboBox.setActionCommand("Change_Dependency_" + testDependency.getDependencyId());
		if(testDependency.getDependencyId()==0)
		{
			btnSave.setEnabled(true);
			btnSave.setActionCommand("Save_Dependency_" + testDependency.getDependencyId());				
		}
		btnSave.setActionCommand("Save_Dependency_" + testDependency.getDependencyId());
		btnRemove.setEnabled(true);
		btnRemove.setActionCommand("Remove_Dependency_" + testDependency.getDependencyId());	
			
		this.repaint();

	}
	
	/**
	 * Gets the child test.
	 *
	 * @return the childTest
	 */
	public AutomationTest getChildTest() {
		return childTest;
	}

	/**
	 * Sets the child test.
	 *
	 * @param childTest the childTest to set
	 */
	public void setChildTest(AutomationTest childTest) {
		this.childTest = childTest;
	}

	/**
	 * Gets the preferred height.
	 *
	 * @return the preferredHeight
	 */
	public int getPreferredHeight() {
		return preferredHeight;
	}

	/**
	 * Gets the preferred width.
	 *
	 * @return the preferredWidth
	 */
	public int getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * Gets the max height.
	 *
	 * @return the maxHeight
	 */
	public int getMaxHeight() {
		return maxHeight;
	}

	/**
	 * Gets the max width.
	 *
	 * @return the maxWidth
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()==comboBox)
		{
			
			 if(e.getStateChange() == ItemEvent.DESELECTED) 
			   {
			      System.out.println("Previous item: " + e.getItem());
			      oldItem = e.getItem();
			   }
			   else if(e.getStateChange() == ItemEvent.SELECTED)
			   {
			      System.out.println("Current \\ New item: " + e.getItem());
			      newItem = e.getItem();
			   }
			 
			 if(oldItem != null && newItem != null && !oldItem.equals(newItem))
			 {
				if(newItem.toString().equals(EdgeTypeEnum.MUST_PASS.name()))
				{
					testDependency.setDependencyType(EdgeTypeEnum.MUST_PASS);
				}else
				{
					testDependency.setDependencyType(EdgeTypeEnum.MUST_RUN);
				}				 
				btnSave.setEnabled(true);
			 }	 		
		}
	}
}
