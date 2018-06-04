package com.gc;

import com4j.*;

/**
 * For HP use. ITDConnection methods for internal use from UI.
 */
@IID("{103564D1-C93A-4C00-81D7-89327EDC766F}")
public interface ITDConnection_Internal extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * For HP use. Generic one string parameter call
	 * </p>
	 * 
	 * @param requestName
	 *            Mandatory java.lang.String parameter.
	 * @param input
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@VTID(3)
	java.lang.String genericOneParamCall(java.lang.String requestName,
			java.lang.String input);

	/**
	 * <p>
	 * For HP use. Generic stream download call
	 * </p>
	 * 
	 * @param requestName
	 *            Mandatory java.lang.String parameter.
	 * @param input
	 *            Mandatory java.lang.String parameter.
	 * @param filePath
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@VTID(4)
	java.lang.String genericOneParamStreamDownloadCall(
			java.lang.String requestName, java.lang.String input,
			java.lang.String filePath);

	// Properties:
}
