package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.server.ui.TestSetSettingsPanel.TextFieldDocumentFilter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import org.apache.logging.log4j.LogManager;

/**
 * The Class TestSetSettingsPanel.
 */
public class TestSetSettingsPanel extends JPanel implements Observer, DocumentListener, FocusListener, ActionListener, ChangeListener{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The test set name text field. */
	private JTextField testSetNameTextField;
	
	/** The num failed retries text field. */
	private JTextField numFailedRetriesTextField;
	
	/** The test set name document. */
	private Document testSetNameDocument;
	
	/** The num failed retries document. */
	private Document numFailedRetriesDocument;
	
	/** The round document. */
	private Document roundDocument;
	
	/** The release document. */
	private Document releaseDocument;
	
	/** The alm path document. */
	private Document almPathDocument;
	
	/** The test set. */
	private AutomationTestSet testSet;
	
	/** The original test name. */
	private String originalTestName;
	
	/** The original num retries. */
	private int originalNumRetries;
	
	/** The original release. */
	private String originalRelease;
	
	/** The original round. */
	private String originalRound;
	
	/** The txt alm path. */
	private JTextField txtAlmPath;
	
	/** The original alm path. */
	private String originalALMPath;
	
	/** The txt release. */
	private JTextField txtRelease;
	
	/** The root path. */
	private final String rootPath = "Root";
	
	/** The regression suite path. */
	private final String regressionSuitePath = "Regression Suite - Automation";
	
	/** The test set name. */
	private String testSetName;
	
	/** The release. */
	private String release = "";
	
	/** The round of regression. */
	private String roundOfRegression = "";
	
	/** The txt round. */
	private JTextField txtRound;	
	
	/** The chckbx hard stop roll. */
	private JCheckBox chckbxHardStopRoll;
	
	/** The lbl number of selected tests. */
	private JLabel lblNumberOfSelectedTests;
	
	/** The number of selected tests. */
	private int numberOfSelectedTests = 0;
	
	/** The total number of tests. */
	private int totalNumberOfTests = 0;
	
	/** The test set name filter. */
	private TextFieldDocumentFilter testSetNameFilter;
	
	/** The release filter. */
	private TextFieldDocumentFilter releaseFilter;
	
	/** The round filter. */
	private TextFieldDocumentFilter roundFilter;
	
	/** The alm path filter. */
	private TextFieldDocumentFilter almPathFilter;
	
	/** The num retries filter. */
	private TextFieldDocumentFilter numRetriesFilter;
	
	/** The log. */
	private static org.apache.logging.log4j.Logger log = LogManager.getLogger(TestSetSettingsPanel.class.getName());
	
	/**
	 * Instantiates a new test set settings panel.
	 *
	 * @param testSet the test set
	 */
	public TestSetSettingsPanel(AutomationTestSet testSet) {
		testSetName = testSet.getTestSetName();
		roundOfRegression = testSet.getRound();
		release = testSet.getRelease();

		setPreferredSize(new Dimension(549, 172));
		setBorder(new TitledBorder(null, "Test Set Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));	
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblTestSetName = new JLabel("Test Set Name:");
		add(lblTestSetName);
		
		JLabel lblNumberOfFailed = new JLabel("<HTML>Number of runs before marking Failed:<FONT COLOR=RED>*</FONT></HTML>");		
		springLayout.putConstraint(SpringLayout.NORTH, lblTestSetName, 11, SpringLayout.SOUTH, lblNumberOfFailed);
		springLayout.putConstraint(SpringLayout.WEST, lblTestSetName, 0, SpringLayout.WEST, lblNumberOfFailed);
		springLayout.putConstraint(SpringLayout.WEST, lblNumberOfFailed, 2, SpringLayout.WEST, this);
		add(lblNumberOfFailed);
		
		testSetNameTextField = new JTextField();		
		springLayout.putConstraint(SpringLayout.NORTH, testSetNameTextField, -3, SpringLayout.NORTH, lblTestSetName);
		testSetNameDocument = testSetNameTextField.getDocument();		
		testSetNameFilter = new TextFieldDocumentFilter();
		((AbstractDocument)testSetNameDocument).setDocumentFilter(testSetNameFilter);
		add(testSetNameTextField);
		testSetNameTextField.setColumns(35);
		testSetNameTextField.addFocusListener(this);
		testSetNameDocument = testSetNameTextField.getDocument(); 
		testSetNameDocument.addDocumentListener(this);
				
		numFailedRetriesTextField = new JTextField();
		if(testSet.getRetryLimit() > 0)
		{
			numFailedRetriesTextField.setText(String.valueOf(testSet.getRetryLimit()));			
		}
		springLayout.putConstraint(SpringLayout.WEST, testSetNameTextField, 0, SpringLayout.WEST, numFailedRetriesTextField);
		springLayout.putConstraint(SpringLayout.NORTH, numFailedRetriesTextField, -3, SpringLayout.NORTH, lblNumberOfFailed);
		numFailedRetriesDocument = numFailedRetriesTextField.getDocument();		
		numRetriesFilter = new TextFieldDocumentFilter();		
		((AbstractDocument)numFailedRetriesDocument).setDocumentFilter(numRetriesFilter);
		numFailedRetriesDocument.addDocumentListener(this);
		numFailedRetriesTextField.setColumns(2);
		numFailedRetriesTextField.addFocusListener(this);
		numFailedRetriesDocument = numFailedRetriesTextField.getDocument();
		numFailedRetriesDocument.addDocumentListener(this);		
		add(numFailedRetriesTextField);
		
		JLabel lblAlmPath = new JLabel("ALM Test Set Path:");
		springLayout.putConstraint(SpringLayout.NORTH, lblAlmPath, 37, SpringLayout.SOUTH, lblNumberOfFailed);
		springLayout.putConstraint(SpringLayout.WEST, lblAlmPath, 2, SpringLayout.WEST, this);
		add(lblAlmPath);
		
		txtAlmPath = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtAlmPath, 6, SpringLayout.SOUTH, testSetNameTextField);
		springLayout.putConstraint(SpringLayout.WEST, txtAlmPath, 0, SpringLayout.WEST, testSetNameTextField);
		springLayout.putConstraint(SpringLayout.EAST, txtAlmPath, 345, SpringLayout.EAST, lblAlmPath);
		txtAlmPath.addFocusListener(this);
		add(txtAlmPath);
		txtAlmPath.setColumns(75);
		
		JLabel lblRound = new JLabel("<HTML>Round:<FONT COLOR=RED>*</FONT></HTML>");
		springLayout.putConstraint(SpringLayout.NORTH, lblNumberOfFailed, 13, SpringLayout.SOUTH, lblRound);
		springLayout.putConstraint(SpringLayout.WEST, lblRound, 2, SpringLayout.WEST, this);
		add(lblRound);
			
		JLabel lblRelease = new JLabel("<HTML>Release:<FONT COLOR=RED>*</FONT></HTML>");
		springLayout.putConstraint(SpringLayout.WEST, lblRelease, 2, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.NORTH, lblRound, 15, SpringLayout.SOUTH, lblRelease);
		springLayout.putConstraint(SpringLayout.NORTH, lblRelease, 0, SpringLayout.NORTH, this);
		add(lblRelease);
		
		txtRelease = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtRelease, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, txtRelease, 155, SpringLayout.EAST, lblRelease);
		
		txtRelease.addFocusListener(this);
		add(txtRelease);
		txtRelease.setColumns(10);
		txtRelease.setEnabled(true);
		txtRelease.setText(testSet.getRelease());
		releaseDocument = txtRelease.getDocument();		
		releaseFilter = new TextFieldDocumentFilter();
		((AbstractDocument)releaseDocument).setDocumentFilter(releaseFilter);
		releaseDocument.addDocumentListener(this);
		
		txtRound= new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, numFailedRetriesTextField, 0, SpringLayout.WEST, txtRound);
		springLayout.putConstraint(SpringLayout.WEST, txtRound, 0, SpringLayout.WEST, txtRelease);
		springLayout.putConstraint(SpringLayout.SOUTH, txtRound, -6, SpringLayout.NORTH, numFailedRetriesTextField);
		add(txtRound);
		txtRound.addFocusListener(this);
		txtRound.setColumns(10);
		txtRound.setText(testSet.getRound());
		roundDocument = txtRound.getDocument();		
		roundFilter = new TextFieldDocumentFilter();
		((AbstractDocument)roundDocument).setDocumentFilter(roundFilter);
		roundDocument.addDocumentListener(this);
		
		JLabel lblV = new JLabel("v");
		springLayout.putConstraint(SpringLayout.SOUTH, lblV, -2, SpringLayout.SOUTH, txtRelease);
		springLayout.putConstraint(SpringLayout.EAST, lblV, -3, SpringLayout.WEST, txtRelease);
		add(lblV);
		
		chckbxHardStopRoll = new JCheckBox("Hard Stop Roll");
		springLayout.putConstraint(SpringLayout.NORTH, chckbxHardStopRoll, -4, SpringLayout.NORTH, lblNumberOfFailed);
		springLayout.putConstraint(SpringLayout.WEST, chckbxHardStopRoll, 6, SpringLayout.EAST, numFailedRetriesTextField);
		chckbxHardStopRoll.addChangeListener(this);
		add(chckbxHardStopRoll);
		
		lblNumberOfSelectedTests = new JLabel("0 / 1 Tests Selected");
		springLayout.putConstraint(SpringLayout.NORTH, lblNumberOfSelectedTests, 6, SpringLayout.SOUTH, txtAlmPath);
		springLayout.putConstraint(SpringLayout.WEST, lblNumberOfSelectedTests, 0, SpringLayout.WEST, testSetNameTextField);
		add(lblNumberOfSelectedTests);
		setTestSet(testSet);
	}
	
	/**
	 * Sets the document listener.
	 *
	 * @param dl the new document listener
	 */
	public void setDocumentListener(DocumentListener dl)
	{
		testSetNameDocument.addDocumentListener(dl);
		numFailedRetriesDocument.addDocumentListener(dl);
	}
	
	/**
	 * Sets the test set.
	 *
	 * @param ts the new test set
	 */
	public void setTestSet(AutomationTestSet ts)
	{
		if(ts != null)
		{
			testSet = ts;
			setTestSetValues();
		}
		this.testSet.addObserver(this);
		this.update();
	}
	
	/**
	 * Gets the test set.
	 *
	 * @return the test set
	 */
	public AutomationTestSet getTestSet()
	{
		return testSet;
	}
	
	/**
	 * Gets the number of retries.
	 *
	 * @return the number of retries
	 */
	public int getNumberOfRetries()
	{
		int value = -1;
		
		try{
			value = Integer.valueOf(numFailedRetriesTextField.getDocument().toString());
		}catch(NumberFormatException ex)
		{
			System.out.print("NumberFormatException Caught: " + ex.getMessage() + "\n\nStack Trace: " + ex.getStackTrace().toString());
			log.error("NumberFormatException Caught: " + ex);			
		}		
		return value;
	}
	
	/**
	 * Sets the number of retries.
	 *
	 * @param value the new number of retries
	 */
	public void setNumberOfRetries(int value)
	{
		numFailedRetriesTextField.setText(String.valueOf(value));
		testSet.setRetryLimit(value);
	}

	/**
	 * Gets the number of selected tests.
	 *
	 * @return the numberOfSelectedTests
	 */
	public int getNumberOfSelectedTests() {
		return numberOfSelectedTests;
	}

	/**
	 * Sets the number of selected tests.
	 *
	 * @param numberOfSelectedTests the numberOfSelectedTests to set
	 */
	public void setNumberOfSelectedTests(int numberOfSelectedTests) {
		this.numberOfSelectedTests = numberOfSelectedTests;
		constructNumberOfSelectedTestsLabel();
	}

	/**
	 * Gets the total number of tests.
	 *
	 * @return the totalNumberOfTests
	 */
	public int getTotalNumberOfTests() {
		return totalNumberOfTests;
	}

	/**
	 * Sets the total number of tests.
	 *
	 * @param totalNumberOfTests the totalNumberOfTests to set
	 */
	public void setTotalNumberOfTests(int totalNumberOfTests) {
		this.totalNumberOfTests = totalNumberOfTests;
		constructNumberOfSelectedTestsLabel();
	}
	
	/**
	 * Update.
	 */
	private synchronized void update()
	{		
		numberOfSelectedTests = testSet.getTests().size();
		constructNumberOfSelectedTestsLabel();
		this.repaint();
	}
	
	/**
	 * Builds the alm path string.
	 *
	 * @return the string
	 */
	private synchronized String buildALMPathString()
	{
		String finalPath;
		finalPath = rootPath 
				  + "\\" + regressionSuitePath ;		
		if(release.trim().length() != 0)
		{
			  finalPath = finalPath + "\\v" + release;
		}		
			
		finalPath = finalPath + "\\" ;
		return finalPath;
	}
	
	/**
	 * Construct number of selected tests label.
	 */
	private void constructNumberOfSelectedTestsLabel()
	{
		lblNumberOfSelectedTests.setText(String.valueOf(numberOfSelectedTests) + "/" + String.valueOf(totalNumberOfTests) + " Tests Selected.");
	}
	
	/**
	 * Checks if is validated.
	 *
	 * @return true, if is validated
	 */
	public boolean isValidated()
	{		
		if(txtRelease.getText().trim().isEmpty() || 
		   txtRound.getText().trim().isEmpty())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validate number of retries text field.
	 *
	 * @return true, if successful
	 */
	private boolean validateNumberOfRetriesTextField()
	{
		try{
			testSet.setRetryLimit(Integer.parseInt(numFailedRetriesTextField.getText()));
			return true;
		}catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(null,
			          "Error: Please enter a numberic value for Number of failed test retries", "Error Message",
			          JOptionPane.ERROR_MESSAGE);
			numFailedRetriesTextField.requestFocus();
			return false;
		}
	}
	
	/**
	 * Validate release text field.
	 *
	 * @return true, if successful
	 */
	private boolean validateReleaseTextField()
	{
		String text = txtRelease.getText().trim();
		if(text.matches("([0-9a-zA-Z]+(\\.[0-9a-zA-Z]+)?)+"))
		{						
			if(!text.matches("([0-9]+(\\.[0-9]+)?)+"))
			{
				System.out.println("There is an alpha character.\nAre you sure this is a valid Release?");
				log.warn("There is an alpha character.\nAre you sure this is a valid Release?");
				int reply = JOptionPane.showConfirmDialog(null, "There is an alpha character.\nAre you sure this is a valid Release?", "Warning!", JOptionPane.YES_NO_OPTION);
				 if (reply == JOptionPane.NO_OPTION) {
						return false;
			        }					        
			}			
		}else
		{
			System.out.println("String has Character that isn't alpha-numeric or a '.'");
			log.error("String has Character that isn't alpha-numeric or a '.'");
		}
		return true;
	}
	
	/**
	 * Validate round text field.
	 *
	 * @return true, if successful
	 */
	private boolean validateRoundTextField()
	{		
		if(!txtRound.getText().trim().equals(""))
		{
			System.out.println("Round Changed To: " + txtRound.getText().trim() + "\nNumber of Characters: " + txtRound.getText().trim().length());
			log.debug("Round Changed To: " + txtRound.getText().trim() + "\nNumber of Characters: " + txtRound.getText().trim().length());
			testSet.setRound(txtRound.getText());
			return true;
		}else
		{
			JOptionPane.showMessageDialog(null,
			          "Error: The Round can not be empty.\nPlease enter a valid Round for the Test Set.", "Error Message",
			          JOptionPane.ERROR_MESSAGE);
			txtRound.requestFocus();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.update();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {		
		if(e.getDocument().equals(txtRelease.getDocument()))
		{
			if(validateReleaseTextField())
			{
				System.out.println("Release Textfield validated.");
				log.debug("Release Textfield validated.");
				setTestSetValues();
			}
		}else if(e.getDocument().equals(txtRound.getDocument()))
		{
			if(validateRoundTextField())
			{
				System.out.println("Round Textfield validated.");
				log.debug("Round Textfield validated.");
				setTestSetValues();
			}
		}else if(e.getDocument().equals(numFailedRetriesTextField.getDocument()))
		{
			if(validateNumberOfRetriesTextField())
			{
				System.out.println("Number of Failed Retries Textfield validated.");
				log.debug("Number of Failed Retries Textfield validated.");
				setTestSetValues();
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == testSetNameTextField)
		{
			originalTestName = testSetNameTextField.getText();
		}else if(e.getSource() == numFailedRetriesTextField && !numFailedRetriesTextField.getText().trim().isEmpty())
		{
			originalNumRetries= Integer.parseInt(numFailedRetriesTextField.getText());
		}else if(e.getSource() ==  txtAlmPath)
		{
			originalALMPath = txtAlmPath.getText();
		}else if(e.getSource() == txtRelease)
		{
			originalRelease = txtRelease.getText();
		}else if(e.getSource() == txtRound)
		{
			originalRound = txtRound.getText();
		}
		this.repaint();
		this.paint(this.getGraphics());
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.repaint();		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	@Override
	public void repaint()
	{
		super.repaint();	
	}
	
	/**
	 * Sets the panel components to read only.
	 */
	public void setPanelComponentsToReadOnly()
	{		
		numFailedRetriesTextField.setEditable(false);
		txtRelease.setEditable(false);
		txtRound.setEditable(false);
		txtAlmPath.setEditable(false);
		chckbxHardStopRoll.setEnabled(false);		
	}
	
	/**
	 * Sets the test set values.
	 */
	private synchronized void setTestSetValues()
	{
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
		Calendar cal = Calendar.getInstance();
		if(!txtRound.getText().trim().isEmpty())
		{
			roundOfRegression = txtRound.getText();			
			testSetName = "v" + release + "  " + "R" + roundOfRegression + " -- " + dateFormat.format(cal.getTime());
			testSet.setTestSetName(testSetName);		
			testSet.setRound(roundOfRegression);				
		}else
		{
			roundOfRegression = "";			
			testSetName = "v" + release + "  " + "R" + roundOfRegression + " -- " + dateFormat.format(cal.getTime());
			testSet.setTestSetName(testSetName);		
			testSet.setRound(roundOfRegression);
		}
		if(!txtRelease.getText().trim().isEmpty())
		{
			release =  txtRelease.getText();			
			
			if(roundOfRegression.isEmpty())
			{
				testSetName = "v" + release + " -- " + dateFormat.format(cal.getTime());
			}else
			{
				testSetName = "v" + release + "  " + "R" + roundOfRegression + " -- " + dateFormat.format(cal.getTime());
			}
			
			testSet.setTestSetName(testSetName);	
			testSet.setRelease(release);									
			testSet.setAlmPath(buildALMPathString());			
		}else
		{
			release =  "";			
			if(roundOfRegression.isEmpty())
			{
				testSetName = "v" + release + " -- " + dateFormat.format(cal.getTime());
			}else
			{
				testSetName = "v" + release + "  " + "R" + roundOfRegression + " -- " + dateFormat.format(cal.getTime());
			}
			
			testSet.setTestSetName(testSetName);	
			testSet.setRelease(release);									
			testSet.setAlmPath(buildALMPathString());
		}
		if(chckbxHardStopRoll.isSelected())
		{
			testSet.setHardStopRoll('Y');
		}else
		{
			testSet.setHardStopRoll('N');
		}
		if(!numFailedRetriesTextField.getText().trim().equals(""))
		{
			if(Integer.parseInt(numFailedRetriesTextField.getText()) != testSet.getRetryLimit())
			{
				testSet.setRetryLimit(Integer.parseInt(numFailedRetriesTextField.getText()));
				System.out.println("Set retry limit on test set to:" + numFailedRetriesTextField.getText());
				log.debug("Set retry limit on test set to:" + numFailedRetriesTextField.getText());
			}
		}else
		{			
			numFailedRetriesTextField.setText("");
			testSet.setRetryLimit(0);
			System.out.println("Retry Limit Text Field is empty, setting retry limit on test set to: 0");
			log.debug("Retry Limit Text Field is empty, setting retry limit on test set to: 0");
		}		
		if(!testSetNameTextField.getText().equals(testSetName))
		{
			testSetNameTextField.setText(testSetName);
		}
		txtAlmPath.setText(buildALMPathString());		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==chckbxHardStopRoll)
		{
			if(chckbxHardStopRoll.isSelected())
			{
				testSet.setHardStopRoll('Y');//Code to change hard stop roll on testset to true.
			}else
			{
				testSet.setHardStopRoll('N');//Code to change hard stop roll on testset to false.				
			}
		}
		
	}
	
	/**
	 * The Class TextFieldDocumentFilter.
	 */
	class TextFieldDocumentFilter extends DocumentFilter {

		  /**
  		 * Instantiates a new text field document filter.
  		 */
  		public TextFieldDocumentFilter() {

		  }

		  /* (non-Javadoc)
  		 * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.DocumentFilter.FilterBypass, int, java.lang.String, javax.swing.text.AttributeSet)
  		 */
  		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string,
		      AttributeSet attr) throws BadLocationException {
			log.debug("TextFieldDocumentFilter: insertString Method invoked.");
		    super.insertString(fb, offset, string, attr);
		  }

		  /* (non-Javadoc)
  		 * @see javax.swing.text.DocumentFilter#remove(javax.swing.text.DocumentFilter.FilterBypass, int, int)
  		 */
  		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
		      throws BadLocationException {
			log.debug("TextFieldDocumentFilter: remove Method invoked.");
		    super.remove(fb, offset, length);
		    setTestSetValues();
		  }

		  /* (non-Javadoc)
  		 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String, javax.swing.text.AttributeSet)
  		 */
  		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
		      AttributeSet attrs) throws BadLocationException {
		    log.debug("TextFieldDocumentFilter: replace Method invoked.");
		    super.replace(fb, offset, length, text, attrs);
		    if(release!=null)
		    	setTestSetValues();
		  }
		}
}
