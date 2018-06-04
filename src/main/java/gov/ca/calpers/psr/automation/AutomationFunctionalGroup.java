package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;
import gov.ca.calpers.psr.automation.server.ui.CollectionsUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

// TODO: Auto-generated Javadoc
/**
 * The Class AutomationFunctionalGroup.
 */
public class AutomationFunctionalGroup implements Comparable<AutomationFunctionalGroup> {

	/** The auto functional group code. */
	private String autoFunctionalGroupCode;
	
	/** The create date. */
	private Date createDate;
	
	/** The update date. */
	private Date updateDate;
	
	/** The short name. */
	private String shortName;
	
	/** The long name. */
	private String longName;
	
	/** The description. */
	private String description;
	
	/** The tests. */
	private List<AutomationTest> tests;
	
	/**
	 * Instantiates a new automation functional group.
	 */
	public AutomationFunctionalGroup()
	{
		setTests(new LinkedList<AutomationTest>());
	}
	
	/**
	 * Gets all AutomationFunctionalGroups.
	 *
	 * @return list of all AutomationFunctionalGroups
	 */
	public synchronized static List<AutomationFunctionalGroup> getAll() 
	{
		CopyOnWriteArrayList<AutomationFunctionalGroup> allFunctionalGroups = new CopyOnWriteArrayList<AutomationFunctionalGroup>();
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM LOV_AUTO_FUNCTIONAL_GRP").addEntity(AutomationFunctionalGroup.class);

		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		List<AutomationFunctionalGroup> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		System.out.println("Query.toString= " + query.toString());
	            for (AutomationFunctionalGroup obj : list) {
		              CollectionsUtil.addInOrder(allFunctionalGroups,obj);		              
		              System.out.println("*** AUTO-FUNCTIONAL-GROUP ADDED ***");
		          }
	            tx.commit();
	            session.close();
		 	}
		 	return allFunctionalGroups;
	}
	
	/**
	 * Gets the group.
	 *
	 * @param groupCode the group code
	 * @return the AutomomationFunctionalGroup
	 */
	public synchronized static AutomationFunctionalGroup getGroup(String groupCode){
		AutomationFunctionalGroup grp = null;
		if(groupCode != null && !groupCode.isEmpty())
		{
			Session session = HibernateUtil.getSesssion();
			  
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		grp = new AutomationFunctionalGroup();
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM LOV_AUTO_FUNCTIONAL_GRP WHERE LOV_AUTO_FUNCTIONAL_GRP_CD = :autoFuncGroupCd ").addEntity(AutomationFunctionalGroup.class);
		 		query.setParameter("autoFuncGroupCd", groupCode);
		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		grp = (AutomationFunctionalGroup) query.list().get(0);
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		System.out.println("Query.toString= " + query.toString());            
	            tx.commit();
	            session.close();
		 	}
		}
	 	return grp;
	}

	/**
	 * Gets the auto functional group code.
	 *
	 * @return the auto functional group code
	 */
	public String getAutoFunctionalGroupCode() {
		return autoFunctionalGroupCode;
	}

	/**
	 * Sets the auto functional group code.
	 *
	 * @param autoFunctionalGroupCode the new auto functional group code
	 */
	public void setAutoFunctionalGroupCode(String autoFunctionalGroupCode) {
		this.autoFunctionalGroupCode = autoFunctionalGroupCode;
	}

	/**
	 * Gets the creates the date.
	 *
	 * @return the creates the date
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * Sets the creates the date.
	 *
	 * @param createDate the new creates the date
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * Gets the update date.
	 *
	 * @return the update date
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * Sets the update date.
	 *
	 * @param updateDate the new update date
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * Gets the short name.
	 *
	 * @return the short name
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Sets the short name.
	 *
	 * @param shortName the new short name
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Gets the long name.
	 *
	 * @return the long name
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * Sets the long name.
	 *
	 * @param longName the new long name
	 */
	public void setLongName(String longName) {
		this.longName = longName;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the tests.
	 *
	 * @return the tests
	 */
	public List<AutomationTest> getTests() {
		return tests;
	}

	/**
	 * Sets the tests.
	 *
	 * @param tests the tests to set
	 */
	public void setTests(List<AutomationTest> tests) {
		this.tests = tests;
	}
	
	/**
	 * Adds the test.
	 *
	 * @param test the test
	 */
	public void addTest(AutomationTest test)
	{
		tests.add(test);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AutomationFunctionalGroup o) {
		
		return this.autoFunctionalGroupCode.compareTo(o.getAutoFunctionalGroupCode());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return longName;
	}
	
	
}
