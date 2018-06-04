package gov.ca.calpers.psr.automation;

/**
 * The Enum AutoFunctionalGroup.
 */
public enum AutoFunctionalGroup {
	
	/** The auto funct grp benefits. */
	AUTO_FUNCT_GRP_BENEFITS ("BEN"),
	
	/** The auto funct grp contracts enrollment. */
	AUTO_FUNCT_GRP_CONTRACTS_ENROLLMENT ("CNE"),
	
	/** The auto funct grp contributions. */
	AUTO_FUNCT_GRP_CONTRIBUTIONS ("CTR"), 
	
	/** The auto funct grp death. */
	AUTO_FUNCT_GRP_DEATH ("DTH"), 
	
	/** The auto funct grp financials. */
	AUTO_FUNCT_GRP_FINANCIALS ("FIN"), 
	
	/** The auto funct grp general. */
	AUTO_FUNCT_GRP_GENERAL ("GEN"), 
	
	/** The auto funct grp health. */
	AUTO_FUNCT_GRP_HEALTH ("HLT"), 
	
	/** The auto funct grp interfaces. */
	AUTO_FUNCT_GRP_INTERFACES ("INT"), 
	
	/** The auto funct grp jlrs. */
	AUTO_FUNCT_GRP_JLRS ("JLR"), 
	
	/** The auto funct grp mss. */
	AUTO_FUNCT_GRP_MSS ("MSS");
	
	/** The name. */
	private final String name;
	
	/**
	 * Instantiates a new auto functional group.
	 *
	 * @param s the s
	 */
	private AutoFunctionalGroup(String s) {
		name = s;
	}

	/**
	 * Equals name.
	 *
	 * @param otherName the other name
	 * @return true, if successful
	 */
	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	
}
