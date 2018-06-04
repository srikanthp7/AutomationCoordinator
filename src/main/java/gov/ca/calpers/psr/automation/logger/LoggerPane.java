package gov.ca.calpers.psr.automation.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextPane;

/**
 * The Class LoggerPane.
 */
public class LoggerPane {
    
    /** The Constant DATE_TIME_FORMAT. */
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM-DD HH:mm:ss");
    
    /** The writable pane. */
    private static JTextPane writablePane;

    /**
     * Initialize.
     *
     * @param writer the writer
     */
    public synchronized static void initialize(JTextPane writer) {
        writablePane = writer;
        writablePane.setContentType("text/html");
    }

    /**
     * Debug.
     *
     * @param message the message
     */
    public synchronized static void debug(String message) {
        writablePane.setText("<font color=\"#778899\">" + DATE_TIME_FORMAT.format(new Date()) + " - DEBUG: " + message
                + "</font><br />" + getPreviousText());
    }

    /**
     * Info.
     *
     * @param message the message
     */
    public synchronized static void info(String message) {
        writablePane
                .setText(DATE_TIME_FORMAT.format(new Date()) + " - INFO: " + message + "<br />" + getPreviousText());
    }

    /**
     * Error.
     *
     * @param message the message
     */
    public synchronized static void error(String message) {
        writablePane.setText("<font color=\"#B22222\">" + DATE_TIME_FORMAT.format(new Date()) + " - ERROR: " + message
                + "</font><br />" + getPreviousText());
    }

    /**
     * Gets the previous text.
     *
     * @return the previous text
     */
    public static String getPreviousText() {
        String lastText = writablePane.getText();
        if (lastText.length() > 100000) {
            lastText = lastText.substring(0, 100000);
        }
        return lastText;
    }
}
