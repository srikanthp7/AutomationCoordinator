package com.gc;

import com4j.*;

/**
 * Services for managing coverage by test configurations.
 */
@IID("{13C5BCEB-E381-447B-8262-89FAA0A6DB99}")
public interface IConfigurationCoverageFactory extends com.gc.IBaseFactoryEx {
	// Methods:
	/**
	 * <p>
	 * Sets cycle context to be used in NewList.
	 * </p>
	 * 
	 * @param coverageByCycles
	 *            Mandatory com.gc.IList parameter.
	 */

	@DISPID(9)
	// = 0x9. The runtime will prefer the VTID if present
	@VTID(17)
	void setCyclesContext(com.gc.IList coverageByCycles);

	/**
	 * <p>
	 * Resets cycle context.
	 * </p>
	 */

	@DISPID(10)
	// = 0xa. The runtime will prefer the VTID if present
	@VTID(18)
	void resetCyclesContext();

	// Properties:
}
