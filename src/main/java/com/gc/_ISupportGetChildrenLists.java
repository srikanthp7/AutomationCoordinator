package com.gc;

import com4j.*;

/**
 * For HP use. _ISupportGetChildrenLists Interface.
 */
@IID("{571138B3-FBBE-4B57-97F5-B61E325AEFBA}")
public interface _ISupportGetChildrenLists extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * For HP use. Returns children factory lists from the given parents
	 * </p>
	 * 
	 * @param filter
	 *            Mandatory com.gc.ITDFilter parameter.
	 * @param parents
	 *            Mandatory com.gc.IList parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@VTID(3)
	com.gc.IList getChildrenLists(com.gc.ITDFilter filter, com.gc.IList parents);

	// Properties:
}
