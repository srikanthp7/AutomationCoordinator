package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.AutomationTestSet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TestSetSelectionDialog
  extends JDialog
  implements ActionListener
{
  private static final long serialVersionUID = 1L;
  private JList runningTestSetList;
  private JList draftTestSetLists;
  private DefaultListModel runningTestSetListModel;
  private DefaultListModel draftTestSetListModel;
  private AutomationTestSet selectedTestSet;
  private JButton loadButton;
  private boolean isRunning = false;
  
  public TestSetSelectionDialog(CopyOnWriteArrayList<AutomationTestSet> runningTestSets, CopyOnWriteArrayList<AutomationTestSet> draftTestSets)
  {
    setResizable(false);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        TestSetSelectionDialog.this.selectedTestSet = new AutomationTestSet();
      }
    });
    setModal(true);
    setSize(new Dimension(295, 510));
    setPreferredSize(new Dimension(400, 500));
    setTitle("Current Unfinished Test Sets");
    JPanel bottomPanel = new JPanel();
    getContentPane().add(bottomPanel, "South");
    bottomPanel.setLayout(new BoxLayout(bottomPanel, 0));
    
    Component horizontalGlue = Box.createHorizontalGlue();
    bottomPanel.add(horizontalGlue);
    
    JButton btnNewTestSet = new JButton("New Test Set");
    btnNewTestSet.setActionCommand("New_Test_Set");
    btnNewTestSet.addActionListener(this);
    bottomPanel.add(btnNewTestSet);
    
    Component horizontalStrut = Box.createHorizontalStrut(20);
    horizontalStrut.setPreferredSize(new Dimension(5, 0));
    horizontalStrut.setMinimumSize(new Dimension(5, 0));
    horizontalStrut.setMaximumSize(new Dimension(5, 32767));
    bottomPanel.add(horizontalStrut);
    
    this.loadButton = new JButton("Load Test Set");
    this.loadButton.setActionCommand("Load_Test_Set");
    this.loadButton.addActionListener(this);
    this.loadButton.setEnabled(false);
    bottomPanel.add(this.loadButton);
    
    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Unfinished Test Sets", 4, 2, null, new Color(0, 0, 0)));
    getContentPane().add(mainPanel, "Center");
    
    SpringLayout layout = new SpringLayout();
    mainPanel.setLayout(layout);
    this.runningTestSetListModel = new DefaultListModel();
    for (AutomationTestSet testSet : runningTestSets)
    {
      this.runningTestSetListModel.addElement(testSet);
      System.out.println("**Added Test Set to List.***");
    }
    this.runningTestSetList = new JList(this.runningTestSetListModel);
    this.runningTestSetList.setSelectionMode(0);
    
    JScrollPane runningListPane = new JScrollPane(this.runningTestSetList);
    runningListPane.setBorder(new TitledBorder(null, "Running or Paused Test Sets", 4, 2, null, null));
    runningListPane.setPreferredSize(new Dimension(258, 200));
    layout.putConstraint("North", runningListPane, 10, "North", mainPanel);
    layout.putConstraint("West", runningListPane, 10, "West", mainPanel);
    mainPanel.add(runningListPane, "Center");
    
    JScrollPane draftListPane = new JScrollPane((Component)null);
    draftListPane.setBorder(new TitledBorder(null, "Draft Test Sets", 4, 2, null, null));
    draftListPane.setPreferredSize(new Dimension(258, 200));
    draftListPane.setSize(new Dimension(300, 300));
    layout.putConstraint("North", draftListPane, 17, "South", runningListPane);
    layout.putConstraint("West", draftListPane, 0, "West", runningListPane);
    mainPanel.add(draftListPane);
    this.draftTestSetListModel = new DefaultListModel();
    for (AutomationTestSet testSet : draftTestSets)
    {
      this.draftTestSetListModel.addElement(testSet);
      System.out.println("**Added Test Set to List.***");
    }
    this.draftTestSetLists = new JList(this.draftTestSetListModel);
    this.draftTestSetLists.setSelectionMode(0);
    draftListPane.setViewportView(this.draftTestSetLists);
    
    this.runningTestSetList.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent se)
      {
        if ((!TestSetSelectionDialog.this.isRunning) && (TestSetSelectionDialog.this.draftTestSetLists.getSelectedIndex() >= 0))
        {
          TestSetSelectionDialog.this.isRunning = true;
          TestSetSelectionDialog.this.draftTestSetLists.clearSelection();
          TestSetSelectionDialog.this.isRunning = false;
        }
        TestSetSelectionDialog.this.selectedTestSet = ((AutomationTestSet)TestSetSelectionDialog.this.runningTestSetList.getSelectedValue());
        TestSetSelectionDialog.this.loadButton.setEnabled(true);
      }
    });
    this.draftTestSetLists.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent se)
      {
        if ((!TestSetSelectionDialog.this.isRunning) && (TestSetSelectionDialog.this.runningTestSetList.getSelectedIndex() >= 0))
        {
          TestSetSelectionDialog.this.isRunning = true;
          TestSetSelectionDialog.this.runningTestSetList.clearSelection();
          TestSetSelectionDialog.this.isRunning = false;
        }
        TestSetSelectionDialog.this.selectedTestSet = ((AutomationTestSet)TestSetSelectionDialog.this.draftTestSetLists.getSelectedValue());
        TestSetSelectionDialog.this.loadButton.setEnabled(true);
      }
    });
  }
  
  public AutomationTestSet showDialog()
  {
    setVisible(true);
    return this.selectedTestSet;
  }
  
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("New_Test_Set"))
    {
      this.selectedTestSet = new AutomationTestSet();
      dispose();
    }
    else if (e.getActionCommand().equals("Load_Test_Set"))
    {
      if (this.selectedTestSet != null) {
        dispose();
      }
    }
  }
}
