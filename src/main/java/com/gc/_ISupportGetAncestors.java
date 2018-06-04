package com.gc;

import com4j.*;

/**
 * For HP user only - Allows getting Ancestors when supported
 */
@IID("{317D7112-B34B-4CE2-BF4B-2D6863A427AB}")
public interface _ISupportGetAncestors extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * For HP us only - Allows batch get for all ancestors in hierarchy
	 * </p>
	 * 
	 * @param ancestorId
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@VTID(3)
	com.gc.IList getAncestors(java.lang.String ancestorId);

	// Properties:
}
