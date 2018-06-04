package com.gc;

import com4j.*;

/**
 * Services to manage component parameters.
 */
@IID("{E90417D6-8F1B-4D48-BCC0-E27D5E4C3813}")
public interface IComponentParamFactory extends com.gc.IBaseFactory {
	// Methods:
	/**
	 * <p>
	 * Updates param factory data when creation source changes
	 * </p>
	 * 
	 * @param nParentCompId
	 *            Mandatory int parameter.
	 */

	@DISPID(8)
	// = 0x8. The runtime will prefer the VTID if present
	@VTID(16)
	void updateExistingComponentParamFactory(int nParentCompId);

	// Properties:
}
