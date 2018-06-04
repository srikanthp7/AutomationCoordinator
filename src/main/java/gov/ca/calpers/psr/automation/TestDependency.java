package gov.ca.calpers.psr.automation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import gov.ca.calpers.psr.automation.directed.graph.EdgeTypeEnum;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;

/**
 * The Class TestDependency.
 */
public class TestDependency implements Comparable<TestDependency>, Serializable, ActionListener{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The dependency id. */
	private long dependencyId;
    
    /** The parent test id. */
    private long parentTestId;
    
    /** The automation script id. */
    private long automationScriptId;
	
	/** The create date. */
	private Date createDate;	
	
	/** The dependency type. */
	private EdgeTypeEnum dependencyType;
	
	/** The disabled. */
	private boolean disabled = false;

	/**
	 * Instantiates a new test dependency.
	 */
	public TestDependency()
	{
		dependencyType = EdgeTypeEnum.MUST_RUN; 
	}		

	/**
	 * Gets the test dependencies by child id.
	 *
	 * @param childId the child id
	 * @return the test dependencies by child id
	 */
	public synchronized static Set<TestDependency> getTestDependenciesByChildId(long childId)
	{		
		Set<TestDependency> allDependencies = new HashSet<TestDependency>();
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM SCRIPT_DEPENDENCY WHERE AUTOMATION_SCRIPT_ID = :childTestId").addEntity(TestDependency.class);
		 		query.setParameter("childTestId", childId);
		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		List<TestDependency> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		System.out.println("Query.toString= " + query.toString());
	            for (TestDependency obj : list) {		              
		            	  allDependencies.add(obj);		              
		            	  System.out.println("*** DEPENDENCY ADDED ***");		              
		          }
	            tx.commit();
	            session.close();
		 	}
		 	return allDependencies;		
	}
	
	/**
	 * Delete.
	 *
	 * @param testDep the test dep
	 */
	public synchronized static void delete(TestDependency testDep)
	{
		Session session = HibernateUtil.getSesssion();	      
	      try{
	    	 session.beginTransaction();
	         session.delete(testDep); 
	         session.getTransaction().commit();
	         testDep.setDependencyId(0);
	      }catch (HibernateException e) {	         
	         if(session.getTransaction() != null)
	         {
	        	 session.getTransaction().rollback();
	         }
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
	}
	
	/**
	 * Save.
	 *
	 * @return the test dependency
	 */
	public TestDependency save()
	{
		if(dependencyId == 0)
		{
			return create(this);
		}else
		{
			return update(this);
		}
	}
	
	
	/**
	 * Creates the.
	 *
	 * @param testDep the test dep
	 * @return the test dependency
	 */
	private synchronized static TestDependency create(TestDependency testDep) {
		Session session = HibernateUtil.getSesssion();
		testDep.setCreateDate(new Date());
		session.beginTransaction();
		long id = (Long) session.save(testDep);
		testDep.setDependencyId(id);
		session.getTransaction().commit();
		session.close();
		return testDep;
	}

	/**
	 * Update.
	 *
	 * @param testDep the test dep
	 * @return the test dependency
	 */
	private synchronized static TestDependency update(TestDependency testDep) {
		Session session = HibernateUtil.getSesssion();
		session.beginTransaction();
		session.update(testDep);
		session.getTransaction().commit();
		session.close();
		return testDep;
	}
	
	/**
	 * Sets the parent test id.
	 *
	 * @param parentTestId the parentTestId to set
	 */
	public void setParentTestId(long parentTestId) {
		this.parentTestId = parentTestId;
	}


	/**
	 * Gets the parent test id.
	 *
	 * @return the parentTestId
	 */
	public long getParentTestId() {
		return parentTestId;
	}


	/**
	 * Sets the automation script id.
	 *
	 * @param automationScriptId the automationScriptId to set
	 */
	public void setAutomationScriptId(long automationScriptId) {
		this.automationScriptId = automationScriptId;
	}


	/**
	 * Gets the automation script id.
	 *
	 * @return the automationScriptId
	 */
	public long getAutomationScriptId() {
		return automationScriptId;
	}


	/**
	 * Sets the creates the date.
	 *
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	/**
	 * Gets the creates the date.
	 *
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}


	/**
	 * Sets the dependency id.
	 *
	 * @param dependencyId the dependencyId to set
	 */
	public void setDependencyId(long dependencyId) {
		this.dependencyId = dependencyId;
	}


	/**
	 * Gets the dependency id.
	 *
	 * @return the dependencyId
	 */
	public long getDependencyId() {
		return dependencyId;
	}

	/**
	 * Gets the dependency type.
	 *
	 * @return the depType
	 */
	public EdgeTypeEnum getDependencyType() {
		return dependencyType;
	}

	/**
	 * Sets the dependency type.
	 *
	 * @param depType the depType to set
	 */
	public void setDependencyType(EdgeTypeEnum depType) {
		this.dependencyType = depType;
	}

	/**
	 * Checks if is disabled.
	 *
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Sets the disabled.
	 *
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TestDependency dep) {
		return this.toString().compareTo(dep.toString());		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.valueOf(dependencyId);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("DISABLE"))
		{
			this.setDisabled(true);
		}
	}
}
