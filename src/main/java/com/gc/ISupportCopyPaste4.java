package com.gc;

import com4j.*;

/**
 * Services for supporting copy and paste.
 */
@IID("{E6A01604-F2AE-4B4E-8A7C-2C918EE8828F}")
public interface ISupportCopyPaste4 extends com.gc.ISupportCopyPaste3 {
	// Methods:
	/**
	 * <p>
	 * For HP use. Pastes data from clipboard.
	 * </p>
	 * 
	 * @param clipboardData
	 *            Mandatory java.lang.String parameter.
	 * @param targetID
	 *            Optional parameter. Default value is ""
	 * @param mode
	 *            Optional parameter. Default value is 0
	 * @param orderID
	 *            Optional parameter. Default value is -1
	 * @return Returns a value of type java.lang.String
	 */

	@VTID(10)
	java.lang.String pasteFromClipBoardEx(java.lang.String clipboardData,
			@Optional @DefaultValue("") java.lang.String targetID,
			@Optional @DefaultValue("0") int mode,
			@Optional @DefaultValue("-1") int orderID);

	// Properties:
}
