/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import gov.ca.calpers.psr.automation.logger.LoggerPane;
import javax.swing.border.TitledBorder;

/**
 * The Class LogPanel.
 *
 * @author burban
 */
public class LogPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The status log. */
	private final JTextPane statusLog;
	
	/**
	 * Instantiates a new log panel.
	 */
	public LogPanel()
	{
		statusLog = new JTextPane();		
		statusLog.setPreferredSize(new Dimension(800, 600));
        LoggerPane.initialize(statusLog);
        JPanel logPanel = new JPanel();
        logPanel.setBorder(new TitledBorder(null, "Log Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        logPanel.setLayout(new BorderLayout(0,0));       
        logPanel.setPreferredSize(new Dimension(800, 600));
        logPanel.add(new JScrollPane(statusLog), BorderLayout.CENTER);
        statusLog.setEditable(false);
        this.setLayout(new BorderLayout(0,0));
        this.add(logPanel, BorderLayout.CENTER);
	}
}
