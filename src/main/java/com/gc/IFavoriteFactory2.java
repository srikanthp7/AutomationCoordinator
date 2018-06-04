package com.gc;

import com4j.*;

/**
 * Services to manage user-defined display settings.
 */
@IID("{C1034F7A-5034-4499-AA9F-348657A5350F}")
public interface IFavoriteFactory2 extends com.gc.IFavoriteFactory {
	// Methods:
	/**
	 * <p>
	 * Convert the filter from XML to text format.
	 * </p>
	 * 
	 * @param filterXml
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(9)
	// = 0x9. The runtime will prefer the VTID if present
	@VTID(17)
	java.lang.String convert(java.lang.String filterXml);

	// Properties:
}
