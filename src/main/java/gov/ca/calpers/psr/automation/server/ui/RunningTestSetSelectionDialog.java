/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;

import gov.ca.calpers.psr.automation.AutomationTestSet;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JButton;

/**
 * The Class RunningTestSetSelectionDialog.
 *
 * @author burban
 */
public class RunningTestSetSelectionDialog extends JDialog implements ActionListener{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The selected test set. */
	private AutomationTestSet selectedTestSet = null;
	
	/** The btn load test set. */
	private JButton btnLoadTestSet;
	
	/** The test set list. */
	private JList testSetList;

	/**
	 * Instantiates a new running test set selection dialog.
	 *
	 * @param runningTestSets the running test sets
	 */
	public RunningTestSetSelectionDialog(CopyOnWriteArrayList<AutomationTestSet> runningTestSets)
	{
		setResizable(false);
		setTitle("Select a Running Test Set");
		this.setModal(true);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		this.setPreferredSize(new Dimension(225, 275));
		this.setSize(getPreferredSize());
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				int dialogButton = JOptionPane.YES_NO_OPTION; 
    			int dialogResult = JOptionPane.showConfirmDialog(null,"Are you sure you would like to exit the server?", "Warning", dialogButton, JOptionPane.QUESTION_MESSAGE);
    			if(dialogResult == JOptionPane.YES_OPTION){
    				System.exit(0);
    			}				
			}			
		});
		
		JPanel listPanel = new JPanel(new BorderLayout(0,0));
		springLayout.putConstraint(SpringLayout.NORTH, listPanel, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, listPanel, 10, SpringLayout.WEST, getContentPane());
		listPanel.setBorder(new TitledBorder(null, "Running Test Sets", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		listPanel.setPreferredSize(new Dimension(200, 200));
		getContentPane().add(listPanel);
		
		JScrollPane testSetScrollPane = new JScrollPane();
		testSetScrollPane.setPreferredSize(new Dimension(200, 200));
		listPanel.add(testSetScrollPane, BorderLayout.CENTER);
		 
		DefaultListModel runningTestSetListModel = new DefaultListModel();
		for(AutomationTestSet testSet : runningTestSets)
		{
			runningTestSetListModel.addElement(testSet);
			System.out.println("**Added Test Set to List.***");
		}
		
		testSetList = new JList(runningTestSetListModel);
		testSetScrollPane.setViewportView(testSetList);
		
		btnLoadTestSet = new JButton("Load Test Set");
		btnLoadTestSet.setActionCommand("Load_Test_Set");
		btnLoadTestSet.addActionListener(this);
		btnLoadTestSet.setEnabled(false);
		springLayout.putConstraint(SpringLayout.NORTH, btnLoadTestSet, 6, SpringLayout.SOUTH, listPanel);
		springLayout.putConstraint(SpringLayout.EAST, btnLoadTestSet, 0, SpringLayout.EAST, listPanel);
		getContentPane().add(btnLoadTestSet);
		
		testSetList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		      @Override
			public void valueChanged(ListSelectionEvent se) {		        
		        selectedTestSet = (AutomationTestSet) testSetList.getSelectedValue();
		        btnLoadTestSet.setEnabled(true);
		      }
		    });
	}
	
	/**
	 * Show dialog.
	 *
	 * @return the automation test set
	 */
	public AutomationTestSet showDialog()
	{
		setVisible(true);
		return selectedTestSet;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Load_Test_Set"))
		{
			if(selectedTestSet != null)
			{
				dispose();
			}
		}
	}
}
