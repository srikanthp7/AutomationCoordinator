package com.gc;

import com4j.*;

/**
 * Services for moving requirements.
 */
@IID("{B496D843-987D-4498-B10D-EE9CD66045B6}")
public interface IReqFactory5 extends com.gc.IReqFactory4 {
	// Methods:
	/**
	 * <p>
	 * Moves the requirements to be child nodes of the specified parent.
	 * </p>
	 * 
	 * @param reqIDs
	 *            Mandatory java.lang.String parameter.
	 * @param newPositions
	 *            Mandatory java.lang.String parameter.
	 * @param newParentId
	 *            Mandatory int parameter.
	 */

	@DISPID(30)
	// = 0x1e. The runtime will prefer the VTID if present
	@VTID(38)
	void moveRequirements(java.lang.String reqIDs,
			java.lang.String newPositions, int newParentId);

	/**
	 * <p>
	 * For HP use. A filter must be passed, also at least one traceability
	 * matrix relation setting.
	 * </p>
	 * 
	 * @param filter
	 *            Mandatory java.lang.String parameter.
	 * @param relationSettings
	 *            Mandatory com.gc.IList parameter.
	 * @param statistics
	 *            Mandatory Holder<com.gc.IList> parameter.
	 * @param rows
	 *            Mandatory Holder<com.gc.IList> parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(31)
	// = 0x1f. The runtime will prefer the VTID if present
	@VTID(39)
	com.gc.IList calculateTraceabilityMatrix(java.lang.String filter,
			com.gc.IList relationSettings, Holder<com.gc.IList> statistics,
			Holder<com.gc.IList> rows);

	/**
	 * <p>
	 * For HP use.
	 * </p>
	 * 
	 * @param relationName
	 *            Mandatory java.lang.String parameter.
	 * @param logicalCondition
	 *            Mandatory java.lang.String parameter.
	 * @param existInIds
	 *            Mandatory boolean parameter.
	 * @param filter
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type
	 *         com.gc.ITraceabilityMatrixRelationSettings
	 */

	@DISPID(32)
	// = 0x20. The runtime will prefer the VTID if present
	@VTID(40)
	com.gc.ITraceabilityMatrixRelationSettings createTraceabilityMatrixRelationSettings(
			java.lang.String relationName, java.lang.String logicalCondition,
			boolean existInIds, java.lang.String filter);

	/**
	 * <p>
	 * For HP use.
	 * </p>
	 * 
	 * @param settings
	 *            Mandatory com.gc.ITraceabilityMatrixRelationSettings
	 *            parameter.
	 * @param reqID
	 *            Mandatory int parameter.
	 * @param tableName
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(33)
	// = 0x21. The runtime will prefer the VTID if present
	@VTID(41)
	com.gc.IList getLinkedEntities(
			com.gc.ITraceabilityMatrixRelationSettings settings, int reqID,
			Holder<java.lang.String> tableName);

	/**
	 * <p>
	 * For HP use.
	 * </p>
	 * 
	 * @param reqIDs
	 *            Mandatory java.lang.String parameter.
	 * @param relationSettings
	 *            Mandatory com.gc.IList parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(34)
	// = 0x22. The runtime will prefer the VTID if present
	@VTID(42)
	com.gc.IList getAllLinkedEntities(java.lang.String reqIDs,
			com.gc.IList relationSettings);

	/**
	 * <p>
	 * For HP use.
	 * </p>
	 * 
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(35)
	// = 0x23. The runtime will prefer the VTID if present
	@VTID(43)
	java.lang.String downloadExportTraceabilityMatrixTemplateFile();

	/**
	 * <p>
	 * Returns the list of test configurations that cover the requirements that
	 * match the requirement filter.
	 * </p>
	 * 
	 * @param reqFilter
	 *            Mandatory java.lang.String parameter.
	 * @param testIDs
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(36)
	// = 0x24. The runtime will prefer the VTID if present
	@VTID(44)
	com.gc.IList getCoverageTestConfigsByReqFilter(java.lang.String reqFilter,
			java.lang.String testIDs);

	// Properties:
}
