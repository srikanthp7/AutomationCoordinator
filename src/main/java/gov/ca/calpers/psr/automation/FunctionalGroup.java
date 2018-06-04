package gov.ca.calpers.psr.automation;

// TODO: Auto-generated Javadoc
/**
 * The Enum FunctionalGroup.
 */
public enum FunctionalGroup {
	
	/** The funct grp benefits. */
	FUNCT_GRP_BENEFITS ("BEN"),	
	
	/** The funct grp contracts enrollment. */
	FUNCT_GRP_CONTRACTS_ENROLLMENT ("CNE"), 
	
	/** The funct grp contributions. */
	FUNCT_GRP_CONTRIBUTIONS ("CTR"),	 
	
	/** The funct grp financials. */
	FUNCT_GRP_FINANCIALS ("FIN"), 
	
	/** The funct grp general. */
	FUNCT_GRP_GENERAL ("GEN"), 
	
	/** The funct grp health. */
	FUNCT_GRP_HEALTH ("HLT"),
	
	/** The funct grp mss. */
	FUNCT_GRP_MSS ("MSS"); 
	
	/** The name. */
	private final String name;
	
	/**
	 * Instantiates a new functional group.
	 *
	 * @param s the s
	 */
	private FunctionalGroup(String s) {
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
