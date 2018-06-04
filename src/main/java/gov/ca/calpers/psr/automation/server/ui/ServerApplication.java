package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.ALMServerConfig;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestManager;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.WorkManager;
import gov.ca.calpers.psr.automation.server.socket.AutomationCoordinatorServer;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.logging.log4j.LogManager;
import org.jdesktop.swingx.painter.MattePainter;

public class ServerApplication
  extends JFrame
  implements ComponentListener, ActionListener, EventListener, ChangeListener
{
  private static final long serialVersionUID = 1L;
  private JPanel mainPanel;
  private JTabbedPane tabs;
  private ControlCenter controlCenter;
  private TestSetCreationPanel testSetCreationPanel;
  private DependenciesPanel dependencyPanel;
  private LogPanel logPanel;
  private ConnectedClientsPanel clientsPanel;
  private ReportingPanel reportingPanel;
  private ALMServerConfig almServerConfig;
  private AutomationCoordinatorServer server;
  private JMenuBar menuBar;
  private JMenu fileMenu;
  private final CopyOnWriteArrayList<AutomationTest> allTests;
  private final AutomationTestSet theTestSet;
  private final WorkManager workManager;
  private JFrame frame;
  private static org.apache.logging.log4j.Logger log = LogManager.getLogger(ServerApplication.class);
  
  static void renderSplashFrame(Graphics2D g, int frame)
  {
    String[] comps = { "Server", "bar", "baz" };
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(120, 140, 200, 40);
    g.setPaintMode();
    g.setColor(Color.BLACK);
    g.drawString("Loading...", 150, 150);
  }
  
  public ServerApplication()
  {
    SplashScreen splash = SplashScreen.getSplashScreen();
    if (splash == null)
    {
      System.out.println("SplashScreen.getSplashScreen() returned null");
      log.debug("SplashScreen.getSplashScreen() returned null");
    }
    else
    {
      Graphics2D g = splash.createGraphics();
      if (g != null)
      {
        renderSplashFrame(g, 0);
        splash.update();
      }
    }
    this.frame = this;
    setDefaultCloseOperation(0);
    this.almServerConfig = ALMServerConfig.getServerDetails();
    CopyOnWriteArrayList<AutomationTestSet> runningTestSets = AutomationTestSet.getTestSetsWithRunningRunStatus();
    CopyOnWriteArrayList<AutomationTestSet> draftTestSets = AutomationTestSet.getTestSetsWithDraftRunStatus();
    if ((!runningTestSets.isEmpty()) || (!draftTestSets.isEmpty()))
    {
      TestSetSelectionDialog dialog = new TestSetSelectionDialog(runningTestSets, draftTestSets);
      dialog.setLocationRelativeTo(null);
      this.theTestSet = dialog.showDialog();
    }
    else
    {
      this.theTestSet = new AutomationTestSet();
    }
    this.theTestSet.setTestSetSelectionCriteria(TestSetSelectionCriteria.getTestSetSelectionCriteriaByTestSetId(this.theTestSet.getTestSetId()));
    this.theTestSet.retrieveTestResults();
    this.workManager = new WorkManager(this.theTestSet);
    
    AutomationTestManager mgr = AutomationTestManager.getAutomationTestManager();
    this.allTests = mgr.getAllActiveRegressionTests();
    CopyOnWriteArrayList<AutomationTest> allTestsControlPanel = new CopyOnWriteArrayList();
    CopyOnWriteArrayList<AutomationTest> allTestsDependencyPanel = new CopyOnWriteArrayList();
    allTestsControlPanel.addAll(this.allTests);
    allTestsDependencyPanel.addAll(this.allTests);
    this.logPanel = new LogPanel();
    this.logPanel.setSize(800, 700);
    this.logPanel.addComponentListener(this);
    this.server = new AutomationCoordinatorServer(ServerStatus.IDLE, this.theTestSet, this.almServerConfig, this.workManager);
    
    setTitle("Automation Job Scheduler");
    createMenu();
    setJMenuBar(this.menuBar);
    setPreferredSize(new Dimension(1000, 700));
    
    this.mainPanel = new JPanel();
    this.mainPanel.setLayout(new BorderLayout(0, 0));
    
    this.tabs = new JTabbedPane();
    this.mainPanel.add(this.tabs);
    getContentPane().add(this.mainPanel);
    
    this.controlCenter = new ControlCenter(this, this.theTestSet, allTestsControlPanel, this.workManager, this.server);
    this.controlCenter.setSize(new Dimension(800, 700));
    this.controlCenter.setPreferredSize(this.controlCenter.getSize());
    this.controlCenter.addComponentListener(this);
    
    this.testSetCreationPanel = new TestSetCreationPanel(this.theTestSet, this.allTests, this.workManager, this);
    this.testSetCreationPanel.setSize(new Dimension(800, 700));
    this.testSetCreationPanel.setPreferredSize(this.testSetCreationPanel.getSize());
    this.testSetCreationPanel.addComponentListener(this);
    
    this.dependencyPanel = new DependenciesPanel(allTestsDependencyPanel, this);
    this.dependencyPanel.setSize(new Dimension(800, 700));
    this.dependencyPanel.setPreferredSize(this.dependencyPanel.getSize());
    this.dependencyPanel.addComponentListener(this);
    
    this.clientsPanel = new ConnectedClientsPanel(this.server);
    this.clientsPanel.setSize(new Dimension(800, 700));
    this.clientsPanel.setPreferredSize(this.clientsPanel.getSize());
    this.clientsPanel.addComponentListener(this);
    
    this.reportingPanel = new ReportingPanel(this.theTestSet);
    this.reportingPanel.setSize(new Dimension(800, 700));
    this.reportingPanel.setPreferredSize(this.reportingPanel.getSize());
    this.reportingPanel.addComponentListener(this);
    try
    {
      Image imgCC = ImageIO.read(getClass().getResourceAsStream("resources/dashboard_16.png"));
      
      this.tabs.addTab("Control Center", new ImageIcon(imgCC), this.controlCenter);
      
      Image imgTS = ImageIO.read(getClass().getResourceAsStream("resources/test_set_16.png"));
      
      this.tabs.addTab("Test Set", new ImageIcon(imgTS), this.testSetCreationPanel);
      
      Image imgDEP = ImageIO.read(getClass().getResourceAsStream("resources/dependency_16.png"));
      
      this.tabs.addTab("Test Dependencies", new ImageIcon(imgDEP), this.dependencyPanel);
      
      Image imgRPT = ImageIO.read(getClass().getResourceAsStream("resources/report-16.png"));
      
      this.tabs.addTab("Report", new ImageIcon(imgRPT), this.reportingPanel);
      
      Image imgCLT = ImageIO.read(getClass().getResourceAsStream("resources/client-icon-16.png"));
      
      this.tabs.addTab("Clients", new ImageIcon(imgCLT), this.clientsPanel);
      
      Image imgLOG = ImageIO.read(getClass().getResourceAsStream("resources/log-16.png"));
      
      this.tabs.addTab("Log", new ImageIcon(imgLOG), this.logPanel);
    }
    catch (IOException ex)
    {
      log.error("Error (IO): ", ex);
    }
    setVisible(true);
    setEnabled(true);
    pack();
    setLocationRelativeTo(null);
    ArrayList<Image> programIcons = new ArrayList();
    try
    {
      programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_16.png")));
      programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_32.png")));
      programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_64.png")));
      programIcons.add(ImageIO.read(getClass().getResourceAsStream("resources/calpers-logo_128.png")));
      setIconImages(programIcons);
    }
    catch (IOException ex)
    {
      log.error("Error (IO): ", ex);
    }
    this.tabs.addChangeListener(this);
    setVisible(true);
    toFront();
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent windowEvent)
      {
        int dialogButton = 0;
        int dialogResult = JOptionPane.showConfirmDialog(ServerApplication.this.frame, "Are you sure you would like to exit the server?", "Warning", dialogButton, 3);
        if (dialogResult == 0) {
          System.exit(0);
        }
      }
    });
  }
  
  public void componentResized(ComponentEvent e) {}
  
  public void componentMoved(ComponentEvent e) {}
  
  public void componentShown(ComponentEvent e) {}
  
  public void componentHidden(ComponentEvent e) {}
  
  private void createMenu()
  {
    this.menuBar = new JMenuBar();
    this.fileMenu = new JMenu("File");
    this.fileMenu.setMnemonic(70);
    JMenuItem exitProgram = new JMenuItem("Exit", 69);
    try
    {
      Image img = ImageIO.read(getClass().getResourceAsStream("resources/exit_16.png"));
      exitProgram.setIcon(new ImageIcon(img));
    }
    catch (IOException ex)
    {
      log.error("Error (IO): ", ex);
    }
    exitProgram.setToolTipText("Exit Job Scheduler");
    exitProgram.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        int dialogButton = 0;
        int dialogResult = JOptionPane.showConfirmDialog(ServerApplication.this.frame, "Are you sure you would like to exit the server?", "Warning", dialogButton, 3);
        if (dialogResult == 0) {
          System.exit(0);
        }
      }
    });
    JMenuItem encryptPassword = new JMenuItem("Encrypt Password", 80);
    encryptPassword.setToolTipText("Create Encrypted Password to copy to Database.");
    encryptPassword.setActionCommand("Encrypt");
    encryptPassword.addActionListener(this);
    this.fileMenu.add(encryptPassword);
    this.fileMenu.add(exitProgram);
    this.menuBar.add(this.fileMenu);
  }
  
  public static void main(String[] args)
  {
    java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      Object painter = UIManager.get("TabbedPane.background");
      UIManager.put("TaskPaneContainer.backgroundPainter", new MattePainter((Paint)painter));
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (InstantiationException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }
    ServerApplication run;
    if (SwingUtilities.isEventDispatchThread()) {
      run = new ServerApplication();
    } else {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          ServerApplication run = new ServerApplication();
        }
      });
    }
  }
  
  public void actionPerformed(ActionEvent e)
  {
    EncryptPasswordPanel panel;
    if (e.getActionCommand().equals("Encrypt")) {
      panel = new EncryptPasswordPanel();
    }
  }
  
  public void stateChanged(ChangeEvent e) {}
}
