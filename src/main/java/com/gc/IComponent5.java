package com.gc;

import com4j.*;

/**
 * Represents a single Business Process Component.
 */
@IID("{56456C93-60C4-43B0-9CBC-041369E52124}")
public interface IComponent5 extends com.gc.IComponent4 {
	// Methods:
	/**
	 * <p>
	 * For HP use. Clear locking info of shadow component after post flow.
	 * </p>
	 */

	@DISPID(37)
	// = 0x25. The runtime will prefer the VTID if present
	@VTID(48)
	void clearShadowCompLockingAfterPostFlow();

	// Properties:
}
