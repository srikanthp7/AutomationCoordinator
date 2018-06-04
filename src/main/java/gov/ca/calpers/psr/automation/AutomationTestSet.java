package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;
import gov.ca.calpers.psr.automation.server.ui.CollectionsUtil;
import gov.ca.calpers.psr.automation.server.ui.TestSetSelectionCriteria;
import gov.ca.calpers.psr.automation.server.ui.TestSetSelectionType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * The Class AutomationTestSet.
 */
public class AutomationTestSet extends Observable implements
		Comparable<AutomationTestSet>, Serializable, Observer{



	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The test set id. */
	private int testSetId;

	/** The test set name. */
	private String testSetName;

	/** The tests. */
	private Set<AutomationTest> tests;

	/** The retry limit. */
	private int retryLimit;

	/** The test set changed. */
	@Transient
	private transient boolean testSetChanged;

	/** The run status. */
	private String runStatus;

	/** The alm path. */
	private String almPath;

	/** The release. */
	private String release;

	/** The round. */
	private String round;
	
	/** The createdBy. */
	private String createdBy;

	/** The hard stop roll. */
	private char hardStopRoll;
	
	/** The test set selection criteria. */
	private Set<TestSetSelectionCriteria> testSetSelectionCriteria;
	
	/** The persisted in alm. */
	private boolean persistedInALM;
	
	/** The execution complete. */
	private boolean executionComplete;

	/** The Constant log. */
	private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(AutomationTestSet.class);
	
	/**
	 * Instantiates a new automation test set.
	 */
	public AutomationTestSet() {
		testSetName = "No Name";
		testSetChanged = false;
		runStatus = TestSetRunStatus.DRAFT.toString();
		almPath = "Root\\Regression Suite - Automation";
		tests = new HashSet<AutomationTest>();
		setTestSetSelectionCriteria(new HashSet<TestSetSelectionCriteria>());		
		release = "";
		round = "";
		testSetId=0;
		createdBy = System.getProperty("user.name") ;
		setHardStopRoll('N');
		setRetryLimit(0);
		setPersistedInALM(false);
		setExecutionComplete(false);
	}

	/**
	 * Instantiates a new automation test set.
	 *
	 * @param testSetName the test set name
	 */
	public AutomationTestSet(String testSetName) {
		this.testSetName = testSetName;
		testSetChanged = false;
		runStatus = TestSetRunStatus.DRAFT.toString();
		tests = new HashSet<AutomationTest>();
		setTestSetSelectionCriteria(new HashSet<TestSetSelectionCriteria>());
		createdBy = System.getProperty("user.name") ;
		setHardStopRoll('N');
		setRetryLimit(4);
		setPersistedInALM(false);
		setExecutionComplete(false);
	}

	/**
	 * Instantiates a new automation test set.
	 *
	 * @param testSetName the test set name
	 * @param retryLimit the retry limit
	 * @param runStatus the run status
	 */
	public AutomationTestSet(String testSetName, int retryLimit,
			TestSetRunStatus runStatus) {
		this.testSetName = testSetName;
		testSetChanged = false;
		this.runStatus = runStatus.toString();
		tests = new HashSet<AutomationTest>();
		setTestSetSelectionCriteria(new HashSet<TestSetSelectionCriteria>());		
		this.createdBy = System.getProperty("user.name") ;
		setHardStopRoll('N');
		this.setRetryLimit(retryLimit);
		setPersistedInALM(false);
		setExecutionComplete(false);
	}

	/**
	 * Save.
	 *
	 * @return the automation test set
	 * @throws Exception the exception
	 */
	public AutomationTestSet save() throws Exception {

		if (testSetId == 0) {
			try {
				return create(this);
			} catch (Exception e) {
				throw e;
			}
		} else {
			try {
				return update(this);
			} catch (Exception e) {
				throw e;
			}
		}

	}
	
	/**
	 * Retrieve test results.
	 */
	public void retrieveTestResults()
	{
		AutomationTestManager mgr = AutomationTestManager.getAutomationTestManager();
		for(AutomationTest test: tests)
		{
			TestResult result = mgr.getTestResultByTestAndTestSetIds(test.getTestId(), testSetId );
			if(result == null)
			{
				result = new TestResult();
				result.setTest(test);
				result.setTestSet(this);			
			}
			test.setTestResult(result);
		}
		dataHasChanged();
	}

	/**
	 * Creates the.
	 *
	 * @param testSet the test set
	 * @return the automation test set
	 * @throws Exception the exception
	 */
	private static AutomationTestSet create(AutomationTestSet testSet)
			throws Exception {
		Session session = HibernateUtil.getSesssion();
		try {
			session.beginTransaction();
			int id = (Integer) session.save(testSet);
			testSet.setTestSetId(id);
			session.getTransaction().commit();
			session.close();
			for(TestSetSelectionCriteria crit : testSet.getTestSetSelectionCriteria())
			{
				crit.setTestSetId(id);
				TestSetSelectionCriteria temp = crit.save();
				crit.setCriteriaId(temp.getCriteriaId());
			}

		} catch (Exception e) {
			session.close();
			throw e;
		}
		return testSet;
	}

	/**
	 * Update.
	 *
	 * @param testSet the test set
	 * @return the automation test set
	 * @throws Exception the exception
	 */
	private static AutomationTestSet update(AutomationTestSet testSet)
			throws Exception {
		Session session = HibernateUtil.getSesssion();
		try {
			session.beginTransaction();
			session.update(testSet);
			session.getTransaction().commit();
			session.close();
			for(TestSetSelectionCriteria crit : testSet.getTestSetSelectionCriteria())
			{
				crit.setTestSetId(testSet.getTestSetId());
				TestSetSelectionCriteria temp = crit.save();
				crit.setCriteriaId(temp.getCriteriaId());
			}
		} catch (Exception e) {
			session.close();
			throw e;
		}

		return testSet;
	}

	/**
	 * Delete.
	 *
	 * @param testSet the test set
	 */
	public static void delete(AutomationTestSet testSet) {
		Session session = HibernateUtil.getSesssion();
		try {
			session.beginTransaction();
			session.delete(testSet);
			session.getTransaction().commit();
			testSet.setTestSetId(0);
		} catch (HibernateException e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	/**
	 * Gets the test sets with draft run status.
	 *
	 * @return the test sets with draft run status
	 */
	public static CopyOnWriteArrayList<AutomationTestSet> getTestSetsWithDraftRunStatus() {
		CopyOnWriteArrayList<AutomationTestSet> allDraftTestSets = new CopyOnWriteArrayList<AutomationTestSet>();
		Session session = HibernateUtil.getSesssion();

		if (session != null) {
			session.setDefaultReadOnly(true);

			Transaction tx = session.beginTransaction();
			// Criteria criteria = session.createCriteria(AutomationTest.class);
			@SuppressWarnings("unchecked")
			Query query = session
					.createSQLQuery(
							"SELECT * FROM AUTOMATION_TEST_SET WHERE RUN_STATUS = 'DRAFT'")
					.addEntity(AutomationTestSet.class);

			// query.setCacheable(false);
			// List<AutomationTest> list = (criteria.list());
			List<AutomationTestSet> list = (query.list());
			// System.out.println("Criteria.toString= " + criteria.toString());
			System.out.println("Query.toString= " + query.toString());
			for (AutomationTestSet obj : list) {
				CollectionsUtil.addInOrder(allDraftTestSets, obj);
			}
			tx.commit();
			session.close();
		}
		return allDraftTestSets;
	}
	
	/**
	 * Gets the test sets with running run status.
	 *
	 * @return the test sets with running run status
	 */
	public static CopyOnWriteArrayList<AutomationTestSet> getTestSetsWithRunningRunStatus() {
		CopyOnWriteArrayList<AutomationTestSet> allDraftTestSets = new CopyOnWriteArrayList<AutomationTestSet>();
		Session session = HibernateUtil.getSesssion();

		if (session != null) {
			session.setDefaultReadOnly(true);

			Transaction tx = session.beginTransaction();
			// Criteria criteria = session.createCriteria(AutomationTest.class);
			@SuppressWarnings("unchecked")
			Query query = session
					.createSQLQuery(
							"SELECT * FROM AUTOMATION_TEST_SET WHERE RUN_STATUS = 'RUNNING' or RUN_STATUS = 'READY_TO_RUN'")
					.addEntity(AutomationTestSet.class);

			// query.setCacheable(false);
			// List<AutomationTest> list = (criteria.list());
			List<AutomationTestSet> list = (query.list());
			// System.out.println("Criteria.toString= " + criteria.toString());
			System.out.println("Query.toString= " + query.toString());
			for (AutomationTestSet obj : list) {
				CollectionsUtil.addInOrder(allDraftTestSets, obj);
			}
			tx.commit();
			session.close();
		}
		return allDraftTestSets;
	}

	/**
	 * Data has changed.
	 */
	private void dataHasChanged() {
		testSetChanged = true;
		setChanged();
		notifyObservers();

	}

	/* (non-Javadoc)
	 * @see java.util.Observable#hasChanged()
	 */
	@Override
	public boolean hasChanged() {
		return testSetChanged;
	}

	/**
	 * Instantiates a new automation test set.
	 *
	 * @param testSetName the test set name
	 * @param testsList the tests list
	 */
	public AutomationTestSet(String testSetName,
			CopyOnWriteArrayList<AutomationTest> testsList) {
		this.testSetName = testSetName;
		for (AutomationTest test : testsList) {
			this.addTest(test);
		}
		// this.setTests(tests);
	}

	/**
	 * Instantiates a new automation test set.
	 *
	 * @param testSetName the test set name
	 * @param tests the tests
	 */
	public AutomationTestSet(String testSetName, Set<AutomationTest> tests) {
		this.testSetName = testSetName;
		this.setTests(tests);
	}

	/**
	 * Saved test set.
	 */
	public void savedTestSet() {
		testSetChanged = false;
	}

	/**
	 * Gets the test set name.
	 *
	 * @return the test set name
	 */
	public String getTestSetName() {
		return testSetName;
	}

	/**
	 * Sets the test set name.
	 *
	 * @param testSetName the new test set name
	 */
	public void setTestSetName(String testSetName) {
		this.testSetName = testSetName;
		dataHasChanged();
	}

	/**
	 * Sets the tests.
	 *
	 * @param tests            the tests to set
	 */
	public void setTests(Set<AutomationTest> tests) {

		this.tests = tests;
		for(AutomationTest test: this.tests)
		{
			test.addObserver(this);
		}
		dataHasChanged();
	}

	/**
	 * Gets the tests.
	 *
	 * @return the tests
	 */
	public Set<AutomationTest> getTests() {

		return tests;
	}
	
	/**
	 * Gets the test by id.
	 *
	 * @param id the id
	 * @return the test by id
	 */
	public AutomationTest getTestById(long id)
	{
		for(AutomationTest test: tests)
		{
			if(test.getTestId()==id)
			{
				return test;
			}
		}
		return null;
	}

	/**
	 * Gets the tests as list.
	 *
	 * @return the tests
	 */
	public CopyOnWriteArrayList<AutomationTest> getTestsAsList() {
		CopyOnWriteArrayList<AutomationTest> testsList = new CopyOnWriteArrayList<AutomationTest>();
		for (AutomationTest test : tests) {
			CollectionsUtil.addInOrder(testsList, test);
		}
		return testsList;
	}

	/**
	 * Clear tests.
	 */
	public void clearTests() {
		tests.clear();
		dataHasChanged();
	}


	/**
	 * Sets the retry limit.
	 *
	 * @param retryLimit            the retryLimit to set
	 */
	public void setRetryLimit(int retryLimit) {
		this.retryLimit = retryLimit;
		System.out.println("Retry Limit Set to: " + retryLimit);
		dataHasChanged();
	}

	/**
	 * Gets the retry limit.
	 *
	 * @return the retryLimit
	 */
	public int getRetryLimit() {
		return retryLimit;
	}

	/**
	 * Sets the run status.
	 *
	 * @param runStatus            the runStatus to set
	 */
	public synchronized void setRunStatus(String runStatus) {
		this.runStatus = runStatus.toString();
		dataHasChanged();
	}

	/**
	 * Gets the run status.
	 *
	 * @return the runStatus
	 */
	public String getRunStatus() {
		return runStatus;
	}

	/**
	 * Gets the alm path.
	 *
	 * @return the alm path
	 */
	public String getAlmPath() {
		return almPath;
	}

	/**
	 * Sets the alm path.
	 *
	 * @param almPath the new alm path
	 */
	public void setAlmPath(String almPath) {
		this.almPath = almPath;
		dataHasChanged();
	}

	/**
	 * Checks if is ready to run.
	 *
	 * @return the isReadyToRun
	 */
	public boolean isReadyToRun() {

		return this.runStatus.equals(TestSetRunStatus.READY_TO_RUN);
	}

	/**
	 * Adds the test.
	 *
	 * @param test the test
	 * @return true if the test was added, false if the test was not added.
	 */
	public synchronized boolean addTest(AutomationTest test) {
		boolean isNewTest = true;
		for ( AutomationTest tst : tests) {
			if (tst.compareTo(test) == 0) {
				isNewTest = false;
				break;
			}
		}
		if (isNewTest) {
			tests.add(test);
			test.getTestResult().setTestSet(this);
			test.addObserver(this);
			dataHasChanged();
		}

		return isNewTest;
	}

	/**
	 * Removes the test.
	 *
	 * @param test the test
	 * @return true if the test was removed, false if the test was not removed.
	 */
	public synchronized boolean removeTest(AutomationTest test) {
		boolean isRemoved = false;
		for (AutomationTest currTest : tests) {
			if (currTest.getTestName().equals(test.getTestName())) {
				tests.remove(currTest);
				dataHasChanged();
				isRemoved = true;
			}
		}

		return isRemoved;
	}

	/**
	 * Checks for tests.
	 *
	 * @return true if test set does have tests, false if test set has no tests.
	 */
	public boolean hasTests() {
		return !tests.isEmpty();
	}

	/**
	 * Copy.
	 *
	 * @return the automation test set
	 */
	public AutomationTestSet copy() {
		AutomationTestSet newTestSet = new AutomationTestSet();
		newTestSet.setTestSetName(testSetName.toString());
		if (tests != null) {
			for (AutomationTest test : tests) {
				AutomationTest copy = test.copy();
				copy.getTestResult().setTestSet(newTestSet);
				copy.getTestResult().setTest(copy);
				newTestSet.addTest(test.copy());				
			}
		}
		
		if(testSetSelectionCriteria != null)
		{
			for(TestSetSelectionCriteria crit: testSetSelectionCriteria)
			{
				newTestSet.addTestSetSelectionCriteria(crit.copy());
			}
		}
		newTestSet.setRetryLimit(retryLimit);
		newTestSet.setAlmPath(almPath);
		newTestSet.setHardStopRoll(hardStopRoll);
		newTestSet.setRelease(release);
		newTestSet.setRound(round);
		newTestSet.setCreatedBy(createdBy);
		newTestSet.setRunStatus(runStatus);
		newTestSet.setTestSetId(testSetId);
		return newTestSet;

	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return tests.isEmpty();
	}
	
	/**
	 * Persist test results.
	 */
	public void persistTestResults()
	{
		for(AutomationTest test: tests)
		{
			TestResult result = test.getTestResult();
			result.setTestSet(this);
			try{
				TestResult temp = result.save();
				result.setId(temp.getId());				
			}catch(Exception e)
			{
				log.error(e.getLocalizedMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AutomationTestSet o) {
		// TODO Auto-generated method stub
		int compareValue = 0;
		compareValue = this.testSetName.compareTo(o.getTestSetName());
		if (compareValue == 0) {
			if (this.retryLimit == o.getRetryLimit()) {
				if (o.getTests().size() == this.tests.size()) {
					boolean result = true;
					for (AutomationTest test : tests) {
						boolean doesNotExist = true;
						for (AutomationTest aTest : o.getTests()) {
							if (aTest.getTestName().equals(test.getTestName())) {
								doesNotExist = false;
							}
						}
						if (doesNotExist) {
							result = false;
							break;
						}
					}
					if (result) {
						compareValue = Character
								.toString(hardStopRoll)
								.compareTo(
										Character.toString(o.getHardStopRoll()));
						if(this.testSetSelectionCriteria.size() == o.getTestSetSelectionCriteria().size())
						{
							for(TestSetSelectionCriteria crit: testSetSelectionCriteria)
							{
								boolean critExists = false;
								for(TestSetSelectionCriteria crit2 : o.getTestSetSelectionCriteria())
								{
									if(crit2.compareTo(crit) == 0)
									{
										critExists=true;
									}
								}
								if(!critExists)
								{
									result = false;
									break;
								}
							}
							if(!result)
							{
								compareValue = -1;
							}
						}
					} else {
						compareValue = -1;
					}
				} else {
					compareValue = -1;
				}
			} else {
				if (o.getRetryLimit() > this.retryLimit) {
					compareValue = -1;
				} else {
					compareValue = 1;
				}
			}

		}
		return compareValue;
	}

	/**
	 * Gets the test set id.
	 *
	 * @return the testSetId
	 */
	public int getTestSetId() {
		return testSetId;
	}

	/**
	 * Sets the test set id.
	 *
	 * @param testSetId            the testSetId to set
	 */
	public void setTestSetId(int testSetId) {
		this.testSetId = testSetId;
		for(AutomationTest test : tests)
		{
			test.getTestResult().setTestSet(this);
		}
		dataHasChanged();
	}

	/**
	 * Gets the release.
	 *
	 * @return the release
	 */
	public String getRelease() {
		return release;
	}

	/**
	 * Sets the release.
	 *
	 * @param release            the release to set
	 */
	public void setRelease(String release) {
		this.release = release;
		dataHasChanged();
	}

	/**
	 * Gets the round.
	 *
	 * @return the round
	 */
	public String getRound() {
		return round;
	}

	/**
	 * Sets the round.
	 *
	 * @param round            the round to set
	 */
	public void setRound(String round) {
		this.round = round;
		dataHasChanged();
	}
	
	/**
	 * Gets the createdBy.
	 *
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	
	/**
	 * Sets the createdBy.
	 *
	 * @param createdBy            the createdBy to set
	 */

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the hard stop roll.
	 *
	 * @return the hardStopRoll
	 */
	public char getHardStopRoll() {
		return hardStopRoll;
	}

	/**
	 * Sets the hard stop roll.
	 *
	 * @param hardStopRoll            the hardStopRoll to set
	 */
	public void setHardStopRoll(char hardStopRoll) {
		this.hardStopRoll = hardStopRoll;
		dataHasChanged();
	}

	/**
	 * Gets the test set selection criteria.
	 *
	 * @return the testSetSelectionCriteria
	 */
	public Set<TestSetSelectionCriteria> getTestSetSelectionCriteria() {
		return testSetSelectionCriteria;
	}

	/**
	 * Sets the test set selection criteria.
	 *
	 * @param testSetSelectionCriteria the testSetSelectionCriteria to set
	 */
	public void setTestSetSelectionCriteria(Set<TestSetSelectionCriteria> testSetSelectionCriteria) {
		this.testSetSelectionCriteria = testSetSelectionCriteria;
		dataHasChanged();
	}
	
	/**
	 * Adds the test set selection criteria.
	 *
	 * @param criteria the criteria
	 */
	public void addTestSetSelectionCriteria(TestSetSelectionCriteria criteria)
	{
		boolean criteriaExists = false;
		for(TestSetSelectionCriteria crit : testSetSelectionCriteria)
		{
			if(crit.compareTo(criteria) == 0)
			{
				criteriaExists = true;
				break;
			}
		}
		if(!criteriaExists)		
		{
			this.testSetSelectionCriteria.add(criteria);
			dataHasChanged();
		}
		
	}
	
	/**
	 * Removes the test set selection criteria.
	 *
	 * @param funcGrp the func grp
	 * @param selectionType the selection type
	 */
	public void removeTestSetSelectionCriteria(String funcGrp, TestSetSelectionType selectionType)
	{
		for(TestSetSelectionCriteria crit : testSetSelectionCriteria)
		{
			if(funcGrp.equals(crit.getSelectionCode()) && selectionType.toString().equals(crit.getSelectionType().toString()))
			{
				testSetSelectionCriteria.remove(crit);
				if(crit.getCriteriaId() > 0)
				{
					TestSetSelectionCriteria.delete(crit);
				}
				break;
			}
		}
	}
	
	/**
	 * Checks if is persisted in alm.
	 *
	 * @return the persistedInALM
	 */
	public boolean isPersistedInALM() {
		return persistedInALM;
	}

	/**
	 * Sets the persisted in alm.
	 *
	 * @param persistedInALM the persistedInALM to set
	 */
	public void setPersistedInALM(boolean persistedInALM) {
		this.persistedInALM = persistedInALM;
	}

	/**
	 * Checks if is execution complete.
	 *
	 * @return the executionComplete
	 */
	public boolean isExecutionComplete() {
		return executionComplete;
	}

	/**
	 * Sets the execution complete.
	 *
	 * @param executionComplete the executionComplete to set
	 */
	public void setExecutionComplete(boolean executionComplete) {
		this.executionComplete = executionComplete;		
		dataHasChanged();
	}

	/**
	 * Contains.
	 *
	 * @param test the test
	 * @return true, if successful
	 */
	public boolean contains( AutomationTest test)	{
		
		for(AutomationTest aTest : tests)
		{
			if(aTest.equals(test))
			{
				return true;
				
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((almPath == null) ? 0 : almPath.hashCode());
		result = prime * result + hardStopRoll;
		result = prime * result + ((release == null) ? 0 : release.hashCode());
		result = prime * result + retryLimit;
		result = prime * result + ((round == null) ? 0 : round.hashCode());
		result = prime * result + testSetId;
		result = prime * result
				+ ((testSetName == null) ? 0 : testSetName.hashCode());
		result = prime
				* result
				+ ((testSetSelectionCriteria == null) ? 0
						: testSetSelectionCriteria.hashCode());
		result = prime * result + ((tests == null) ? 0 : tests.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AutomationTestSet)) {
			return false;
		}
		AutomationTestSet other = (AutomationTestSet) obj;
		if (almPath == null) {
			if (other.almPath != null) {
				return false;
			}
		} else if (!almPath.equals(other.almPath)) {
			return false;
		}
		if (hardStopRoll != other.hardStopRoll) {
			return false;
		}
		if (release == null) {
			if (other.release != null) {
				return false;
			}
		} else if (!release.equals(other.release)) {
			return false;
		}
		if (retryLimit != other.retryLimit) {
			return false;
		}
		if (round == null) {
			if (other.round != null) {
				return false;
			}
		} else if (!round.equals(other.round)) {
			return false;
		}
		if (testSetId != other.testSetId) {
			return false;
		}
		if (testSetName == null) {
			if (other.testSetName != null) {
				return false;
			}
		} else if (!testSetName.equals(other.testSetName)) {
			return false;
		}
		if (testSetSelectionCriteria == null) {
			if (other.testSetSelectionCriteria != null) {
				return false;
			}
		} else if (!testSetSelectionCriteria
				.equals(other.testSetSelectionCriteria)) {
			return false;
		}
		if (tests == null) {
			if (other.tests != null) {
				return false;
			}
		} else if (!tests.equals(other.tests)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return testSetName;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		dataHasChanged();
	}

}