package gov.ca.calpers.psr.automation.server.ui;

import java.awt.Dimension;
import java.awt.Graphics;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * The Class TestSetConfigureSplitPane.
 *
 * @author burban
 */
public class TestSetConfigureSplitPane extends JSplitPane{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The has proportional location. */
    private boolean hasProportionalLocation = false;
    
    /** The proportional location. */
    private double proportionalLocation = 0.5;    
    
    /** The is painted. */
    private boolean isPainted = false;       
    
    /** The buttons. */
    private ArrayList<JButton> buttons;
	
	/**
	 * Instantiates a new test set configure split pane.
	 *
	 * @param buttons the buttons
	 */
	public TestSetConfigureSplitPane(ArrayList<JButton> buttons)
	{
		this.buttons=buttons;
		this.setUI(new ButtonDividerUI(buttons));
	}

	
	/**
	 * The Class ButtonDividerUI.
	 *
	 * @author burban
	 */
	class ButtonDividerUI extends BasicSplitPaneUI
	{
	   
   	/** The buttons. */
   	protected ArrayList<JButton> buttons;
	  
	   /**
   	 * Instantiates a new button divider ui.
   	 *
   	 * @param buttons the buttons
   	 */
	public ButtonDividerUI(ArrayList<JButton> buttons) {
	      this.buttons = buttons;
	   }
	  
	   /* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicSplitPaneUI#createDefaultDivider()
	 */
	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
	      BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {
	         /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getDividerSize() {
	            if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
	               int maxSize = 0;
	               for( JButton button : buttons)
	               {
	            	   if(button.getPreferredSize().width > maxSize)
	            	   {
	            		   maxSize = button.getPreferredSize().width;
	            	   }
	               }
	            	return maxSize + 6;
	            }
	            int maxSize = 0;
	            for( JButton button : buttons)
	               {	            	   
	            	   if(button.getPreferredSize().height > maxSize)
	            	   {
	            		   maxSize = button.getPreferredSize().height;
	            	   }
	               }
	            return maxSize + 6;
	         }
	      };
	      BoxLayout layout = new BoxLayout(divider, BoxLayout.Y_AXIS);	      
	      divider.setLayout(layout);
	      divider.add(Box.createVerticalGlue());
	      for( JButton button : buttons)
	      {
	    	  divider.add(button);
	    	  divider.add(Box.createRigidArea(new Dimension(2, 5)));
	      }	      
	      divider.add(Box.createVerticalGlue());
	  
	      return divider;
	   }
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JSplitPane#setDividerLocation(double)
	 */
	@Override
	public void setDividerLocation(double proportionalLocation) {
		if (!isPainted) {
			hasProportionalLocation = true;
			this.proportionalLocation = proportionalLocation;
		} else {
			super.setDividerLocation(proportionalLocation);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (!isPainted) {
			if (hasProportionalLocation) {
				super.setDividerLocation(proportionalLocation);
			}
			isPainted = true;
		}
	}
}
