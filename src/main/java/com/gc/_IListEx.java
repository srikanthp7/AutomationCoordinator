package com.gc;

import com4j.*;

/**
 * For HP use. _IListEx Interface.
 */
@IID("{DD5DE9AF-BAEF-4FA0-8555-DED76D4C9D2F}")
public interface _IListEx extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * For HP use.
	 * </p>
	 * 
	 * @return Returns a value of type boolean
	 */

	@DISPID(1)
	// = 0x1. The runtime will prefer the VTID if present
	@VTID(7)
	boolean isIdListSupported();

	/**
	 * <p>
	 * For HP use.
	 * </p>
	 * 
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(2)
	// = 0x2. The runtime will prefer the VTID if present
	@VTID(8)
	com.gc.IList getIdList();

	@VTID(8)
	@ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = { com.gc.IList.class })
	java.lang.Object getIdList(int index);

	// Properties:
}
