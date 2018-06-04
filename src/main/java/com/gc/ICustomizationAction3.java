package com.gc;

import com4j.*;

/**
 * Represents a type of user action. Actions are listed in the AC_ACTION_NAME
 * field of the Actions table.
 */
@IID("{9F69B635-7345-48B2-9DCB-A5C94F4CA568}")
public interface ICustomizationAction3 extends com.gc.ICustomizationAction2 {
	// Methods:
	/**
	 * <p>
	 * Returns the permission type according to the group mask.
	 * </p>
	 * <p>
	 * Getter method for the COM property "ActionPermissionType"
	 * </p>
	 * 
	 * @param userMask
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type int
	 */

	@DISPID(13)
	// = 0xd. The runtime will prefer the VTID if present
	@VTID(21)
	int actionPermissionType(java.lang.String userMask);

	// Properties:
}
