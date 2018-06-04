package gov.ca.calpers.psr.automation.server.ui;

import gov.ca.calpers.psr.automation.AutomationFunctionalGroup;
import gov.ca.calpers.psr.automation.AutomationTest;
import gov.ca.calpers.psr.automation.AutomationTestSet;
import gov.ca.calpers.psr.automation.ExecutionStatus;
import gov.ca.calpers.psr.automation.TestResult;
import gov.ca.calpers.psr.automation.UnitOfWork;
import gov.ca.calpers.psr.automation.UnitOfWorkFlow;
import gov.ca.calpers.psr.automation.WorkerBucket;
import gov.ca.calpers.psr.automation.directed.graph.Edge;
import gov.ca.calpers.psr.automation.interfaces.NodeValueChanged;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * The Class RoadMap.
 */
public class RoadMap extends AutomationTestTreeForDisplay implements
		NodeValueChanged, Observer, MouseListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The nodes to update. */
	private final Set<TreeNode> nodesToUpdate = new HashSet<TreeNode>();

	/** The test set. */
	private AutomationTestSet theTestSet;

	/** The all work. */
	private CopyOnWriteArrayList<UnitOfWork> allWork = null;

	/** The work flow. */
	private UnitOfWorkFlow workFlow = null;

	/** The view. */
	private ScenariosView view;

	/**
	 * Instantiates a new road map.
	 * 
	 * @param funcGroups
	 *            the func groups
	 * @param testSet
	 *            the test set
	 * @param view
	 *            the view
	 */
	public RoadMap(List<AutomationFunctionalGroup> funcGroups,
			AutomationTestSet testSet, ScenariosView view) {
		super(funcGroups, testSet);
		theTestSet = testSet;
		this.view = view;
		this.getRoot().setUserObject(theTestSet);
		theTestSet.addObserver(this);
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		for (AutomationTest test : theTestSet.getTestsAsList()) {
			this.addTestToTree(test);
		}
		this.addMouseListener(this);
	}

	/**
	 * Register popup.
	 */
	public void registerPopup() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {

				if (e.isPopupTrigger()) {
					TreePath path = getPathForLocation(e.getX(), e.getY());
					TreePath[] selectionPaths = getSelectionPaths();
					TreePath[] newSelection = new TreePath[selectionPaths.length + 1];
					for (int i = 0; i < selectionPaths.length; i++) {
						newSelection[i] = selectionPaths[i];
					}
					newSelection[selectionPaths.length] = path;
					setSelectionPaths(newSelection);
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							final TreePath[] selectedPath = getSelectionPaths();
							if (selectedPath.length <= 0) {
								return;
							}
							JPopupMenu popup = new JPopupMenu();
							JMenuItem passMenu = new JMenuItem("Passed");
							JMenuItem notRunMenu = new JMenuItem("Not Run");
							JMenuItem blockedMenu = new JMenuItem("Blocked");
							JMenuItem failedMenu = new JMenuItem("Failed");

							JMenu depMenu = new JMenu("Dependencies");
							boolean hasDependencies = false;
							for (TreePath path : selectedPath) {
								DefaultMutableTreeNode nodeValue = (DefaultMutableTreeNode) path
										.getLastPathComponent();
								if (nodeValue.getUserObject() instanceof AutomationTest) {
									AutomationTest test = (AutomationTest) nodeValue
											.getUserObject();
									if (allWork != null && !allWork.isEmpty()
											&& workFlow != null) {
										for (UnitOfWork work : allWork) {
											if (work.getAutoTest()
													.getTestName()
													.equals(test.getTestName())) {
												for (Edge edge : work
														.getInEdges()) {
													hasDependencies = true;
													JMenu menu = new JMenu(edge
															.getChildNode()
															.getName());
													if (edge.isDisabled()) {
														JMenuItem enableItem = new JMenuItem(
																"Re-Enable");
														enableItem
																.setActionCommand("RE_ENABLE");
														enableItem
																.addActionListener(edge);
														menu.add(enableItem);
													} else {
														JMenuItem disableItem = new JMenuItem(
																"Disable");
														disableItem
																.setActionCommand("DISABLE");
														disableItem
																.addActionListener(edge);
														menu.add(disableItem);
													}
													depMenu.add(menu);
												}
												break;
											}
										}
									}
								}
							}

							popup.add(passMenu);
							popup.add(notRunMenu);
							popup.add(blockedMenu);
							popup.add(failedMenu);
							if (hasDependencies) {
								popup.add(depMenu);
							}

							passMenu.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									updateStatus(selectedPath,
											ExecutionStatus.MANUALLY_PASSED);

								}

							});
							notRunMenu.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									updateStatus(selectedPath,
											ExecutionStatus.NOT_RUN);
								}

							});

							blockedMenu.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									updateStatus(selectedPath,
											ExecutionStatus.BLOCKED);
								}
							});
							failedMenu.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									updateStatus(selectedPath,
											ExecutionStatus.FAILED);
								}
							});
							popup.show(e.getComponent(), e.getX(), e.getY());

						}

						private void updateStatus(
								final TreePath[] selectedPath,
								ExecutionStatus status) {
							for (TreePath path : selectedPath) {
								DefaultMutableTreeNode nodeValue = (DefaultMutableTreeNode) path
										.getLastPathComponent();
								if (nodeValue.getUserObject() instanceof WorkerBucket) {
									WorkerBucket bucket = (WorkerBucket) nodeValue
											.getUserObject();
									setAllChildToState(bucket, status);
								}
								if (nodeValue.getUserObject() instanceof UnitOfWork) {
									UnitOfWork newTest = (UnitOfWork) nodeValue
											.getUserObject();
									newTest.updateStatus(status);
									workFlow.reportWorkStatus(newTest, status,
											true);
								}
								if (nodeValue.getUserObject() instanceof AutomationTest) {
									AutomationTest newTest = (AutomationTest) nodeValue
											.getUserObject();
									if (allWork != null && !allWork.isEmpty()
											&& workFlow != null) {
										for (UnitOfWork work : allWork) {
											if (work.getAutoTest()
													.getTestName()
													.equals(newTest
															.getTestName())) {
												workFlow.reportWorkStatus(work,
														status, true);
												break;
											}
										}
									} else {
										newTest.setExecutionStatus(status);
										TestResult result;
										try {
										
											result = newTest.getTestResult().save();
											newTest.getTestResult().setId(result.getId());
											
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						}
					});

				}
			}
		});
	}

	/**
	 * Register selection view.
	 */
	public void registerSelectionView() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				TreePath[] selectionPaths = getSelectionPaths();
				if (selectionPaths.length != 0) {
					TreePath selectionPath = selectionPaths[0];
					DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) selectionPath
							.getLastPathComponent();
					CopyOnWriteArrayList<AutomationTest> selectedTests = new CopyOnWriteArrayList<AutomationTest>();
					if (lastNode.getUserObject() instanceof AutomationFunctionalGroup) {
						AutomationFunctionalGroup currGroup = (AutomationFunctionalGroup) lastNode
								.getUserObject();

						for (AutomationTest test : theTestSet.getTestsAsList()) {
							if (test.getAutoFunctionalGroupCode().equals(
									currGroup.getAutoFunctionalGroupCode())) {
								selectedTests.add(test);
							}

						}
						List<AutomationTest> list = new ArrayList<AutomationTest>(
								selectedTests);
						Collections.sort(list);
					} else if (lastNode.getUserObject() instanceof AutomationTestSet) {
						AutomationTestSet testSet = (AutomationTestSet) lastNode
								.getUserObject();
						for (AutomationTest test : testSet.getTestsAsList()) {
							selectedTests.add(test);
						}
					}
					if (!selectedTests.isEmpty()) {
						List<AutomationTest> list = new ArrayList<AutomationTest>(
								selectedTests);
						Collections.sort(list);
						CopyOnWriteArrayList<AutomationTest> returnList = new CopyOnWriteArrayList<AutomationTest>(
								list);
						view.setTestsList(returnList);
					}
				}
			}
		});
	}

	/**
	 * Sets the all child to state.
	 * 
	 * @param parent
	 *            the parent
	 * @param state
	 *            the state
	 */
	private void setAllChildToState(WorkerBucket parent, ExecutionStatus state) {
		List<UnitOfWork> allWorks = parent.getAllWorks();
		for (UnitOfWork work : allWorks) {
			if (work instanceof WorkerBucket) {
				WorkerBucket newParent = (WorkerBucket) work;
				setAllChildToState(newParent, state);
			}
			work.updateStatus(state);

		}
	}

	/**
	 * Update.
	 * 
	 * @param root
	 *            the root
	 */
	public void update(UnitOfWork root) {

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
		root.setCallback(this, rootNode);
		if (root instanceof WorkerBucket) {
			addToNode(rootNode, (WorkerBucket) root);
		}
		setModel(new DefaultTreeModel(rootNode));
	}

	/**
	 * Adds the to node.
	 * 
	 * @param rootNode
	 *            the root node
	 * @param bucket
	 *            the bucket
	 */
	public void addToNode(DefaultMutableTreeNode rootNode, WorkerBucket bucket) {
		for (UnitOfWork work : bucket.getAllWorks()) {
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(work);
			work.setCallback(this, newNode);
			rootNode.add(newNode);
			if (work instanceof WorkerBucket) {
				WorkerBucket theBucket = (WorkerBucket) work;
				addToNode(newNode, theBucket);
			}
		}
	}

	/**
	 * Gets the the test set.
	 * 
	 * @return the theTestSet
	 */
	public AutomationTestSet getTheTestSet() {
		return theTestSet;
	}

	/**
	 * Sets the test set.
	 * 
	 * @param theTestSet
	 *            the theTestSet to set
	 */
	public void setTestSet(AutomationTestSet theTestSet) {
		this.theTestSet = theTestSet;

	}

	/**
	 * Gets the all work.
	 * 
	 * @return the allWork
	 */
	public CopyOnWriteArrayList<UnitOfWork> getAllWork() {
		return allWork;
	}

	/**
	 * Sets the all work.
	 * 
	 * @param allWork
	 *            the allWork to set
	 */
	public void setAllWork(CopyOnWriteArrayList<UnitOfWork> allWork) {
		this.allWork = allWork;
	}

	/**
	 * Gets the work flow.
	 * 
	 * @return the workFlow
	 */
	public UnitOfWorkFlow getWorkFlow() {
		return workFlow;
	}

	/**
	 * Sets the work flow.
	 * 
	 * @param workFlow
	 *            the workFlow to set
	 */
	public void setWorkFlow(UnitOfWorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.ca.calpers.psr.automation.interfaces.NodeValueChanged#notifyChanged
	 * (java.lang.Object)
	 */
	@Override
	public void notifyChanged(Object theNode) {
		final TreeNode associatedNode = (TreeNode) theNode;
		DefaultMutableTreeNode userNode = (DefaultMutableTreeNode) associatedNode;
		UnitOfWork data = (UnitOfWork) userNode.getUserObject();
		if (data.getExecutionStatus().equals(ExecutionStatus.IN_PROGRESS)) {
			nodesToUpdate.add(associatedNode);
		} else {
			nodesToUpdate.remove(associatedNode);
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((DefaultTreeModel) getModel()).nodeChanged(associatedNode);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.removeAllTestsFromTree();
		for (AutomationTest test : theTestSet.getTestsAsList()) {
			this.addTestToTree(test);
		}

		if (theTestSet.hasTests() && this.getSelectionPath() == null) {
			DefaultMutableTreeNode rootNode = getRoot();
			TreePath path = new TreePath(rootNode.getPath());
			this.setSelectionPath(path);
		}
		TreePath[] selectionPaths = getSelectionPaths();
		if (selectionPaths.length != 0) {
			TreePath selectionPath = selectionPaths[0];
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) selectionPath
					.getLastPathComponent();
			CopyOnWriteArrayList<AutomationTest> selectedTests = new CopyOnWriteArrayList<AutomationTest>();
			if (lastNode.getUserObject() instanceof AutomationFunctionalGroup) {
				AutomationFunctionalGroup currGroup = (AutomationFunctionalGroup) lastNode
						.getUserObject();

				for (AutomationTest test : theTestSet.getTestsAsList()) {
					if (test.getAutoFunctionalGroupCode().equals(
							currGroup.getAutoFunctionalGroupCode())) {
						selectedTests.add(test);
					}

				}
				List<AutomationTest> list = new ArrayList<AutomationTest>(
						selectedTests);
				Collections.sort(list);
			} else if (lastNode.getUserObject() instanceof AutomationTestSet) {
				AutomationTestSet testSet = (AutomationTestSet) lastNode
						.getUserObject();
				for (AutomationTest test : testSet.getTestsAsList()) {
					selectedTests.add(test);
				}
			}
			if (!selectedTests.isEmpty()) {
				List<AutomationTest> list = new ArrayList<AutomationTest>(
						selectedTests);
				Collections.sort(list);
				CopyOnWriteArrayList<AutomationTest> returnList = new CopyOnWriteArrayList<AutomationTest>(
						list);
				view.setTestsList(returnList);
			}
		}

	}

	/**
	 * Update status.
	 * 
	 * @param selectedPath
	 *            the selected path
	 * @param status
	 *            the status
	 */
	private void updateStatus(final TreePath[] selectedPath,
			ExecutionStatus status) {
		for (TreePath path : selectedPath) {
			DefaultMutableTreeNode nodeValue = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			if (nodeValue.getUserObject() instanceof WorkerBucket) {
				WorkerBucket bucket = (WorkerBucket) nodeValue.getUserObject();
				setAllChildToState(bucket, status);
			}
			if (nodeValue.getUserObject() instanceof UnitOfWork) {
				UnitOfWork newTest = (UnitOfWork) nodeValue.getUserObject();
				newTest.updateStatus(status);
				workFlow.reportWorkStatus(newTest, status, true);
			}
			if (nodeValue.getUserObject() instanceof AutomationTest) {
				AutomationTest newTest = (AutomationTest) nodeValue
						.getUserObject();
				if (allWork != null && !allWork.isEmpty() && workFlow != null) {
					for (UnitOfWork work : allWork) {
						if (work.getAutoTest().getTestName()
								.equals(newTest.getTestName())) {
							workFlow.reportWorkStatus(work, status, true);
							break;
						}
					}
				} else {
					newTest.setExecutionStatus(status);
					try {
						newTest.getTestResult().save();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			int selRow = this.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = this.getPathForLocation(e.getX(), e.getY());
			this.setSelectionPath(selPath);
			if (selRow > -1) {
				this.setSelectionRow(selRow);
			}
		}
		if (e.isPopupTrigger()) {
			TreePath path = getPathForLocation(e.getX(), e.getY());
			TreePath[] selectionPaths = getSelectionPaths();
			TreePath[] newSelection = new TreePath[selectionPaths.length + 1];
			for (int i = 0; i < selectionPaths.length; i++) {
				newSelection[i] = selectionPaths[i];
			}
			newSelection[selectionPaths.length] = path;
			setSelectionPaths(newSelection);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					final TreePath[] selectedPath = getSelectionPaths();
					if (selectedPath.length <= 0) {
						return;
					}
					JPopupMenu popup = new JPopupMenu();
					JMenuItem passMenu = new JMenuItem("Passed");
					JMenuItem notRunMenu = new JMenuItem("Not Run");
					JMenuItem blockedMenu = new JMenuItem("Blocked");
					JMenuItem failedMenu = new JMenuItem("Failed");

					JMenu depMenu = new JMenu("Dependencies");
					boolean hasDependencies = false;
					boolean selectionIsATest = true;
					for (TreePath path : selectedPath) {
						if (!(((DefaultMutableTreeNode) path
								.getLastPathComponent()).getUserObject() instanceof AutomationTest)) {
							selectionIsATest = false;
						}
						DefaultMutableTreeNode nodeValue = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						if (nodeValue.getUserObject() instanceof AutomationTest) {
							AutomationTest test = (AutomationTest) nodeValue
									.getUserObject();
							if (allWork != null && !allWork.isEmpty()
									&& workFlow != null) {
								for (UnitOfWork work : allWork) {
									if (work.getAutoTest().getTestName()
											.equals(test.getTestName())) {
										for (Edge edge : work.getInEdges()) {
											hasDependencies = true;
											JMenu menu = new JMenu(edge
													.getChildNode().getName());
											if (edge.isDisabled()) {
												JMenuItem enableItem = new JMenuItem(
														"Re-Enable");
												enableItem
														.setActionCommand("RE_ENABLE");
												enableItem
														.addActionListener(edge);
												menu.add(enableItem);
											} else {
												JMenuItem disableItem = new JMenuItem(
														"Disable");
												disableItem
														.setActionCommand("DISABLE");
												disableItem
														.addActionListener(edge);
												menu.add(disableItem);
											}
											depMenu.add(menu);
										}
										break;
									}
								}
							}
						}
					}

					popup.add(passMenu);
					popup.add(notRunMenu);
					popup.add(blockedMenu);
					popup.add(failedMenu);
					if (hasDependencies) {
						popup.add(depMenu);
					}

					passMenu.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateStatus(selectedPath,
									ExecutionStatus.MANUALLY_PASSED);

						}

					});
					notRunMenu.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateStatus(selectedPath, ExecutionStatus.NOT_RUN);
						}

					});

					blockedMenu.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateStatus(selectedPath, ExecutionStatus.BLOCKED);
						}
					});
					failedMenu.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateStatus(selectedPath, ExecutionStatus.FAILED);
						}
					});
					if (selectionIsATest) {
						System.out.println("selectionIsATest= "
								+ selectionIsATest);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}

		TreePath[] selectionPaths = getSelectionPaths();
		if (selectionPaths.length != 0) {
			TreePath selectionPath = selectionPaths[0];
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) selectionPath
					.getLastPathComponent();
			CopyOnWriteArrayList<AutomationTest> selectedTests = new CopyOnWriteArrayList<AutomationTest>();
			if (lastNode.getUserObject() instanceof AutomationFunctionalGroup) {
				// AutomationFunctionalGroup currGroup =
				// (AutomationFunctionalGroup) lastNode.getUserObject();
				//
				// for(AutomationTest test: theTestSet.getTestsAsList())
				// {
				// if(test.getAutoFunctionalGroupCode().equals(currGroup.getAutoFunctionalGroupCode()))
				// {
				// selectedTests.add(test);
				// }
				// }
				for (int i = 0; i < lastNode.getChildCount(); i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) lastNode
							.getChildAt(i);
					selectedTests.add((AutomationTest) child.getUserObject());
				}
			} else if (lastNode.getUserObject() instanceof AutomationTestSet) {
				AutomationTestSet testSet = (AutomationTestSet) lastNode
						.getUserObject();
				for (AutomationTest test : testSet.getTestsAsList()) {
					selectedTests.add(test);
				}
			}
			if (!selectedTests.isEmpty()) {
				// List<AutomationTest> list = new
				// ArrayList<AutomationTest>(selectedTests);
				// Collections.sort(list);
				// CopyOnWriteArrayList<AutomationTest> returnList = new
				// CopyOnWriteArrayList<AutomationTest>(list);

				view.setTestsList(selectedTests);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
