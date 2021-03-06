package com.gc;

import com4j.*;

/**
 * For HP use. Represents an analysis item file.
 */
@IID("{22AF4E1D-1158-44DB-8DC8-BAC830AB9354}")
public interface IAnalysisItemFile extends com.gc.IBaseField {
	// Methods:
	/**
	 * <p>
	 * Sets the path of the file to be uploaded to the server on post operation.
	 * </p>
	 * 
	 * @param filePath
	 *            Mandatory java.lang.String parameter.
	 */

	@DISPID(15)
	// = 0xf. The runtime will prefer the VTID if present
	@VTID(20)
	void setFilePath(java.lang.String filePath);

	/**
	 * <p>
	 * Downloads the file to the client machine and returns the downloaded file
	 * path.
	 * </p>
	 * 
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(16)
	// = 0x10. The runtime will prefer the VTID if present
	@VTID(21)
	java.lang.String download();

	// Properties:
}
