package gov.ca.calpers.psr.automation.server.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import gov.ca.calpers.psr.automation.TripleDES;

/**
 * The Class EncryptPasswordPanel.
 */
public class EncryptPasswordPanel extends JDialog implements ActionListener, FocusListener{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The txt encrypted password. */
	private JTextField txtEncryptedPassword;
	
	/** The txt plain text password. */
	private JTextField txtPlainTextPassword;
	
	/** The btn copy to clipboard. */
	private JButton btnCopyToClipboard;
	
	/** The btn close. */
	private JButton btnClose;
	
	/**
	 * Instantiates a new encrypt password panel.
	 */
	public EncryptPasswordPanel() {
		setResizable(false);
		setModal(true);
		setSize(new Dimension(459, 127));
		this.setLocationRelativeTo(null);
		setMaximumSize(getSize());
		setMinimumSize(getSize());
		setPreferredSize(getSize());
		setTitle("Encrypt Password");
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JLabel lblEnterPassword = new JLabel("Enter Password:");
		springLayout.putConstraint(SpringLayout.NORTH, lblEnterPassword, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblEnterPassword, 10, SpringLayout.WEST, this);
		getContentPane().add(lblEnterPassword);
		
		txtEncryptedPassword = new JTextField();
		txtEncryptedPassword.setEditable(false);
		getContentPane().add(txtEncryptedPassword);
		txtEncryptedPassword.setColumns(20);
		
		JLabel lblEncryptedPassword = new JLabel("Encrypted Password:");
		springLayout.putConstraint(SpringLayout.NORTH, txtEncryptedPassword, -3, SpringLayout.NORTH, lblEncryptedPassword);
		springLayout.putConstraint(SpringLayout.WEST, txtEncryptedPassword, 13, SpringLayout.EAST, lblEncryptedPassword);
		springLayout.putConstraint(SpringLayout.NORTH, lblEncryptedPassword, 12, SpringLayout.SOUTH, lblEnterPassword);
		springLayout.putConstraint(SpringLayout.WEST, lblEncryptedPassword, 0, SpringLayout.WEST, lblEnterPassword);
		getContentPane().add(lblEncryptedPassword);
		
		txtPlainTextPassword = new JTextField();
		springLayout.putConstraint(SpringLayout.EAST, txtEncryptedPassword, 0, SpringLayout.EAST, txtPlainTextPassword);
		springLayout.putConstraint(SpringLayout.EAST, txtPlainTextPassword, -10, SpringLayout.EAST, getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, txtPlainTextPassword, -3, SpringLayout.NORTH, lblEnterPassword);
		springLayout.putConstraint(SpringLayout.WEST, txtPlainTextPassword, 36, SpringLayout.EAST, lblEnterPassword);
		txtPlainTextPassword.setActionCommand("Plain_Text");
		txtPlainTextPassword.addActionListener(this);
		txtPlainTextPassword.addFocusListener(this);
		getContentPane().add(txtPlainTextPassword);
		txtPlainTextPassword.setColumns(20);
		
		btnClose = new JButton("Close");
		springLayout.putConstraint(SpringLayout.NORTH, btnClose, 6, SpringLayout.SOUTH, txtEncryptedPassword);
		springLayout.putConstraint(SpringLayout.EAST, btnClose, 0, SpringLayout.EAST, txtEncryptedPassword);
		btnClose.addActionListener(this);
		btnClose.setActionCommand("Close");
		getContentPane().add(btnClose);
		
		btnCopyToClipboard = new JButton("Copy to Clipboard");
		springLayout.putConstraint(SpringLayout.NORTH, btnCopyToClipboard, 0, SpringLayout.NORTH, btnClose);
		springLayout.putConstraint(SpringLayout.EAST, btnCopyToClipboard, -6, SpringLayout.WEST, btnClose);
		btnCopyToClipboard.setEnabled(false);
		btnCopyToClipboard.addActionListener(this);
		btnCopyToClipboard.setActionCommand("Clipboard");
		getContentPane().add(btnCopyToClipboard);
		pack();
		setVisible(true);
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Close")
		{
			dispose();
		}
		if(e.getActionCommand() == "Clipboard")
		{
			String toClipboard = txtEncryptedPassword.getText();
			StringSelection stringSelection = new StringSelection(toClipboard);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);			
		}
		if(e.getActionCommand() == "Plain_Text")
		{
			txtEncryptedPassword.setText(encryptPassword(txtPlainTextPassword.getText()));
			btnCopyToClipboard.setEnabled(true);
		}
	}
	
	/**
	 * Encrypt password.
	 *
	 * @param plainTextPassword the plain text password
	 * @return the string
	 */
	private String encryptPassword(String plainTextPassword)
	{
		TripleDES encryptor = null;
		try {
			encryptor = new TripleDES();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(encryptor == null)
		{
			return null;
		}else
		{
			return encryptor.encrypt(plainTextPassword);
		}		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		if(e.getComponent()==txtPlainTextPassword)
		{
			txtEncryptedPassword.setText(encryptPassword(txtPlainTextPassword.getText()));
			btnCopyToClipboard.setEnabled(true);
		}
	}

}
