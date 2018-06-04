package com.gc;

import com4j.*;

/**
 * For HP use. IBusinessViewsExcelReportManager2 Interface.
 */
@IID("{6CC7E636-B288-4F0F-B5DA-DD6144FF19D5}")
public interface IBusinessViewsExcelReportManager2 extends
		com.gc.IBusinessViewsExcelReportManager {
	// Methods:
	/**
	 * <p>
	 * Downloads remote excel report result file.
	 * </p>
	 * 
	 * @param fileNameToDownload
	 *            Mandatory java.lang.String parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(15)
	// = 0xf. The runtime will prefer the VTID if present
	@VTID(20)
	java.lang.String downloadRemoteExcelReportResultFile(
			java.lang.String fileNameToDownload, java.lang.String projectId);

	/**
	 * <p>
	 * Aborts remote excel report generation task.
	 * </p>
	 * 
	 * @param taskID
	 *            Mandatory int parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 */

	@DISPID(16)
	// = 0x10. The runtime will prefer the VTID if present
	@VTID(21)
	void abortRemoteExcelReportGenerationTask(int taskID,
			java.lang.String projectId);

	/**
	 * <p>
	 * Aborts excel report generation task.
	 * </p>
	 * 
	 * @param taskID
	 *            Mandatory int parameter.
	 */

	@DISPID(17)
	// = 0x11. The runtime will prefer the VTID if present
	@VTID(22)
	void abortExcelReportGenerationTask(int taskID);

	/**
	 * <p>
	 * Returns excel report generation task detail.
	 * </p>
	 * 
	 * @param taskID
	 *            Mandatory int parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(18)
	// = 0x12. The runtime will prefer the VTID if present
	@VTID(23)
	java.lang.String getExecutionTaskDetails(int taskID);

	/**
	 * <p>
	 * Returns remote excel report generation task details.
	 * </p>
	 * 
	 * @param taskID
	 *            Mandatory int parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(19)
	// = 0x13. The runtime will prefer the VTID if present
	@VTID(24)
	java.lang.String getRemoteExecutionTaskDetails(int taskID,
			java.lang.String projectId);

	/**
	 * <p>
	 * Decline excel report result file download.
	 * </p>
	 * 
	 * @param fileNameToDownload
	 *            Mandatory java.lang.String parameter.
	 */

	@DISPID(20)
	// = 0x14. The runtime will prefer the VTID if present
	@VTID(25)
	void declineDownloadExcelReportResultFile(
			java.lang.String fileNameToDownload);

	/**
	 * <p>
	 * Decline remote excel report result file download.
	 * </p>
	 * 
	 * @param fileNameToDownload
	 *            Mandatory java.lang.String parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 */

	@DISPID(21)
	// = 0x15. The runtime will prefer the VTID if present
	@VTID(26)
	void declineDownloadRemoteExcelReportResultFile(
			java.lang.String fileNameToDownload, java.lang.String projectId);

	/**
	 * <p>
	 * Extracts metadata of a DQL to be executed.
	 * </p>
	 * 
	 * @param dql
	 *            Mandatory java.lang.String parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(22)
	// = 0x16. The runtime will prefer the VTID if present
	@VTID(27)
	java.lang.String getDqlExecutionMetadata(java.lang.String dql,
			java.lang.String projectId);

	/**
	 * <p>
	 * Extracts metadata of a Configuration to be executed.
	 * </p>
	 * 
	 * @param configurationXML
	 *            Mandatory java.lang.String parameter.
	 * @param worksheetID
	 *            Mandatory java.lang.String parameter.
	 * @param projectId
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type java.lang.String
	 */

	@DISPID(23)
	// = 0x17. The runtime will prefer the VTID if present
	@VTID(28)
	java.lang.String getConfigurationExecutionMetadata(
			java.lang.String configurationXML, java.lang.String worksheetID,
			java.lang.String projectId);

	// Properties:
}
