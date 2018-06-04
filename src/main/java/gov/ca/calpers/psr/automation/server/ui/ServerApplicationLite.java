/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.painter.MattePainter;

import gov.ca.calpers.psr.automation.AutomationTestSet;

/**
 * The Class ServerApplicationLite.
 *
 * @author burban
 */
public class ServerApplicationLite extends JFrame implements ComponentListener, ActionListener{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The menu bar. */
	private JMenuBar menuBar;
	
	/** The file menu. */
	private JMenu fileMenu;
	
	/** The main panel. */
	private JPanel mainPanel;
	
	/** The tabs. */
	private JTabbedPane tabs;
	
	/** The control center. */
	private ControlCenterLite controlCenter;
	
	/** The test set. */
	private AutomationTestSet theTestSet;
	
	/** The frame. */
	private JFrame frame;
	
	/** The timer. */
	private Timer timer =  new Timer(5000, this);
	
	/**
	 * Render splash frame.
	 *
	 * @param g the g
	 * @param frame the frame
	 */
	static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"Server Lite", "bar", "baz"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120,140,200,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading...", 150, 150);
    }
	
	/**
	 * Instantiates a new server application lite.
	 */
	public ServerApplicationLite()
	{
		frame = this;
		final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");           
        }else
        {
	        Graphics2D g = splash.createGraphics();
	        if (g == null) {
	            System.out.println("g is null");	          
	        }else
	        {
	        	renderSplashFrame(g, 0);
	        	splash.update();
	        }
        }
        this.setPreferredSize(new Dimension(1000,600));		
        CopyOnWriteArrayList<AutomationTestSet> runningTestSets = AutomationTestSet.getTestSetsWithRunningRunStatus();
        		
		RunningTestSetSelectionDialog dialog = new RunningTestSetSelectionDialog(runningTestSets);
		dialog.setLocationRelativeTo(null);
		theTestSet = dialog.showDialog();				
		
		theTestSet.setTestSetSelectionCriteria(TestSetSelectionCriteria.getTestSetSelectionCriteriaByTestSetId(theTestSet.getTestSetId()));
        
        theTestSet.retrieveTestResults();
        
        setTitle("Automation Job Scheduler");
        createMenu();
        setJMenuBar(menuBar);
		JPanel mainPanel = new JPanel(new BorderLayout(0,0));
		ControlCenterLite controlCenter = new ControlCenterLite(theTestSet);
		ReportingPanel reportingPanel = new ReportingPanel(theTestSet);
		controlCenter.setPreferredSize(new Dimension(800,500));
		tabs = new JTabbedPane();   
		mainPanel.add(tabs);
		try {
			Image imgCC = ImageIO.read(getClass().getResourceAsStream(
					"resources/dashboard_16.png"));
			
			tabs.addTab("Control Center", new ImageIcon(imgCC), controlCenter);
			
			Image imgRPT = ImageIO.read(getClass().getResourceAsStream(
					"resources/report-16.png"));
			
			tabs.addTab("Report", new ImageIcon(imgRPT), reportingPanel);
		} catch (IOException ex) { 
			System.out.println("Searchable Error: " + ex.getMessage());			
		}

		this.add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(mainPanel);
		setVisible(true);
        setEnabled(true);
        pack();
        this.setLocationRelativeTo(null);        
        ArrayList<Image> programIcons = new ArrayList<Image>();
        try {
            programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_16.png")));
            programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_32.png")));
            programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_64.png")));
            programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_128.png")));
            this.setIconImages(programIcons);
          } catch (IOException ex) {
          	System.out.println("Error: " + ex.getMessage());
          }
        
        setVisible(true);
        toFront();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            	int dialogButton = JOptionPane.YES_NO_OPTION; 
    			int dialogResult = JOptionPane.showConfirmDialog(frame,"Are you sure you would like to exit the server?", "Warning", dialogButton, JOptionPane.QUESTION_MESSAGE);
    			if(dialogResult == JOptionPane.YES_OPTION){
    				System.exit(0);
    			}
            }
        });
        timer.start();
	}
	
	/**
	 * Retrieve test results.
	 */
	private synchronized void retrieveTestResults()
	{
		theTestSet.retrieveTestResults();	    	
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==timer){
			retrieveTestResults();
		    }
	}

	/**
	 * Creates the menu.
	 */
	private void createMenu()
    {
    	menuBar = new JMenuBar();
    	fileMenu = new JMenu("File");
    	fileMenu.setMnemonic(KeyEvent.VK_F);
    	JMenuItem exitProgram = new JMenuItem("Exit", KeyEvent.VK_E);
    	try {
            Image img = ImageIO.read(getClass().getResourceAsStream("resources/exit_16.png"));
            exitProgram.setIcon(new ImageIcon(img));
          } catch (IOException ex) {
          	System.out.println("Error: " + ex.getMessage()); 
          }    	
    	exitProgram.setToolTipText("Exit Job Scheduler");
    	exitProgram.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent event){
    			int dialogButton = JOptionPane.YES_NO_OPTION; 
    			int dialogResult = JOptionPane.showConfirmDialog(frame,"Are you sure you would like to exit the server?", "Warning", dialogButton, JOptionPane.QUESTION_MESSAGE);
    			if(dialogResult == JOptionPane.YES_OPTION){
    				System.exit(0);
    			}
    		}
    	});

    	fileMenu.add(exitProgram);
    	menuBar.add(fileMenu);    	
    }
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Object painter = UIManager.get("TabbedPane.background");
            UIManager.put("TaskPaneContainer.backgroundPainter", new MattePainter((Paint) painter));
        } catch (ClassNotFoundException e) {
            // null
            e.printStackTrace();
        } catch (InstantiationException e) {
            // null
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // null
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // null
            e.printStackTrace();
        }
        
        if (SwingUtilities.isEventDispatchThread()) {
        	ServerApplicationLite run = new ServerApplicationLite();
        	} else {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() { 
            	ServerApplicationLite run = new ServerApplicationLite();
            }
        });
        }

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

}
