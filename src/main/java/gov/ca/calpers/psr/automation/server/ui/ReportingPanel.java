/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.FunctionalGroup;
import gov.ca.calpers.psr.automation.RollIndicatorEnum;
import gov.ca.calpers.psr.automation.TablePacker;
import gov.ca.calpers.psr.automation.TestResult;

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JTable;

import java.awt.GridLayout;

import javax.swing.UIManager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JScrollPane;
import javax.swing.JList;

import org.apache.logging.log4j.LogManager;
import org.jdesktop.swingx.JXTable;

/**
 * The Class ReportingPanel.
 *
 * @author burban
 */
public class ReportingPanel extends JPanel implements Observer, ActionListener, MouseListener, ChangeListener{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The test set. */
	private final AutomationTestSet testSet; 
	
	/** The func group status table. */
	private JXTable funcGroupStatusTable;
	
	/** The num of runs table. */
	private JXTable numOfRunsTable;
	
	/** The overall status table. */
	private JXTable overallStatusTable;
	
	/** The roll progress table. */
	private JXTable rollProgressTable;
	
	/** The func group status table model. */
	private DefaultTableModel funcGroupStatusTableModel;
	
	/** The num of runs table model. */
	private DefaultTableModel numOfRunsTableModel;
	
	/** The overall status table model. */
	private DefaultTableModel overallStatusTableModel;
	
	/** The roll progress table model. */
	private DefaultTableModel rollProgressTableModel;
	
	/** The list model. */
	private DefaultListModel listModel = new DefaultListModel();
	
	/** The func group status title. */
	private static String[] funcGroupStatusTitle = {"Functional Group", "Passed", "Failed", "Blocked" };
	
	/** The num of runs title. */
	private static String[] numOfRunsTitle = {"# of Runs", "# of Scripts","% of Scripts"};
	
	/** The overall status title. */
	private static String[] overallStatusTitle = {"Status", "# of Scripts", "% of Scripts"};
	
	/** The roll progress title. */
	private static String[] rollProgressTitle = {"Roll Phase", "Total", "Remaining"};
	
	/** The packer. */
	private TablePacker packer;
	
	/** The run count map. */
	ConcurrentHashMap<String, CopyOnWriteArrayList<AutomationTest>> runCountMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<AutomationTest>>();
	
	/** The timer. */
	private Timer timer =  new Timer(5000, this);
	
	private JPanel failedTestsPanel;
	
	private JScrollPane failedTestScrollPane;
	
	private CheckBoxList failedTestCheckBoxList = new CheckBoxList();
	
	private DefaultListModel failedTestListModel;
		
	private CopyOnWriteArrayList<AutomationTest> failedTestsHistoryList = new CopyOnWriteArrayList<AutomationTest>();
	
	private JPanel thisWholePanel = this;
	
	private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(ReportingPanel.class);
	
	/**
	 * Instantiates a new reporting panel.
	 *
	 * @param testSet the test set
	 */
	public ReportingPanel(AutomationTestSet testSet)
	{
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		setPreferredSize(new Dimension(800, 639));
		setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.testSet = testSet;
		//this.testSet.addObserver(this);
		
		JPanel funcGroupStatusPanel = new JPanel();
		layout.putConstraint(SpringLayout.NORTH, funcGroupStatusPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, funcGroupStatusPanel, 0, SpringLayout.WEST, this);
		funcGroupStatusPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Functional Group Status Metrics", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		funcGroupStatusPanel.setPreferredSize(new Dimension(500, 258));
		add(funcGroupStatusPanel);
		funcGroupStatusPanel.setLayout(new SpringLayout());
		
		funcGroupStatusTable = new JXTable();
		JTableHeader statusHeader = funcGroupStatusTable.getTableHeader();
		statusHeader.setDefaultRenderer(new AlignmentTableHeaderCellRenderer(statusHeader.getDefaultRenderer(), false));
		funcGroupStatusTableModel = new DefaultTableModel(funcGroupStatusTitle, 0);
        funcGroupStatusTable.setModel(funcGroupStatusTableModel);                
        funcGroupStatusTable.setShowGrid(true, true);
        funcGroupStatusTable.setEditable(false);
        funcGroupStatusTable.packAll();
        funcGroupStatusTable.getTableHeader().setReorderingAllowed(false);   
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        for(int i = 1; i < 4;i++)
        {
        	funcGroupStatusTable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
       
		
		funcGroupStatusTable.setDoubleBuffered(true);
		JScrollPane funcGroupPane = new JScrollPane(funcGroupStatusTable);
		funcGroupPane.setSize(new Dimension(485, 215));
		funcGroupPane.setPreferredSize(new Dimension(485, 190));
		funcGroupStatusPanel.add(funcGroupPane, BorderLayout.CENTER);
		
		JPanel numOfRunsPanel = new JPanel();
		layout.putConstraint(SpringLayout.NORTH, numOfRunsPanel, 6, SpringLayout.SOUTH, funcGroupStatusPanel);
		layout.putConstraint(SpringLayout.WEST, numOfRunsPanel, 0, SpringLayout.WEST, funcGroupStatusPanel);
		numOfRunsPanel.setPreferredSize(new Dimension(784, 200));
		numOfRunsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "# of Runs Metrics", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		add(numOfRunsPanel);
		numOfRunsPanel.setLayout(new GridLayout(1, 2, 0, 0));
		
		JPanel tablePanel = new JPanel();
		tablePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Metrics", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		tablePanel.setPreferredSize(new Dimension(100, 100));
		
		numOfRunsPanel.add(tablePanel);
		tablePanel.setLayout(new BorderLayout(0, 0));		
						
		numOfRunsTable = new JXTable();
		JTableHeader runsHeader = numOfRunsTable.getTableHeader();
		runsHeader.setDefaultRenderer(new AlignmentTableHeaderCellRenderer(runsHeader.getDefaultRenderer(), true));
		numOfRunsTableModel = new DefaultTableModel(numOfRunsTitle,0 );
		numOfRunsTable.setModel(numOfRunsTableModel);
		numOfRunsTable.setShowGrid(true, true);	
		numOfRunsTable.setEditable(true);		
		numOfRunsTable.getTableHeader().setReorderingAllowed(false);
		numOfRunsTable.getColumn(0).setCellRenderer(new ButtonRenderer());
		numOfRunsTable.getColumn(0).setCellEditor(new ButtonEditor(new JCheckBox(), listModel, runCountMap));
		for(int i = 1; i < 3;i++)
        {
        	numOfRunsTable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
		
		tablePanel.add(new JScrollPane(numOfRunsTable),BorderLayout.CENTER);
		
		
		
		JPanel listPanel = new JPanel();
		listPanel.setBorder(new TitledBorder(null, "Tests", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		numOfRunsPanel.add(listPanel);
		listPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		listPanel.add(scrollPane, BorderLayout.CENTER);		
		JList list = new JList(listModel);
		scrollPane.setViewportView(list);
		
		numOfRunsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		funcGroupStatusTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		JPanel overallStatusPanel = new JPanel();
		layout.putConstraint(SpringLayout.NORTH, overallStatusPanel, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, overallStatusPanel, -2, SpringLayout.EAST, this);
		overallStatusPanel.setPreferredSize(new Dimension(280, 145));
		overallStatusPanel.setBorder(new TitledBorder(null, "Overall Status Metrics", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		layout.putConstraint(SpringLayout.WEST, overallStatusPanel, 5, SpringLayout.EAST, funcGroupStatusPanel);
		add(overallStatusPanel);
		overallStatusPanel.setLayout(new BorderLayout(0, 0));
		
		overallStatusTable = new JXTable();
		JTableHeader overallStatusHeader = overallStatusTable.getTableHeader();
		statusHeader.setDefaultRenderer(new AlignmentTableHeaderCellRenderer(overallStatusHeader.getDefaultRenderer(), false));
		overallStatusTableModel = new DefaultTableModel(overallStatusTitle, 0);
		overallStatusTable.setModel(overallStatusTableModel);                
		overallStatusTable.setShowGrid(true, true);
		overallStatusTable.setEditable(false);
		overallStatusTable.packAll();
		overallStatusTable.getTableHeader().setReorderingAllowed(false);        
		for(int i = 1; i < 3;i++)
        {
			overallStatusTable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
		
		overallStatusPanel.add(new JScrollPane(overallStatusTable), BorderLayout.CENTER);
		
		JPanel rollProgressPanel = new JPanel();
		layout.putConstraint(SpringLayout.NORTH, rollProgressPanel, 151, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, rollProgressPanel, 0, SpringLayout.EAST, overallStatusPanel);
		layout.putConstraint(SpringLayout.EAST, numOfRunsPanel, 0, SpringLayout.EAST, rollProgressPanel);
		layout.putConstraint(SpringLayout.WEST, rollProgressPanel, 6, SpringLayout.EAST, funcGroupStatusPanel);
		rollProgressPanel.setBorder(new TitledBorder(null, "Roll Progress", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rollProgressPanel.setPreferredSize(new Dimension(280, 108));
		add(rollProgressPanel);
		rollProgressPanel.setLayout(new BorderLayout(0, 0));
		
		rollProgressTable = new JXTable();
		JTableHeader rollProgressHeader = rollProgressTable.getTableHeader();
		statusHeader.setDefaultRenderer(new AlignmentTableHeaderCellRenderer(rollProgressHeader.getDefaultRenderer(), false));
		rollProgressTableModel = new DefaultTableModel(rollProgressTitle, 0);
		rollProgressTable.setModel(rollProgressTableModel);                
		rollProgressTable.setShowGrid(true, true);
		rollProgressTable.setEditable(false);
		rollProgressTable.packAll();
		rollProgressTable.getTableHeader().setReorderingAllowed(false);        
		
		for(int i = 1; i < 3;i++)
        {
			rollProgressTable.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
		
		rollProgressPanel.add(new JScrollPane(rollProgressTable), BorderLayout.CENTER);		
		
		failedTestsPanel = new JPanel();
		layout.putConstraint(SpringLayout.NORTH, failedTestsPanel, 6, SpringLayout.SOUTH, numOfRunsPanel);
		layout.putConstraint(SpringLayout.SOUTH, failedTestsPanel, -4, SpringLayout.SOUTH, this);
		failedTestsPanel.setBorder(new TitledBorder(null, "Failed Tests", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		layout.putConstraint(SpringLayout.WEST, failedTestsPanel, 0, SpringLayout.WEST, numOfRunsPanel);
		layout.putConstraint(SpringLayout.EAST, failedTestsPanel, 0, SpringLayout.EAST, numOfRunsPanel);
		add(failedTestsPanel);
		failedTestsPanel.setLayout(new BorderLayout(0, 0));
		
		failedTestScrollPane = new JScrollPane();
		failedTestsPanel.add(failedTestScrollPane, BorderLayout.CENTER);
				
		failedTestCheckBoxList.addMouseListener(this);		
		failedTestCheckBoxList.setOpaque(true);		
		failedTestCheckBoxList.setDoubleBuffered(true);		
		failedTestCheckBoxList.setBorder(new EmptyBorder(1,1,1,1));	
		
		failedTestListModel = new DefaultListModel();
		failedTestCheckBoxList.setModel(failedTestListModel);
		failedTestScrollPane.setViewportView(failedTestCheckBoxList);
		
		numOfRunsTable.packAll();
		funcGroupStatusTable.packAll();
		
		if(this.testSet.hasTests())
		{
			redrawData();
		}
		timer.start();
	}
	
	/**
	 * Pack.
	 *
	 * @param table the table
	 * @param rowsIncluded the rows included
	 * @param distributeExtraArea the distribute extra area
	 */
	public synchronized void pack(JXTable table, int rowsIncluded, boolean distributeExtraArea) {
		packer = new TablePacker(rowsIncluded, true);
		if (isShowing()) {
			packer.pack(table);
			packer = null;
		}
	}
	
	/**
	 * Redraw data.
	 */
	private synchronized void redrawData() {
		
		SwingUtilities.invokeLater(new Runnable(){@Override
		public void run(){
			System.out.println("Refreshing reporting tables.");
			
		CopyOnWriteArrayList<AutomationTest> tests = testSet.getTestsAsList();
		int totalNumberOfTests = tests.size();
		int[] totalTests = {0,0,0};
		int[] totalBenefits = {0,0,0};
		int[] totalContributions = {0,0,0};
		int[] totalHealth = {0,0,0};
		int[] totalContracts = {0,0,0};
		int[] totalFinancials = {0,0,0};
		int[] totalGeneral = {0,0,0};
		int[] totalMSS = {0,0,0};
		int totalNOTRUN = 0;
		int totalINPROGRESS = 0;
		int totalPASSED = 0;
		int totalFAILED = 0;
		int totalBLOCKED = 0;
		int totalNumberOfPreRollScripts = 0;
		int totalNumberOfRollScripts = 0;
		int totalNumberOfPostRollScripts = 0;
		int totalPreRoll = 0;
		int totalRoll = 0;
		int totalPostRoll = 0;
		CopyOnWriteArrayList<AutomationTest> failedTestsList = new CopyOnWriteArrayList<AutomationTest>();
		runCountMap.clear();
				
		for(AutomationTest test : tests)
		{
			TestResult result = test.getTestResult();
			if(test.getRollIndicator() != null)
			{
				if(test.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
				{
					totalNumberOfPreRollScripts++;
				}else if(test.getRollIndicator().equals(RollIndicatorEnum.ROLL))
				{
					totalNumberOfRollScripts++;
				}else if(test.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
				{
					totalNumberOfPostRollScripts++;
				}
			}
			
			if(result.getExecutionStatus().equals(ExecutionStatus.NOT_RUN))
			{
				totalNOTRUN++;
			}else if(result.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS))
			{
				totalINPROGRESS++;
			}else if(result.getExecutionStatus().equals(ExecutionStatus.PASSED) || result.getExecutionStatus().equals(ExecutionStatus.MANUALLY_PASSED))
			{
				totalPASSED++;
				int index = 0;
				totalTests[index] = totalTests[index] + 1;
				if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_BENEFITS.toString()))
				{					
					totalBenefits[index] = totalBenefits[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString()))
				{
					
					totalContributions[index] = totalContributions[index] + 1;
				}
				else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_HEALTH.toString()))
				{
					totalHealth[index] = totalHealth[index] + 1;
				}
				else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString()))
				{
					totalContracts[index] = totalContracts[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString()))
				{
					totalFinancials[index] = totalFinancials[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_GENERAL.toString()))
				{
					totalGeneral[index] = totalGeneral[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_MSS.toString()))
				{
					totalMSS[index] = totalMSS[index] + 1;
				}			
			}else if(result.getExecutionStatus().equals(ExecutionStatus.FAILED))
			{				
				totalFAILED++;
				if(!failedTestsList.contains(test))
				{
					failedTestsList.add(test);
				}
				int index = 1;
				totalTests[index] = totalTests[index] + 1;
				if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_BENEFITS.toString()))
				{					
					totalBenefits[index] = totalBenefits[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString()))
				{
					
					totalContributions[index] = totalContributions[index] + 1;
				}
				else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_HEALTH.toString()))
				{
					totalHealth[index] = totalHealth[index] + 1;
				}
				else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString()))
				{
					totalContracts[index] = totalContracts[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString()))
				{
					totalFinancials[index] = totalFinancials[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_GENERAL.toString()))
				{
					totalGeneral[index] = totalGeneral[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_MSS.toString()))
				{
					totalMSS[index] = totalMSS[index] + 1;
				}
			}else if(result.getExecutionStatus().equals(ExecutionStatus.BLOCKED))
			{
				totalBLOCKED++;
				int index = 2;
				totalTests[index] = totalTests[index] + 1;
				if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_BENEFITS.toString()))
				{					
					totalBenefits[index] = totalBenefits[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString()))
				{
					
					totalContributions[index] = totalContributions[index] + 1;
				}
				else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_HEALTH.toString()))
				{
					totalHealth[index] = totalHealth[index] + 1;
				}
				else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString()))
				{
					totalContracts[index] = totalContracts[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString()))
				{
					totalFinancials[index] = totalFinancials[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_GENERAL.toString()))
				{
					totalGeneral[index] = totalGeneral[index] + 1;
				}else if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_MSS.toString()))
				{
					totalMSS[index] = totalMSS[index] + 1;
				}
			}			
			
			if(test.hasCompleted())
			{
				if(runCountMap.containsKey(String.valueOf(result.getRunCount())))
				{
					runCountMap.get(String.valueOf(result.getRunCount())).add(test);
				}else
				{
					CopyOnWriteArrayList<AutomationTest> list = new CopyOnWriteArrayList<AutomationTest>();
					list.add(test);
					runCountMap.put(String.valueOf(result.getRunCount()), list);
				}
				
				if(test.getRollIndicator() != null)
				{
					if(test.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
					{
						totalPreRoll++;
					}else if(test.getRollIndicator().equals(RollIndicatorEnum.ROLL))
					{
						totalRoll++;
					}else if(test.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
					{
						totalPostRoll++;
					}
				}
			}
		}
			
			
		// Functional Group Status Table
	    	    //Update the model here
	    	try{	    		
	    		
	        	if(funcGroupStatusTableModel.getRowCount() > 0)
	        	{    		
	        		int rowCount = funcGroupStatusTableModel.getRowCount();
	        		for (int i = rowCount - 1; i > -1; i--) {
	        			funcGroupStatusTableModel.removeRow(i);
	        	    }
	        	}
	        	
	        	//Benefits row
	        	Vector<Object> benefitsData = new Vector<Object>();
	        	String preHtml = "<html><font color='"
    					+ "#228B22'>";
    			String postHtml = "</color></font></html>";    
    			benefitsData.add("Benefits");
    			benefitsData.add(preHtml + totalBenefits[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			benefitsData.add(preHtml + totalBenefits[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			benefitsData.add(preHtml + totalBenefits[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(benefitsData);
    			
    			//Contributions row
    			Vector<Object> contributionsData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			contributionsData.add("Contributions");
    			contributionsData.add(preHtml + totalContributions[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			contributionsData.add(preHtml + totalContributions[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			contributionsData.add(preHtml + totalContributions[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(contributionsData);
    			
    			//Health row
    			Vector<Object> healthData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			healthData.add("Health");
    			healthData.add(preHtml + totalHealth[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			healthData.add(preHtml + totalHealth[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			healthData.add(preHtml + totalHealth[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(healthData);
    			
    			//Contracts row
    			Vector<Object> contractsData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			contractsData.add("Contracts");
    			contractsData.add(preHtml + totalContracts[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			contractsData.add(preHtml + totalContracts[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			contractsData.add(preHtml + totalContracts[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(contractsData);
    			
    			//Financials row
    			Vector<Object> financialData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			financialData.add("Financial");
    			financialData.add(preHtml + totalFinancials[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			financialData.add(preHtml + totalFinancials[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			financialData.add(preHtml + totalFinancials[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(financialData);
    			
    			//General row
    			Vector<Object> generalData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			generalData.add("General");
    			generalData.add(preHtml + totalGeneral[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			generalData.add(preHtml + totalGeneral[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			generalData.add(preHtml + totalGeneral[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(generalData);
    			
    			//MSS row
    			Vector<Object> mssData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			mssData.add("MSS");
    			mssData.add(preHtml + totalMSS[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			mssData.add(preHtml + totalMSS[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			mssData.add(preHtml + totalMSS[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(mssData);
    		    		  			
    		
    			
    			//Total scripts counts 
    			Vector<Object> totalCountData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			totalCountData.add("");
    			totalCountData.add(preHtml + totalTests[0] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			totalCountData.add(preHtml + totalTests[1] + postHtml);
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			totalCountData.add(preHtml + totalTests[2] + postHtml);    			
    			funcGroupStatusTableModel.addRow(totalCountData);
    			
    			//Total percentages
    			
    			Vector<Object> totalPercentagesData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			totalPercentagesData.add("");    			
    			float passedPerc = ((float)totalTests[0] * 100) / totalNumberOfTests;
    			DecimalFormat numberFormat = new DecimalFormat("#0.00");    			
    			totalPercentagesData.add(preHtml + numberFormat.format(passedPerc)+ "%" + postHtml);
    			preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";
    			float failedPerc = ((float)totalTests[1] * 100) / totalNumberOfTests;
    			totalPercentagesData.add(preHtml + numberFormat.format(failedPerc)+ "%" + postHtml);    			
    			preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>"; 
    			float blockedPerc = ((float)totalTests[2] * 100) / totalNumberOfTests;
    			totalPercentagesData.add(preHtml + numberFormat.format(blockedPerc)+ "%" + postHtml);    			   			
    			funcGroupStatusTableModel.addRow(totalPercentagesData);   		
    			
    			//Overall Status Table
    			
    			if(overallStatusTableModel.getRowCount() > 0)
	        	{
	        		int rowCount = overallStatusTableModel.getRowCount();
	        		for (int i = rowCount - 1; i > -1; i--) {
	        			overallStatusTableModel.removeRow(i);
	        	    }
	        	}
	        	
	        	//Not Run row
	        	Vector<Object> notRunData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#778899'>";
    			postHtml = "</color></font></html>";    
    			notRunData.add(preHtml + "Not Run"+ postHtml);
    			notRunData.add("" + totalNOTRUN);  
    			float notRunPerc = ((float)totalNOTRUN * 100) / totalNumberOfTests;
    			notRunData.add(numberFormat.format(notRunPerc)+ "%");    			    			
    			overallStatusTableModel.addRow(notRunData);
    			
    			//In Progress row
	        	Vector<Object> inProgressData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#0000FF'>";
    			postHtml = "</color></font></html>";    
    			inProgressData.add(preHtml + "In Progress"+ postHtml);
    			inProgressData.add("" + totalINPROGRESS);  
    			float inProgressPerc = ((float)totalINPROGRESS * 100) / totalNumberOfTests;
    			inProgressData.add(numberFormat.format(inProgressPerc)+ "%");    			    			
    			overallStatusTableModel.addRow(inProgressData);
    			
    			//Passed row
	        	Vector<Object> passedData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#228B22'>";
    			postHtml = "</color></font></html>";    
    			passedData.add(preHtml + "Passed"+ postHtml);
    			passedData.add("" + totalPASSED);  
    			float overallPassedPerc = ((float)totalPASSED * 100) / totalNumberOfTests;
    			passedData.add(numberFormat.format(overallPassedPerc)+ "%");    			    			
    			overallStatusTableModel.addRow(passedData);
    			
    			//Failed row
	        	Vector<Object> failedData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#B22222'>";
    			postHtml = "</color></font></html>";    
    			failedData.add(preHtml + "Failed"+ postHtml);
    			failedData.add("" + totalFAILED);  
    			float overallFailedPerc = ((float)totalFAILED * 100) / totalNumberOfTests;
    			failedData.add(numberFormat.format(overallFailedPerc)+ "%");    			    			
    			overallStatusTableModel.addRow(failedData);
    			
    			//Blocked row
	        	Vector<Object> blockedData = new Vector<Object>();
	        	preHtml = "<html><font color='"
    					+ "#FFA500'>";
    			postHtml = "</color></font></html>";    
    			blockedData.add(preHtml + "Blocked"+ postHtml);
    			blockedData.add("" + totalBLOCKED);  
    			float overallBlockedPerc = ((float)totalBLOCKED * 100) / totalNumberOfTests;
    			blockedData.add(numberFormat.format(overallBlockedPerc)+ "%");    			    			
    			overallStatusTableModel.addRow(blockedData);
    			
    			
    			// Run # totals Table
    			if(numOfRunsTableModel.getRowCount() > 0)
	        	{    		
	        		int rowCount = numOfRunsTableModel.getRowCount();
	        		for (int i = rowCount - 1; i > -1; i--) {
	        			numOfRunsTableModel.removeRow(i);
	        	    }
	        	}
    			
    			int maxValue = 0;
    			for(String number : runCountMap.keySet())
    			{
    				if(Integer.valueOf(number) > maxValue)
    				{
    					maxValue = Integer.valueOf(number);
    				} 
    			}
    			
    			for(int i = 1; i <= maxValue; i++)
    			{
    				if(runCountMap.containsKey(String.valueOf(i)))
    				{
    					Vector<Object> rowData = new Vector<Object>();
	        			rowData.add(String.valueOf(i));    	
	        			rowData.add(String.valueOf(runCountMap.get(String.valueOf(i)).size()));
	        			float runPerc = ((float)runCountMap.get(String.valueOf(i)).size() * 100) / totalNumberOfTests;
	        			rowData.add(String.valueOf(numberFormat.format(runPerc) + "%"));
	        			numOfRunsTableModel.addRow(rowData);
    				}
    			}
    			
    			// Roll Progress Table
    			if(rollProgressTableModel.getRowCount() > 0)
	        	{
	        		//System.out.println("Removing all rows for re-draw.");    		
	        		int rowCount = rollProgressTableModel.getRowCount();
	        		for (int i = rowCount - 1; i > -1; i--) {
	        			rollProgressTableModel.removeRow(i);
	        	    }
	        	}
    			
    			//Pre Roll row
	        	Vector<Object> preRollData = new Vector<Object>();	        	   
    			preRollData.add("Pre");
    			preRollData.add("" + totalNumberOfPreRollScripts);    			
    			preRollData.add(totalNumberOfPreRollScripts - totalPreRoll);    			    			
    			rollProgressTableModel.addRow(preRollData);
    			
    			Vector<Object> rollData = new Vector<Object>();	        	   
    			rollData.add("Roll");
    			rollData.add("" + totalNumberOfRollScripts);    			
    			rollData.add(totalNumberOfRollScripts - totalRoll);    			    			
    			rollProgressTableModel.addRow(rollData);
    			
    			Vector<Object> postRollData = new Vector<Object>();	        	   
    			postRollData.add("Post Roll");
    			postRollData.add("" + totalNumberOfPostRollScripts);    			
    			postRollData.add(totalNumberOfPostRollScripts - totalPostRoll);    			    			
    			rollProgressTableModel.addRow(postRollData);
    			
    			for(AutomationTest test : failedTestsList)
    			{
    				if(!failedTestsHistoryList.contains(test))
    				{    					
    					failedTestCheckBoxList.addCheckbox(new JCheckBox(test.getTestName()));
    					failedTestsHistoryList.add(test);
    				}
    			}   		
    			
	    		}catch(Exception e)
	    		{
	    			log.error("Error (Unknown): ", e);
	    		}
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		//redrawData();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==timer){
			if(this.testSet.hasTests())
			redrawData();
		    }
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
}

class ButtonRenderer extends JButton implements TableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ButtonRenderer() {
        setOpaque(false);
        setBorderPainted(false);
        setBackground(Color.WHITE);
        setFocusPainted(false);
        setMargin(new Insets(0,0,0,0));
        setContentAreaFilled(false);
        setForeground(Color.BLUE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	setOpaque(false);
        setBorderPainted(false);

        setBackground(Color.WHITE);
        setForeground(Color.BLUE);
        setFocusPainted(false);
        setMargin(new Insets(0,0,0,0));
        setContentAreaFilled(false);
        setText((value == null) ? "" : value.toString());
        setToolTipText("Click to populate the list to the right with the tests that have failed " + value + " time(s).");
        return this;
    }
}

class AlignmentTableHeaderCellRenderer implements TableCellRenderer {
	 
	  private final TableCellRenderer wrappedRenderer;
	  private final JLabel label;
	  private boolean allCentered;
	  public AlignmentTableHeaderCellRenderer(TableCellRenderer wrappedRenderer, boolean allCentered) {
	    if (!(wrappedRenderer instanceof JLabel)) {
	      throw new IllegalArgumentException("The supplied renderer must inherit from JLabel");
	    }
	    this.allCentered = allCentered;
	    this.wrappedRenderer = wrappedRenderer;
	    this.label = (JLabel) wrappedRenderer;
	  }
	 
	  @Override
	  public Component getTableCellRendererComponent(JTable table, Object value,
	          boolean isSelected, boolean hasFocus, int row, int column) {
	    wrappedRenderer.getTableCellRendererComponent(table, value,
	            isSelected, hasFocus, row, column);
	    if(allCentered)
	    {
	    	label.setHorizontalAlignment(SwingConstants.CENTER);
	    }else
	    {
	    	label.setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
	    }
	    return label;
	  }
}

class ButtonEditor extends DefaultCellEditor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton button;
    private String label;
    private boolean isPushed;
    private DefaultListModel listModel;
    private ConcurrentHashMap<String, CopyOnWriteArrayList<AutomationTest>> runCountMap;
    
    public ButtonEditor(JCheckBox checkBox, DefaultListModel model, ConcurrentHashMap<String, CopyOnWriteArrayList<AutomationTest>> map) {
        super(checkBox);
        listModel = model;
        runCountMap = map;
        button = new JButton();
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0,0,0,0));
        button.setContentAreaFilled(false); 
        button.setForeground(Color.BLUE);        
        button.setToolTipText("Click to populate the list to the right with the tests that have failed " + label + " time(s).");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0,0,0,0));
        button.setContentAreaFilled(false);
        button.setForeground(Color.BLUE);
        button.setToolTipText("Click to populate the list to the right with the tests that have failed " + label + " time(s).");
        button.setBackground(Color.WHITE);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
        	listModel.clear();
        	for(AutomationTest test : runCountMap.get(label))
        	{
        		listModel.addElement(test);
        	}
        }else
        {
        	listModel.clear();
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
    
   
}




