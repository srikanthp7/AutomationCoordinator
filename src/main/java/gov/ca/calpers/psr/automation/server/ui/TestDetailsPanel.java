package gov.ca.calpers.psr.automation.server.ui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;

import gov.ca.calpers.psr.automation.AutomationTest;

import java.awt.Dimension;

/**
 * The Class TestDetailsPanel.
 */
public class TestDetailsPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The txt test name. */
	private JTextField txtTestName;
	
	/** The txt test id. */
	private JTextField txtTestId;
	
	/** The test. */
	private AutomationTest theTest;
	
	/**
	 * Instantiates a new test details panel.
	 */
	public TestDetailsPanel() {
		setPreferredSize(new Dimension(450, 80));
		setBorder(new TitledBorder(null, "Test Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblTestName = new JLabel("Test Name:");
		lblTestName.setBounds(197, 25, 55, 14);
		add(lblTestName);
		
		txtTestName = new JTextField();
		txtTestName.setEditable(false);
		txtTestName.setText("No Test Selected");
		springLayout.putConstraint(SpringLayout.NORTH, lblTestName, 3, SpringLayout.NORTH, txtTestName);
		springLayout.putConstraint(SpringLayout.EAST, lblTestName, -6, SpringLayout.WEST, txtTestName);
		springLayout.putConstraint(SpringLayout.NORTH, txtTestName, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, txtTestName, 71, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, txtTestName, -204, SpringLayout.EAST, this);
		add(txtTestName);
		txtTestName.setColumns(10);
		
		JLabel lblTestId = new JLabel("Test ID:");
		springLayout.putConstraint(SpringLayout.WEST, lblTestId, 0, SpringLayout.WEST, lblTestName);
		add(lblTestId);
		
		txtTestId = new JTextField();
		txtTestId.setEditable(false);
		txtTestId.setText("0");
		springLayout.putConstraint(SpringLayout.NORTH, lblTestId, 3, SpringLayout.NORTH, txtTestId);
		springLayout.putConstraint(SpringLayout.NORTH, txtTestId, 6, SpringLayout.SOUTH, txtTestName);
		springLayout.putConstraint(SpringLayout.WEST, txtTestId, 0, SpringLayout.WEST, txtTestName);
		springLayout.putConstraint(SpringLayout.EAST, txtTestId, 126, SpringLayout.WEST, this);
		add(txtTestId);
		txtTestId.setColumns(10);
	}

	/**
	 * Gets the automation test.
	 *
	 * @return the theTest
	 */
	public AutomationTest getAutomationTest() {
		return theTest;
	}

	/**
	 * Sets the automation test.
	 *
	 * @param aTest the new automation test
	 */
	public void setAutomationTest(AutomationTest aTest) {
		this.theTest = aTest;
		txtTestId.setText(String.valueOf(theTest.getTestId()));
		txtTestName.setText(theTest.getTestName());
	}
}
