package com.gc;

import com4j.*;

/**
 * Additional properties of the customization field.
 */
@IID("{4AB48AF3-DF92-438C-A354-143DBF378088}")
public interface ICustomizationField6 extends com.gc.ICustomizationField5 {
	// Methods:
	/**
	 * <p>
	 * Marks field as supporting/not supporting HTML.
	 * </p>
	 * <p>
	 * Getter method for the COM property "OutputSanitizationType"
	 * </p>
	 * 
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(56)
	// = 0x38. The runtime will prefer the VTID if present
	@VTID(102)
	java.lang.String outputSanitizationType();

	/**
	 * <p>
	 * Marks field as supporting/not supporting HTML.
	 * </p>
	 * <p>
	 * Setter method for the COM property "OutputSanitizationType"
	 * </p>
	 * 
	 * @param pSanitizationType
	 *            Mandatory java.lang.String parameter.
	 */

	@DISPID(56)
	// = 0x38. The runtime will prefer the VTID if present
	@VTID(103)
	void outputSanitizationType(java.lang.String pSanitizationType);

	// Properties:
}
