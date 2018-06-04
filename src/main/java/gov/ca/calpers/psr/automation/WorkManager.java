package gov.ca.calpers.psr.automation;

import gov.ca.calpers.psr.automation.directed.graph.DirectedGraph;
import gov.ca.calpers.psr.automation.directed.graph.Node;
import gov.ca.calpers.psr.automation.directed.graph.TreeManager;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;

public class WorkManager
  implements Observer
{
  private final AutomationTestSet testSet;
  private CopyOnWriteArrayListWithStatus<AutomationTest> independentTestList = new CopyOnWriteArrayListWithStatus();
  private CopyOnWriteArrayListWithStatus<AutomationTest> preRollIndependentTestList = new CopyOnWriteArrayListWithStatus();
  private CopyOnWriteArrayListWithStatus<AutomationTest> postRollIndependentTestList = new CopyOnWriteArrayListWithStatus();
  private CopyOnWriteArrayListWithStatus<DirectedGraph> dependencyTreeList;
  private CopyOnWriteArrayListWithStatus<CopyOnWriteArrayListWithStatus<Node>> topologicalSortedList;
  private TreeManager treeManager;
  private DependencyWorkBucket dependencyWorkBucket;
  private boolean hasProcessedTestSet = false;
  private ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustRunTests = new ConcurrentHashMap();
  private ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustPassTests = new ConcurrentHashMap();
  
  public WorkManager(AutomationTestSet testSet)
  {
    this.testSet = testSet;
    this.treeManager = new TreeManager(this.testSet);
    this.dependencyWorkBucket = new DependencyWorkBucket();
    if (this.testSet.getTestSetId() != 0) {
      try
      {
        processTestSet();
      }
      catch (IllegalArgumentException t)
      {
        JOptionPane.showMessageDialog(null, "Dependency Tree Error", "Cycle Detected in dependencies. Please check dependencies and remove the cycle before saving the test set again.", 0);
      }
    }
  }
  
  public AutomationTestSet getTestSet()
  {
    return this.testSet;
  }
  
  public CopyOnWriteArrayList<AutomationTest> getIndependentTestList()
  {
    return this.independentTestList;
  }
  
  public CopyOnWriteArrayList<DirectedGraph> getDependencyTreeList()
  {
    return this.dependencyTreeList;
  }
  
  public boolean processTestSet()
    throws IllegalArgumentException
  {
    System.out.println("Starting to process Test Set");
    this.independentTestList = new CopyOnWriteArrayListWithStatus();
    this.dependencyTreeList = new CopyOnWriteArrayListWithStatus();
    this.treeManager = new TreeManager(this.testSet);
    CopyOnWriteArrayList<AutomationTest> possibleIndependentTestList = new CopyOnWriteArrayList();
    CopyOnWriteArrayList<AutomationTest> possiblePreRollIndependentTestList = new CopyOnWriteArrayList();
    CopyOnWriteArrayList<AutomationTest> possiblePostRollIndependentTestList = new CopyOnWriteArrayList();
    CopyOnWriteArrayList<AutomationTest> possibleRollIndependentTestList = new CopyOnWriteArrayList();
    CopyOnWriteArrayList<AutomationTest> dependentTestList = new CopyOnWriteArrayList();
    ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingTests = null;
    for (AutomationTest test : this.testSet.getTestsAsList())
    {
      System.out.println("Processing test: " + test.getTestName());
      if (test.getTestDependencies().isEmpty())
      {
        if (!this.treeManager.testExistsInATree(test))
        {
          System.out.println(test.getTestName() + " does not exist in a tree.");
          if ((test.getRollIndicator() == null) || (test.getRollIndicator().equals(RollIndicatorEnum.NONE)))
          {
            possibleIndependentTestList.add(test);
            System.out.println("Adding " + test.getTestName() + " to the possible independent test list.");
          }
          else if (test.getRollIndicator().equals(RollIndicatorEnum.PRE_ROLL))
          {
            possiblePreRollIndependentTestList.add(test);
            System.out.println("Adding " + test.getTestName() + " to the possible pre-roll independent test list.");
          }
          else if (test.getRollIndicator().equals(RollIndicatorEnum.POST_ROLL))
          {
            possiblePostRollIndependentTestList.add(test);
            System.out.println("Adding " + test.getTestName() + " to the possible post-roll independent test list.");
          }
          else if (test.getRollIndicator().equals(RollIndicatorEnum.ROLL))
          {
            possibleRollIndependentTestList.add(test);
            System.out.println("Adding " + test.getTestName() + " to the possible roll independent test list.");
          }
        }
        else
        {
          System.out.println(test.getTestName() + " already exists in a tree. Not adding test to the independent test list.");
        }
      }
      else
      {
        System.out.println("Adding " + test.getTestName() + " to the Tree Manager.");
        dependentTestList.add(test);
      }
    }
    System.out.println("Adding dependent tests to tree manager");
    
    boolean hasMissingTests = this.treeManager.addTests(dependentTestList);
    if (hasMissingTests)
    {
      this.missingMustRunTests = this.treeManager.getMissingMustRunTests();
      this.missingMustPassTests = this.treeManager.getMissingMustPassTests();
    }
    for (AutomationTest test : possibleIndependentTestList) {
      if (!this.treeManager.testExistsInATree(test)) {
        this.independentTestList.add(test);
      }
    }
    for (AutomationTest test : possiblePreRollIndependentTestList) {
      if (!this.treeManager.testExistsInATree(test)) {
        this.preRollIndependentTestList.add(test);
      }
    }
    for (AutomationTest test : possiblePostRollIndependentTestList) {
      if (!this.treeManager.testExistsInATree(test)) {
        this.postRollIndependentTestList.add(test);
      }
    }
    for (AutomationTest test : possibleRollIndependentTestList) {
      if (!this.treeManager.testExistsInATree(test)) {
        System.out.println("ERROR!!!! A Roll test was not added to a dependency Tree ( " + test.getTestName() + " ). ");
      }
    }
    this.dependencyTreeList = this.treeManager.getTreeList();
    System.out.println("Number of Digraphs created: " + this.dependencyTreeList.size());
    System.out.println("**************************************************");
    int counter = 0;
    for (DirectedGraph graph : this.dependencyTreeList)
    {
      System.out.println("Number of tests in digraph #" + counter++ + ": " + graph.size());
      Iterator<Node> iter = graph.iterator();
      while (iter.hasNext())
      {
        Node node = (Node)iter.next();
        System.out.println(node.getName());
      }
      System.out.println("--------------------------------------------");
    }
    try
    {
      this.topologicalSortedList = this.treeManager.getTopologicalSortedList();
    }
    catch (IllegalArgumentException t)
    {
      throw new IllegalArgumentException("Error: " + t.getMessage(), t);
    }
    int listCount;
    if (this.topologicalSortedList != null)
    {
      listCount = 0;
      for (CopyOnWriteArrayList<Node> list : this.topologicalSortedList)
      {
        System.out.println("Displaying Sorted List of Digraph #" + ++listCount);
       int nodeCount = 0;
        for (Node node : list) {
          System.out.println("Node #" + ++nodeCount + ": " + node.getName());
        }
      }
    }
    
    System.out.println("Total Number of Independent Tests: " + this.independentTestList.size());
    try
    {
      this.dependencyWorkBucket = this.treeManager.getWork();
    }
    catch (IllegalArgumentException t)
    {
      throw new IllegalArgumentException("Error: " + t.getMessage(), t);
    }
    System.out.println("Total Number of Pre Roll Tree Lists: " + this.dependencyWorkBucket.getPreRollTreeList().size());
    int preRollTreeCounter;
    if (this.dependencyWorkBucket.getPreRollTreeList().size() > 0)
    {
      preRollTreeCounter = 0;
      for (CopyOnWriteArrayList<Node> list : this.dependencyWorkBucket.getPreRollTreeList())
      {
        System.out.println("Pre-Roll Tree List # " + ++preRollTreeCounter);
        System.out.println("--Tree List Size: " + list.size());
       int  preRollTreeNodeCounter = 0;
        for (Node node : list) {
          System.out.println("--Node " + ++preRollTreeNodeCounter + ": " + node.getName());
        }
      }
    }
  
    this.dependencyWorkBucket.addIndependentPreRollTests(this.preRollIndependentTestList);
    System.out.println("Total Number of Pre Roll Independent Tests: " + this.dependencyWorkBucket.getIndependentPreRollTests().size());
    int preRollTestCounter;
    if (this.dependencyWorkBucket.getIndependentPreRollTests().size() > 0)
    {
      preRollTestCounter = 0;
      for (Node test : this.dependencyWorkBucket.getIndependentPreRollTests()) {
        System.out.println("Pre-Roll Independent Test # " + ++preRollTestCounter + ": " + test.getName());
      }
    }
    System.out.println("Total Number of Roll Tree Lists: " + this.dependencyWorkBucket.getRollTreeList().size());
    int rollTreeCounter;
    if (this.dependencyWorkBucket.getRollTreeList().size() > 0)
    {
      rollTreeCounter = 0;
      for (CopyOnWriteArrayList<Node> list : this.dependencyWorkBucket.getRollTreeList())
      {
        System.out.println("Roll Tree List # " + ++rollTreeCounter);
        System.out.println("--Tree List Size: " + list.size());
        int rollTreeNodeCounter = 0;
        for (Node node : list) {
          System.out.println("--Node " + ++rollTreeNodeCounter + ": " + node.getName());
        }
      }
    }
    
    this.dependencyWorkBucket.addIndependentPostRollTests(this.postRollIndependentTestList);
    System.out.println("Total Number of Post Roll Tree Lists: " + this.dependencyWorkBucket.getPostRollTreeList().size());
    int postRollTreeCounter;
    if (this.dependencyWorkBucket.getPostRollTreeList().size() > 0)
    {
      postRollTreeCounter = 0;
      for (CopyOnWriteArrayList<Node> list : this.dependencyWorkBucket.getPostRollTreeList())
      {
        System.out.println("Post-Roll Tree List # " + ++postRollTreeCounter);
        System.out.println("--Tree List Size: " + list.size());
        int postRollTreeNodeCounter = 0;
        for (Node node : list) {
          System.out.println("--Node " + ++postRollTreeNodeCounter + ": " + node.getName());
        }
      }
    }
    
    System.out.println("Total Number of Post Roll Independent Tests: " + this.dependencyWorkBucket.getIndependentPostRollTests().size());
    int postRollTestCounter;
    if (this.dependencyWorkBucket.getIndependentPostRollTests().size() > 0)
    {
      postRollTestCounter = 0;
      for (Node test : this.dependencyWorkBucket.getIndependentPostRollTests()) {
        System.out.println("Pre-Roll Independent Test # " + ++postRollTestCounter + ": " + test.getName());
      }
    }
    System.out.println("Total Number of Non-Roll Tree Lists: " + this.dependencyWorkBucket.getNonRollTreeList().size());
    int nonRollTreeCounter;
    if (this.dependencyWorkBucket.getNonRollTreeList().size() > 0)
    {
      nonRollTreeCounter = 0;
      for (CopyOnWriteArrayList<Node> list : this.dependencyWorkBucket.getNonRollTreeList())
      {
        System.out.println("Non-Roll Tree List # " + ++nonRollTreeCounter);
        System.out.println("--Tree List Size: " + list.size());
        int nonRollTreeNodeCounter = 0;
        for (Node node : list) {
          System.out.println("--Node " + ++nonRollTreeNodeCounter + ": " + node.getName());
        }
      }
    }
    
    this.hasProcessedTestSet = true;
    return hasMissingTests;
  }
  
  public boolean hasProcessedTestSet()
  {
    return this.hasProcessedTestSet;
  }
  
  public DependencyWorkBucket getDependencyWorkBucket()
  {
    return this.dependencyWorkBucket;
  }
  
  public ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> getMissingMustRunTests()
  {
    return this.missingMustRunTests;
  }
  
  public void setMissingMustRunTests(ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustRunTests)
  {
    this.missingMustRunTests = missingMustRunTests;
  }
  
  public ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> getMissingMustPassTests()
  {
    return this.missingMustPassTests;
  }
  
  public void setMissingMustPassTests(ConcurrentHashMap<AutomationTest, CopyOnWriteArrayList<AutomationTest>> missingMustPassTests)
  {
    this.missingMustPassTests = missingMustPassTests;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.dependencyWorkBucket == null ? 0 : this.dependencyWorkBucket.hashCode());
    
    result = 31 * result + (this.independentTestList == null ? 0 : this.independentTestList.hashCode());
    
    result = 31 * result + (this.testSet == null ? 0 : this.testSet.hashCode());
    result = 31 * result + (this.treeManager == null ? 0 : this.treeManager.hashCode());
    
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof WorkManager)) {
      return false;
    }
    WorkManager other = (WorkManager)obj;
    if (this.dependencyWorkBucket == null)
    {
      if (other.dependencyWorkBucket != null) {
        return false;
      }
    }
    else if (!this.dependencyWorkBucket.equals(other.dependencyWorkBucket)) {
      return false;
    }
    if (this.independentTestList == null)
    {
      if (other.independentTestList != null) {
        return false;
      }
    }
    else if (!this.independentTestList.equals(other.independentTestList)) {
      return false;
    }
    if (this.testSet == null)
    {
      if (other.testSet != null) {
        return false;
      }
    }
    else if (!this.testSet.equals(other.testSet)) {
      return false;
    }
    if (this.treeManager == null)
    {
      if (other.treeManager != null) {
        return false;
      }
    }
    else if (!this.treeManager.equals(other.treeManager)) {
      return false;
    }
    return true;
  }
  
  public void update(Observable o, Object arg) {}
}
