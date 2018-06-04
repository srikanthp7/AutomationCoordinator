package com.gc;

import com4j.*;

/**
 * Represents a graph built through a method.
 */
@IID("{16D6E6A8-E711-40AE-9B22-9471B1DC6F8C}")
public interface IGraph3 extends com.gc.IGraph2 {
	// Methods:
	/**
	 * <p>
	 * Request for drill down data. Pass entity type and a list of IDs and get a
	 * list of entities (as in NewList request)
	 * </p>
	 * 
	 * @param entityType
	 *            Mandatory java.lang.String parameter.
	 * @param idList
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(17)
	// = 0x11. The runtime will prefer the VTID if present
	@VTID(23)
	com.gc.IList drillDownData(java.lang.String entityType,
			java.lang.String idList);

	/**
	 * <p>
	 * Cross project graph data drill-down.
	 * </p>
	 * 
	 * @param areas
	 *            Mandatory java.lang.Object parameter.
	 * @param parameterMap
	 *            Mandatory com4j.Com4jObject parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(18)
	// = 0x12. The runtime will prefer the VTID if present
	@VTID(24)
	com.gc.IList crossDrillDownEx(
			@MarshalAs(NativeType.VARIANT) java.lang.Object areas,
			@MarshalAs(NativeType.Dispatch) com4j.Com4jObject parameterMap);

	// Properties:
}
