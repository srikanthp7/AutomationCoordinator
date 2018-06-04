package com.gc;

import com4j.*;

/**
 * For HP use. Represents an entity that participate in baselines.
 */
@IID("{9B0DFEB8-0550-44EE-8FB2-7B675009B02F}")
public interface IBaselinedEntity extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * A list of IBaseline objects in which the entity participated.
	 * </p>
	 * <p>
	 * Getter method for the COM property "Baselines"
	 * </p>
	 * 
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(1)
	// = 0x1. The runtime will prefer the VTID if present
	@VTID(7)
	com.gc.IList baselines();

	@VTID(7)
	@ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = { com.gc.IList.class })
	java.lang.Object baselines(int index);

	// Properties:
}
