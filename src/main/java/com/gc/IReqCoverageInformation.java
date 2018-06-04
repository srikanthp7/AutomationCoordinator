package com.gc;

import com4j.*;

/**
 * Services for managing requirement coverage information.
 */
@IID("{860CDC34-7616-4A2B-9005-9FBEF00FEF68}")
public interface IReqCoverageInformation extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * For HP use. Returns the amount of covering enitities according to their
	 * status.
	 * </p>
	 * 
	 * @param filter
	 *            Mandatory java.lang.String parameter.
	 * @param coverageByCycles
	 *            Mandatory com.gc.IList parameter.
	 * @return Returns a value of type java.lang.Object
	 */

	@DISPID(1)
	// = 0x1. The runtime will prefer the VTID if present
	@VTID(7)
	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object getCoverageEntitiesSummary(java.lang.String filter,
			com.gc.IList coverageByCycles);

	/**
	 * <p>
	 * Sets cycle context for the ReqCoverageFactory to be used in NewList.
	 * </p>
	 * 
	 * @param coverageByCycles
	 *            Mandatory com.gc.IList parameter.
	 */

	@DISPID(2)
	// = 0x2. The runtime will prefer the VTID if present
	@VTID(8)
	void setCyclesContext(com.gc.IList coverageByCycles);

	/**
	 * <p>
	 * Resets cycle context.
	 * </p>
	 */

	@DISPID(3)
	// = 0x3. The runtime will prefer the VTID if present
	@VTID(9)
	void resetCyclesContext();

	// Properties:
}
