package com.gc;

import com4j.*;

/**
 * Represents a single file or Internet address attached to a field object.
 */
@IID("{ACA3EE6C-9D33-47E0-9AE1-629C27601A8E}")
public interface IAttachment3 extends com.gc.IAttachment {
	// Methods:
	/**
	 * <p>
	 * The attachment file size in bytes.
	 * </p>
	 * <p>
	 * Getter method for the COM property "FileSizeEx"
	 * </p>
	 * 
	 * @return Returns a value of type long
	 */

	@DISPID(24)
	// = 0x18. The runtime will prefer the VTID if present
	@VTID(36)
	long fileSizeEx();

	// Properties:
}
