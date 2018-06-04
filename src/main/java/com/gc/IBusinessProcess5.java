package com.gc;

import com4j.*;

/**
 * Represents a single Business Process Test.
 */
@IID("{4F006939-1B8E-4F93-B22A-5CFFF8BEC098}")
public interface IBusinessProcess5 extends com.gc.IBusinessProcess4 {
	// Methods:
	/**
	 * <p>
	 * Generates an XML description of the BPT.
	 * </p>
	 * 
	 * @param showDesignSteps
	 *            Mandatory boolean parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(136)
	// = 0x88. The runtime will prefer the VTID if present
	@VTID(84)
	java.lang.String generateXMLDescription(boolean showDesignSteps);

	// Properties:
}
