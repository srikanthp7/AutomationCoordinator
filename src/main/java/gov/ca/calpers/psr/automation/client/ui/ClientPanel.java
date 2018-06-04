package gov.ca.calpers.psr.automation.client.ui;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.UnitOfWork;
import gov.ca.calpers.psr.automation.client.WorkHandler;
import gov.ca.calpers.psr.automation.command.Ack;
import gov.ca.calpers.psr.automation.command.ExecutingServerStatus;
import gov.ca.calpers.psr.automation.command.FinishedServerStatus;
import gov.ca.calpers.psr.automation.command.IdleServerStatus;
import gov.ca.calpers.psr.automation.command.PausedServerStatus;
import gov.ca.calpers.psr.automation.command.ServerStatusRequest;
import gov.ca.calpers.psr.automation.command.StatusUpdate;
import gov.ca.calpers.psr.automation.command.StoppedServerStatus;
import gov.ca.calpers.psr.automation.command.WorkRequest;
import gov.ca.calpers.psr.automation.command.WorkResponse;
import gov.ca.calpers.psr.automation.command.WorkResult;
import gov.ca.calpers.psr.automation.pojo.Instruction;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.painter.MattePainter;

public class ClientPanel  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss");
  private final JTextField host = new JTextField();
  private JPanel northPanel;
  private final JCheckBox chckbxConnect = new JCheckBox("Connect");
  private ExecutorService dispatcherExecutor;
  private final String statusDisconnected = "<html><font color=\"red\">Disconnected</font></html>";
  private final String statusConnected = "<html><font color=\"#008000\">Connected</font></html>";
  private final String statusReconnecting = "<html><font color=\"#FFA500\">Reconnecting</font></html>";
  private final String statusIdle = "<html><font color=\"#0000FF\">Idle</font></html>";
  private final String statusRunning = "<html><font color=\"#008000\">Running</font></html>";
  private final String statusFinished = "<html><font color=\"#008000\">Finished</font></html>";
  private final String statusStopped = "<html><font color=\"#E80000\">Stopped</font></html>";
  private final String statusPaused = "<html><font color=\"#0000FF\">Idle</font></html>";
  private final String statusNotAcceptingConnections = "<html><font color=\"red\">Server is not accepting connection.  Retry later.</font></html>";
  private final String statusDisconnectUnexectedly = "<html><font color=\"red\">Server has disconnected unexpectedly.</font></html>";
  private JLabel lblClientConnectionStatusValue;
  private JLabel lblServerStatusValue;
  private JLabel lblTestName;
  private JLabel lblTestNameValue;
  private JLabel lblTestStatus;
  private JLabel lblRunCount;
  private JLabel lblTestStatusValue;
  private JLabel lblRunCountValue;
  private JLabel lblStartTime;
  private JLabel lblStartTimeValue;
  private WorkHandler handler;
  private static Logger log = LogManager.getLogger(ClientPanel.class);
  private JPanel testInfoPanel;
  private JPanel clientServerStatusPanel;
  private Color defaultPanelColor;
  private String distributeFrmInd;
  
  static void renderSplashFrame(Graphics2D g, int frame)
  {
    String[] comps = { "Server", "bar", "baz" };
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(120, 140, 200, 40);
    g.setPaintMode();
    g.setColor(Color.BLACK);
    g.drawString("Loading...", 150, 150);
  }
  
  public ClientPanel()
  {
    setBorder(new EtchedBorder(1, null, null));
    SplashScreen splash = SplashScreen.getSplashScreen();
    if (splash == null)
    {
      System.out.println("SplashScreen.getSplashScreen() returned null");
      log.debug("SplashScreen.getSplashScreen() returned null");
    }
    else
    {
      Graphics2D g = splash.createGraphics();
      if (g == null)
      {
        System.out.println("g is null");
        log.debug("g is null");
      }
      else
      {
        renderSplashFrame(g, 0);
        splash.update();
      }
    }
    setName("Job Scheduler Client");
    setSize(new Dimension(530, 500));
    setPreferredSize(new Dimension(530, 225));
    setMinimumSize(new Dimension(530, 225));
    this.dispatcherExecutor = Executors.newSingleThreadExecutor(new ThreadFactory()
    {
      public Thread newThread(Runnable r)
      {
        Thread thread = new Thread(r);
        thread.setName("Client_Worker");
        return thread;
      }
    });
    setLayout(new BorderLayout(0, 0));
    init();
    
    add(getNorthPanel(), "North");
  }
  
  private void init()
  {
    this.host.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    this.host.setColumns(10);
    getNorthPanel().add(this.host);
    this.host.setText(ClientProperty.getIntance().getServer());
    this.host.addFocusListener(new FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        ClientProperty.getIntance().updateServer(ClientPanel.this.host.getText());
      }
    });
    JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
    centerPanel.setBorder(new EtchedBorder(1, null, null));
    add(centerPanel, "Center");
    SpringLayout testInfoLayout = new SpringLayout();
    this.testInfoPanel = new JPanel(testInfoLayout);
    this.testInfoPanel.setBorder(new TitledBorder(null, "Current Test", 4, 2, null, null));
    SpringLayout clientServerLayout = new SpringLayout();
    this.clientServerStatusPanel = new JPanel(clientServerLayout);
    this.clientServerStatusPanel.setPreferredSize(new Dimension(500, 65));
    centerPanel.add(this.testInfoPanel, "Center");
    
    this.lblTestName = new JLabel("Test Name:");
    testInfoLayout.putConstraint("North", this.lblTestName, 0, "North", this.testInfoPanel);
    testInfoLayout.putConstraint("West", this.lblTestName, 10, "West", this.testInfoPanel);
    this.testInfoPanel.add(this.lblTestName);
    
    this.lblTestNameValue = new JLabel("");
    testInfoLayout.putConstraint("North", this.lblTestNameValue, 0, "North", this.lblTestName);
    testInfoLayout.putConstraint("West", this.lblTestNameValue, 10, "East", this.lblTestName);
    this.testInfoPanel.add(this.lblTestNameValue);
    
    this.lblTestStatus = new JLabel("Status:");
    testInfoLayout.putConstraint("North", this.lblTestStatus, 7, "South", this.lblTestName);
    testInfoLayout.putConstraint("West", this.lblTestStatus, 0, "West", this.lblTestName);
    this.testInfoPanel.add(this.lblTestStatus);
    
    this.lblRunCount = new JLabel("Run Count:");
    testInfoLayout.putConstraint("North", this.lblRunCount, 7, "South", this.lblTestStatus);
    testInfoLayout.putConstraint("West", this.lblRunCount, 0, "West", this.lblTestName);
    this.testInfoPanel.add(this.lblRunCount);
    
    this.lblTestStatusValue = new JLabel("");
    testInfoLayout.putConstraint("North", this.lblTestStatusValue, 7, "South", this.lblTestNameValue);
    testInfoLayout.putConstraint("West", this.lblTestStatusValue, 0, "West", this.lblTestNameValue);
    this.testInfoPanel.add(this.lblTestStatusValue);
    
    this.lblRunCountValue = new JLabel("");
    testInfoLayout.putConstraint("North", this.lblRunCountValue, 7, "South", this.lblTestStatusValue);
    testInfoLayout.putConstraint("West", this.lblRunCountValue, 0, "West", this.lblTestNameValue);
    testInfoLayout.putConstraint("South", this.lblRunCountValue, 0, "South", this.lblRunCount);
    this.testInfoPanel.add(this.lblRunCountValue);
    
    this.lblStartTime = new JLabel("Start Time:");
    testInfoLayout.putConstraint("North", this.lblStartTime, 7, "South", this.lblRunCount);
    testInfoLayout.putConstraint("West", this.lblStartTime, 0, "West", this.lblTestName);
    this.testInfoPanel.add(this.lblStartTime);
    
    this.lblStartTimeValue = new JLabel("");
    testInfoLayout.putConstraint("North", this.lblStartTimeValue, 7, "South", this.lblRunCountValue);
    testInfoLayout.putConstraint("West", this.lblStartTimeValue, 0, "West", this.lblTestNameValue);
    testInfoLayout.putConstraint("South", this.lblStartTimeValue, 0, "South", this.lblStartTime);
    this.testInfoPanel.add(this.lblStartTimeValue);
    this.clientServerStatusPanel.setBorder(new TitledBorder(null, "Client & Server Status", 4, 2, null, null));
    
    centerPanel.add(this.clientServerStatusPanel, "North");
    
    JLabel lblClientStatus = new JLabel("Client Status:");
    clientServerLayout.putConstraint("North", lblClientStatus, 0, "North", this.clientServerStatusPanel);
    clientServerLayout.putConstraint("West", lblClientStatus, 10, "West", this.clientServerStatusPanel);
    this.clientServerStatusPanel.add(lblClientStatus);
    
    this.lblClientConnectionStatusValue = new JLabel("<html><font color=\"red\">Disconnected</font></html>");
    clientServerLayout.putConstraint("North", this.lblClientConnectionStatusValue, 0, "North", lblClientStatus);
    clientServerLayout.putConstraint("West", this.lblClientConnectionStatusValue, 10, "East", lblClientStatus);
    this.clientServerStatusPanel.add(this.lblClientConnectionStatusValue);
    
    JLabel lblServerStatus = new JLabel("Server Status:");
    clientServerLayout.putConstraint("North", lblServerStatus, 6, "South", lblClientStatus);
    clientServerLayout.putConstraint("West", lblServerStatus, 0, "West", lblClientStatus);
    this.clientServerStatusPanel.add(lblServerStatus);
    
    this.lblServerStatusValue = new JLabel("<html><font color=\"red\">Disconnected</font></html>");
    clientServerLayout.putConstraint("North", this.lblServerStatusValue, 6, "South", this.lblClientConnectionStatusValue);
    clientServerLayout.putConstraint("West", this.lblServerStatusValue, 0, "West", this.lblClientConnectionStatusValue);
    this.clientServerStatusPanel.add(this.lblServerStatusValue);
  }
  
  private JPanel getNorthPanel()
  {
    if (this.northPanel == null)
    {
      this.northPanel = new JPanel();
      this.northPanel.setBorder(new EtchedBorder(1, null, null));
      this.northPanel.setPreferredSize(new Dimension(500, 70));
      SpringLayout springLayout = new SpringLayout();
      springLayout.putConstraint("South", this.chckbxConnect, -10, "South", this.northPanel);
      springLayout.putConstraint("East", this.host, -102, "East", this.northPanel);
      springLayout.putConstraint("North", this.host, 10, "North", this.northPanel);
      this.northPanel.setLayout(springLayout);
      this.chckbxConnect.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          ClientPanel.this.onlineStateChanged();
        }
      });
      this.chckbxConnect.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              ClientPanel.this.evalCondition();
            }
          });
        }
      });
      this.northPanel.add(this.chckbxConnect);
      
      JLabel label = new JLabel("Host:");
      springLayout.putConstraint("South", this.chckbxConnect, 35, "South", label);
      springLayout.putConstraint("North", label, 10, "North", this.northPanel);
      springLayout.putConstraint("West", this.host, 6, "East", label);
      springLayout.putConstraint("West", label, 7, "West", this.northPanel);
      this.northPanel.add(label);
      this.defaultPanelColor = this.northPanel.getBackground();
    }
    return this.northPanel;
  }
  
  private void evalCondition()
  {
    this.dispatcherExecutor.execute(new Runnable()
    {
      public void run()
      {
        boolean loggedOnce = false;
        boolean displayedIdleMessage = false;
        boolean displayedStoppedMessage = false;
        boolean displayedPausedMessage = false;
        boolean displayedExecutingMessage = false;
        boolean displayedFinishedMessage = false;
        boolean displayedServerStatusRequest = false;
        boolean executingTest = false;
        boolean receivedNull = false;
        
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try
        {
          socket = new Socket(ClientPanel.this.host.getText(), 4000);
          out = new ObjectOutputStream(socket.getOutputStream());
          out.flush();
          in = new ObjectInputStream(socket.getInputStream());
        }
        catch (Exception e)
        {
          if ("Connection refused: connect".equals(e.getMessage()))
          {
            if (!loggedOnce)
            {
              ClientPanel.log.error("Server is not accepting connection.  Retry later");
              loggedOnce = true;
            }
            ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
            
            ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Server is not accepting connection.  Retry later.</font></html>");
          }
          else
          {
            ClientPanel.log.error("Error initializing socket and/or input and output streams: ", e);
          }
        }
        if (socket != null)
        {
          WorkResponse work = null;
          int runCount = 0;
          while (ClientPanel.this.chckbxConnect.isSelected())
          {
            try
            {
              if (socket == null) {
                while ((ClientPanel.this.chckbxConnect.isSelected()) && (socket == null)) {
                  try
                  {
                    ClientPanel.log.info("Socket is null, creating a new socket");
                    loggedOnce = false;
                    displayedIdleMessage = false;
                    displayedStoppedMessage = false;
                    displayedPausedMessage = false;
                    displayedExecutingMessage = false;
                    displayedFinishedMessage = false;
                    displayedServerStatusRequest = false;
                    executingTest = false;
                    receivedNull = false;
                    executingTest = false;
                    ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"#FFA500\">Reconnecting</font></html>");
                    
                    socket = new Socket(ClientPanel.this.host.getText(), 4000);
                    
                    out = new ObjectOutputStream(socket.getOutputStream());
                    
                    out.flush();
                    in = new ObjectInputStream(socket.getInputStream());
                    
                    ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"#008000\">Connected</font></html>");
                  }
                  catch (Exception e)
                  {
                    if ("Connection refused: connect".equals(e.getMessage()))
                    {
                      if (!loggedOnce)
                      {
                        ClientPanel.log.error("Server is not accepting connection.  Retry later");
                        loggedOnce = true;
                      }
                      ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Server is not accepting connection.  Retry later.</font></html>");
                    }
                    else
                    {
                      ClientPanel.log.error("Error creating new Socket: ", e);
                    }
                    try
                    {
                      Thread.sleep(10000L);
                    }
                    catch (InterruptedException ex)
                    {
                      ex.printStackTrace();
                      ClientPanel.log.error("Error during thread sleep: ", ex);
                    }
                  }
                }
              }
              if (!loggedOnce)
              {
                ClientPanel.log.info("Creating a connection to the server.");
                loggedOnce = true;
              }
              ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"#008000\">Connected</font></html>");
              if ((!displayedIdleMessage) && (!displayedStoppedMessage) && (!displayedPausedMessage) && (!receivedNull) && (!displayedServerStatusRequest)) {
                ClientPanel.log.info("Sending server status request to server");
              }
              out.reset();
              out.writeObject(new ServerStatusRequest());
              out.flush();
              
              ClientPanel.log.info("Waiting on work from server.");
              Object object = in.readObject();
              if ((object instanceof IdleServerStatus))
              {
                if (!displayedIdleMessage)
                {
                  ClientPanel.log.info("Server is currently in status: Idle.");
                  displayedIdleMessage = true;
                }
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                receivedNull = false;
              }
              else if ((object instanceof StoppedServerStatus))
              {
                if (!displayedStoppedMessage)
                {
                  ClientPanel.log.info("Server is currently in status: Stopped.");
                  displayedStoppedMessage = true;
                }
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#E80000\">Stopped</font></html>");
                receivedNull = false;
              }
              else if ((object instanceof PausedServerStatus))
              {
                if (!displayedPausedMessage)
                {
                  ClientPanel.log.info("Server is currently in status: Paused.");
                  displayedPausedMessage = true;
                }
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                receivedNull = false;
              }
              else if ((object instanceof FinishedServerStatus))
              {
                if (!displayedFinishedMessage)
                {
                  ClientPanel.log.info("Server is currently in status: Finished.");
                  displayedFinishedMessage = true;
                }
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#008000\">Finished</font></html>");
                receivedNull = false;
              }
              else if ((object instanceof ExecutingServerStatus))
              {
                if ((!displayedExecutingMessage) && ((object instanceof ExecutingServerStatus)))
                {
                  ClientPanel.log.info("Server is currently in status: Executing.");
                  displayedExecutingMessage = true;
                }
                receivedNull = false;
                if (executingTest)
                {
                  ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#008000\">Running</font></html>");
                  ClientPanel.log.info("Test was executing prior to server status change. Continuing execution of test: " + work.getWork().getTestName());
                  if (displayedIdleMessage)
                  {
                    displayedIdleMessage = false;
                    ClientPanel.log.info("Server is no longer idle. Awaiting work from server.");
                  }
                  if (displayedStoppedMessage)
                  {
                    displayedStoppedMessage = false;
                    ClientPanel.log.info("Server is no longer in status: Stopped. Awaiting work from server.");
                  }
                  if (displayedPausedMessage)
                  {
                    displayedPausedMessage = false;
                    ClientPanel.log.info("Server is no longer in status: Paused. Awaiting work from server.");
                  }
                  if (displayedServerStatusRequest) {
                    displayedServerStatusRequest = false;
                  }
                  Instruction inst = work.getWork();
                  runCount = work.getWork().getRunCount();
                  ClientPanel.this.distributeFrmInd = AutomationTest.getDistributeFormIndOfTest(work.getWork().getTestName());
                  if (ClientPanel.this.distributeFrmInd.equals("Y")) {
                    ClientPanel.this.setBackground(Color.MAGENTA);
                  }
                  else {
                      ClientPanel.this.setBackground(ClientPanel.this.defaultPanelColor);
                    }
                  boolean serverStatusChanged = false;
                  while ((runCount < inst.getMaxFailedRetries()) && (!inst.getStatus().equals(ExecutionStatus.PASSED)))
                  {
                    ClientPanel.log.debug("In ClientPanel While loop #1 for current Test.");
                    if ((inst.getStatus().equals(ExecutionStatus.FAILED)) || (inst.getStatus().equals(ExecutionStatus.IN_PROGRESS)))
                    {
                      inst.setStatus(ExecutionStatus.IN_PROGRESS);
                      ClientPanel.this.handler.setStatus(ExecutionStatus.IN_PROGRESS);
                      ClientPanel.this.updateLastWorkStatus(ClientPanel.this.handler, inst);
                      StatusUpdate statusUpdate = new StatusUpdate();
                      statusUpdate.setWork(inst);
                      statusUpdate.setStatus(inst.getStatus());
                      
                      statusUpdate.setRunCount(runCount);
                      
                      ClientPanel.log.info("Sending result object back to server");
                      out.reset();
                      out.writeObject(statusUpdate);
                      out.flush();
                      object = in.readObject();
                      if ((object instanceof IdleServerStatus))
                      {
                        if (!displayedIdleMessage)
                        {
                          ClientPanel.log.info("Server is currently in status: Idle.");
                          displayedIdleMessage = true;
                        }
                        ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                        
                        receivedNull = false;
                        serverStatusChanged = true;
                        break;
                      }
                      if ((object instanceof StoppedServerStatus))
                      {
                        if (!displayedStoppedMessage)
                        {
                          ClientPanel.log.info("Server is currently in status: Stopped.");
                          displayedStoppedMessage = true;
                        }
                        ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#E80000\">Stopped</font></html>");
                        
                        receivedNull = false;
                        serverStatusChanged = true;
                        break;
                      }
                      if ((object instanceof PausedServerStatus))
                      {
                        if (!displayedPausedMessage)
                        {
                          ClientPanel.log.info("Server is currently in status: Paused.");
                          displayedPausedMessage = true;
                        }
                        ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                        
                        receivedNull = false;
                        serverStatusChanged = true;
                        break;
                      }
                      if ((object instanceof FinishedServerStatus))
                      {
                        if (!displayedFinishedMessage)
                        {
                          ClientPanel.log.info("Server is currently in status: Finished.");
                          displayedFinishedMessage = true;
                        }
                        ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#008000\">Finished</font></html>");
                        
                        receivedNull = false;
                        serverStatusChanged = true;
                        break;
                      }
                      if ((object instanceof Ack)) {
                        ClientPanel.log.info("Getting ack from server that the status update had been accepted. (1)");
                      }
                    }
                    runCount++;
                    ClientPanel.this.handler = new WorkHandler(work.getWork());
                    
                    ExecutionStatus status = ClientPanel.this.handler.getStatus();
                    
                    System.out.println("Retrieved status from handler: " + status.toString());
                    
                    ClientPanel.log.debug("Retrieved status from handler: " + status.toString());
                    
                    inst.setEndTime(Long.valueOf(System.currentTimeMillis()));
                    
                    ClientPanel.this.handler.setTotalRunCount(runCount);
                    inst.setRunCount(runCount);
                    inst.setStatus(status);
                    ClientPanel.this.updateLastWorkStatus(ClientPanel.this.handler, inst);
                    try
                    {
                      Thread.sleep(10000L);
                    }
                    catch (InterruptedException e)
                    {
                      ClientPanel.log.error("Error during thread sleep: ", e);
                    }
                  }
                  if (serverStatusChanged)
                  {
                    serverStatusChanged = false;
                    break;
                  }
                  work = null;
                  executingTest = false;
                  WorkResult result = new WorkResult();
                  result.setWork(inst);
                  result.setStatus(inst.getStatus());
                  result.setFailedRetryCount(runCount);
                  ClientPanel.log.info("Sending result object back to server");
                  out.reset();
                  out.writeObject(result);
                  out.flush();
                  
                  object = in.readObject();
                  if ((object instanceof Ack)) {
                    ClientPanel.log.info("Getting ack from server that the work status had been accepted. (2)");
                  }
                }
                else
                {
                  ClientPanel.log.info("No current test running, requesting work from server.");
                  while ((ClientPanel.this.chckbxConnect.isSelected()) && (socket != null))
                  {
                    ClientPanel.log.debug("In ClientPanel While loop #2 for current Test.");
                    ClientPanel.log.debug("Requesting Work from Server.");
                    out.reset();
                    out.writeObject(new WorkRequest());
                    out.flush();
                    ClientPanel.log.debug("Output Stream has been flushed. Now reading input stream from Server.");
                    object = in.readObject();
                    if ((object instanceof IdleServerStatus))
                    {
                      if (!displayedIdleMessage)
                      {
                        ClientPanel.log.info("Server is currently in status: Idle.");
                        displayedIdleMessage = true;
                      }
                      ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                      
                      receivedNull = false;
                      break;
                    }
                    if ((object instanceof StoppedServerStatus))
                    {
                      if (!displayedStoppedMessage)
                      {
                        ClientPanel.log.info("Server is currently in status: Stopped.");
                        displayedStoppedMessage = true;
                      }
                      ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#E80000\">Stopped</font></html>");
                      
                      receivedNull = false;
                      break;
                    }
                    if ((object instanceof PausedServerStatus))
                    {
                      if (!displayedPausedMessage)
                      {
                        ClientPanel.log.info("Server is currently in status: Paused.");
                        displayedPausedMessage = true;
                      }
                      ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                      
                      receivedNull = false;
                      break;
                    }
                    if ((object instanceof FinishedServerStatus))
                    {
                      if (!displayedPausedMessage)
                      {
                        ClientPanel.log.info("Server is currently in status: Finished.");
                        displayedFinishedMessage = true;
                      }
                      ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#008000\">Finished</font></html>");
                      
                      receivedNull = false;
                      break;
                    }
                    if ((object instanceof WorkResponse))
                    {
                      runCount = 0;
                      receivedNull = false;
                      if (displayedIdleMessage)
                      {
                        displayedIdleMessage = false;
                        ClientPanel.log.info("Server is no longer idle. Awaiting work from server.");
                      }
                      if (displayedStoppedMessage)
                      {
                        displayedStoppedMessage = false;
                        ClientPanel.log.info("Server is no longer in status: Stopped. Awaiting work from server.");
                      }
                      if (displayedPausedMessage)
                      {
                        displayedPausedMessage = false;
                        ClientPanel.log.info("Server is no longer in status: Paused. Awaiting work from server.");
                      }
                      work = (WorkResponse)object;
                      executingTest = true;
                      ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#008000\">Running</font></html>");
                      if (!ClientPanel.this.chckbxConnect.isSelected())
                      {
                        WorkResult result = new WorkResult();
                        result.setStatus(ExecutionStatus.NOT_RUN);
                        result.setWork(work.getWork());
                        out = new ObjectOutputStream(socket.getOutputStream());
                        
                        out.writeObject(result);
                        out.flush();
                        
                        in = new ObjectInputStream(socket.getInputStream());
                        
                        object = in.readObject();
                        if (!(object instanceof Ack)) {
                          break;
                        }
                        ClientPanel.log.info("Getting ack from server that the work status had been accepted. (3)"); break;
                      }
                      ClientPanel.log.info("Getting assigned work " + work.getWork().getTestName());
                      
                      work.getWork().setStartTime(System.currentTimeMillis());
                      
                      ClientPanel.this.appendWorkToTable(work);
                      Instruction inst = work.getWork();
                      runCount = inst.getRunCount();
                      ClientPanel.this.distributeFrmInd = AutomationTest.getDistributeFormIndOfTest(work.getWork().getTestName());
                      if (ClientPanel.this.distributeFrmInd.equals("Y")) {
                        ClientPanel.this.setBackground(Color.MAGENTA);
                      }
                      else {
                          ClientPanel.this.setBackground(ClientPanel.this.defaultPanelColor);
                      }
                      boolean serverStatusChanged = false;
                      while ((runCount < inst.getMaxFailedRetries()) && (!inst.getStatus().equals(ExecutionStatus.PASSED)))
                      {
                        ClientPanel.log.debug("In ClientPanel While loop #3 for current Test.");
                        if (inst.getStatus().equals(ExecutionStatus.FAILED))
                        {
                          inst.setStatus(ExecutionStatus.IN_PROGRESS);
                          ClientPanel.this.handler.setStatus(ExecutionStatus.IN_PROGRESS);
                          ClientPanel.this.updateLastWorkStatus(ClientPanel.this.handler, inst);
                          
                          StatusUpdate statusUpdate = new StatusUpdate();
                          statusUpdate.setWork(inst);
                          statusUpdate.setStatus(inst.getStatus());
                          
                          statusUpdate.setRunCount(runCount);
                          
                          ClientPanel.log.info("Sending result object back to server");
                          out.reset();
                          out.writeObject(statusUpdate);
                          out.flush();
                          object = in.readObject();
                          if ((object instanceof IdleServerStatus))
                          {
                            if (!displayedIdleMessage)
                            {
                              ClientPanel.log.info("Server is currently in status: Idle.");
                              displayedIdleMessage = true;
                            }
                            ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                            
                            receivedNull = false;
                            serverStatusChanged = true;
                            break;
                          }
                          if ((object instanceof StoppedServerStatus))
                          {
                            if (!displayedStoppedMessage)
                            {
                              ClientPanel.log.info("Server is currently in status: Stopped.");
                              displayedStoppedMessage = true;
                            }
                            ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#E80000\">Stopped</font></html>");
                            
                            receivedNull = false;
                            serverStatusChanged = true;
                            break;
                          }
                          if ((object instanceof PausedServerStatus))
                          {
                            if (!displayedPausedMessage)
                            {
                              ClientPanel.log.info("Server is currently in status: Paused.");
                              displayedPausedMessage = true;
                            }
                            ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                            
                            receivedNull = false;
                            serverStatusChanged = true;
                            break;
                          }
                          if ((object instanceof FinishedServerStatus))
                          {
                            if (!displayedFinishedMessage)
                            {
                              ClientPanel.log.info("Server is currently in status: Finished.");
                              displayedFinishedMessage = true;
                            }
                            ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                            
                            receivedNull = false;
                            serverStatusChanged = true;
                            break;
                          }
                          if ((object instanceof Ack)) {
                            ClientPanel.log.info("Getting ack from server that the status update had been accepted. (4)");
                          }
                        }
                        runCount++;
                        ClientPanel.this.handler = new WorkHandler(work.getWork());
                        
                        ExecutionStatus status = ClientPanel.this.handler.getStatus();
                        
                        System.out.println("Retrieved status from handler: " + status.toString());
                        
                        ClientPanel.log.info("Retrieved status from handler: " + status.toString());
                        
                        inst.setEndTime(Long.valueOf(System.currentTimeMillis()));
                        
                        ClientPanel.this.handler.setTotalRunCount(runCount);
                        inst.setRunCount(runCount);
                        inst.setStatus(status);
                        ClientPanel.this.updateLastWorkStatus(ClientPanel.this.handler, inst);
                        try
                        {
                          Thread.sleep(10000L);
                        }
                        catch (InterruptedException e)
                        {
                          e.printStackTrace();
                          ClientPanel.log.error("Error during thread sleep: ", e);
                        }
                      }
                      if (serverStatusChanged)
                      {
                        serverStatusChanged = false;
                        break;
                      }
                      work = null;
                      executingTest = false;
                      WorkResult result = new WorkResult();
                      result.setWork(inst);
                      result.setStatus(inst.getStatus());
                      result.setFailedRetryCount(runCount);
                      ClientPanel.log.info("Sending result object back to server");
                      out.reset();
                      out.writeObject(result);
                      out.flush();
                      
                      object = in.readObject();
                      if ((object instanceof Ack)) {
                        ClientPanel.log.info("Getting ack from server that the work status had been accepted. (5)");
                      }
                    }
                    else if (object == null)
                    {
                      if (!receivedNull)
                      {
                        ClientPanel.log.info("Recevied Null. Server is either processing work or there is no more work.");
                        ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"#0000FF\">Idle</font></html>");
                        
                        receivedNull = true;
                      }
                    }
                    else
                    {
                      ClientPanel.log.error("Unknown Error!!! Investigate object sent from the server.");
                      ClientPanel.log.error("Object toString method:\n\n" + object.toString());
                    }
                    try
                    {
                      Thread.sleep(10000L);
                    }
                    catch (InterruptedException e)
                    {
                      e.printStackTrace();
                      ClientPanel.log.error("Error during thread sleep: ", e);
                    }
                  }
                }
              }
            }
            catch (ClassNotFoundException e)
            {
              ClientPanel.log.error("Error - Exception Caught: ", e);
              ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
              
              ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
              try
              {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
              }
              catch (IOException ex)
              {
                ClientPanel.log.error("IOException occurred while closing socket: ", ex);
              }
              socket = null;
              executingTest = false;
            }
            catch (EOFException e)
            {
              ClientPanel.log.error("EOFException occurred while reading from socket: ", e);
              
              ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
              
              ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
              try
              {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
              }
              catch (IOException ex)
              {
                ClientPanel.log.error("IOException occurred while closing socket: ", ex);
              }
              socket = null;
              executingTest = false;
            }
            catch (Exception e)
            {
              if ("Connection refused: connect".equals(e.getMessage()))
              {
                if (!loggedOnce)
                {
                  ClientPanel.log.error("Server is not accepting connection.  Retry later");
                  loggedOnce = true;
                }
                ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
                
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Server is not accepting connection.  Retry later.</font></html>");
              }
              else if ("Connection reset by peer: socket write error".equals(e.getMessage()))
              {
                ClientPanel.log.error("Server has disconnected unexpectedly: ", e);
                
                ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
                
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Server has disconnected unexpectedly.</font></html>");
                try
                {
                  socket.shutdownInput();
                  socket.shutdownOutput();
                  socket.close();
                }
                catch (IOException ex)
                {
                  ClientPanel.log.error("IOException occurred while closing socket: ", ex);
                }
                socket = null;
                executingTest = false;
              }
              else if ("Software caused connection abort: socket write error".equals(e.getMessage()))
              {
                ClientPanel.log.error("Software caused connection abort with Socket. Creating a new socket.");
                ClientPanel.log.error(e.getMessage());
                ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
                
                ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
                try
                {
                  socket.shutdownInput();
                  socket.shutdownOutput();
                  socket.close();
                }
                catch (IOException ex)
                {
                  ClientPanel.log.error("IOException occurred while closing socket: ", ex);
                }
                socket = null;
                executingTest = false;
              }
              else
              {
                ClientPanel.log.error("Error - Unhandled Exception Caught: ", e);
              }
            }
            try
            {
              Thread.sleep(10000L);
            }
            catch (InterruptedException e)
            {
              e.printStackTrace();
              ClientPanel.log.error("InterruptedException caught while thread is sleeping.", e);
            }
          }
        }
        ClientPanel.log.info("Available Checkbox has been unselected. Client goes offline mode.");
        ClientPanel.this.chckbxConnect.setSelected(false);
        ClientPanel.this.host.setEditable(true);
        ClientPanel.this.host.setEnabled(true);
        ClientPanel.this.lblClientConnectionStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
        ClientPanel.this.lblServerStatusValue.setText("<html><font color=\"red\">Disconnected</font></html>");
        try
        {
          socket.shutdownInput();
          socket.shutdownOutput();
          socket.close();
          loggedOnce = false;
        }
        catch (IOException e)
        {
          ClientPanel.log.error("IOException occurred while closing socket: ", e);
        }
      }
    });
  }
  
  private boolean onlineStateChanged()
  {
    if (this.chckbxConnect.isSelected())
    {
      String hostId = this.host.getText();
      try
      {
        if ((hostId == null) || (hostId.trim().isEmpty())) {
          throw new UnknownHostException();
        }
        InetAddress inet = InetAddress.getByName(hostId);
        inet.getHostAddress();
        this.chckbxConnect.setEnabled(true);
        this.host.setEnabled(false);
      }
      catch (UnknownHostException e)
      {
        log.error("Cannot find the host id.  Please enter correct host name / ip address.");
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            ClientPanel.this.chckbxConnect.setSelected(false);
            ClientPanel.this.host.setEnabled(true);
          }
        });
      }
    }
    else
    {
      this.host.setEnabled(false);
    }
    return true;
  }
  
  private void updateLastWorkStatus(final WorkHandler handler, final Instruction work)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        ClientPanel.log.info("Distribute Forrm Ind IS EQUAL TO" + ClientPanel.this.distributeFrmInd);
        int runCount = work.getWork().getMaxReRunCount();
        ClientPanel.this.lblTestNameValue.setText(work.getWork().getTestName());
        ClientPanel.log.info("-------------Current Test From UnitOfWork IS EQUAL TO--" + work.getWork().getTestName());
        ClientPanel.log.info("-------------Current Test From Instruction IS EQUAL TO--" + work.getTestName());
        ClientPanel.this.lblTestStatusValue.setText(handler.getStatus().toString());
        ClientPanel.this.lblRunCountValue.setText(String.valueOf(handler.getTotalRunCount()));
        ClientPanel.log.info("HANDLER COUNT IS EQUAL TO:)" + handler.getTotalRunCount());
        if ((handler.getTotalRunCount() < runCount) && (handler.getStatus().toString() == "IN_PROGRESS"))
        {
          ClientPanel.this.northPanel.setBackground(Color.YELLOW);
        }
        else if ((handler.getTotalRunCount() == runCount) && (handler.getStatus().toString() == "FAILED"))
        {
          ClientPanel.this.northPanel.setBackground(Color.RED);
          ClientPanel.this.lblTestNameValue.setText(work.getWork().getTestName());
        }
        else if (handler.getStatus().toString() == "PASSED")
        {
          ClientPanel.this.northPanel.setBackground(Color.GREEN);
        }
        if (ClientPanel.this.distributeFrmInd.equals("Y")) {
          ClientPanel.this.setBackground(Color.MAGENTA);
        }
        ClientPanel.this.validate();
        ClientPanel.this.repaint();
      }
    });
  }
  
  private void appendWorkToTable(final WorkResponse work)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        ClientPanel.log.info("In appendWorkToTable Current Test IS EQUAL TO" + work.getWork().getTestName());
        ClientPanel.this.lblTestNameValue.setText(work.getWork().getTestName());
        ClientPanel.this.lblTestNameValue.repaint();
        ClientPanel.this.lblTestStatusValue.setText(work.getWork().getStatus().toString());
        ClientPanel.this.lblRunCountValue.setText(String.valueOf(work.getWork().getRunCount()));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(work.getWork().getStartTime());
        ClientPanel.this.lblStartTimeValue.setText(ClientPanel.DATE_TIME_FORMAT.format(cal.getTime()));
        if (ClientPanel.this.handler.getStatus().toString() == "IN_PROGRESS") {
          ClientPanel.this.northPanel.setBackground(Color.YELLOW);
        } else {
          ClientPanel.this.northPanel.setBackground(ClientPanel.this.defaultPanelColor);
        }
        ClientPanel.this.validate();
        ClientPanel.this.repaint();
      }
    });
  }
  
  public synchronized boolean isConnectChecked()
  {
    return this.chckbxConnect.isSelected();
  }
  
  public static void main(String[] args)
  {
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
    if (SwingUtilities.isEventDispatchThread())
    {
      JFrame fram = new JFrame();
      ArrayList<Image> programIcons = new ArrayList();
      try
      {
        programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_16.png")));
        programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_32.png")));
        programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_64.png")));
        programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_128.png")));
        fram.setIconImages(programIcons);
      }
      catch (IOException ex)
      {
        System.out.println("Error: " + ex.getMessage());
        log.error(ex.getStackTrace());
      }
      fram.getContentPane().add(new ClientPanel());
      fram.setTitle("Job Scheduler Client");
      fram.setVisible(true);
      fram.setEnabled(true);
      fram.setDefaultCloseOperation(3);
      fram.pack();
      fram.validate();
      fram.repaint();
    }
    else
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          JFrame fram = new JFrame();
          ArrayList<Image> programIcons = new ArrayList();
          try
          {
            programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_16.png")));
            programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_32.png")));
            programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_64.png")));
            programIcons.add(ImageIO.read(ClientPanel.class.getResourceAsStream("resources/calpers-logo_128.png")));
            fram.setIconImages(programIcons);
          }
          catch (IOException ex)
          {
            System.out.println("Error: " + ex.getMessage());
            ClientPanel.log.error(ex.getStackTrace());
          }
          fram.getContentPane().add(new ClientPanel());
          fram.setTitle("Job Scheduler Client");
          fram.setVisible(true);
          fram.setEnabled(true);
          fram.addWindowListener(new WindowAdapter()
          {
            public void windowClosing(WindowEvent windowEvent)
            {
              System.exit(0);
            }
          });
          fram.setDefaultCloseOperation(3);
          fram.pack();
          fram.validate();
          fram.repaint();
        }
      });
    }
  }
}
