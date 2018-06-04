package gov.ca.calpers.psr.automation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Enum ExecutionStatus.
 */
public enum ExecutionStatus implements Serializable {
    
 /** The not run. */
 NOT_RUN(1), 
 /** The in progress. */
 IN_PROGRESS(10), 
 /** The passed. */
 PASSED(20), 
 /** The manually passed. */
 MANUALLY_PASSED(30), 
 /** The failed. */
 FAILED(40), 
 /** The blocked. */
 BLOCKED(50);
    
    /** The code. */
    private int code;

    /** The code to status mapping. */
    private static Map<Integer, ExecutionStatus> codeToStatusMapping = new HashMap<Integer, ExecutionStatus>();
    static {
        for (ExecutionStatus s : values()) {
            codeToStatusMapping.put(s.code, s);
        }
    }

    /**
     * Instantiates a new execution status.
     *
     * @param code the code
     */
    private ExecutionStatus(int code) {
        this.code = code;
    }

    /**
     * From value.
     *
     * @param value the value
     * @return the execution status
     */
    public static ExecutionStatus fromValue(int value) {
        return codeToStatusMapping.get(value);
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        return code;
    }
}
