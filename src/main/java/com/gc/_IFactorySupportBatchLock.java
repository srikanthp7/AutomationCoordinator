package com.gc;

import com4j.*;

/**
 * Extends IBaseFactory
 */
@IID("{33A6EC9D-8D08-4598-AD39-A614D9409ECC}")
public interface _IFactorySupportBatchLock extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * Locks the given items.
	 * </p>
	 * 
	 * @param entities
	 *            Mandatory com.gc.IList parameter.
	 */

	@VTID(3)
	void lockList(com.gc.IList entities);

	// Properties:
}
