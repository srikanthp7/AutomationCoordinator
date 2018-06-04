/**
 * 
 */
package gov.ca.calpers.psr.automation.directed.graph;

import gov.ca.calpers.psr.automation.GeneralEnumMapUserType;

/**
 * The Class EdgeTypeUserType.
 *
 * @author burban
 */
public class EdgeTypeUserType extends GeneralEnumMapUserType {
	
	/**
	 * Instantiates a new edge type user type.
	 *
	 * @param enumClass the enum class
	 */
	protected EdgeTypeUserType(Class enumClass) {
		super(enumClass);
		// TODO Auto-generated constructor stub
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     *
     */
    public EdgeTypeUserType() {
        super(EdgeTypeEnum.class);
    }
}
