package gov.ca.calpers.psr.automation.server.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The Class CheckBoxList.
 *
 * @author burban
 */
public class CheckBoxList extends JList
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The no focus border. */
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	/** The read only. */
	private boolean readOnly = false;

   /**
    * Instantiates a new check box list.
    */
   public CheckBoxList()
   {
      setCellRenderer(new CellRenderer());

      addMouseListener(new MouseAdapter()
         {
            @Override
			public void mousePressed(MouseEvent e)
            {
               if(readOnly)
               {
            	   
               }else
               {
	            	int index = locationToIndex(e.getPoint());
	
	               if (index != -1) {
	                  JCheckBox checkbox = (JCheckBox)
	                              getModel().getElementAt(index);
	                  checkbox.setSelected(
	                                     !checkbox.isSelected());
	                  repaint();
	               }
               }
            }
         }
      );

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   }
   /* (non-Javadoc)
	 * @see javax.swing.JComponent#processMouseEvent(java.awt.event.MouseEvent)
	 */
	@Override
	protected void processMouseEvent(MouseEvent e) 
	{
		if(readOnly)
		{
			//this will prevent all mouse actions 
		}else
		{
			super.processMouseEvent(e);
		}
	}
	   
	 /**
 	 * Sets the read only.
 	 *
 	 * @param value the new read only
 	 */
	public void setReadOnly(boolean value)
	{
	   this.readOnly = value;
	}
	
	public void addCheckbox(JCheckBox checkBox) {
	    ListModel currentList = this.getModel();
	    JCheckBox[] newList = new JCheckBox[currentList.getSize() + 1];
	    for (int i = 0; i < currentList.getSize(); i++) {
	        newList[i] = (JCheckBox) currentList.getElementAt(i);
	    }
	    newList[newList.length - 1] = checkBox;
	    setListData(newList);
	}
	
  	/**
	   * The Class CellRenderer.
	   *
	   * @author burban
	   */
	protected class CellRenderer implements ListCellRenderer
	{
		  /* (non-Javadoc)
		   * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		   */
		@Override
		public Component getListCellRendererComponent(
	                    JList list, Object value, int index,
	                    boolean isSelected, boolean cellHasFocus)
	      {
	         JCheckBox checkbox = (JCheckBox) value;
	         checkbox.setBackground(getBackground());
	         checkbox.setForeground(getForeground());
	         checkbox.setEnabled(isEnabled());
	         checkbox.setFont(getFont());
	         checkbox.setFocusPainted(false);
	         checkbox.setBorderPainted(false);
	         checkbox.setMultiClickThreshhold(2000);
	         checkbox.setBorder(isSelected ?
	          UIManager.getBorder(
	           "List.focusCellHighlightBorder") : noFocusBorder);
	         return checkbox;
	      }
	 }
}

