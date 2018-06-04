package com.gc;

import com4j.*;

/**
 * For HP use. Services to manage Dashboard Pages.
 */
@IID("{1001E71F-5744-4624-8000-633698EB940D}")
public interface IDashboardPageFactory extends com.gc.IBaseFactoryEx {
	// Methods:
	/**
	 * <p>
	 * Returns those dashboard page objects from the specified ID list that
	 * include private analysis items.
	 * </p>
	 * 
	 * @param pIdsList
	 *            Mandatory com.gc.IList parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(9)
	// = 0x9. The runtime will prefer the VTID if present
	@VTID(17)
	com.gc.IList getPagesWithPrivateItems(com.gc.IList pIdsList);

	// Properties:
}
