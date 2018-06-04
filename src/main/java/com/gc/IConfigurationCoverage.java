package com.gc;

import com4j.*;

/**
 * A requirement coverage by a test configuration.
 */
@IID("{3CD3479F-6A9C-4428-B63C-FB81C8398823}")
public interface IConfigurationCoverage extends com.gc.IBaseField {
	// Methods:
	/**
	 * <p>
	 * The status of the configuration coverage.
	 * </p>
	 * <p>
	 * Getter method for the COM property "Status"
	 * </p>
	 * 
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(11)
	// = 0xb. The runtime will prefer the VTID if present
	@VTID(20)
	java.lang.String status();

	/**
	 * <p>
	 * The configuration covering the requirement.
	 * </p>
	 * <p>
	 * Getter method for the COM property "Configuration"
	 * </p>
	 * 
	 * @return Returns a value of type com4j.Com4jObject
	 */

	@DISPID(12)
	// = 0xc. The runtime will prefer the VTID if present
	@VTID(21)
	@ReturnValue(type = NativeType.Dispatch)
	com4j.Com4jObject configuration();

	/**
	 * <p>
	 * The criteria included in the configuration coverage.
	 * </p>
	 * <p>
	 * Getter method for the COM property "CriterionCoverage"
	 * </p>
	 * 
	 * @param coverageByCycles
	 *            Mandatory com.gc.IList parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(13)
	// = 0xd. The runtime will prefer the VTID if present
	@VTID(22)
	com.gc.IList criterionCoverage(com.gc.IList coverageByCycles);

	// Properties:
}
