package com.gc;

import com4j.*;

/**
 * For HP use. Represents a bpm represetable entity of bpm linkable entity
 * between two requirements.
 */
@IID("{5318B832-77F0-4E0D-B94D-3A2971000427}")
public interface IBPMRepresentable2 extends com.gc.IBPMRepresentable {
	// Methods:
	/**
	 * <p>
	 * Returns the list of all tests that cover the current representative
	 * entity and match the filter.
	 * </p>
	 * 
	 * @param includeIndirectLinkage
	 *            Mandatory boolean parameter.
	 * @param includeChildren
	 *            Mandatory boolean parameter.
	 * @param includeLinkage
	 *            Mandatory boolean parameter.
	 * @param filter
	 *            Mandatory java.lang.String parameter.
	 * @return Returns a value of type com.gc.IList
	 */

	@DISPID(18)
	// = 0x12. The runtime will prefer the VTID if present
	@VTID(10)
	com.gc.IList getLinkedTestsEx(boolean includeIndirectLinkage,
			boolean includeChildren, boolean includeLinkage,
			java.lang.String filter);

	// Properties:
}
