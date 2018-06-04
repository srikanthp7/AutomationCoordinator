package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.TablePacker;
import gov.ca.calpers.psr.automation.UnitOfWork;
import gov.ca.calpers.psr.automation.directed.graph.Edge;
import gov.ca.calpers.psr.automation.interfaces.NodeValueChanged;
import gov.ca.calpers.psr.automation.pojo.TimeUtils;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.LogManager;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * The Class ScenariosView.
 */
public class ScenariosView extends JXTable implements NodeValueChanged, Observer  {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant DATE_TIME_FORMAT. */
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss");
    
    /** The title. */
    private static String[] title = { "Test Name", "Start Time", "Duration", "Status", "Run Count", "Client" };
    
    /** The test set. */
    private final AutomationTestSet theTestSet;
    
    /** The tests. */
    private CopyOnWriteArrayList<AutomationTest> tests;
    
    /** The packer. */
    private TablePacker packer = null;
    
    private TableColumnAdjuster tca;
    
    /** The all work. */
    private CopyOnWriteArrayList<UnitOfWork> allWork = null;
      
    private static org.apache.logging.log4j.Logger log =  LogManager.getLogger(ScenariosView.class);
    
    private static int TEST_NAME_COLUMN = 0;
    
    private static int START_TIME_COLUMN = 1;
    
    private static int DURATION_COLUMN = 2;
    
    private static int STATUS_COLUMN = 3;
    
    private static int RUN_COUNT_COLUMN = 4;
    
    private static int CLIENT_COLUMN = 5;
    
	/**
	 * Instantiates a new scenarios view.
	 *
	 * @param testSet the test set
	 * @param tests the tests
	 */
	public ScenariosView(AutomationTestSet testSet, CopyOnWriteArrayList<AutomationTest> tests) {
        super();
        theTestSet= testSet;
        theTestSet.addObserver(this);
        this.tests = tests;
        setModel(new DefaultTableModel(null, title));        
        setShowGrid(false, false);
        addHighlighter(HighlighterFactory.createSimpleStriping());
        setEditable(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        this.getColumnModel().getColumn(RUN_COUNT_COLUMN).setCellRenderer(centerRenderer);
        packAll();
        getTableHeader().setReorderingAllowed(false);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tca = new TableColumnAdjuster(this);
		packColumns();
        redrawData();     
        // Disable sorting of the columns due to html tags causing sorting issues.
        this.setAutoCreateRowSorter(false);
        this.setRowSorter(new TableRowSorter(this.getModel()) {
            @Override
            public boolean isSortable(int column) {
                return false;
            }
        });
    }

    /**
     * Redraw data.
     */
    public synchronized void redrawData() {    	
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Update the model here
				try {
					
					DefaultTableModel model = (DefaultTableModel) getModel();
					if (model.getRowCount() > 0) {
						System.out.println("Removing all rows for re-draw.");
						int rowCount = model.getRowCount();
						for (int i = rowCount - 1; i > -1; i--) {
							model.removeRow(i);
						}
					}

					if (allWork != null && !allWork.isEmpty()) {
						for (AutomationTest test : tests) {
							boolean waitingOnDependencies = false;
							if (test.getExecutionStatus().equals(
									ExecutionStatus.NOT_RUN)) {
								for (UnitOfWork work : allWork) {
									if (work.getAutoTest().getTestName()
											.equals(test.getTestName())) {
										for (Edge edge : work.getInEdges()) {
											if (!edge.isDisabled()) {
												AutomationTest aTest = (AutomationTest) edge
														.getChildNode()
														.getUserObject();

												if (aTest
														.getExecutionStatus()
														.equals(ExecutionStatus.NOT_RUN)
														|| aTest.getExecutionStatus()
																.equals(ExecutionStatus.IN_PROGRESS)) {
													waitingOnDependencies = true;
													break;
												}
											}
										}
									}
								}
							}
							if (waitingOnDependencies) {
								Vector<Object> rowData = new Vector<Object>();
								String preHtml = "<html><font color='#808080'><i>";
								String postHtml = "</i></color></font></html>";
								rowData.add(preHtml + test.getTestName()
										+ postHtml);
								rowData.add(preHtml
										+ (test.getTestResult().getStartTime() > 0 ? DATE_TIME_FORMAT
												.format(test.getTestResult()
														.getStartTime())
												: "0:00:00") + postHtml);
								rowData.add(preHtml
										+ (test.getTestResult()
												.getFinalDuration() > 0 ? TimeUtils
												.calculateDurationInHoursMinutesSeconds(test
														.getTestResult()
														.getFinalDuration())
												: "0:00:00") + postHtml);
								rowData.add(preHtml + test.getExecutionStatus()
										+ postHtml);
								rowData.add(preHtml
										+ String.valueOf(test.getRunCount())
										+ postHtml);
								rowData.add(preHtml + test.getClient()
										+ postHtml);
								model.addRow(rowData);
							} else {
								Vector<Object> rowData = new Vector<Object>();
								rowData.add(test.getTestName());
								rowData.add((test.getTestResult()
										.getStartTime() > 0 ? DATE_TIME_FORMAT
										.format(test.getTestResult()
												.getStartTime()) : "0:00:00"));
								rowData.add(test.getTestResult()
										.getFinalDuration() > 0 ? TimeUtils
										.calculateDurationInHoursMinutesSeconds(test
												.getTestResult()
												.getFinalDuration())
										: "0:00:00");
								rowData.add(test.getExecutionStatusHTML());
								rowData.add(String.valueOf(test.getRunCount()));
								rowData.add(test.getClient());
								model.addRow(rowData);
							}
						}
					} else {
						for (AutomationTest test : tests) {
							Vector<Object> rowData = new Vector<Object>();
							rowData.add(test.getTestName());
							rowData.add((test.getTestResult().getStartTime() > 0 ? DATE_TIME_FORMAT
									.format(test.getTestResult().getStartTime())
									: ""));
							rowData.add(test.getTestResult().getFinalDuration() > 0 ? TimeUtils
									.calculateDurationInHoursMinutesSeconds(test
											.getTestResult().getFinalDuration())
									: "0:00:00");
							rowData.add(test.getExecutionStatusHTML());
							rowData.add(String.valueOf(test.getRunCount()));
							rowData.add(test.getClient());
							model.addRow(rowData);
						}
					}
				} catch (Exception e) {
					log.error("Error (Unknown): ", e);
				}	
				tca.adjustColumns();
			}
		});
    }
    
    /**
     * Sets the all work.
     *
     * @param allWork the allWork to set
     */
	public synchronized void setAllWork(CopyOnWriteArrayList<UnitOfWork> allWork) {
		this.allWork = allWork;
	}
	
	/**
	 * Sets the tests list.
	 *
	 * @param tests the new tests list
	 */
	public synchronized void setTestsList(CopyOnWriteArrayList<AutomationTest> tests)
	{
		this.tests = tests;
		this.redrawData();
	}

    /* (non-Javadoc)
     * @see gov.ca.calpers.psr.automation.interfaces.NodeValueChanged#notifyChanged(java.lang.Object)
     */
    @Override
    public synchronized void notifyChanged(Object associatedNode) {
        UnitOfWork work = (UnitOfWork) associatedNode;
        // find row that match the work
        System.out.println("Inside notifyChanged for ScenariosView.");
        int rowCount = getModel().getRowCount();       
        for (int i = 0; i < rowCount; i++) {
            String testName = getModel().getValueAt(i, 1).toString();
            if (testName.equals(work.getTestName())) {
                // update the data
                // "Start Time", "Duration", "Status", "Run Count", "Client"
                getModel().setValueAt((work.getStartTime() > 0 ? DATE_TIME_FORMAT.format(work.getStartTime()) : ""), i,
                        2);
                if (work.getStartTime() > 0) {
                    if (work.getFinalDuration() > 0) {
                        getModel()
                                .setValueAt((String.valueOf(work.getFinalDuration() / (1000 * 60)) + " min(s)"), i, 3);
                    } else {
                        long duration = System.currentTimeMillis() - work.getStartTime();
                        getModel().setValueAt((String.valueOf(duration / (1000 * 60)) + " min(s)"), i, 3);
                    }
                } else {
                    setValueAt("0:00:00", i, 3);
                }
                getModel().setValueAt(work.getExecutionStatusHTML(), i, 4);
                getModel().setValueAt(String.valueOf(work.getRunCount()), i, 5);
                break;
            }
        }
    }
    
	/**
	 * Pack.
	 *
	 * @param rowsIncluded the rows included
	 * @param distributeExtraArea the distribute extra area
	 */
	public synchronized void pack(int rowsIncluded, boolean distributeExtraArea) {
		packer = new TablePacker(rowsIncluded, true);
		if (isShowing()) {
			packer.pack(this);
			packer = null;
		}
	}
	
	private synchronized void packColumns()
	{
		for (int column = 0; column < this.getColumnCount(); column++)
		{
		    TableColumn tableColumn = this.getColumnModel().getColumn(column);
		    int preferredWidth = tableColumn.getMinWidth();
		    int maxWidth = tableColumn.getMaxWidth();
		 
		    for (int row = 0; row < this.getRowCount(); row++)
		    {
		        TableCellRenderer cellRenderer = this.getCellRenderer(row, column);
		        Component c = this.prepareRenderer(cellRenderer, row, column);
		        int width = c.getPreferredSize().width + this.getIntercellSpacing().width;
		        preferredWidth = Math.max(preferredWidth, width);
		 
		        //  We've exceeded the maximum width, no need to check other rows
		 
		        if (preferredWidth >= maxWidth)
		        {
		            preferredWidth = maxWidth;
		            break;
		        }
		    }
		 
		    tableColumn.setPreferredWidth( preferredWidth );
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JTable#addNotify()
	 */
	@Override
	public synchronized void addNotify() {
		super.addNotify();
		if (packer != null) {
			packer.pack(this);
			packer = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		redrawData();		
	}
}
