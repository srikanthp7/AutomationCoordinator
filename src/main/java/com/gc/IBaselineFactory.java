package com.gc;

import com4j.*;

/**
 * For HP use. Services for managing baselines.
 */
@IID("{3D7543C1-9B84-457E-9A1A-CCB7D20663FA}")
public interface IBaselineFactory extends com.gc.IBaseFactoryEx {
	// Methods:
	/**
	 * <p>
	 * A list of IBaselineItem objects in which the entity participated.
	 * </p>
	 * 
	 * @param pEntity
	 *            Mandatory com.gc.IBaseField parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(9)
	// = 0x9. The runtime will prefer the VTID if present
	@VTID(17)
	com.gc.IList baselineItems(com.gc.IBaseField pEntity);

	// Properties:
}
