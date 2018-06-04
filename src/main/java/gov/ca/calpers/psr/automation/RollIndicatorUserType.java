/**
 * 
 */
package gov.ca.calpers.psr.automation;

/**
 * The Class RollIndicatorUserType.
 *
 * @author burban
 */
public class RollIndicatorUserType extends GeneralEnumMapUserType {
	
	/**
	 * Instantiates a new roll indicator user type.
	 *
	 * @param enumClass the enum class
	 */
	protected RollIndicatorUserType(Class enumClass) {
		super(enumClass);
		// TODO Auto-generated constructor stub
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     *
     */
    public RollIndicatorUserType() {
        super(RollIndicatorEnum.class);

    }
}