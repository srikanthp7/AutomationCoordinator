package gov.ca.calpers.psr.automation.server.ui;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import gov.ca.calpers.psr.automation.ALMServerConfig;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.TestResult;
import gov.ca.calpers.psr.automation.TestSetRunStatus;
import gov.ca.calpers.psr.automation.UnitOfWork;
import gov.ca.calpers.psr.automation.UnitOfWorkFlow;
import gov.ca.calpers.psr.automation.WorkManager;
import gov.ca.calpers.psr.automation.WorkerBucket;
import gov.ca.calpers.psr.automation.logger.LoggerPane;
import gov.ca.calpers.psr.automation.server.socket.AutomationCoordinatorServer;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

public class ControlCenter
  extends JPanel
  implements ComponentListener, Observer, ActionListener
{
  private static final long serialVersionUID = 1L;
  private AutomationCoordinatorServer currentServer = null;
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
  private ScheduledFuture<?> currentEvent = null;
  private ALMServerConfig almServerConfig;
  private final AutomationTestSet theTestSet;
  private final RoadMap testTree;
  private final CopyOnWriteArrayList<AutomationTest> allTests;
  private ServerStatus serverStatus;
  private WorkManager workManager;
  boolean startScheduled = false;
  private boolean isPaused = false;
  private boolean processingFinishedTestSet = false;
  private static Logger log = LogManager.getLogger(ControlCenter.class);
  private final JSplitPane centerPanel;
  private final ScenariosView tableView;
  private final StatusPanel southPanel;
  private final JPanel northPanel;
  private final JPanel testController;
  private final JLabel lblRelease;
  private final JTextField txtRelease;
  private final JLabel lblRound;
  private final JTextField txtRound;
  private final JLabel lblStartTime;
  private final JXDatePicker startTime;
  private final JXTaskPaneContainer serverInfo;
  private final JLabel lblServer;
  private final JTextField server;
  private final JLabel lblDomain;
  private final JLabel lblProject;
  private final JTextField domain;
  private final JTextField project;
  private final JLabel lblTestSetLocation;
  private final JTextField txtTestSetPath;
  private final JLabel lblTestSetName;
  private final JTextField txtTestSetName;
  private final JCheckBoxReadOnly chckbxHardStopRoll;
  private final JLabel lblNumberOfSelectedTests;
  private final JButton releaseHardStop;
  private JLabel lblEnvValue;
  private final JButton start;
  private final JButton pause;
  private final JButton stop;
  private final JCheckBox chckbxAvailable;
  
  public ControlCenter(ComponentListener mainFrame, AutomationTestSet testSet, CopyOnWriteArrayList<AutomationTest> allTests, WorkManager workManager, AutomationCoordinatorServer aServer)
  {
    this.theTestSet = testSet;
    this.theTestSet.addObserver(this);
    this.allTests = allTests;
    this.serverStatus = ServerStatus.IDLE;
    this.currentServer = aServer;
    
    setLayout(new BorderLayout(0, 0));
    addComponentListener(mainFrame);
    this.almServerConfig = ALMServerConfig.getServerDetails();
    setLayout(new BorderLayout(0, 0));
    this.northPanel = new JPanel();
    this.northPanel.setLayout(new BorderLayout(0, 0));
    
    this.testController = new JPanel();
    add(this.northPanel, "North");
    
    this.northPanel.add(this.testController, "South");
    this.serverInfo = new JXTaskPaneContainer();
    this.serverInfo.setBorder(BorderFactory.createEmptyBorder());
    
    JXTaskPane serverConfiguration = new JXTaskPane();
    serverConfiguration.setSpecial(true);
    serverConfiguration.setCollapsed(true);
    serverConfiguration.setTitle("Server Configuration");
    JXPanel serverConfigurationPanel = (JXPanel)serverConfiguration.getContentPane();
    serverConfigurationPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(100dlu;default):grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(100dlu;default)"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("center:max(17dlu;default)") }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("max(13dlu;default)"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC }));
    
    this.lblDomain = new JLabel("Domain:");
    serverConfiguration.getContentPane().add(this.lblDomain, "2, 2, right, default");
    
    this.domain = new JTextField();
    this.domain.setText(this.almServerConfig.getAlmDomain());
    this.domain.setEditable(false);
    
    serverConfiguration.getContentPane().add(this.domain, "4, 2, fill, default");
    this.domain.setColumns(10);
    
    this.lblProject = new JLabel("Project:");
    serverConfiguration.getContentPane().add(this.lblProject, "6, 2, right, default");
    
    this.project = new JTextField();
    this.project.setText(this.almServerConfig.getAlmProject());
    this.project.setEditable(false);
    
    serverConfiguration.getContentPane().add(this.project, "8, 2, fill, default");
    this.project.setColumns(10);
    
    this.lblServer = new JLabel("Server:");
    serverConfiguration.getContentPane().add(this.lblServer, "10, 2, right, default");
    
    this.server = new JTextField();
    this.server.setText(this.almServerConfig.getURL());
    this.server.setEditable(false);
    serverConfiguration.getContentPane().add(this.server, "12, 2, 5, 1, fill, default");
    this.server.setColumns(10);
    
    this.lblTestSetLocation = new JLabel("Test Set Path:");
    serverConfiguration.getContentPane().add(this.lblTestSetLocation, "2, 4, right, default");
    
    this.txtTestSetPath = new JTextField();
    this.txtTestSetPath.setText(this.theTestSet.getAlmPath());
    this.txtTestSetPath.setEditable(false);
    serverConfiguration.getContentPane().add(this.txtTestSetPath, "4, 4, 5, 1, fill, default");
    this.txtTestSetPath.setColumns(10);
    
    this.lblTestSetName = new JLabel("Test Set Name:");
    serverConfiguration.getContentPane().add(this.lblTestSetName, "10, 4, right, default");
    
    this.txtTestSetName = new JTextField();
    this.txtTestSetName.setText(this.theTestSet.getTestSetName());
    this.txtTestSetName.setEditable(false);
    serverConfiguration.getContentPane().add(this.txtTestSetName, "12, 4, fill, default");
    this.txtTestSetName.setColumns(10);
    
    this.serverInfo.add(serverConfiguration);
    this.northPanel.add(this.serverInfo, "North");
    
    this.testController.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(40dlu;default)"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(40dlu;default)"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("center:max(17dlu;default)"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("max(13dlu;default)") }));
    
    this.lblRelease = new JLabel("Release:");
    this.testController.add(this.lblRelease, "2, 2");
    
    this.txtRelease = new JTextField();
    this.testController.add(this.txtRelease, "4, 2");
    this.txtRelease.setColumns(4);
    if (!this.theTestSet.getRelease().trim().isEmpty()) {
      this.txtRelease.setText("v" + this.theTestSet.getRelease());
    }
    this.lblRound = new JLabel("Round:");
    this.testController.add(this.lblRound, "6, 2");
    
    this.txtRound = new JTextField();
    this.testController.add(this.txtRound, "8, 2");
    this.txtRound.setColumns(12);
    this.txtRound.setText(this.theTestSet.getRound());
    
    this.lblStartTime = new JLabel("Start Time");
    this.testController.add(this.lblStartTime, "14, 2");
    
    this.startTime = new JXDatePicker();
    
    this.testController.add(this.startTime, "16, 2, left, default");
    this.startTime.setFormats(new DateFormat[] { this.dateFormatter });
    
    this.startTime.getEditor().addFocusListener(new FocusAdapter()
    {
      public void focusLost(FocusEvent e) {}
    });
    this.chckbxHardStopRoll = new JCheckBoxReadOnly();
    this.chckbxHardStopRoll.setText("Hard Stop Roll");
    this.testController.add(this.chckbxHardStopRoll, "18, 2");
    
    this.releaseHardStop = new JButton("Release");
    if (this.theTestSet.getHardStopRoll() == 'Y') {
      this.releaseHardStop.setEnabled(true);
    } else {
      this.releaseHardStop.setEnabled(false);
    }
    this.releaseHardStop.setActionCommand("Release_Hard_Stop");
    this.releaseHardStop.addActionListener(this);
    this.testController.add(this.releaseHardStop, "20, 2, left, default");
    
    this.lblNumberOfSelectedTests = new JLabel(String.valueOf(this.theTestSet.getTests().size()) + "/" + String.valueOf(allTests.size()) + " Tests in Test Set.");
    this.testController.add(this.lblNumberOfSelectedTests, "22, 2");
    this.chckbxAvailable = new JCheckBox("Available");
    this.start = new JButton("Start");
    this.start.setEnabled(false);
    this.start.addActionListener(this);
    this.start.setActionCommand("Start_Test_Set");
    this.testController.add(this.start, "24, 2");
    
    this.chckbxAvailable.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ControlCenter.this.evaluateCondition(e);
      }
    });
    this.pause = new JButton("Pause");
    this.pause.addActionListener(this);
    this.pause.setEnabled(false);
    this.pause.setActionCommand("Pause_Test_Set");
    this.testController.add(this.pause, "26, 2");
    this.stop = new JButton("Stop");
    this.stop.addActionListener(this);
    this.stop.setActionCommand("Stop_Test_Set");
    this.stop.setEnabled(false);
    this.testController.add(this.stop, "28, 2");
    
    this.centerPanel = new JSplitPane();
    this.centerPanel.setOneTouchExpandable(true);
    this.tableView = new ScenariosView(this.theTestSet, this.theTestSet.getTestsAsList());
    this.testTree = new RoadMap(new CopyOnWriteArrayList(), this.theTestSet, this.tableView);
    this.testTree.setTestSet(this.theTestSet);
    this.centerPanel.setLeftComponent(new JScrollPane(this.testTree));
    
    this.tableView.setAutoResizeMode(4);
    this.centerPanel.setRightComponent(new JScrollPane(this.tableView));
    add(this.centerPanel, "Center");
    
    this.centerPanel.setDividerLocation(300);
    this.southPanel = new StatusPanel(this.currentServer);
    add(this.southPanel, "South");
    
    this.southPanel.setPreferredSize(new Dimension(this.southPanel.getPreferredSize().width, this.southPanel.getPreferredSize().height));
    this.southPanel.setMinimumSize(new Dimension(this.southPanel.getPreferredSize().width, this.southPanel.getPreferredSize().height));
    this.southPanel.setMaximumSize(new Dimension(this.southPanel.getPreferredSize().width, this.southPanel.getPreferredSize().height));
    
    this.workManager = workManager;
    if ((this.theTestSet.getRunStatus().equals(TestSetRunStatus.RUNNING.toString())) || (this.theTestSet.getRunStatus().equals(TestSetRunStatus.READY_TO_RUN.toString())))
    {
      this.start.setEnabled(true);
      this.stop.setEnabled(false);
      this.pause.setEnabled(false);
    }
  }
  
  private void constructNumberOfSelectedTestsLabel()
  {
    this.lblNumberOfSelectedTests.setText(String.valueOf(this.theTestSet.getTests().size()) + "/" + String.valueOf(this.allTests.size()) + " Tests in Test Set.");
  }
  
  private boolean registerScheduler(final ActionEvent e)
  {
    String value = this.startTime.getEditor().getText();
    Date date = null;
    if ((value != null) && (!value.isEmpty())) {
      try
      {
        date = this.dateFormatter.parse(value);
      }
      catch (ParseException e1) {}
    }
    if (date != null)
    {
      Calendar future = Calendar.getInstance();
      future.setTime(date);
      Calendar current = Calendar.getInstance();
      if (future.after(current))
      {
        if ((this.currentEvent != null) && (!this.currentEvent.isDone()))
        {
          LoggerPane.debug("Cancelling last scheduled event.");
          this.currentEvent.cancel(true);
          this.currentEvent = null;
        }
        LoggerPane.info("Scheduling to start the server at " + this.dateFormatter.format(date));
        this.currentEvent = Executors.newSingleThreadScheduledExecutor().schedule(new Runnable()
        {
          public void run()
          {
            ControlCenter.this.currentEvent = null;
            ControlCenter.this.evaluateCondition(e);
          }
        }, future.getTimeInMillis() - current.getTimeInMillis(), TimeUnit.MILLISECONDS);
        
        this.startScheduled = true;
        return true;
      }
      LoggerPane.error("The entered date is in the past.  Cannot schedule an event in the past.");
    }
    return false;
  }
  
  private void evaluateCondition(final ActionEvent e)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if (e.getActionCommand().equals("Start_Test_Set"))
        {
          if ((ControlCenter.this.startTime.getDate() != null) && (!ControlCenter.this.startScheduled))
          {
            boolean status = ControlCenter.this.registerScheduler(e);
            if (!status)
            {
              ControlCenter.this.start.setEnabled(true);
              ControlCenter.this.stop.setEnabled(false);
              ControlCenter.this.pause.setEnabled(false);
              return;
            }
            ControlCenter.this.start.setEnabled(false);
            ControlCenter.this.start.setText("Scheduled");
            ControlCenter.this.stop.setEnabled(false);
            ControlCenter.this.pause.setEnabled(false);
            if (ControlCenter.this.currentServer != null) {
              ControlCenter.this.setServerStatus(ServerStatus.IDLE);
            }
          }
          else
          {
            ControlCenter.this.start.setEnabled(false);
            ControlCenter.this.start.setText("Start");
            ControlCenter.this.stop.setEnabled(true);
            ControlCenter.this.pause.setEnabled(true);
            ControlCenter.this.setServerStatus(ServerStatus.EXECUTING_TEST_SET);
            ControlCenter.this.theTestSet.setRunStatus(TestSetRunStatus.RUNNING.toString());
            UnitOfWorkFlow workFlow = UnitOfWorkFlow.getInstance(ControlCenter.this.theTestSet, ControlCenter.this.workManager, ControlCenter.this.currentServer);
            if (!ControlCenter.this.isPaused) {
              for (UnitOfWork work : workFlow.getAllWork()) {
                if (work.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS)) {
                  work.updateStatus(ExecutionStatus.NOT_RUN);
                }
              }
            }
            workFlow.processWorkQueues();
            if (ControlCenter.this.isPaused) {
              ControlCenter.this.isPaused = false;
            }
            try
            {
              ControlCenter.this.theTestSet.save();
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
              ControlCenter.log.error("Error (Unknown): ", ex);
            }
            ControlCenter.this.tableView.setAllWork(workFlow.getAllWork());
            ControlCenter.this.testTree.setAllWork(workFlow.getAllWork());
            ControlCenter.this.testTree.setWorkFlow(workFlow);
            if (ControlCenter.this.currentServer == null)
            {
              LoggerPane.debug("Starting the server.");
              ControlCenter.this.currentServer = new AutomationCoordinatorServer(ControlCenter.this.serverStatus, ControlCenter.this.theTestSet, ControlCenter.this.almServerConfig, ControlCenter.this.workManager);
            }
            LoggerPane.info("Execution of Test Set started");
          }
        }
        else if (e.getActionCommand().equals("Pause_Test_Set"))
        {
          if (ControlCenter.this.currentServer != null)
          {
            ControlCenter.this.setServerStatus(ServerStatus.PAUSED);
            ControlCenter.this.start.setEnabled(true);
            ControlCenter.this.pause.setEnabled(false);
            ControlCenter.this.isPaused = true;
          }
        }
        else if (e.getActionCommand().equals("Stop_Test_Set"))
        {
          LoggerPane.debug("Stopping Test Set Execution.");
          ControlCenter.this.start.setEnabled(false);
          ControlCenter.this.stop.setEnabled(false);
          ControlCenter.this.pause.setEnabled(false);
          ControlCenter.this.setServerStatus(ServerStatus.STOPPED);
          if (!ControlCenter.this.processingFinishedTestSet)
          {
            ControlCenter.this.processingFinishedTestSet = true;
            SwingUtilities.invokeLater(new Runnable()
            {
              public void run()
              {
                while (!ControlCenter.this.currentServer.allClientAreFinished()) {
                  try
                  {
                    Thread.sleep(5000L);
                  }
                  catch (InterruptedException e)
                  {
                    ControlCenter.log.error("Error (Interrupted): ", e);
                  }
                }
                ControlCenter.this.theTestSet.setRunStatus(TestSetRunStatus.COMPLETE.toString());
                ControlCenter.this.theTestSet.persistTestResults();
                try
                {
                  ControlCenter.this.theTestSet.save();
                }
                catch (Exception ex)
                {
                  ControlCenter.log.error("Error (Unknown): ", ex);
                }
              }
            });
            ControlCenter.this.processingFinishedTestSet = false;
          }
        }
      }
    });
  }
  
  public Set<TestResult> saveDefaultTestResult()
  {
    UnitOfWork root = UnitOfWorkFlow.getInstance(this.theTestSet, this.workManager, this.currentServer).getRootNode();
    Set<TestResult> results = new HashSet();
    if ((root instanceof WorkerBucket)) {
      appendTestResult((WorkerBucket)root, results);
    }
    root.evaluateState();
    return results;
  }
  
  private void appendTestResult(WorkerBucket root, Set<TestResult> results)
  {
    for (UnitOfWork work : root.getAllWorks()) {
      if ((work instanceof WorkerBucket))
      {
        WorkerBucket theBucket = (WorkerBucket)work;
        appendTestResult(theBucket, results);
      }
      else
      {
        results.add(work.saveTestResult());
      }
    }
  }
  
  public void packTable()
  {
    if (this.tableView.isShowing()) {
      this.tableView.pack(0, true);
    }
  }
  
  public void componentResized(ComponentEvent e) {}
  
  public void componentMoved(ComponentEvent e) {}
  
  public void componentShown(ComponentEvent e) {}
  
  public void componentHidden(ComponentEvent e) {}
  
  public void update(Observable o, Object arg)
  {
    this.txtTestSetPath.setText(this.theTestSet.getAlmPath());
    this.txtTestSetName.setText(this.theTestSet.getTestSetName());
    this.txtRound.setText(this.theTestSet.getRound());
    if (!this.theTestSet.getRelease().trim().isEmpty()) {
      this.txtRelease.setText("v" + this.theTestSet.getRelease());
    }
    if (this.theTestSet.getHardStopRoll() == 'Y')
    {
      this.chckbxHardStopRoll.setSelected(true);
      this.releaseHardStop.setEnabled(true);
    }
    else
    {
      this.chckbxHardStopRoll.setSelected(false);
      this.releaseHardStop.setEnabled(false);
    }
    if (this.theTestSet.isExecutionComplete())
    {
      if (!this.processingFinishedTestSet)
      {
        this.processingFinishedTestSet = true;
        setServerStatus(ServerStatus.FINISHED);
        if (!this.theTestSet.getRunStatus().equals(TestSetRunStatus.COMPLETE.toString())) {
          this.theTestSet.setRunStatus(TestSetRunStatus.COMPLETE.toString());
        }
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            while (!ControlCenter.this.currentServer.allClientAreFinished()) {
              try
              {
                Thread.sleep(5000L);
              }
              catch (InterruptedException e)
              {
                ControlCenter.log.error("Error (Interrupted): ", e);
              }
            }
            //Srikanth 12/13/2017
          //ControlCenter.this.theTestSet.persistTestResults();
            try
            {
              ControlCenter.this.theTestSet.save();
            }
            catch (Exception ex)
            {
              ControlCenter.log.error("Error (Unknown): ", ex);
            }
          }
        });
        this.processingFinishedTestSet = false;
      }
    }
    else if (this.theTestSet.hasTests())
    {
      if (this.serverStatus.equals(ServerStatus.IDLE)) {
        for (AutomationTest test : this.theTestSet.getTests()) {
          this.testTree.addTestToTree(test);
        }
      }
      if ((!this.serverStatus.equals(ServerStatus.EXECUTING_TEST_SET)) && (!this.serverStatus.equals(ServerStatus.EXECUTING_ROLL_FAILED)))
      {
        if ((this.theTestSet.isPersistedInALM()) && (!this.serverStatus.equals(ServerStatus.STOPPED)) && (!this.serverStatus.equals(ServerStatus.FINISHED))) {
          this.start.setEnabled(true);
        } else {
          this.start.setEnabled(false);
        }
      }
      else {
        this.start.setEnabled(false);
      }
    }
    else
    {
      this.start.setEnabled(false);
    }
    constructNumberOfSelectedTestsLabel();
  }
  
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("Start_Test_Set"))
    {
      System.out.println("Start button pressed...");
      log.debug("Start button pressed...");
      this.serverStatus = ServerStatus.EXECUTING_TEST_SET;
      evaluateCondition(e);
    }
    if (e.getActionCommand().equals("Pause_Test_Set"))
    {
      System.out.println("Pause button pressed...");
      log.debug("Pause button pressed...");
      this.serverStatus = ServerStatus.PAUSED;
      evaluateCondition(e);
    }
    if (e.getActionCommand().equals("Stop_Test_Set"))
    {
      System.out.println("Stop button pressed...");
      log.debug("Stop button pressed...");
      this.serverStatus = ServerStatus.IDLE;
      evaluateCondition(e);
    }
    if (e.getActionCommand().equals("Release_Hard_Stop"))
    {
      System.out.println("Release Hard Stop button pressed...");
      log.debug("Release Hard Stop button pressed...");
      this.theTestSet.setHardStopRoll('N');
    }
  }
  
  public ServerStatus getServerStatus()
  {
    return this.serverStatus;
  }
  
  public void setServerStatus(ServerStatus serverStatus)
  {
    this.serverStatus = serverStatus;
    if (this.currentServer != null) {
      this.currentServer.setServerStatus(serverStatus);
    }
    if (this.serverStatus.equals(ServerStatus.FINISHED))
    {
      this.start.setEnabled(false);
      this.pause.setEnabled(false);
      this.stop.setEnabled(false);
    }
  }
  
  class JCheckBoxReadOnly
    extends JCheckBox
  {
    private static final long serialVersionUID = 1L;
    
    JCheckBoxReadOnly() {}
    
    protected void processMouseEvent(MouseEvent e) {}
  }
}
