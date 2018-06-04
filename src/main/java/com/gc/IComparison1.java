package com.gc;

import com4j.*;

/**
 * For HP use. Represents a Comparison.
 */
@IID("{8E422E74-3D8B-4507-B992-C5224F2742D0}")
public interface IComparison1 extends com.gc.IComparison {
	// Methods:
	/**
	 * <p>
	 * Get a Comparison detailed report as a CSV String - this string can be
	 * saved by the UI/OTA Script to a file
	 * </p>
	 * 
	 * @param comparisonSettingsList
	 *            Mandatory com.gc.IList parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(28)
	// = 0x1c. The runtime will prefer the VTID if present
	@VTID(38)
	com.gc.IList getCSVReportOnLastComparison(
			com.gc.IList comparisonSettingsList);

	// Properties:
}
