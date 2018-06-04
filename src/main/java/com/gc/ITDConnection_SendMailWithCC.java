package com.gc;

import com4j.*;

/**
 * Send Email
 */
@IID("{E78B2EA2-0E06-4DCF-A676-4747F885BE15}")
public interface ITDConnection_SendMailWithCC extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * Sends Mail.
	 * </p>
	 * 
	 * @param sendTo
	 *            Mandatory java.lang.String parameter.
	 * @param ccTo
	 *            Mandatory java.lang.String parameter.
	 * @param sendFrom
	 *            Optional parameter. Default value is ""
	 * @param subject
	 *            Optional parameter. Default value is ""
	 * @param message
	 *            Optional parameter. Default value is ""
	 * @param attachArray
	 *            Optional parameter. Default value is
	 *            com4j.Variant.getMissing()
	 * @param bsFormat
	 *            Optional parameter. Default value is ""
	 */

	@DISPID(1)
	// = 0x1. The runtime will prefer the VTID if present
	@VTID(3)
	void sendMailWithCC(
			java.lang.String sendTo,
			java.lang.String ccTo,
			@Optional @DefaultValue("") java.lang.String sendFrom,
			@Optional @DefaultValue("") java.lang.String subject,
			@Optional @DefaultValue("") java.lang.String message,
			@Optional @MarshalAs(NativeType.VARIANT) java.lang.Object attachArray,
			@Optional @DefaultValue("") java.lang.String bsFormat);

	// Properties:
}
