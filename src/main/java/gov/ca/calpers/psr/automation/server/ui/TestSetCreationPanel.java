package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.ALMServerConfig;
import gov.ca.calpers.psr.automation.AutoFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.FunctionalGroup;
import gov.ca.calpers.psr.automation.TestDependency;
import gov.ca.calpers.psr.automation.TestSetRunStatus;
import gov.ca.calpers.psr.automation.WorkManager;
import gov.ca.calpers.psr.automation.directed.graph.EdgeTypeEnum;
import gov.ca.calpers.psr.automation.pojo.HibernateUtil;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.gc.ClassFactory;
import com.gc.IBaseFactory;
import com.gc.ISysTreeNode;
import com.gc.ITDConnection;
import com.gc.ITest;
import com.gc.ITestFactory;
import com.gc.ITestSet;
import com.gc.ITestSetFactory;
import com.gc.ITestSetFolder;
import com.gc.ITestSetTreeManager;

import com4j.Variant;

/**
 * The Class TestSetCreationPanel.
 */
public class TestSetCreationPanel extends JPanel implements ComponentListener, ActionListener, Observer, MouseListener, ChangeListener {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The split pane. */
	private JSplitPane splitPane;
	
	/** The all tests panel. */
	private JScrollPane allTestsPanel;
	
	/** The selected tests panel. */
	private JScrollPane selectedTestsPanel;	
	
	/** The all tests. */
	private CopyOnWriteArrayList<AutomationTest> allTests;
	
	/** The all functional groups. */
	private CopyOnWriteArrayList<AutomationFunctionalGroup> allFunctionalGroups;
	
	/** The selected functional groups. */
	private CopyOnWriteArrayList<AutomationFunctionalGroup> selectedFunctionalGroups;
	
	/** The auto functional group list. */
	private CheckBoxList autoFunctionalGroupList;
	
	/** The functional group list. */
	private CheckBoxList functionalGroupList;
	
	/** The auto functional group model. */
	private DefaultListModel autoFunctionalGroupModel;
	
	/** The functional group model. */
	private DefaultListModel functionalGroupModel;
	
	/** The chk auto benefits. */
	private JCheckBox chkAutoBenefits;
	
	/** The chk auto contracts and enroll. */
	private JCheckBox chkAutoContractsAndEnroll;
	
	/** The chk auto contributions. */
	private JCheckBox chkAutoContributions;
	
	/** The chk auto death. */
	private JCheckBox chkAutoDeath;
	
	/** The chk auto financials. */
	private JCheckBox chkAutoFinancials;
	
	/** The chk auto general. */
	private JCheckBox chkAutoGeneral;
	
	/** The chk auto health. */
	private JCheckBox chkAutoHealth;
	
	/** The chk auto interfaces. */
	private JCheckBox chkAutoInterfaces;
	
	/** The chk auto jlrs. */
	private JCheckBox chkAutoJLRS;
	
	/** The chk auto mss. */
	private JCheckBox chkAutoMSS;
	
	/** The chk benefits. */
	private JCheckBox chkBenefits;
	
	/** The chk contracts and enroll. */
	private JCheckBox chkContractsAndEnroll;
	
	/** The chk contributions. */
	private JCheckBox chkContributions;
	
	/** The chk financials. */
	private JCheckBox chkFinancials;
	
	/** The chk general. */
	private JCheckBox chkGeneral;
	
	/** The chk health. */
	private JCheckBox chkHealth;
	
	/** The chk mss. */
	private JCheckBox chkMSS;

    /** The all tests panel list. */
    private JList allTestsPanelList;
    
    /** The selected tests panel list. */
    private JList selectedTestsPanelList;
    
    /** The all tests model. */
    private DefaultListModel allTestsModel = new DefaultListModel();
    
    /** The selected tests model. */
    private DefaultListModel selectedTestsModel = new DefaultListModel();
    
    /** The add test button. */
    private JButton addTestButton;
    
    /** The remove test button. */
    private JButton removeTestButton;
    
    /** The create alm test set. */
    private JButton createALMTestSet;
    
    /** The revert button. */
    private JButton revertButton;
    
    /** The medium button dimensions. */
    private Dimension mediumButtonDimensions = new Dimension(75,30);
    
    /** The large button dimensions. */
    private Dimension largeButtonDimensions = new Dimension(100,30);
    
    /** The test set settings. */
    private TestSetSettingsPanel testSetSettings;
	
	/** The bottom panel. */
	private JPanel bottomPanel;
	
	/** The work in progress test set. */
	private AutomationTestSet workInProgressTestSet;
	
	/** The original test set. */
	private final AutomationTestSet originalTestSet;
	
	/** The default all tests list. */
	private final ArrayList<AutomationTest> defaultAllTestsList;
	
	/** The original states. */
	private CheckBoxStates originalStates = new CheckBoxStates();
	
	/** The all tests tree. */
	private volatile AutomationTestTreeForDisplay allTestsTree;
	
	/** The selected tests tree. */
	private volatile AutomationTestTreeForDisplay selectedTestsTree;
	
	/** The glass pane. */
	private ProgressGlassPane glassPane;
	
	/** The main frame. */
	private JFrame mainFrame;
	
	/** The Constant MAX_DELAY. */
	private static final int MAX_DELAY = 300;
	
	/** The alm test set persisted. */
	private boolean almTestSetPersisted = false;
	
	/** The alm server config. */
	private ALMServerConfig almServerConfig;
	
	/** The save button. */
	private JButton saveButton;
	
	/** The can save. */
	private boolean canSave = false;
	
	/** The can save alm. */
	private boolean canSaveALM = false;
	
	/** The changed since last save. */
	private boolean changedSinceLastSave = false;	
	
	/** The work manager. */
	private WorkManager workManager;
	
	/** The log. */
	private static org.apache.logging.log4j.Logger log = LogManager.getLogger(TestSetCreationPanel.class);
	
	/**
	 * Instantiates a new test set creation panel.
	 *
	 * @param testSet the test set
	 * @param allTests the all tests
	 * @param workManager the work manager
	 * @param mainFrame the main frame
	 */
	public TestSetCreationPanel(AutomationTestSet testSet, CopyOnWriteArrayList<AutomationTest> allTests, WorkManager workManager, JFrame mainFrame)
	{
		this.almServerConfig = ALMServerConfig.getServerDetails();		
		this.allTests = allTests;		
		allFunctionalGroups = new CopyOnWriteArrayList<AutomationFunctionalGroup>();	
		selectedFunctionalGroups = new CopyOnWriteArrayList<AutomationFunctionalGroup>();
		setupAllTests();
		buildAutoFunctionalGroupTestLists();
		defaultAllTestsList = new ArrayList<AutomationTest>();
		this.createDefaultAllTestsList(allTests);
		this.originalTestSet = testSet;
		workInProgressTestSet = testSet.copy();
		workInProgressTestSet.addObserver(this);

		
		this.setLayout(new BorderLayout(0,0));
		setPreferredSize(new Dimension(800, 500));		
		splitPane = new JSplitPane();	
		splitPane.setLeftComponent(setupLeftPanel());
		splitPane.setRightComponent(setupRightPanel());
		splitPane.setResizeWeight(0.10);
		splitPane.addComponentListener(this);
		add(splitPane, BorderLayout.CENTER);
		setOriginalGroupCheckBoxStates();	
		this.mainFrame = mainFrame;		
		this.mainFrame.setGlassPane(glassPane = new ProgressGlassPane());
		glassPane.addMouseListener(new MouseAdapter() {});
	    glassPane.addMouseMotionListener(new MouseMotionAdapter() {});
	    glassPane.addKeyListener(new KeyAdapter() {});
	    populateTabDataWithTestSet();
	    changedSinceLastSave=false;
	    canSave = false;
	    disableEnableButtons();
	    this.workManager = workManager;
	    if(originalTestSet.getRunStatus().equals(TestSetRunStatus.RUNNING.toString()))
	    {
	    	almTestSetPersisted=true;
            canSaveALM=false;
            canSave=false;
            changedSinceLastSave=false;
            originalTestSet.setPersistedInALM(true);
            workInProgressTestSet.setPersistedInALM(true);
            try{
    			workManager.processTestSet();
    		}catch(IllegalArgumentException t)
    		{    			
				JOptionPane.showMessageDialog(mainFrame,
						"Dependency Tree Error",
						"Cycle Detected in dependencies. Please check dependencies and remove the cycle before saving the test set again.",					        
				        JOptionPane.ERROR_MESSAGE);					    
    		}
            setPanelComponentsToReadOnly();
	    }
	}
	
	/**
	 * Setup left panel.
	 *
	 * @return the j panel
	 */
	private JPanel setupLeftPanel()
	{		
		JPanel testCategoriesPanel = new JPanel();		
		JPanel autoFunctionalGroupPanel = new JPanel();
		testCategoriesPanel.setBorder(new TitledBorder(null, "Test Selection Criteria", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		BoxLayout layout = new BoxLayout(testCategoriesPanel,BoxLayout.Y_AXIS);
		testCategoriesPanel.setLayout(layout);
		testCategoriesPanel.setPreferredSize(new Dimension(150, 500));		
		BorderLayout autoFunctionalGroupLayout = new BorderLayout(0,0);
		autoFunctionalGroupPanel.setLayout(autoFunctionalGroupLayout);		
		autoFunctionalGroupPanel.setBorder(new TitledBorder("Auto Functional Group"));
		autoFunctionalGroupPanel.setSize(new Dimension(160,188));		
		autoFunctionalGroupPanel.setPreferredSize(new Dimension(160,188));
		autoFunctionalGroupPanel.setMaximumSize(new Dimension(32768,188));
		autoFunctionalGroupList = new CheckBoxList();
		autoFunctionalGroupList.addMouseListener(this);
		autoFunctionalGroupList.setSize(new Dimension(160, 180));
		autoFunctionalGroupList.setOpaque(true);
		autoFunctionalGroupList.setPreferredSize(new Dimension(160, 180));
		autoFunctionalGroupList.setMinimumSize(new Dimension(160, 180));
		autoFunctionalGroupList.setMaximumSize(new Dimension(32768, 180));
		autoFunctionalGroupList.setDoubleBuffered(true);
		autoFunctionalGroupList.setForeground(UIManager.getColor("Panel.foreground"));
		autoFunctionalGroupList.setBackground(UIManager.getColor("Panel.background"));
		autoFunctionalGroupModel = new DefaultListModel();
		autoFunctionalGroupList.setModel(autoFunctionalGroupModel);	
		autoFunctionalGroupList.setListData(createAutoFunctionalCheckBoxes());
		autoFunctionalGroupList.setForeground(UIManager.getColor("Panel.foreground"));
		autoFunctionalGroupPanel.add(autoFunctionalGroupList, BorderLayout.CENTER);
		testCategoriesPanel.add(autoFunctionalGroupPanel);
		JPanel functionalGroupPanel = new JPanel();
		BorderLayout functionalGroupLayout = new BorderLayout(0,0);
		functionalGroupPanel.setLayout(functionalGroupLayout);
		functionalGroupPanel.setBorder(new TitledBorder("CalPERS Functional Groups"));
		functionalGroupPanel.setSize(new Dimension(160, 140));
		functionalGroupPanel.setPreferredSize(new Dimension(160, 140));
		functionalGroupPanel.setMaximumSize(new Dimension(32768, 140));
		functionalGroupList = new CheckBoxList();
		functionalGroupList.addMouseListener(this);
		functionalGroupList.setSize(new Dimension(75, 75));
		functionalGroupList.setOpaque(true);
		functionalGroupList.setPreferredSize(new Dimension(75, 75));
		functionalGroupList.setMinimumSize(new Dimension(75, 75));
		functionalGroupList.setMaximumSize(new Dimension(75, 75));
		functionalGroupList.setDoubleBuffered(true);		
		functionalGroupList.setForeground(UIManager.getColor("Panel.foreground"));
		functionalGroupList.setBackground(UIManager.getColor("Panel.background"));
		functionalGroupModel = new DefaultListModel();
		functionalGroupList.setModel(functionalGroupModel);				
		functionalGroupList.setListData(createFunctionalCheckBoxes());
		functionalGroupPanel.add(functionalGroupList, BorderLayout.CENTER);
		testCategoriesPanel.add(functionalGroupPanel);		
		testCategoriesPanel.add(Box.createVerticalGlue());
		
		return testCategoriesPanel;		
	}
	
	/**
	 * Creates the auto functional check boxes.
	 *
	 * @return the j check box[]
	 */
	private JCheckBox[] createAutoFunctionalCheckBoxes()
	{
		JCheckBox[] jcb = new JCheckBox[10];
		chkAutoBenefits = new JCheckBox("Benefits");
		chkAutoBenefits.setMargin(new Insets(0,0,0,0));
		chkAutoBenefits.addChangeListener(this);
		chkAutoContractsAndEnroll = new JCheckBox("Contracts & Enrollment");
		chkAutoContractsAndEnroll.setMargin(new Insets(0,0,0,0));
		chkAutoContractsAndEnroll.addChangeListener(this);
		chkAutoContributions = new JCheckBox("Contributions");
		chkAutoContributions.setMargin(new Insets(0,0,0,0));
		chkAutoContributions.addChangeListener(this);
		chkAutoDeath = new JCheckBox("Death");
		chkAutoDeath.setMargin(new Insets(0,0,0,0));
		chkAutoDeath.addChangeListener(this);
		chkAutoFinancials = new JCheckBox("Financials");
		chkAutoFinancials.setMargin(new Insets(0,0,0,0));
		chkAutoFinancials.addChangeListener(this);
		chkAutoGeneral = new JCheckBox("General");
		chkAutoGeneral.setMargin(new Insets(0,0,0,0));
		chkAutoGeneral.addChangeListener(this);
		chkAutoHealth = new JCheckBox("Health");
		chkAutoHealth.setMargin(new Insets(0,0,0,0));
		chkAutoHealth.addChangeListener(this);
		chkAutoInterfaces = new JCheckBox("Interaces");
		chkAutoInterfaces.setMargin(new Insets(0,0,0,0));
		chkAutoInterfaces.addChangeListener(this);
		chkAutoJLRS = new JCheckBox("JLRS");
		chkAutoJLRS.setMargin(new Insets(0,0,0,0));
		chkAutoJLRS.addChangeListener(this);
		chkAutoMSS = new JCheckBox("Member Self Service");
		chkAutoMSS.setMargin(new Insets(0,0,0,0));
		chkAutoMSS.addChangeListener(this);
		
		jcb[0]=chkAutoBenefits;
		jcb[1]=chkAutoContractsAndEnroll;
		jcb[2]=chkAutoContributions;
		jcb[3]=chkAutoDeath;
		jcb[4]=chkAutoFinancials;
		jcb[5]=chkAutoGeneral;
		jcb[6]=chkAutoHealth;
		jcb[7]=chkAutoInterfaces;
		jcb[8]=chkAutoJLRS;
		jcb[9]=chkAutoMSS;

		return jcb;
	}
	
	/**
	 * Creates the functional check boxes.
	 *
	 * @return the j check box[]
	 */
	private JCheckBox[] createFunctionalCheckBoxes()
	{
		JCheckBox[] jcb = new JCheckBox[7];
		chkBenefits = new JCheckBox("Benefits");
		chkBenefits.setMargin(new Insets(0,0,0,0));
		chkBenefits.addChangeListener(this);		
		chkContractsAndEnroll = new JCheckBox("Contracts & Enrollment");
		chkContractsAndEnroll.setMargin(new Insets(0,0,0,0));
		chkContractsAndEnroll.addChangeListener(this);
		chkContributions = new JCheckBox("Contributions");
		chkContributions.setMargin(new Insets(0,0,0,0));
		chkContributions.addChangeListener(this);		
		chkFinancials = new JCheckBox("Financials");
		chkFinancials.setMargin(new Insets(0,0,0,0));
		chkFinancials.addChangeListener(this);
		chkGeneral = new JCheckBox("General");
		chkGeneral.setMargin(new Insets(0,0,0,0));
		chkGeneral.addChangeListener(this);
		chkHealth = new JCheckBox("Health");
		chkHealth.setMargin(new Insets(0,0,0,0));
		chkHealth.addChangeListener(this);		
		chkMSS = new JCheckBox("Member Self Service");
		chkMSS.setMargin(new Insets(0,0,0,0));
		chkMSS.addChangeListener(this);
		
		jcb[0]=chkBenefits;
		jcb[1]=chkContractsAndEnroll;
		jcb[2]=chkContributions;
		jcb[3]=chkFinancials;
		jcb[4]=chkGeneral;
		jcb[5]=chkHealth;
		jcb[6]=chkMSS;

		return jcb;
	}
	
	/**
	 * Setup right panel.
	 *
	 * @return the j panel
	 */
	private JPanel setupRightPanel()
	{
		JPanel testSetConfigPanel = new JPanel();
		testSetConfigPanel.setBorder(BorderFactory.createTitledBorder("Test Set Config"));
		testSetConfigPanel.setLayout(new BorderLayout(0,0));
		JPanel testSetPanel = new JPanel(new BorderLayout(0,0));
		
		//Create Settings Panel and add it to the main panel
        testSetSettings = new TestSetSettingsPanel(workInProgressTestSet);
        testSetSettings.setTotalNumberOfTests(allTests.size());
        testSetPanel.add(testSetSettings, BorderLayout.NORTH);
        
        ArrayList<JButton> buttons = new ArrayList<JButton>();
        addTestButton = new JButton("  >  ");
        addTestButton.setActionCommand(ApplicationCommands.ADD.name());
        addTestButton.setMargin(new Insets(0, 0, 0, 0));   
        addTestButton.addActionListener(this);
        addTestButton.setPreferredSize(new Dimension(25,25));
        addTestButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        buttons.add(addTestButton);
        removeTestButton = new JButton("  <  ");        
        removeTestButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        removeTestButton.setActionCommand(ApplicationCommands.REMOVE.name());
        removeTestButton.setMargin(new Insets(0, 0, 0, 0));
        removeTestButton.addActionListener(this);
        removeTestButton.setPreferredSize(new Dimension(25,25));
        buttons.add(removeTestButton);
        
        allTestsPanelList = new JList();
        allTestsPanelList.setBorder(new EmptyBorder(1, 1, 1, 1));
        selectedTestsPanelList = new JList();
        selectedTestsPanelList.setBorder(new EmptyBorder(1, 1, 1, 1));
        allTestsTree = new AutomationTestTreeForDisplay(allFunctionalGroups, "All Tests"); 
        allTestsTree.setRootVisible(true);
        selectedTestsTree = new AutomationTestTreeForDisplay(selectedFunctionalGroups, "Selected Tests");
        allTestsPanel = new JScrollPane(allTestsTree);
        selectedTestsPanel = new JScrollPane(selectedTestsTree);
        allTestsPanel.setDoubleBuffered(true);        
        selectedTestsPanel.setDoubleBuffered(true);
        allTestsPanelList.setModel(allTestsModel);
		selectedTestsPanelList.setModel(selectedTestsModel);
		TestSetConfigureSplitPane testSetSplitPane = new TestSetConfigureSplitPane(buttons);
		testSetSplitPane.setResizeWeight(0.5);
		testSetSplitPane.setDividerLocation(0.5);
		testSetSplitPane.setDoubleBuffered(true);
		testSetSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);		
		testSetSplitPane.setBorder(BorderFactory.createTitledBorder("Select Tests for Test Set"));
		testSetSplitPane.setLeftComponent(allTestsPanel);
		testSetSplitPane.setRightComponent(selectedTestsPanel);		
        testSetPanel.add(testSetSplitPane, BorderLayout.CENTER);
        
        bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(0, 0, 0, 4));  
        bottomPanel.setPreferredSize(new Dimension(500, 50));
		BoxLayout buttonLayout = new BoxLayout(bottomPanel, BoxLayout.X_AXIS);
		bottomPanel.setLayout(buttonLayout);
		
		createALMTestSet = new JButton("Create in ALM");
		createALMTestSet.setMaximumSize(new Dimension(75, 30));
		try {
			Image img = ImageIO.read(getClass().getResourceAsStream(
					"resources/alm-logo_16.jpg"));
			createALMTestSet.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
			System.out.println("Searchable Error: " + ex.getMessage());
			log.error("Error (IO): ", ex);
		}
		createALMTestSet.setActionCommand(ApplicationCommands.SET_READY_TO_RUN.toString());
		createALMTestSet.setEnabled(false);
		createALMTestSet.addActionListener(this);
		createALMTestSet.setPreferredSize(largeButtonDimensions);
		createALMTestSet.setMargin(new Insets(1, 0, 1, 0));

		
		revertButton = new JButton("Revert");
		revertButton.setMaximumSize(new Dimension(75, 30));
		try {
			Image img = ImageIO.read(getClass().getResourceAsStream(
					"resources/revert_16.png"));
			revertButton.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
			log.error("Error (IO): ", ex);
		}
		revertButton.setActionCommand(ApplicationCommands.REVERT.toString());
		revertButton.setEnabled(false);
		revertButton.addActionListener(this);
		revertButton.setPreferredSize(mediumButtonDimensions);
		revertButton.setMargin(new Insets(1, 0, 1, 0));

		// Add Buttons nicely spaced and anchored to the right side of the panel		
		bottomPanel.add(Box.createHorizontalGlue());
		
		saveButton = new JButton("Save");
		saveButton.setMaximumSize(new Dimension(75, 30));
		try {
			Image img = ImageIO.read(getClass().getResourceAsStream(
					"resources/ready_16.png"));
			saveButton.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
			log.error("Error (IO): ", ex);
		}
		saveButton.setActionCommand(ApplicationCommands.SAVE_TEST_SET.toString());
		saveButton.setEnabled(false);
		saveButton.addActionListener(this);
		saveButton.setPreferredSize(mediumButtonDimensions);
		saveButton.setMargin(new Insets(1, 0, 1, 0));
		
		
		
		bottomPanel.add(saveButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		bottomPanel.add(createALMTestSet);
		bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		bottomPanel.add(revertButton);        
        
        testSetPanel.add(bottomPanel, BorderLayout.SOUTH);        
        testSetConfigPanel.add(testSetPanel, BorderLayout.CENTER);
        return testSetConfigPanel;
		
	}
	
	/**
	 * Builds the auto functional group test lists.
	 */
	private void buildAutoFunctionalGroupTestLists()
	{
		allFunctionalGroups = (CopyOnWriteArrayList<AutomationFunctionalGroup>) AutomationFunctionalGroup.getAll();
		for(AutomationFunctionalGroup grp : allFunctionalGroups)
		{
			for(AutomationTest test : allTests)
			{
				if(test.getAutoFunctionalGroupCode().equals(grp.getAutoFunctionalGroupCode()))
				{
					grp.addTest(test);
				}
			}
		}
	}
	
	/**
	 * Creates the default all tests list.
	 *
	 * @param allTests the all tests
	 */
	private synchronized void createDefaultAllTestsList(List<AutomationTest> allTests)
	{		
		for(AutomationTest test: allTests)
		{
			final AutomationTest finalTest = test.copy();
			defaultAllTestsList.add(finalTest);
		}
	}
	
	/**
	 * Reset all tests list.
	 */
	private synchronized void resetAllTestsList()
	{
		allTests.clear();
		for(AutomationTest test: defaultAllTestsList)
		{
			allTests.add(test.copy());
		}
				
		for(AutomationTest test: allTests)
		{			
				allTestsTree.addTestToTree(test);	
		}
	}
	
	/**
	 * Setup all tests.
	 */
	private synchronized void setupAllTests()
	{
		allFunctionalGroups= (CopyOnWriteArrayList<AutomationFunctionalGroup>) AutomationFunctionalGroup.getAll();
		for(AutomationTest test : allTests)
		{
			test.getTestDependencies();
		}
	}
	
	/**
	 * Adds the auto funct group scripts to test set.
	 *
	 * @param autoFunctGroupCode the auto funct group code
	 */
	private synchronized void addAutoFunctGroupScriptsToTestSet(String autoFunctGroupCode)
	{
		CopyOnWriteArrayList<AutomationTest> testList = allTestsTree.getAllAutoFunctionalGroupTests(autoFunctGroupCode);
		
		for(AutomationTest test : testList)
		{			
			DefaultMutableTreeNode parent = selectedTestsTree.addTestToTree(test);
			if(parent != null)
			{
				selectedTestsTree.sortAutomationTestTree(parent);
			}
			allTestsTree.removeTestFromTree(test);
		}
		this.buildTestSet();
		canSave=true;
		canSaveALM=false;
		changedSinceLastSave=true;
	}
	
	/**
	 * Adds the funct group scripts to test set.
	 *
	 * @param functGroupCode the funct group code
	 */
	private synchronized void addFunctGroupScriptsToTestSet(String functGroupCode)
	{
		CopyOnWriteArrayList<AutomationTest> testList = allTestsTree.getAllFunctionalGroupTests(functGroupCode);
		
		for(AutomationTest test : testList)
		{
			DefaultMutableTreeNode parent = selectedTestsTree.addTestToTree(test);
			if(parent != null)
			{
				selectedTestsTree.sortAutomationTestTree(parent);
			}
			allTestsTree.removeTestFromTree(test);
		}
		this.buildTestSet();
		canSave=true;
		canSaveALM=false;
		changedSinceLastSave=true;
	}
		
	/**
	 * Removes the auto funct group scripts from test set.
	 *
	 * @param autoFunctGroupCode the auto funct group code
	 */
	private synchronized void removeAutoFunctGroupScriptsFromTestSet(String autoFunctGroupCode)
	{
		CopyOnWriteArrayList<AutomationTest> testList = selectedTestsTree.getAllAutoFunctionalGroupTests(autoFunctGroupCode);
		filterOutFunctionalGroupsFromAutoFunctionalGroups(testList);		
		for(AutomationTest test : testList)
		{
			allTestsTree.addTestToTree(test);			
			selectedTestsTree.removeTestFromTree(test);
		}
		this.buildTestSet();
		canSave=true;
		canSaveALM=false;
		changedSinceLastSave=true;
	}
	

	/**
	 * Removes the funct group scripts from test set.
	 *
	 * @param functGroupCode the funct group code
	 */
	private synchronized void removeFunctGroupScriptsFromTestSet(String functGroupCode)
	{
		CopyOnWriteArrayList<AutomationTest> testList = selectedTestsTree.getAllFunctionalGroupTests(functGroupCode);
		filterOutAutoFunctionalGroupsFromFunctionalGroups(testList);		
		for(AutomationTest test : testList)
		{
			allTestsTree.addTestToTree(test);			
			selectedTestsTree.removeTestFromTree(test);
		}
		this.buildTestSet();
		canSave=true;
		canSaveALM=false;
		changedSinceLastSave=true;
	}
	
	/**
	 * Filter out auto functional groups from functional groups.
	 *
	 * @param allFunctionalGroupTests the all functional group tests
	 */
	private synchronized void filterOutAutoFunctionalGroupsFromFunctionalGroups(CopyOnWriteArrayList<AutomationTest> allFunctionalGroupTests)
	{
		
		for(AutomationTest test: allFunctionalGroupTests)
		{
			
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_BENEFITS.toString()))
			{
				if(chkAutoBenefits.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT.toString()))
			{
				if(chkAutoContractsAndEnroll.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRIBUTIONS.toString()))
			{
				if(chkAutoContributions.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_DEATH.toString()))
			{
				if(chkAutoDeath.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_FINANCIALS.toString()))
			{
				if(chkAutoFinancials.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_GENERAL.toString()))
			{
				if(chkAutoGeneral.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_HEALTH.toString()))
			{
				if(chkAutoHealth.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_INTERFACES.toString()))
			{
				if(chkAutoInterfaces.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_JLRS.toString()))
			{
				if(chkAutoJLRS.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}
			if(test.getAutoFunctionalGroupCode().equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_MSS.toString()))
			{
				if(chkAutoMSS.isSelected()){
					allFunctionalGroupTests.remove(test);
				}
			}			
		}
	}

	
	
	/**
	 * Filter out functional groups from auto functional groups.
	 *
	 * @param allAutoFunctionalGroupTests the all auto functional group tests
	 */
	private synchronized void filterOutFunctionalGroupsFromAutoFunctionalGroups(CopyOnWriteArrayList<AutomationTest> allAutoFunctionalGroupTests)
	{
		
		for(AutomationTest test: allAutoFunctionalGroupTests)
		{
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_BENEFITS.toString()))
			{
				if(chkBenefits.isSelected()){
					allAutoFunctionalGroupTests.remove(test);					
				}
			}
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString()))
			{
				if(chkContractsAndEnroll.isSelected()){
					allAutoFunctionalGroupTests.remove(test);					
				}
			}
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString()))
			{
				if(chkContributions.isSelected()){
					allAutoFunctionalGroupTests.remove(test);				
				}
			}			
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString()))
			{
				if(chkFinancials.isSelected()){
					allAutoFunctionalGroupTests.remove(test);
				}
			}
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_GENERAL.toString()))
			{
				if(chkGeneral.isSelected()){
					allAutoFunctionalGroupTests.remove(test);
				}
			}
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_HEALTH.toString()))
			{
				if(chkHealth.isSelected()){
					allAutoFunctionalGroupTests.remove(test);
				}
			}			
			if(test.getFunctionalGroupCode().equals(FunctionalGroup.FUNCT_GRP_MSS.toString()))
			{
				if(chkMSS.isSelected()){
					allAutoFunctionalGroupTests.remove(test);
				}
			}				
		}
	}
	
	/**
	 * Save work in progress test set.
	 */
	private void saveWorkInProgressTestSet()
	{
		originalTestSet.setTestSetName(workInProgressTestSet.getTestSetName());
		originalTestSet.setRetryLimit(workInProgressTestSet.getRetryLimit());
		originalTestSet.setAlmPath(workInProgressTestSet.getAlmPath());
		originalTestSet.setRelease(workInProgressTestSet.getRelease());
		originalTestSet.setRound(workInProgressTestSet.getRound());
		originalTestSet.setHardStopRoll(workInProgressTestSet.getHardStopRoll());
		originalTestSet.setTestSetId(workInProgressTestSet.getTestSetId());
		originalTestSet.setRunStatus(workInProgressTestSet.getRunStatus());
		originalTestSet.setTestSetSelectionCriteria(workInProgressTestSet.getTestSetSelectionCriteria());
		originalTestSet.getTests().clear();		
		setOriginalGroupCheckBoxStates();		
		for(AutomationTest test: workInProgressTestSet.getTests())
		{
			originalTestSet.addTest(test);
		}
		try{
			AutomationTestSet temp = originalTestSet.save();
			if(temp!=null)
			{
				originalTestSet.setTestSetId(temp.getTestSetId());
				workInProgressTestSet.setTestSetId(temp.getTestSetId());
			}
			canSave = false;
			changedSinceLastSave = false;
			canSaveALM = true;
		}catch(Exception ex)
		{			
			JOptionPane.showMessageDialog (mainFrame, "A Test Set already exists with the name:\n\n" + workInProgressTestSet.getTestSetName() + "\n\nPlease change the Release or Round to make the Test Set Name unique.", "Duplicate Test Set Name", JOptionPane.ERROR_MESSAGE);
			log.debug("Error (Unknown): ",ex);
		}
		filterAllTestsBasedOnSelectedTestOnLoad();
	}
	
	/**
	 * Complete work in progress test set.
	 */
	private void completeWorkInProgressTestSet()
	{
		originalTestSet.setTestSetName(workInProgressTestSet.getTestSetName());
		originalTestSet.setRetryLimit(workInProgressTestSet.getRetryLimit());
		originalTestSet.setAlmPath(workInProgressTestSet.getAlmPath());
		originalTestSet.setRelease(workInProgressTestSet.getRelease());
		originalTestSet.setRound(workInProgressTestSet.getRound());
		originalTestSet.setHardStopRoll(workInProgressTestSet.getHardStopRoll());
		originalTestSet.setTestSetId(workInProgressTestSet.getTestSetId());
		originalTestSet.setRunStatus(workInProgressTestSet.getRunStatus());
		originalTestSet.getTests().clear();		
		setOriginalGroupCheckBoxStates();		
		for(AutomationTest test: workInProgressTestSet.getTests())
		{
			originalTestSet.addTest(test.copy());
		}
		for(AutomationTest test: workInProgressTestSet.getTests())
		{
			originalTestSet.addTest(test.copy());
		}
		try{
			AutomationTestSet temp = originalTestSet.save();
			if(temp!=null)
			{
				originalTestSet.setTestSetId(temp.getTestSetId());
				workInProgressTestSet.setTestSetId(temp.getTestSetId());
			}
			
			canSave = false;
			changedSinceLastSave = false;
			canSaveALM = true;
		}catch(Exception ex)
		{
			ex.printStackTrace();			 
			JOptionPane.showMessageDialog (mainFrame, "A Test Set already exists with the name:\n\n" + workInProgressTestSet.getTestSetName() + "\n\nPlease change the Release or Round to make the Test Set Name unique.", "Duplicate Test Set Name", JOptionPane.ERROR_MESSAGE);			
		}
		filterAllTestsBasedOnSelectedTestOnLoad();
		if(!almTestSetPersisted)
		{
			mainFrame.getGlassPane().setVisible(true);
			startCreateTestSet();
			workInProgressTestSet.setPersistedInALM(true);
			originalTestSet.setPersistedInALM(true);
			originalTestSet.setTestSetId(workInProgressTestSet.getTestSetId());
		}
	}
	
	/**
	 * Revert work in progress test set.
	 */
	private void revertWorkInProgressTestSet()
	{
		workInProgressTestSet.setTestSetName(originalTestSet.getTestSetName());
		workInProgressTestSet.setRetryLimit(originalTestSet.getRetryLimit());
		workInProgressTestSet.setAlmPath(originalTestSet.getAlmPath());
		workInProgressTestSet.setRelease(originalTestSet.getRelease());
		workInProgressTestSet.setRound(originalTestSet.getRound());
		workInProgressTestSet.setHardStopRoll(originalTestSet.getHardStopRoll());
		workInProgressTestSet.getTests().clear();
		for(AutomationTest test: originalTestSet.getTests())
		{
			workInProgressTestSet.addTest(test.copy());
		}
		buildSelectedTestTree();
		filterAllTestsBasedOnSelectedTestOnLoad();
		disableButtons();
		changedSinceLastSave=false;
	}

	/**
	 * Builds the selected test tree.
	 */
	private void buildSelectedTestTree()
	{
		
		CopyOnWriteArrayList<AutomationFunctionalGroup> funcGroups = new CopyOnWriteArrayList<AutomationFunctionalGroup>();
		for(AutomationTest test : workInProgressTestSet.getTests())
		{
			boolean groupDoesNotExist = true;
			for(AutomationFunctionalGroup grp : funcGroups)
			{
				if(test.getAutoFunctionalGroupCode().equals(grp.getAutoFunctionalGroupCode()))
				{
					grp.addTest(test);
					groupDoesNotExist = false;
				}
			}
			if(groupDoesNotExist)
			{
				AutomationFunctionalGroup grp = AutomationFunctionalGroup.getGroup(test.getAutoFunctionalGroupCode());
				grp.addTest(test);
				funcGroups.add(grp);
			}
		}		
		selectedTestsTree = new AutomationTestTreeForDisplay(funcGroups, "Selected Tests");
		selectedTestsPanel.setViewportView(selectedTestsTree);
	}
	
	/**
	 * Filter all tests based on selected test on load.
	 */
	private void filterAllTestsBasedOnSelectedTestOnLoad()
	{
		resetAllTestsList();
		CopyOnWriteArrayList<AutomationTest> selectedTests = selectedTestsTree.getAllTests();
		for(AutomationTest test: selectedTests)
		{
			allTests.remove(test);
			allTestsTree.removeTestFromTree(test);
		}
		for(AutomationTest test : workInProgressTestSet.getTests())
		{
			for( AutomationTest allTest : allTests)
			{
				if(allTest.compareTo(test) == 0)
				{
					if(selectedTestsTree.hasTest(allTest))
					{
						allTests.remove(allTest);
						allTestsTree.removeTestFromTree(allTest);
					}else
					{
						System.out.println("*********************************************************");
						System.out.println("*****   ERROR!!! We should never see this message.  *****");
						System.out.println("***** workInProgressTestSet and Selected Tests Tree *****");
						System.out.println("***** 				are out of synch. 				*****");						
						System.out.println("*********************************************************");
					}
					break;
				}
			}
		}
	}
	
	/**
	 * Builds the test set.
	 */
	private void buildTestSet()
	{
		workInProgressTestSet.clearTests();
		
		for(AutomationTest test: selectedTestsTree.getAllTests())
		{
			workInProgressTestSet.addTest(test);	
		}
		setTestSetAutoFunctionalGroups();
		setTestSetFunctionalGroups();
		System.out.println("TestSet Built.");
	}
	
	/**
	 * Sets the test set auto functional groups.
	 */
	private void setTestSetAutoFunctionalGroups()
	{
		if(chkAutoBenefits.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_BENEFITS.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);			
		}else if(!chkAutoBenefits.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_BENEFITS.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoContractsAndEnroll.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoContractsAndEnroll.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoContributions.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRIBUTIONS.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoContributions.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRIBUTIONS.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoDeath.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_DEATH.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoDeath.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_DEATH.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoFinancials.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_FINANCIALS.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoFinancials.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_FINANCIALS.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoGeneral.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_GENERAL.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoGeneral.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_GENERAL.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoHealth.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_HEALTH.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoHealth.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_HEALTH.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoInterfaces.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_INTERFACES.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoInterfaces.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_INTERFACES.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoJLRS.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_JLRS.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoJLRS.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_JLRS.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
		if(chkAutoMSS.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(AutoFunctionalGroup.AUTO_FUNCT_GRP_MSS.toString());
			criteria.setSelectionType(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkAutoMSS.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(AutoFunctionalGroup.AUTO_FUNCT_GRP_MSS.toString(), TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP);
		}
	}
	
	/**
	 * Sets the test set functional groups.
	 */
	private void setTestSetFunctionalGroups()
	{
		if(chkBenefits.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_BENEFITS.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkBenefits.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_BENEFITS.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		if(chkContractsAndEnroll.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkContractsAndEnroll.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		if(chkContributions.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkContributions.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		if(chkFinancials.isSelected())			
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkFinancials.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		if(chkGeneral.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_GENERAL.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkGeneral.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_GENERAL.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		if(chkHealth.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_HEALTH.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkHealth.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_HEALTH.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		if(chkMSS.isSelected())
		{
			TestSetSelectionCriteria criteria = new TestSetSelectionCriteria();
			criteria.setSelectionCode(FunctionalGroup.FUNCT_GRP_MSS.toString());
			criteria.setSelectionType(TestSetSelectionType.FUNCTIONAL_GROUP);
			workInProgressTestSet.addTestSetSelectionCriteria(criteria);
		}else if(!chkMSS.isSelected())
		{
			workInProgressTestSet.removeTestSetSelectionCriteria(FunctionalGroup.FUNCT_GRP_MSS.toString(), TestSetSelectionType.FUNCTIONAL_GROUP);
		}
		
	}
	
	/**
	 * Sets the original group check box states.
	 */
	private void setOriginalGroupCheckBoxStates()
	{
		originalStates.setAutoBenefitsChecked(chkAutoBenefits.isSelected());
		originalStates.setAutoContractsEnrollmentChecked(chkAutoContractsAndEnroll.isSelected());
		originalStates.setAutoContributionsChecked(chkAutoContributions.isSelected());
		originalStates.setAutoDeathChecked(chkAutoDeath.isSelected());
		originalStates.setAutoFinancialsChecked(chkAutoFinancials.isSelected());
		originalStates.setAutoGeneralChecked(chkAutoGeneral.isSelected());
		originalStates.setAutoHealthChecked(chkAutoHealth.isSelected());
		originalStates.setAutoInterfacesChecked(chkAutoInterfaces.isSelected());
		originalStates.setAutoJLRSChecked(chkAutoJLRS.isSelected());
		originalStates.setAutoMSSChecked(chkAutoMSS.isSelected());
		
		originalStates.setBenefitsChecked(chkBenefits.isSelected());
		originalStates.setContractsEnrollmentsChecked(chkContractsAndEnroll.isSelected());
		originalStates.setContributionsChecked(chkContributions.isSelected());		
		originalStates.setFinancialsChecked(chkFinancials.isSelected());
		originalStates.setGeneralChecked(chkGeneral.isSelected());
		originalStates.setHealthChecked(chkHealth.isSelected());		
		originalStates.setMSSChecked(chkMSS.isSelected());
	}
	
	/**
	 * Revert group check boxes.
	 */
	private void revertGroupCheckBoxes()
	{
		chkAutoBenefits.setSelected(originalStates.isAutoBenefitsChecked());
		chkAutoContractsAndEnroll.setSelected(originalStates.isAutoContractsEnrollmentChecked());
		chkAutoContributions.setSelected(originalStates.isAutoContributionsChecked());
		chkAutoFinancials.setSelected(originalStates.isAutoFinancialsChecked());
		chkAutoGeneral.setSelected(originalStates.isAutoGeneralChecked());
		chkAutoHealth.setSelected(originalStates.isAutoHealthChecked());
		chkAutoMSS.setSelected(originalStates.isAutoMSSChecked());
		chkAutoDeath.setSelected(originalStates.isAutoDeathChecked());
		chkAutoInterfaces.setSelected(originalStates.isAutoInterfacesChecked());
		chkAutoJLRS.setSelected(originalStates.isAutoJLRSChecked());
		
		chkBenefits.setSelected(originalStates.isBenefitsChecked());
		chkContractsAndEnroll.setSelected(originalStates.isContractsEnrollmentChecked());
		chkContributions.setSelected(originalStates.isContributionsChecked());		
		chkFinancials.setSelected(originalStates.isFinancialsChecked());
		chkGeneral.setSelected(originalStates.isGeneralChecked());
		chkHealth.setSelected(originalStates.isHealthChecked());
		chkMSS.setSelected(originalStates.isMSSChecked());
	}
	
	/**
	 * Disable enable buttons.
	 */
	private void disableEnableButtons()
	{		
		if(workInProgressTestSet.isEmpty() || workInProgressTestSet.compareTo(originalTestSet) == 0 || workInProgressTestSet.getRelease() == "" || workInProgressTestSet.getRound() == "" || workInProgressTestSet.getRetryLimit() <= 0)
		{	
			saveButton.setEnabled(false);			
		}else if(workInProgressTestSet.getRelease() != "" && workInProgressTestSet.getRound() != "" && workInProgressTestSet.getRetryLimit() >= 1)
		{
			saveButton.setEnabled(true);	
		}
		if(workInProgressTestSet.compareTo(originalTestSet) != 0)
		{
			revertButton.setEnabled(changedSinceLastSave);
		}else
		{
			revertButton.setEnabled(false);
		}
		
		if(!almTestSetPersisted)
		{
			createALMTestSet.setEnabled(canSaveALM);
		}else
		{
			createALMTestSet.setEnabled(false);
		}
		
	}
	
	/**
	 * Disable buttons.
	 */
	private void disableButtons()
	{
		System.out.println("Disabling Buttons");
		log.debug("Disabling Buttons");
		createALMTestSet.setEnabled(false);
		revertButton.setEnabled(false);
	}
	
	/**
	 * Sets the panel components to read only.
	 */
	private void setPanelComponentsToReadOnly()
	{
		//Test Settings Panel
		testSetSettings.setPanelComponentsToReadOnly();
		//Functional Groups
		autoFunctionalGroupList.setReadOnly(true);
		functionalGroupList.setReadOnly(true);			
		//Test Panels (ScrollPanes)
		allTestsPanel.setEnabled(false);		
		allTestsTree.setEnabled(false);		
		selectedTestsPanel.setEnabled(false);
		selectedTestsTree.setEnabled(false);
		//Buttons
		addTestButton.setEnabled(false);
		removeTestButton.setEnabled(false);
		saveButton.setEnabled(false);
		revertButton.setEnabled(false);
		createALMTestSet.setEnabled(false);
		
	}
	
	/**
	 * Populate tab data with test set.
	 */
	private void populateTabDataWithTestSet()
	{
		if(originalTestSet.getTestSetSelectionCriteria().isEmpty() && originalTestSet.hasTests())
		{
			buildSelectedTestTree();			
			canSaveALM=true;
			changedSinceLastSave = false;
			
		}else
		{
			for(TestSetSelectionCriteria criteria : originalTestSet.getTestSetSelectionCriteria())
			{
				if(criteria.getSelectionType().equals(TestSetSelectionType.AUTOMATION_FUNCTIONAL_GROUP))
				{
					enableAutomationFunctionalGroupCheckBox(criteria.getSelectionCode());
				}else if(criteria.getSelectionType().equals(TestSetSelectionType.FUNCTIONAL_GROUP))
				{
					enableFunctionalGroupCheckBox(criteria.getSelectionCode());
				}else
				{
					enableOtherSelectionCheckBox(criteria.getSelectionCode());
				}					
			}
		}
	}
	
	/**
	 * Enable automation functional group check box.
	 *
	 * @param automationFunctionalGroupCode the automation functional group code
	 */
	private void enableAutomationFunctionalGroupCheckBox(String automationFunctionalGroupCode)
	{
		if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_BENEFITS.toString())) {
			chkAutoBenefits.setSelected(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT
						.toString())) {
			chkAutoContractsAndEnroll.setSelected(true);

		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRIBUTIONS
						.toString())) {
			chkAutoContributions.setSelected(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_DEATH.toString())) {
			chkAutoDeath.setSelected(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_FINANCIALS
						.toString())) {
			chkAutoFinancials.setSelected(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_GENERAL.toString())) {
			chkAutoGeneral.setSelected(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_HEALTH.toString())) {
			chkAutoHealth.setSelected(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_INTERFACES
						.toString())) {
			chkAutoInterfaces.setEnabled(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_JLRS.toString())) {
			chkAutoJLRS.setEnabled(true);
		} else if (automationFunctionalGroupCode
				.equals(AutoFunctionalGroup.AUTO_FUNCT_GRP_MSS.toString())) {
			chkAutoMSS.setSelected(true);
		}
	}
	
	/**
	 * Enable functional group check box.
	 *
	 * @param functionalGroupCode the functional group code
	 */
	private void enableFunctionalGroupCheckBox(String functionalGroupCode)
	{
		
		if (functionalGroupCode.equals(FunctionalGroup.FUNCT_GRP_BENEFITS
				.toString())) {
			chkBenefits.setSelected(true);
		} else if (functionalGroupCode
				.equals(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT
						.toString())) {
			chkContractsAndEnroll.setSelected(true);
		} else if (functionalGroupCode
				.equals(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString())) {
			chkContributions.setSelected(true);
		} else if (functionalGroupCode
				.equals(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString())) {
			chkFinancials.setSelected(true);
		} else if (functionalGroupCode.equals(FunctionalGroup.FUNCT_GRP_GENERAL
				.toString())) {
			chkGeneral.setSelected(true);
		} else if (functionalGroupCode.equals(FunctionalGroup.FUNCT_GRP_HEALTH
				.toString())) {
			chkHealth.setSelected(true);
		} else if (functionalGroupCode.equals(FunctionalGroup.FUNCT_GRP_MSS
				.toString())) {
			chkMSS.setSelected(true);
		}
	}
	
	/**
	 * Not Used yet. For future selection criteria.
	 * Enable other selection check box.
	 *
	 * @param otherSelectionCode the other selection code
	 */
	private void enableOtherSelectionCheckBox(String otherSelectionCode)
	{
		
	}
	
	/**
	 * List tests.
	 */
	public void listTests( ){
		 
		 Session session = HibernateUtil.getSecondarySesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);	
		 		Transaction tx = session.beginTransaction();
		 		Query query = session.createSQLQuery("SELECT * FROM AUTOMATION_SCRIPT").addEntity(AutomationTest.class);
 		 		@SuppressWarnings("unchecked")
				List<AutomationTest> list = (query.list());
		 		System.out.println("Query.toString= " + query.toString());
	            for (AutomationTest obj : list) {
		              CollectionsUtil.addInOrder(allTests,obj);
		              System.out.println("*** TEST ADDED ***");
		              log.debug("*** TEST ADDED: " + obj.getTestName() + " ***");
		          }
	            tx.commit();
	            session.close();
		 	}	     
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void actionPerformed(ActionEvent e) {

		TreePath[] paths;
		System.out.println("Action occurred: " + e.getActionCommand());
		log.debug("Action occurred: " + e.getActionCommand());
		switch (ApplicationCommands.valueOf(e.getActionCommand())){		
		
		case SAVE_TEST_SET:			
			System.out.println("Setting Test Set to DRAFT...");
			log.debug("Setting Test Set to DRAFT...");
			workInProgressTestSet.setRunStatus("DRAFT");
			this.buildTestSet();
			WorkManager tempManager = new WorkManager(workInProgressTestSet);
			System.out.println("Work Manager setup.");
			try{
				tempManager.processTestSet();
				ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustPassTests = tempManager.getMissingMustPassTests();
				ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustRunTests = tempManager.getMissingMustRunTests();				
				if(!missingMustPassTests.isEmpty())
				{
					String missingTestString = ""; 
					for(AutomationTest test : missingMustPassTests.keySet())
					{
						CopyOnWriteArrayList<AutomationTest> tests = missingMustPassTests.get(test);
						for(AutomationTest aTest : tests)
						{
							missingTestString = missingTestString + aTest.getTestName() + " (is depended on by " + test.getTestName() + ")\n"; 
							
						}
					}
					JOptionPane.showMessageDialog(mainFrame,							
							"Please add the following tests to satisfy dependencies\n\n" + missingTestString + "\n",
							"Missing MUST_PASS Dependent Tests",
					        JOptionPane.ERROR_MESSAGE);
					break;
				}else if(!missingMustRunTests.isEmpty())
				{
					String missingTestString = ""; 
					for(AutomationTest test : missingMustRunTests.keySet())
					{
						CopyOnWriteArrayList<AutomationTest> tests = missingMustRunTests.get(test);
						for(AutomationTest aTest : tests)
						{
							missingTestString = missingTestString + aTest.getTestName() + " (is depended on by " + test.getTestName() + ")\n"; 
							
						}
					}
					int reply = JOptionPane.showConfirmDialog(mainFrame,							
							"The following tests are missing from the test set and have MUST_RUN dependencies\n\n" + missingTestString + "\n\nWould you like to disable these dependencies to allow the Test Set to be created and executed?\n",
							"Missing MUST_RUN Dependent Tests",
							JOptionPane.ERROR_MESSAGE,
					        JOptionPane.YES_NO_OPTION);
					if(reply == JOptionPane.YES_OPTION)
					{
						for(AutomationTest test : missingMustRunTests.keySet())
						{
							CopyOnWriteArrayList<AutomationTest> tests = missingMustRunTests.get(test);
							for(AutomationTest aTest : tests)
							{										
								for(TestDependency dep : test.getTestDependencies())
								{
									if(dep.getParentTestId() == aTest.getTestId())
									{
										if(dep.getDependencyType().equals(EdgeTypeEnum.MUST_RUN))
										{
											dep.setDisabled(true);
										}
									}
								}										
							}
						}
					}else
					{
						break;
					}							
				}			
				saveWorkInProgressTestSet();			
				disableEnableButtons();	
			}catch(IllegalArgumentException t)
			{				
				JOptionPane.showMessageDialog(mainFrame,
						"Cycle Detected in dependencies. Please check dependencies and remove the cycle before saving the test set again. (" + t.getMessage() +")",
						"Dependency Tree Error",							
				        JOptionPane.ERROR_MESSAGE);			
			}						
			break;
		case SET_READY_TO_RUN:			
			System.out.println("Setting Test Set to READY TO RUN...");
			log.debug("Setting Test Set to READY TO RUN...");
			workInProgressTestSet.setRunStatus(TestSetRunStatus.READY_TO_RUN.toString());			
			this.buildTestSet();			
			completeWorkInProgressTestSet();
			try{
				workManager.processTestSet();
			}catch(IllegalArgumentException t)
			{
				JOptionPane.showMessageDialog(mainFrame,
						"Dependency Tree Error",
						"Cycle Detected in dependencies. Please check dependencies and remove the cycle before saving the test set again.",					        
				        JOptionPane.ERROR_MESSAGE);					    
			}
			break;
		case REVERT:
			System.out.println("Reverting Test Set to Original...");
			log.debug("Reverting Test Set to Original...");
			revertGroupCheckBoxes();			
			createALMTestSet.setEnabled(false);
			revertButton.setEnabled(false);
			revertWorkInProgressTestSet();
			canSave=false;
			canSaveALM=false;
			changedSinceLastSave=false;
			disableEnableButtons();
			testSetSettings.repaint();
			this.repaint();
			break;
		case ADD:				
			paths = allTestsTree.getSelectionPaths();
			addTestToSelectedTestsTree(paths);
			this.buildTestSet();
			canSave=true;
			canSaveALM=false;
			changedSinceLastSave=true;
			disableEnableButtons();
			break;			
		case REMOVE:			
			paths = selectedTestsTree.getSelectionPaths();
			removeTestFromSelectedTestsTree(paths);
			this.buildTestSet();	
			canSave=true;
			canSaveALM=false;
			changedSinceLastSave=true;
			disableEnableButtons();
			break;
		}
	}

	/**
	 * Adds the test to selected tests tree.
	 *
	 * @param paths the paths
	 */
	private synchronized void addTestToSelectedTestsTree(TreePath[] paths)
	{
		for(TreePath path : paths)
		{
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			
			if(lastNode.getUserObject() instanceof  AutomationFunctionalGroup)
			{
				AutomationFunctionalGroup currGroup = (AutomationFunctionalGroup) lastNode.getUserObject();
									
				for(AutomationTest groupTest: currGroup.getTests())
				{						
					if(!selectedTestsTree.hasTest(groupTest))
					{
						selectedTestsTree.addTestToTree(groupTest);	
					}
					allTestsTree.removeTestFromTree(groupTest);
				}					
			}else if(lastNode.getUserObject() instanceof AutomationTest)
			{
				AutomationTest test = (AutomationTest) lastNode.getUserObject();					
				if(!selectedTestsTree.hasTest(test))
				{
					selectedTestsTree.addTestToTree(test);	
				}					
				allTestsTree.removeTestFromTree(test);					
			}				
		}
	}
	
	
	/**
	 * Removes the test from selected tests tree.
	 *
	 * @param paths the paths
	 */
	private synchronized void removeTestFromSelectedTestsTree(TreePath[] paths)
	{
		for(TreePath path : paths)
		{
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			
			if(lastNode.getUserObject() instanceof  AutomationFunctionalGroup)
			{
				AutomationFunctionalGroup currGroup = (AutomationFunctionalGroup) lastNode.getUserObject();
				for(AutomationTest test: currGroup.getTests())
				{
					if(!allTestsTree.hasTest(test))
					{
						allTestsTree.addTestToTree(test);
					}
					selectedTestsTree.removeTestFromTree(test);						
				}
			}else if(lastNode.getUserObject() instanceof AutomationTest)
			{
				AutomationTest test = (AutomationTest) lastNode.getUserObject();
									
				if(!allTestsTree.hasTest(test))
				{
					allTestsTree.addTestToTree(test);
				}
				selectedTestsTree.removeTestFromTree(test);				
			}				
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

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {	
		if(saveButton!=null && revertButton != null && createALMTestSet != null)
		{
			disableEnableButtons();
		}
		this.repaint();
	}
	
	/**
	 * The Class CheckBoxStates.
	 */
	private class CheckBoxStates
	{
		
		/** The benefits checked. */
		private boolean benefitsChecked = false;
		
		/** The contracts enrollment checked. */
		private boolean contractsEnrollmentChecked = false;
		
		/** The contributions checked. */
		private boolean contributionsChecked = false;
		
		/** The financials checked. */
		private boolean financialsChecked = false;
		
		/** The general checked. */
		private boolean generalChecked = false;
		
		/** The health checked. */
		private boolean healthChecked = false;		
		
		/** The mss checked. */
		private boolean mssChecked = false;
		
		/** The auto benefits checked. */
		private boolean autoBenefitsChecked = false;
		
		/** The auto contracts enrollment checked. */
		private boolean autoContractsEnrollmentChecked = false;
		
		/** The auto contributions checked. */
		private boolean autoContributionsChecked = false;
		
		/** The auto death checked. */
		private boolean autoDeathChecked = false;
		
		/** The auto financials checked. */
		private boolean autoFinancialsChecked = false;
		
		/** The auto general checked. */
		private boolean autoGeneralChecked = false;
		
		/** The auto health checked. */
		private boolean autoHealthChecked = false;
		
		/** The auto interfaces checked. */
		private boolean autoInterfacesChecked = false;
		
		/** The auto jlrs checked. */
		private boolean autoJLRSChecked = false;
		
		/** The auto mss checked. */
		private boolean autoMSSChecked = false;
		
		/**
		 * Sets the benefits checked.
		 *
		 * @param benefitsChecked the new benefits checked
		 */
		public void setBenefitsChecked(boolean benefitsChecked) {
			this.benefitsChecked = benefitsChecked;
		}
		
		/**
		 * Sets the contracts enrollments checked.
		 *
		 * @param contractsEnrollmentChecked the new contracts enrollments checked
		 */
		public void setContractsEnrollmentsChecked(boolean contractsEnrollmentChecked) {
			this.contractsEnrollmentChecked = contractsEnrollmentChecked;
		}
		
		/**
		 * Sets the contributions checked.
		 *
		 * @param contributionsChecked the new contributions checked
		 */
		public void setContributionsChecked(boolean contributionsChecked) {
			this.contributionsChecked = contributionsChecked;
		}
		
		/**
		 * Sets the financials checked.
		 *
		 * @param financialsChecked the new financials checked
		 */
		public void setFinancialsChecked(boolean financialsChecked) {
			this.financialsChecked = financialsChecked;
		}
		
		/**
		 * Sets the general checked.
		 *
		 * @param generalChecked the new general checked
		 */
		public void setGeneralChecked(boolean generalChecked) {
			this.generalChecked = generalChecked;
		}
		
		/**
		 * Sets the health checked.
		 *
		 * @param healthChecked the new health checked
		 */
		public void setHealthChecked(boolean healthChecked) {
			this.healthChecked = healthChecked;
		}
		
		/**
		 * Sets the MSS checked.
		 *
		 * @param mssChecked the new MSS checked
		 */
		public void setMSSChecked(boolean mssChecked) {
			this.mssChecked = mssChecked;
		}		
		
		/**
		 * Checks if is benefits checked.
		 *
		 * @return true, if is benefits checked
		 */
		public boolean isBenefitsChecked()
		{
			return benefitsChecked;
		}
		
		/**
		 * Checks if is contracts enrollment checked.
		 *
		 * @return true, if is contracts enrollment checked
		 */
		public boolean isContractsEnrollmentChecked()		
		{
			return contractsEnrollmentChecked;
		}
		
		/**
		 * Checks if is contributions checked.
		 *
		 * @return true, if is contributions checked
		 */
		public boolean isContributionsChecked()
		{
			return contributionsChecked;
		}
				
		/**
		 * Checks if is financials checked.
		 *
		 * @return true, if is financials checked
		 */
		public boolean isFinancialsChecked()
		{
			return financialsChecked;
		}
		
		/**
		 * Checks if is general checked.
		 *
		 * @return true, if is general checked
		 */
		public boolean isGeneralChecked()
		{
			return generalChecked;
		}
		
		/**
		 * Checks if is health checked.
		 *
		 * @return true, if is health checked
		 */
		public boolean isHealthChecked()
		{
			return healthChecked;
		}

		/**
		 * Checks if is MSS checked.
		 *
		 * @return true, if is MSS checked
		 */
		public boolean isMSSChecked()
		{
			return mssChecked;
		}
		
		/**
		 * Checks if is auto benefits checked.
		 *
		 * @return true, if is auto benefits checked
		 */
		public boolean isAutoBenefitsChecked() {
			return autoBenefitsChecked;
		}

		/**
		 * Sets the auto benefits checked.
		 *
		 * @param autoBenefitsChecked the new auto benefits checked
		 */
		public void setAutoBenefitsChecked(boolean autoBenefitsChecked) {
			this.autoBenefitsChecked = autoBenefitsChecked;
		}

		/**
		 * Checks if is auto contracts enrollment checked.
		 *
		 * @return true, if is auto contracts enrollment checked
		 */
		public boolean isAutoContractsEnrollmentChecked() {
			return autoContractsEnrollmentChecked;
		}

		/**
		 * Sets the auto contracts enrollment checked.
		 *
		 * @param autoContractsEnrollmentChecked the new auto contracts enrollment checked
		 */
		public void setAutoContractsEnrollmentChecked(
				boolean autoContractsEnrollmentChecked) {
			this.autoContractsEnrollmentChecked = autoContractsEnrollmentChecked;
		}

		/**
		 * Checks if is auto contributions checked.
		 *
		 * @return true, if is auto contributions checked
		 */
		public boolean isAutoContributionsChecked() {
			return autoContributionsChecked;
		}

		/**
		 * Sets the auto contributions checked.
		 *
		 * @param autoContributionsChecked the new auto contributions checked
		 */
		public void setAutoContributionsChecked(boolean autoContributionsChecked) {
			this.autoContributionsChecked = autoContributionsChecked;
		}

		/**
		 * Checks if is auto death checked.
		 *
		 * @return true, if is auto death checked
		 */
		public boolean isAutoDeathChecked() {
			return autoDeathChecked;
		}

		/**
		 * Sets the auto death checked.
		 *
		 * @param autoDeathChecked the new auto death checked
		 */
		public void setAutoDeathChecked(boolean autoDeathChecked) {
			this.autoDeathChecked = autoDeathChecked;
		}

		/**
		 * Checks if is auto financials checked.
		 *
		 * @return true, if is auto financials checked
		 */
		public boolean isAutoFinancialsChecked() {
			return autoFinancialsChecked;
		}

		/**
		 * Sets the auto financials checked.
		 *
		 * @param autoFinancialsChecked the new auto financials checked
		 */
		public void setAutoFinancialsChecked(boolean autoFinancialsChecked) {
			this.autoFinancialsChecked = autoFinancialsChecked;
		}

		/**
		 * Checks if is auto general checked.
		 *
		 * @return true, if is auto general checked
		 */
		public boolean isAutoGeneralChecked() {
			return autoGeneralChecked;
		}

		/**
		 * Sets the auto general checked.
		 *
		 * @param autoGeneralChecked the new auto general checked
		 */
		public void setAutoGeneralChecked(boolean autoGeneralChecked) {
			this.autoGeneralChecked = autoGeneralChecked;
		}

		/**
		 * Checks if is auto health checked.
		 *
		 * @return true, if is auto health checked
		 */
		public boolean isAutoHealthChecked() {
			return autoHealthChecked;
		}

		/**
		 * Sets the auto health checked.
		 *
		 * @param autoHealthChecked the new auto health checked
		 */
		public void setAutoHealthChecked(boolean autoHealthChecked) {
			this.autoHealthChecked = autoHealthChecked;
		}

		/**
		 * Checks if is auto interfaces checked.
		 *
		 * @return true, if is auto interfaces checked
		 */
		public boolean isAutoInterfacesChecked() {
			return autoInterfacesChecked;
		}

		/**
		 * Sets the auto interfaces checked.
		 *
		 * @param autoInterfacesChecked the new auto interfaces checked
		 */
		public void setAutoInterfacesChecked(boolean autoInterfacesChecked) {
			this.autoInterfacesChecked = autoInterfacesChecked;
		}

		/**
		 * Checks if is auto jlrs checked.
		 *
		 * @return true, if is auto jlrs checked
		 */
		public boolean isAutoJLRSChecked() {
			return autoJLRSChecked;
		}

		/**
		 * Sets the auto jlrs checked.
		 *
		 * @param autoJLRSChecked the new auto jlrs checked
		 */
		public void setAutoJLRSChecked(boolean autoJLRSChecked) {
			this.autoJLRSChecked = autoJLRSChecked;
		}

		/**
		 * Checks if is auto mss checked.
		 *
		 * @return true, if is auto mss checked
		 */
		public boolean isAutoMSSChecked() {
			return autoMSSChecked;
		}

		/**
		 * Sets the auto mss checked.
		 *
		 * @param autoMSSChecked the new auto mss checked
		 */
		public void setAutoMSSChecked(boolean autoMSSChecked) {
			this.autoMSSChecked = autoMSSChecked;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public synchronized void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		disableEnableButtons();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {		
		if(e.getSource() == chkAutoBenefits)
		{
			if(chkAutoBenefits.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_BENEFITS.toString());					
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_BENEFITS.toString());			
			}		
		}else if(e.getSource() == chkAutoContractsAndEnroll)
		{
			if(chkAutoContractsAndEnroll.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT.toString());								
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT.toString());			
			}
		}else if(e.getSource() == chkAutoContributions)
		{
			if(chkAutoContributions.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRIBUTIONS.toString());								
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_CONTRIBUTIONS.toString());			
			}		
		}else if(e.getSource() == chkAutoDeath)
		{
			if(chkAutoDeath.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_DEATH.toString());								
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_DEATH.toString());			
			}		
		}else if(e.getSource() == chkAutoFinancials)
		{
			if(chkAutoFinancials.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_FINANCIALS.toString());			
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_FINANCIALS.toString());			
			}		
		}else if(e.getSource() == chkAutoGeneral)
		{
			if(chkAutoGeneral.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_GENERAL.toString());								
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_GENERAL.toString());			
			}		
		}else if(e.getSource() == chkAutoHealth)
		{
			if(chkAutoHealth.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_HEALTH.toString());						
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_HEALTH.toString());			
			}		
		}else if(e.getSource() == chkAutoInterfaces)
		{
			if(chkAutoInterfaces.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_INTERFACES.toString());							
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_INTERFACES.toString());			
			}		
		}else if(e.getSource() == chkAutoJLRS)
		{
			if(chkAutoJLRS.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_JLRS.toString());						
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_JLRS.toString());			
			}		
		}else if(e.getSource() == chkAutoMSS)
		{
			if(chkAutoMSS.isSelected())
			{
				addAutoFunctGroupScriptsToTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_MSS.toString());						
			}else
			{				
				removeAutoFunctGroupScriptsFromTestSet(AutoFunctionalGroup.AUTO_FUNCT_GRP_MSS.toString());			
			}		
		}else if(e.getSource() == chkBenefits)
		{
			if(chkBenefits.isSelected())
			{
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_BENEFITS.toString());								
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_BENEFITS.toString());			
			}		
		}else if(e.getSource() == chkContractsAndEnroll)
		{
			if(chkContractsAndEnroll.isSelected())
			{
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString());							
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_CONTRACTS_ENROLLMENT.toString());			
			}		
		}else if(e.getSource() == chkContributions)
		{
			if(chkContributions.isSelected())
			{
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString());								
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_CONTRIBUTIONS.toString());			
			}		
		}else if(e.getSource() == chkFinancials)
		{
			if(chkFinancials.isSelected())
			{
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString());								
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_FINANCIALS.toString());			
			}		
		}else if(e.getSource() == chkGeneral)
		{
			if(chkGeneral.isSelected())
			{			
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_GENERAL.toString());								
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_GENERAL.toString());			
			}		
		}else if(e.getSource() == chkHealth)
		{
			if(chkHealth.isSelected())
			{			
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_HEALTH.toString());								
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_HEALTH.toString());			
			}		
		}else if(e.getSource() == chkMSS)
		{
			if(chkMSS.isSelected())
			{
				addFunctGroupScriptsToTestSet(FunctionalGroup.FUNCT_GRP_MSS.toString());						
			}else
			{				
				removeFunctGroupScriptsFromTestSet(FunctionalGroup.FUNCT_GRP_MSS.toString());			
			}		
		}
		disableEnableButtons();
	}
	
	/**
	 * Gets the scaled value.
	 *
	 * @param oldMin the old min
	 * @param oldMax the old max
	 * @param newMin the new min
	 * @param newMax the new max
	 * @param oldValue the old value
	 * @return the scaled value
	 */
	private int getScaledValue(int oldMin, int oldMax, int newMin, int newMax, int oldValue) 
	{
		int newValue;
        int newRange;
		int oldRange = (oldMax - oldMin);
        if (oldRange == 0)
            newValue = newMin;
        else
        {
            newRange = (newMax - newMin);  
            newValue = (((oldValue - oldMin) * newRange) / oldRange) + newMin;
        }
        return newValue;
	}
	
	/**
	 * Start create test set.
	 */
	private void startCreateTestSet() {
        Thread testSetCreator = new Thread(new Runnable() {
            @Override
			public void run() {
            	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            	int i = 0;                                
                String url = almServerConfig.getURL();
        		String domain = almServerConfig.getAlmDomain();
        		String project = almServerConfig.getAlmProject();
        		String username = almServerConfig.getUsername();
        		String password = almServerConfig.getDecryptedCredential();
        		String path = System.getProperty("java.library.path");
    			System.out.println("Library Path: " + path);
    			log.debug("Library Path: " + path);
                
                    try {
                        ITDConnection itd = ClassFactory.createTDConnection();
            			itd.initConnectionEx(url);
            			System.out.println("***** QC Connection Status: " + itd.connected() + " *****");
            			log.debug("***** QC Connection Status: " + itd.connected() + " *****");
            			itd.connectProjectEx(domain,project,username,password);
            						
            			ITestSetTreeManager treeManager = itd.testSetTreeManager().queryInterface(ITestSetTreeManager.class);
            			ITestSetFolder baseFolder = treeManager.nodeByPath("Root\\Regression Suite - Automation").queryInterface(ITestSetFolder.class);
            			String release = "v" + workInProgressTestSet.getRelease();
            			try{
            				ISysTreeNode child = baseFolder.findChildNode(release);
            			}catch(Exception e)
            			{
            				baseFolder.addNode(release);
            			}
            									
            			ITestSetFolder testSetFolder = treeManager.nodeById(baseFolder.findChildNode(release).nodeID()).queryInterface(ITestSetFolder.class);
            			ITestSetFactory factory = testSetFolder.testSetFactory().queryInterface(ITestSetFactory.class);
            			ITestSet testSet = factory.addItem(new Variant(Variant.Type.VT_NULL)).queryInterface(ITestSet.class);
            			testSet.name(workInProgressTestSet.getTestSetName());
            			testSet.status("Open");
            			try{
            				testSet.post();            			
            			}catch(Exception e)
            			{
            				//Display Dialog and return;
            				testSet = null;
            				System.out.println("*** Test Set Already Exists With Name = " + workInProgressTestSet.getTestSetName() + ". ****");
            				log.error("*** Test Set Already Exists With Name = " + workInProgressTestSet.getTestSetName() + ". ****");
            				log.trace("Error (General Exception): ", e);
            				JOptionPane.showMessageDialog (mainFrame, "<html>A Test Set already exists in ALM with the name:\n\n<b>" + workInProgressTestSet.getTestSetName() + "</b>\n\nPlease change the Release or Round, or delete the test set in ALM.\nNote: This test set does not exist in the Job Scheduler database, but does exist in ALM.", "Duplicate Test Set Name in ALM", JOptionPane.ERROR_MESSAGE);
            				glassPane.setVisible(false);
                            glassPane.setProgress(0);
                            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            workInProgressTestSet.getTests();
                            AutomationTestSet.delete(workInProgressTestSet);
                            workInProgressTestSet.setTestSetId(0);
                            almTestSetPersisted=false;
                            Thread.currentThread().interrupt();
                            return;
            			}            			
            			
            			testSet.unLockObject();
            			
            			i++;
            			glassPane.setProgress(getScaledValue(1, workInProgressTestSet.getTests().size() + 1, 1, 100, i));
            			for(AutomationTest test : workInProgressTestSet.getTests())
            			{
            				System.out.println("Current Test being added to Test Set: " + test.getTestName());
            				log.debug("Current Test being added to Test Set: " + test.getTestName());            				
            				try{
	            				ITestFactory sTestFactory = (itd.testFactory()).queryInterface(ITestFactory.class);
	            				ITest iTest = (sTestFactory.item(test.getQcTestId())).queryInterface(ITest.class);            				
	            				IBaseFactory testFactory = testSet.tsTestFactory().queryInterface(IBaseFactory.class);
	            				testFactory.addItem(iTest);
	            				//Make sure test is 'Ready to Run' 
	            				if(!iTest.field("TS_STATUS").equals("Ready to Run"))
	            				{	            					
	            					for(AutomationTest oTest : originalTestSet.getTestsAsList())
	            					{
	            						if(oTest.getTestName().equals(test.getTestName()))
	            						{
	            							oTest.setExecutionStatus(ExecutionStatus.BLOCKED);
	            							break;
	            						}
	            					}	            					
	            				}	            				
            				}catch(Exception e)
            				{
            					e.printStackTrace();
            					JOptionPane.showMessageDialog (mainFrame, "<html>Error adding tests to test set:  <b>" + workInProgressTestSet.getTestSetName() + "</b>\n\nPlease Please check to make sure none of the tests are checked out\nand also manually delete the test set created in ALM.", "Error Adding Test To Test Set Name In ALM", JOptionPane.ERROR_MESSAGE);
                    			workInProgressTestSet.setRunStatus(TestSetRunStatus.DRAFT.toString());
                    			try {
        							workInProgressTestSet.save();
        						} catch (Exception e1) {
        							e1.printStackTrace();
        						} 
                    			Thread.currentThread().interrupt();
                    			return;
            				}
            				i++;
            				glassPane.setProgress(getScaledValue(1, workInProgressTestSet.getTests().size() + 1, 1, 100, i));
            			}
            			itd.disconnectProject();
            		}catch(Exception e)
            		{
            			e.printStackTrace();
            			log.error("Error (Uncknown): ", e);            			
            		}                   
                glassPane.setVisible(false);
                glassPane.setProgress(0);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                almTestSetPersisted=true;
                canSaveALM=false;
                canSave=false;
                changedSinceLastSave=false;
                setPanelComponentsToReadOnly();
            }
        });               
        testSetCreator.start();          
    }
	

	/**
	 * The Class ProgressGlassPane.
	 */
	class ProgressGlassPane extends JComponent {
	    
    	/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		
		/** The Constant BAR_WIDTH. */
		private static final int BAR_WIDTH = 200;
	    
    	/** The Constant BAR_HEIGHT. */
    	private static final int BAR_HEIGHT = 10;
	    
	    /** The text color. */
    	private final Color TEXT_COLOR = new Color(0x333333);
	    
    	/** The border color. */
    	private final Color BORDER_COLOR = new Color(0x333333);
	    
	    /** The gradient fractions. */
    	private final float[] GRADIENT_FRACTIONS = new float[] {
	        0.0f, 0.499f, 0.5f, 1.0f
	    };
	    
    	/** The gradient colors. */
    	private final Color[] GRADIENT_COLORS = new Color[] {
	        Color.GRAY, Color.DARK_GRAY, Color.BLACK, Color.GRAY
	    };
	    
    	/** The GRADIEN t_ colo r2. */
    	private final Color GRADIENT_COLOR2 = Color.WHITE;
	    
    	/** The GRADIEN t_ colo r1. */
    	private final Color GRADIENT_COLOR1 = Color.GRAY;

	    /** The message. */
    	private String message = "Creating Test Set in ALM...";
	    
    	/** The progress. */
    	private int progress = 0;
	    
	    /**
    	 * Instantiates a new progress glass pane.
    	 */
    	public ProgressGlassPane() {
	        setBackground(Color.WHITE);
	        setFont(new Font("Default", Font.BOLD, 16));
	        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    }

	    /**
    	 * Gets the progress.
    	 *
    	 * @return the progress
    	 */
    	public int getProgress() {
	        return progress;
	    }

	    /**
    	 * Sets the progress.
    	 *
    	 * @param progress the new progress
    	 */
    	public void setProgress(int progress) {
	        int oldProgress = this.progress;
	        this.progress = progress;
	        
	        // computes the damaged area
	        FontMetrics metrics = getGraphics().getFontMetrics(getFont()); 
	        int w = (int) (BAR_WIDTH * (oldProgress / 100.0f));
	        int x = w + (getWidth() - BAR_WIDTH) / 2;
	        int y = (getHeight() - BAR_HEIGHT) / 2;
	        y += metrics.getDescent() / 2;
	        
	        w = (int) (BAR_WIDTH * (progress / 100.0f)) - w;
	        int h = BAR_HEIGHT;
	        
	        repaint(x, y, w, h);
	    }
	    
	    /* (non-Javadoc)
    	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    	 */
    	@Override
	    protected void paintComponent(Graphics g) {
	        // enables anti-aliasing
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        // gets the current clipping area
	        Rectangle clip = g.getClipBounds();
	        
	        // sets a 65% translucent composite
	        AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.65f);
	        Composite composite = g2.getComposite();
	        g2.setComposite(alpha);
	        
	        // fills the background
	        g2.setColor(getBackground());
	        g2.fillRect(clip.x, clip.y, clip.width, clip.height);
	        
	        // centers the progress bar on screen
	        FontMetrics metrics = g.getFontMetrics();        
	        int x = (getWidth() - BAR_WIDTH) / 2;
	        int y = (getHeight() - BAR_HEIGHT - metrics.getDescent()) / 2;
	        
	        // draws the text
	        g2.setColor(TEXT_COLOR);
	        g2.drawString(message, x, y);
	        
	        // goes to the position of the progress bar
	        y += metrics.getDescent();
	        
	        // computes the size of the progress indicator
	        int w = (int) (BAR_WIDTH * (progress / 100.0f));
	        int h = BAR_HEIGHT;
	        
	        // draws the content of the progress bar
	        Paint paint = g2.getPaint();
	        
	        // bar's background
	        Paint gradient = new GradientPaint(x, y, GRADIENT_COLOR1,
	                x, y + h, GRADIENT_COLOR2);
	        g2.setPaint(gradient);
	        g2.fillRect(x, y, BAR_WIDTH, BAR_HEIGHT);
	        
	        // actual progress
	        gradient = new LinearGradientPaint(x, y, x, y + h,
	                GRADIENT_FRACTIONS, GRADIENT_COLORS);
	        g2.setPaint(gradient);
	        g2.fillRect(x, y, w, h);
	        
	        g2.setPaint(paint);
	        
	        // draws the progress bar border
	        g2.drawRect(x, y, BAR_WIDTH, BAR_HEIGHT);
	        
	        g2.setComposite(composite);
	    }		
	}
}
