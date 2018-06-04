package com.gc;

import com4j.*;

/**
 * For HP use. Services to manage analysis segments.
 */
@IID("{BA2A4EE4-E252-434E-82E7-C79C0953CE59}")
public interface IAnalysisSegmentFactory extends com.gc.IBaseFactoryEx {
	// Methods:
	/**
	 * <p>
	 * Returns the list of segments per extension.
	 * </p>
	 * 
	 * @param extension
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(9)
	// = 0x9. The runtime will prefer the VTID if present
	@VTID(17)
	com.gc.IList getExtensionSegments(java.lang.String extension);

	// Properties:
}
