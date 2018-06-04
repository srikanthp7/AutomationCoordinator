package com.gc;

import com4j.*;

/**
 * IConnectionSettingsInternal interface
 */
@IID("{7FDA3E1E-2F34-4142-95C7-38FFFD2AF86D}")
public interface IConnectionSettingsInternal extends com.gc.IConnectionSettings {
	// Methods:
	/**
	 * <p>
	 * Get server credentials
	 * </p>
	 * 
	 * @param pLogin
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @param pPass
	 *            Mandatory Holder<java.lang.String> parameter.
	 */

	@DISPID(13)
	// = 0xd. The runtime will prefer the VTID if present
	@VTID(23)
	void getWebServerCredentials(Holder<java.lang.String> pLogin,
			Holder<java.lang.String> pPass);

	/**
	 * <p>
	 * Get proxy credentials
	 * </p>
	 * 
	 * @param pLogin
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @param pPass
	 *            Mandatory Holder<java.lang.String> parameter.
	 */

	@DISPID(14)
	// = 0xe. The runtime will prefer the VTID if present
	@VTID(24)
	void getProxyCredentials(Holder<java.lang.String> pLogin,
			Holder<java.lang.String> pPass);

	/**
	 * <p>
	 * Get cookie
	 * </p>
	 * 
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(15)
	// = 0xf. The runtime will prefer the VTID if present
	@VTID(25)
	java.lang.String getCookie();

	// Properties:
}
