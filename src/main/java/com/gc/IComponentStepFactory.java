package com.gc;

import com4j.*;

/**
 * Services to manage component steps.
 */
@IID("{685BEC35-0493-4D13-9763-E43B3BE47C02}")
public interface IComponentStepFactory extends com.gc.IBaseFactory {
	// Methods:
	/**
	 * <p>
	 * Updates step factory data when creation source changes
	 * </p>
	 * 
	 * @param nParentFacetId
	 *            Mandatory int parameter.
	 * @param vbIsCreatedDirectlyFromComponent
	 *            Mandatory boolean parameter.
	 */

	@DISPID(8)
	// = 0x8. The runtime will prefer the VTID if present
	@VTID(16)
	void updateExistingComponentStepFactory(int nParentFacetId,
			boolean vbIsCreatedDirectlyFromComponent);

	// Properties:
}
