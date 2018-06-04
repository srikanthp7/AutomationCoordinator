package com.gc;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
	private ClassFactory() {
	} // instanciation is not allowed

	/**
	 * Represents a single server connection.
	 */
	public static com.gc.ITDConnection10 createTDConnection() {
		return COM4J.createInstance(com.gc.ITDConnection10.class,
				"{C5CBD7B2-490C-45F5-8C40-B8C3D108E6D7}");
	}

	/**
	 * Services to create and maintain lists. Use any factory object to create
	 * any number of list instances for objects in the factory.
	 */
	public static com.gc.IList createList() {
		return COM4J.createInstance(com.gc.IList.class,
				"{9007A7F1-AC71-4563-A943-CFF4051E7E3D}");
	}

	/**
	 * For HP use. ComFrec Class.
	 */
	public static com.gc.IComFrec createComFrec() {
		return COM4J.createInstance(com.gc.IComFrec.class,
				"{B2F590F7-BD30-45DD-90B7-F243D7E8B210}");
	}

	/**
	 * Support for hash generation.
	 */
	public static com.gc.IAmarillusHash createAmarillusHash() {
		return COM4J.createInstance(com.gc.IAmarillusHash.class,
				"{61C395DB-BDD5-4431-995D-E5F38E8FAC70}");
	}

	/**
	 * Provides services for grouping.
	 */
	public static com.gc.IGroupingManager createGroupingManager() {
		return COM4J.createInstance(com.gc.IGroupingManager.class,
				"{F801F7A2-04DF-4DD3-8A5E-C0CC66E0595E}");
	}

	/**
	 * A set of entities having the same value in a specific field.
	 */
	public static com.gc.IGroupingItem createGroupingItem() {
		return COM4J.createInstance(com.gc.IGroupingItem.class,
				"{904CED76-CF4A-4C85-BB23-2B4A9DCB1D6A}");
	}

	/**
	 * Custom audit event data.
	 */
	public static com.gc.IAuditRecordData createAuditRecordData() {
		return COM4J.createInstance(com.gc.IAuditRecordData.class,
				"{EB64EF73-64A5-48A1-8CE4-07C5487E32A6}");
	}

	/**
	 * Represents an Entity Subtype.
	 */
	public static com.gc.IEntitySubtype createEntitySubtype() {
		return COM4J.createInstance(com.gc.IEntitySubtype.class,
				"{270E00D6-722A-4249-A7D0-18127F459416}");
	}

	/**
	 * For HP use. Singleton class that assists in passing warning information
	 * to and from QC modules.
	 */
	public static com.gc.IWarningInfo createWarningInfo() {
		return COM4J.createInstance(com.gc.IWarningInfo.class,
				"{0A3D3A7A-A798-4437-91B6-870D71DB0C10}");
	}

	/**
	 * TDConnectionSettings Class
	 */
	public static com.gc.IConnectionSettings createConnectionSettings() {
		return COM4J.createInstance(com.gc.IConnectionSettings.class,
				"{6AC3694C-553B-4B2E-91C6-B97536B35B2B}");
	}

	/**
	 * For HP use. Export Class.
	 */
	public static com.gc.IExport createExport() {
		return COM4J.createInstance(com.gc.IExport.class,
				"{DCB4C421-E9F4-4A89-9190-B49411B17167}");
	}

	/**
	 * For HP use. Import Class.
	 */
	public static com.gc.IImport createImport() {
		return COM4J.createInstance(com.gc.IImport.class,
				"{CD3E5686-4B11-462F-9619-D2FA447DCE96}");
	}

	/**
	 * For HP use. Utility routines and properties.
	 */
	public static com.gc.ITDUtils createTDUtils() {
		return COM4J.createInstance(com.gc.ITDUtils.class,
				"{977FEB6A-82DF-4F53-ADA2-F722F7E07D23}");
	}

	/**
	 * Represents a Requirement Type.
	 */
	public static com.gc.IReqType2 createReqType() {
		return COM4J.createInstance(com.gc.IReqType2.class,
				"{1E2919FE-D94C-4E66-9FF6-9187A65A674E}");
	}
}
