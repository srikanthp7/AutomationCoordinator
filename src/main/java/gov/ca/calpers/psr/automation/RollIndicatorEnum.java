package gov.ca.calpers.psr.automation;

import java.util.HashMap;
import java.util.Map;

import gov.ca.calpers.psr.automation.interfaces.Enumable;


/**
 * The Enum RollIndicatorEnum.
 * This ENUM is reflective of the LOV_ROLL_IND table
 */
public enum RollIndicatorEnum implements Enumable{
	
	/** The pre roll. */
	PRE_ROLL ("PRE"),
	
	/** The roll. */
	ROLL ("ROL"),
	
	/** The post roll. */
	POST_ROLL ("PST"),
	
	/** The none. */
	NONE (null);

	/** The code. */
	private String code;

    /** The code to status mapping. */
    private static Map<String, RollIndicatorEnum> codeToStatusMapping = new HashMap<String, RollIndicatorEnum>();
    static {
        for (RollIndicatorEnum s : values()) {
            codeToStatusMapping.put(s.code, s);
        }
    }

    /**
     * Instantiates a new roll indicator enum.
     *
     * @param code the code
     */
    private RollIndicatorEnum(String code) {
        this.code = code;
    }

    /**
     * From value.
     *
     * @param value the value
     * @return the roll indicator enum
     */
    public static RollIndicatorEnum fromValue(String value) {
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
