package com.gc;

import com4j.*;

/**
 * For HP use. Information on a specific version.
 */
@IID("{F6376DA6-02E6-4A5E-99BB-C8485CEFF72E}")
public interface IVersionItem2 extends com.gc.IVersionItem {
	// Methods:
	/**
	 * <p>
	 * The check-in date and time using the server's time.
	 * </p>
	 * <p>
	 * Getter method for the COM property "DateEx"
	 * </p>
	 * 
	 * @return Returns a value of type java.lang.Object
	 */

	@DISPID(7)
	// = 0x7. The runtime will prefer the VTID if present
	@VTID(13)
	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object dateEx();

	/**
	 * <p>
	 * For HP use. The list of IBaseline objects to which this version belongs.
	 * </p>
	 * <p>
	 * Getter method for the COM property "Baselines"
	 * </p>
	 * 
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(8)
	// = 0x8. The runtime will prefer the VTID if present
	@VTID(14)
	com.gc.IList baselines();

	@VTID(14)
	@ReturnValue(type = NativeType.VARIANT, defaultPropertyThrough = { com.gc.IList.class })
	java.lang.Object baselines(int index);

	// Properties:
}
