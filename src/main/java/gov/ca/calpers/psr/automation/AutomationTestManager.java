package gov.ca.calpers.psr.automation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;
import gov.ca.calpers.psr.automation.server.ui.CollectionsUtil;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * The Class AutomationTestManager.
 */
public class AutomationTestManager {
	
	/** The test manager. */
	private static AutomationTestManager testManager;
	
	/**
	 * Instantiates a new automation test manager.
	 */
	private AutomationTestManager()
	{
		
	}
	
	/**
	 * Gets the automation test manager.
	 *
	 * @return the automation test manager
	 */
	public static synchronized AutomationTestManager getAutomationTestManager()
	  {
	    if (testManager == null)
	        testManager = new AutomationTestManager();		
	    return testManager;
	  }
	
	 /* (non-Javadoc)
 	 * @see java.lang.Object#clone()
 	 */
 	@Override
	public Object clone() throws CloneNotSupportedException
	 {
		 throw new CloneNotSupportedException(); 
	 }

	 /**
 	 * Gets the all active regression tests.
 	 *
 	 * @return the all active regression tests
 	 */
 	public synchronized CopyOnWriteArrayList<AutomationTest> getAllActiveRegressionTests()
	 {
		 CopyOnWriteArrayList<AutomationTest> allTests = new CopyOnWriteArrayList<AutomationTest>();
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM AUTOMATION_SCRIPT WHERE LOV_SCRIPT_TYPE_CD = 'REG' AND LOV_SCRIPT_STATUS_CD = 'ACT' AND QC_TEST_ID <> '-999999'").addEntity(AutomationTest.class);

		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		List<AutomationTest> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		System.out.println("Query.toString= " + query.toString());
	            for (final AutomationTest obj : list) {
		              if(validateTest(obj))
		              {
		            	  CollectionsUtil.addInOrder(allTests,obj);		              
		            	  System.out.println("*** TEST ADDED ***");
		              }
		          }
	            tx.commit();
	            session.close();
		 	}
		 	return allTests;	     
	 }
	 
	 /**
 	 * Gets the test by name.
 	 *
 	 * @param testName the test name
 	 * @return the test by name
 	 */
 	public synchronized AutomationTest getTestByName(String testName)
	 {
		 final AutomationTest test;
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM AUTOMATION_SCRIPT WHERE SCRIPT_NAME = :uniqueTestName").addEntity(AutomationTest.class);
		 		query.setParameter("uniqueTestName", testName);
		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		@SuppressWarnings("rawtypes")
		 		List<AutomationTest> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		 if (list.size() == 1) {
		                test = list.get(0);
		            } else {		            	
		                throw new RuntimeException("Duplicated test name.  Not support: " + testName + "\n*** Remove all but one of the duplicated test.***");
		            }
		       
	            tx.commit();
	            session.close();
		 	}else{
		 		test=null;
		 	}
		 	return test;
	 }
	 
	 /**
 	 * Gets the test by id.
 	 *
 	 * @param iD the i d
 	 * @return the test by id
 	 */
 	public synchronized AutomationTest getTestById(long iD)
	 {
		 final AutomationTest test;
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM AUTOMATION_SCRIPT WHERE AUTOMATION_SCRIPT_ID = :uniqueTestId").addEntity(AutomationTest.class);
		 		query.setParameter("uniqueTestId", iD);
		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		@SuppressWarnings("rawtypes")
		 		List<AutomationTest> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		 if (list.size() == 1) {
		                test = list.get(0);
		            } else {
		                throw new RuntimeException("Duplicated test name.  Not support: " + iD + "\n*** Remove all but one of the duplicated test.***");
		            }
		       
	            tx.commit();
	            session.close();
		 	}else{
		 		test = null;		 	
		 	}
		 	return test;
	 }
	 
	 /**
 	 * Gets the test result by test and test set ids.
 	 *
 	 * @param testId the test id
 	 * @param testSetId the test set id
 	 * @return the test result by test and test set ids
 	 */
 	public synchronized TestResult getTestResultByTestAndTestSetIds(long testId, int testSetId)
	 {
		 final TestResult testResult;
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM TESTRESULT WHERE AUTOMATION_TEST_SET_ID = :testSetId AND AUTOMATION_SCRIPT_ID = :testId").addEntity(TestResult.class);
		 		query.setParameter("testSetId", testSetId);
		 		query.setParameter("testId", testId);
		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		@SuppressWarnings("rawtypes")
		 		List<TestResult> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		 if (list.size() == 1) {
		                testResult = list.get(0);
		            } else if(list.size() > 1){		            	
		                throw new RuntimeException("More than 1 test results were returned for test set id: " + testSetId + " and test id: " + testId + ".\n Total results returned: " + list.size());
		            }else
		            {
		            	testResult = null;
		            }
		       
	            tx.commit();
	            session.close();
		 	}else{
		 		testResult=null;
		 	}
		 	return testResult;
	 }
	 
	 /**
 	 * Validate test.
 	 *
 	 * @param AutomationTest test
 	 * @return true, if successful
 	 */
 	private synchronized boolean validateTest(final AutomationTest test)
	 {
		 
		 if(test.getAutoFunctionalGroupCode() == null)
		 {
			 System.out.println("***Test is not valid!***\nTest has a null Auto Functional Group Code.");
			 return false;
		 }		
		 if(test.getFunctionalGroupCode() == null)
		 {
			 System.out.println("***Test is not valid!***\nTest has a null Functional Group Code.");
			 return false;
		 }		 
		 return true;
	 }
	
	
}
