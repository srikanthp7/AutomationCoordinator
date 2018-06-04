package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TestSetSelectionCriteria
  implements Comparable<TestSetSelectionCriteria>, Serializable
{
  private static final long serialVersionUID = 1L;
  private long criteriaId;
  private long testSetId;
  private String selectionCode;
  private TestSetSelectionType selectionType;
  
  public TestSetSelectionCriteria save()
  {
    if (this.criteriaId == 0L) {
      return create(this);
    }
    return update(this);
  }
  
  public static TestSetSelectionCriteria create(TestSetSelectionCriteria criteria)
  {
    Session session = HibernateUtil.getSesssion();
    try
    {
      session.beginTransaction();
      long id = ((Long)session.save(criteria)).longValue();
      criteria.setCriteriaId(id);
      session.getTransaction().commit();
      session.close();
    }
    catch (Exception e)
    {
      session.close();
      e.printStackTrace();
    }
    return criteria;
  }
  
  public static TestSetSelectionCriteria update(TestSetSelectionCriteria criteria)
  {
    Session session = HibernateUtil.getSesssion();
    try
    {
      session.beginTransaction();
      session.update(criteria);
      session.getTransaction().commit();
      session.close();
    }
    catch (Exception e)
    {
      session.close();
      e.printStackTrace();
    }
    return criteria;
  }
  
  public static synchronized void delete(TestSetSelectionCriteria criteria)
  {
    Session session = HibernateUtil.getSesssion();
    try
    {
      session.beginTransaction();
      session.delete(criteria);
      session.getTransaction().commit();
      criteria.setCriteriaId(0L);
    }
    catch (HibernateException e)
    {
      if (session.getTransaction() != null) {
        session.getTransaction().rollback();
      }
      e.printStackTrace();
    }
    finally
    {
      session.close();
    }
  }
  
  public static synchronized Set<TestSetSelectionCriteria> getTestSetSelectionCriteriaByTestSetId(long testSetId)
  {
    Set<TestSetSelectionCriteria> selectedCriteria = new HashSet(0);
    Session session = HibernateUtil.getSesssion();
    if (session != null)
    {
      session.setDefaultReadOnly(true);
      
      Transaction tx = session.beginTransaction();
      
      Query query = session.createSQLQuery("SELECT * FROM TS_SELECTION_CRITERIA WHERE AUTOMATION_TEST_SET_ID= :testSetId").addEntity(TestSetSelectionCriteria.class);
      query.setParameter("testSetId", Long.valueOf(testSetId));
      List<TestSetSelectionCriteria> list = query.list();
      System.out.println("Query.toString= " + query.toString());
      for (TestSetSelectionCriteria obj : list)
      {
        selectedCriteria.add(obj);
        System.out.println("*** CRITERIA ADDED ***");
      }
      tx.commit();
      session.close();
    }
    return selectedCriteria;
  }
  
  public long getCriteriaId()
  {
    return this.criteriaId;
  }
  
  public void setCriteriaId(long criteriaId)
  {
    this.criteriaId = criteriaId;
  }
  
  public long getTestSetId()
  {
    return this.testSetId;
  }
  
  public void setTestSetId(long testSetId)
  {
    this.testSetId = testSetId;
  }
  
  public String getSelectionCode()
  {
    return this.selectionCode;
  }
  
  public void setSelectionCode(String selectionCode)
  {
    this.selectionCode = selectionCode;
  }
  
  public TestSetSelectionType getSelectionType()
  {
    return this.selectionType;
  }
  
  public void setSelectionType(TestSetSelectionType selectionType)
  {
    this.selectionType = selectionType;
  }
  
  public int compareTo(TestSetSelectionCriteria o)
  {
    if ((this.selectionCode.equals(o.getSelectionCode())) && (this.selectionType.equals(o.getSelectionType()))) {
      return 0;
    }
    return -1;
  }
  
  public TestSetSelectionCriteria copy()
  {
    TestSetSelectionCriteria newCriteria = new TestSetSelectionCriteria();
    newCriteria.setCriteriaId(getCriteriaId());
    newCriteria.setSelectionCode(getSelectionCode());
    newCriteria.setSelectionType(getSelectionType());
    newCriteria.setTestSetId(getTestSetId());
    return newCriteria;
  }
}
