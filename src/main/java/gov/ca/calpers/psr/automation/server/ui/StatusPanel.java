/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

import gov.ca.calpers.psr.automation.server.socket.AutomationCoordinatorServer;

import javax.swing.border.EtchedBorder;

/**
 * The Class StatusPanel.
 *
 * @author burban
 */
public class StatusPanel extends JPanel implements Observer{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The status idle. */
	private final String statusIdle = "<html><font color=\"#0000FF\">Idle</font></html>";
	
	/** The status running. */
	private final String statusRunning = "<html><font color=\"#008000\">Running</font></html>";
	
	/** The status finished. */
	private final String statusFinished = "<html><font color=\"#008000\">Finished</font></html>";
	
	/** The status stopped. */
	private final String statusStopped = "<html><font color=\"#E80000\">Stopped</font></html>";
	
	/** The status paused. */
	private final String statusPaused = "<html><font color=\"#0000FF\">Idle</font></html>";
	
	private final String statusRunningRollFailed = "<html><p><span style=\"color:#008000\">Running</span> - <span style=\"color:#E80000\">ROLL FAILED</span></p></html>";
	
	/** The hard stop yes. */
	private final String hardStopYes = "<html><font color=\"#E80000\">Yes</font></html>";
	
	/** The hard stop no. */
	private final String hardStopNo = "<html><font color=\"#008000\">No</font></html>";
	
	/** The server. */
	private AutomationCoordinatorServer server;
	
	/** The lbl status. */
	private JLabel lblStatus;
	
	/** The lbl hard stop status. */
	private JLabel lblHardStopStatus;
	
	/** The lbl hard stop active. */
	private JLabel lblHardStopActive;
	
	/** The layout. */
	private SpringLayout layout;

	/**
	 * Instantiates a new status panel.
	 *
	 * @param server the server
	 */
	public StatusPanel(AutomationCoordinatorServer server)
	{
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.server = server;
		server.addObserver(this);
		setPreferredSize(new Dimension(800, 40));
		layout = new SpringLayout();
		this.setLayout(layout);
		JLabel lblServerStatus = new JLabel("Server Status:");
		layout.putConstraint(SpringLayout.NORTH, lblServerStatus, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, lblServerStatus, 10, SpringLayout.WEST, this);
		add(lblServerStatus);
		
		lblStatus = new JLabel("");
		layout.putConstraint(SpringLayout.NORTH, lblStatus, 0, SpringLayout.NORTH, lblServerStatus);
		layout.putConstraint(SpringLayout.WEST, lblStatus, 6, SpringLayout.EAST, lblServerStatus);
		add(lblStatus);
		
		lblHardStopStatus = new JLabel("");
		layout.putConstraint(SpringLayout.NORTH, lblHardStopStatus, 0, SpringLayout.NORTH, lblServerStatus);
		layout.putConstraint(SpringLayout.EAST, lblHardStopStatus, -15, SpringLayout.EAST, this);
		add(lblHardStopStatus);
		
		lblHardStopActive = new JLabel("Hard Stop Active:");
		layout.putConstraint(SpringLayout.NORTH, lblHardStopActive, 0, SpringLayout.NORTH, lblServerStatus);
		layout.putConstraint(SpringLayout.EAST, lblHardStopActive, -5, SpringLayout.WEST, lblHardStopStatus);
		add(lblHardStopActive);
		updateLabels();
	}
	
	/**
	 * Update labels.
	 */
	private synchronized void updateLabels()
	{
		SwingUtilities.invokeLater(new Runnable(){@Override
		public void run(){
		
		switch (server.getServerStatus()){
		case IDLE: 
			lblStatus.setText(statusIdle);
			lblHardStopStatus.setText(hardStopNo);
			break;
		case EXECUTING_TEST_SET:
			lblStatus.setText(statusRunning);
			lblHardStopStatus.setText(hardStopNo);
			break;
		case STOPPED:
			lblStatus.setText(statusStopped);
			lblHardStopStatus.setText(hardStopNo);
			break;
		case FINISHED:
			lblStatus.setText(statusFinished);
			lblHardStopStatus.setText(hardStopNo);
			break;
		case PAUSED:
			lblStatus.setText(statusPaused);
			lblHardStopStatus.setText(hardStopNo);
			break;
		case HARD_STOP:
			lblStatus.setText(statusRunning);
			lblHardStopStatus.setText(hardStopYes);
			break;
		case EXECUTING_ROLL_FAILED:
			lblStatus.setText(statusRunningRollFailed);
			lblHardStopStatus.setText(hardStopNo);
			break;
		}	
		}});
	}
	

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		updateLabels();
	}
}
