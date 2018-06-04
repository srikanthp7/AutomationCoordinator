package com.gc;

import com4j.*;

/**
 * For HP use. IBusinessViewsExcelReportManager Interface.
 */
@IID("{19496082-F839-4590-AFA4-A699BB177B80}")
public interface IBusinessViewsExcelReportManager extends Com4jObject {
	// Methods:
	/**
	 * <p>
	 * Generates a report data file according to the provided Dql.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(1)
	// = 0x1. The runtime will prefer the VTID if present
	@VTID(7)
	java.lang.String executeDql(java.lang.String dql, int maxRowsCountToFetch,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Starts excel report dql execution on the server according to the provided
	 * Dql string.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type int
	 */

	@DISPID(2)
	// = 0x2. The runtime will prefer the VTID if present
	@VTID(8)
	int startDqlExecution(java.lang.String dql, int maxRowsCountToFetch,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Downloads a excel report result file
	 * </p>
	 * 
	 * @param fileNameToDownload
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(3)
	// = 0x3. The runtime will prefer the VTID if present
	@VTID(9)
	java.lang.String downloadExcelReportResultFile(
			java.lang.String fileNameToDownload);

	/**
	 * <p>
	 * Generates a report data file according to the provided configuration xml.
	 * </p>
	 * 
	 * @param configurationXML
	 *            Mandatory java.lang.String parameter.
	 * @param worksheetID
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(5)
	// = 0x5. The runtime will prefer the VTID if present
	@VTID(10)
	java.lang.String executeConfiguration(java.lang.String configurationXML,
			java.lang.String worksheetID, int maxRowsCountToFetch,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Starts excel report execution on the server according to the provided
	 * configuration XML.
	 * </p>
	 * 
	 * @param configurationXML
	 *            Mandatory java.lang.String parameter.
	 * @param worksheetID
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type int
	 */

	@DISPID(6)
	// = 0x6. The runtime will prefer the VTID if present
	@VTID(11)
	int startConfigurationExecution(java.lang.String configurationXML,
			java.lang.String worksheetID, int maxRowsCountToFetch,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Translates configuration xml to dql.
	 * </p>
	 * 
	 * @param configurationXML
	 *            Mandatory java.lang.String parameter.
	 * @param worksheetID
	 *            Mandatory java.lang.String parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @param resultDql
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type boolean
	 */

	@DISPID(7)
	// = 0x7. The runtime will prefer the VTID if present
	@VTID(12)
	boolean translateConfigurationToDql(java.lang.String configurationXML,
			java.lang.String worksheetID,
			Holder<java.lang.String> resultAdditionalData,
			Holder<java.lang.String> resultDql);

	/**
	 * <p>
	 * Starts excel report dql execution on the server according to the provided
	 * Dql string on remote project.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type int
	 */

	@DISPID(8)
	// = 0x8. The runtime will prefer the VTID if present
	@VTID(13)
	int startDqlExecutionOnRemoteProject(java.lang.String dql,
			int maxRowsCountToFetch, java.lang.String projectId,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Starts excel report execution on the server according to the provided
	 * configuration XML on remote project.
	 * </p>
	 * 
	 * @param configurationXML
	 *            Mandatory java.lang.String parameter.
	 * @param worksheetID
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type int
	 */

	@DISPID(9)
	// = 0x9. The runtime will prefer the VTID if present
	@VTID(14)
	int startConfigurationExecutionOnRemoteProject(
			java.lang.String configurationXML, java.lang.String worksheetID,
			int maxRowsCountToFetch, java.lang.String projectId,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Generates a report data file according to the provided Dql on remote
	 * project.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(10)
	// = 0xa. The runtime will prefer the VTID if present
	@VTID(15)
	java.lang.String executeDqlOnRemoteProject(java.lang.String dql,
			int maxRowsCountToFetch, java.lang.String projectId,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Generates a report data file according to the provided configuration xml
	 * on remote project.
	 * </p>
	 * 
	 * @param configurationXML
	 *            Mandatory java.lang.String parameter.
	 * @param worksheetID
	 *            Mandatory java.lang.String parameter.
	 * @param maxRowsCountToFetch
	 *            Mandatory int parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @param resultAdditionalData
	 *            Mandatory Holder<java.lang.String> parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(11)
	// = 0xb. The runtime will prefer the VTID if present
	@VTID(16)
	java.lang.String executeConfigurationOnRemoteProject(
			java.lang.String configurationXML, java.lang.String worksheetID,
			int maxRowsCountToFetch, java.lang.String projectId,
			Holder<java.lang.String> resultAdditionalData);

	/**
	 * <p>
	 * Validates Dql according to current project customization.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(12)
	// = 0xc. The runtime will prefer the VTID if present
	@VTID(17)
	java.lang.String validateDql(java.lang.String dql);

	/**
	 * <p>
	 * Validates Dql according to remote project customization.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(13)
	// = 0xd. The runtime will prefer the VTID if present
	@VTID(18)
	java.lang.String validateDqlOnRemoteProject(java.lang.String dql,
			java.lang.String projectId);

	/**
	 * <p>
	 * Returns maximal number of rows allowed on auto preview request.
	 * </p>
	 * <p>
	 * Getter method for the COM property "AutoPreviewMaxRows"
	 * </p>
	 * 
	 * @return Returns a value of type int
	 */

	@DISPID(14)
	// = 0xe. The runtime will prefer the VTID if present
	@VTID(19)
	int autoPreviewMaxRows();

	// Properties:
}
