/**
 * 
 */
package gov.ca.calpers.psr.automation.directed.graph;

import java.util.HashMap;
import java.util.Map;

import gov.ca.calpers.psr.automation.directed.graph.EdgeTypeEnum;
import gov.ca.calpers.psr.automation.interfaces.Enumable;

/**
 * The Enum EdgeTypeEnum.
 *
 * @author burban
 */
public enum EdgeTypeEnum implements Enumable{
	
	/** The must run. */
	MUST_RUN("R"), 
	/** The must pass. */
	MUST_PASS("P");
	
    /** The code. */
    private String code;

    /** The code to status mapping. */
    private static Map<String, EdgeTypeEnum> codeToStatusMapping = new HashMap<String, EdgeTypeEnum>();
    static {
        for (EdgeTypeEnum s : values()) {
            codeToStatusMapping.put(s.code, s);
        }
    }

    /**
     * Instantiates a new edge type enum.
     *
     * @param code the code
     */
    private EdgeTypeEnum(String code) {
        this.code = code;
    }

    /**
     * From value.
     *
     * @param value the value
     * @return the edge type enum
     */
    public static EdgeTypeEnum fromValue(String value) {
        return codeToStatusMapping.get(value);
    }

    /* (non-Javadoc)
     * @see gov.ca.calpers.psr.automation.interfaces.Enumable#getValue()
     */
    @Override
	public String getValue() {
        return code;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
	public String toString()
    {
    	return code;
    }

	/* (non-Javadoc)
	 * @see gov.ca.calpers.psr.automation.interfaces.Enumable#getEnumFromValue(java.lang.String)
	 */
	@Override
	public Enum getEnumFromValue(String value) {
		// TODO Auto-generated method stub
		return EnumableHelper.getEnumFromValue(this, value, null);
	}

}
