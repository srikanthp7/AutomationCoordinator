package com.gc;

import com4j.*;

/**
 * For HP use. The collection of QPM KPI Types.
 */
@IID("{0F933F7B-03C9-4FC5-AD8D-CC8D21FB08C8}")
public interface ICustomizationKPITypes2 extends com.gc.ICustomizationKPITypes {
	// Methods:
	/**
	 * <p>
	 * Renames an ICustomizationKPIType in the internal KPI Type map.
	 * </p>
	 * 
	 * @param oldName
	 *            Mandatory java.lang.String parameter.
	 * @param newName
	 *            Mandatory java.lang.String parameter.
	 */

	@DISPID(6)
	// = 0x6. The runtime will prefer the VTID if present
	@VTID(12)
	void updateInternalKPITypeMap(java.lang.String oldName,
			java.lang.String newName);

	// Properties:
}
