/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.TablePacker;
import gov.ca.calpers.psr.automation.pojo.TimeUtils;

/**
 * The Class ScenariosViewLite.
 *
 * @author burban
 */
public class ScenariosViewLite extends JXTable implements Observer{

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
	
	
	
	/**
	 * Instantiates a new scenarios view lite.
	 *
	 * @param testSet the test set
	 * @param tests the tests
	 */
	public ScenariosViewLite(AutomationTestSet testSet, CopyOnWriteArrayList<AutomationTest> tests)
	{
		super();
        theTestSet= testSet;
        theTestSet.addObserver(this);
        this.tests = tests;
        setModel(new DefaultTableModel(null, title));        
        setShowGrid(false, false);
        addHighlighter(HighlighterFactory.createSimpleStriping());
        setEditable(false);
        packAll();
        getTableHeader().setReorderingAllowed(false);
        redrawData();

	}
	
    /**
     * Redraw data.
     */
    public synchronized void redrawData() {
    	SwingUtilities.invokeLater(new Runnable(){@Override
		public void run(){
    	    //Update the model here
    		DefaultTableModel model = (DefaultTableModel) getModel();
    		if(model.getRowCount() > 0)
        	{
        		System.out.println("Removing all rows for re-draw.");    		
        		int rowCount = model.getRowCount();
        		for (int i = rowCount - 1; i > -1; i--) {
        			model.removeRow(i);
        	    }
        	}
    		
				for(AutomationTest test: tests)
	        	{	        		
        			Vector<Object> rowData = new Vector<Object>();                	
                	rowData.add(test.getTestName());    	
                	rowData.add((test.getTestResult().getStartTime() > 0 ? DATE_TIME_FORMAT.format(test.getTestResult().getStartTime()) : ""));
                	rowData.add(test.getTestResult().getFinalDuration() > 0 ? TimeUtils.calculateDurationInHoursMinutesSeconds(test.getTestResult().getFinalDuration()): "0:00:00");         
                	rowData.add(test.getExecutionStatusHTML());
                    rowData.add(String.valueOf(test.getRunCount()));
                    rowData.add(test.getClient());
                    model.addRow(rowData);	        	
	        	}
    	}});
    	        	
    }
        
	 /**
 	 * Sets the tests list.
 	 *
 	 * @param tests the new tests list
 	 */
	public void setTestsList(CopyOnWriteArrayList<AutomationTest> tests)
	 {
		 this.tests = tests;
		 redrawData();
	 }


	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		redrawData();
	}

}
