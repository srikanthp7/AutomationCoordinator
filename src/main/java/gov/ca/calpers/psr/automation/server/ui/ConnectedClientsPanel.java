/**
 * 
 */
package gov.ca.calpers.psr.automation.server.ui;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import gov.ca.calpers.psr.automation.server.socket.AutomationCoordinatorServer;
import gov.ca.calpers.psr.automation.server.socket.ClientHandler;

import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.UIManager;
import java.awt.Color;

/**
 * The Class ConnectedClientsPanel.
 *
 * @author burban
 */
public class ConnectedClientsPanel extends JPanel implements Observer{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The server. */
	private AutomationCoordinatorServer server;
	
	/** The clients. */
	private CopyOnWriteArrayList<ClientHandler> clients;
	
	/** The connected model. */
	private DefaultListModel connectedModel;
	
	/** The disconnected model. */
	private DefaultListModel disconnectedModel;
	
	/** The lbl conn client value. */
	private JLabel lblConnClientValue;
	
	/** The connected scroll pane. */
	private JScrollPane connectedScrollPane;
	
	/** The disconnected scroll pane. */
	private JScrollPane disconnectedScrollPane;
	
	/** The connected client list. */
	private JList connectedClientList;
	
	/** The disconnected client list. */
	private JList disconnectedClientList;
	
	/** The connected clients. */
	private CopyOnWriteArrayList<ClientHandler> connectedClients = new CopyOnWriteArrayList<ClientHandler>();
	
	/** The disconnected clients. */
	private CopyOnWriteArrayList<ClientHandler> disconnectedClients = new CopyOnWriteArrayList<ClientHandler>();

	/**
	 * Instantiates a new connected clients panel.
	 *
	 * @param server the server
	 */
	public ConnectedClientsPanel(AutomationCoordinatorServer server)
	{
		setPreferredSize(new Dimension(800, 500));
		SpringLayout springLayout = new SpringLayout();
		this.setLayout(springLayout);
		
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Clients", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.server=server;
		clients = server.getClientHandlers();
		server.addObserver(this);
		
		connectedScrollPane = new JScrollPane();
		connectedScrollPane.setBorder(new TitledBorder(null, "Connected Clients", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		springLayout.putConstraint(SpringLayout.SOUTH, connectedScrollPane, -30, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, connectedScrollPane, 210, SpringLayout.WEST, this);
		connectedScrollPane.setDoubleBuffered(true);
		springLayout.putConstraint(SpringLayout.NORTH, connectedScrollPane, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, connectedScrollPane, 10, SpringLayout.WEST, this);
		connectedScrollPane.setSize(new Dimension(200, 200));
		connectedScrollPane.setPreferredSize(new Dimension(200, 400));		
		add(connectedScrollPane);
		connectedModel = new DefaultListModel();
		connectedClientList = new JList(connectedModel);	
		
		for(ClientHandler handler : clients)
		{
			connectedModel.addElement(handler);
		}
		connectedScrollPane.setViewportView(connectedClientList);
		
		JLabel lblNumOfConnectedClients = new JLabel("Number of Connected Clients:");
		springLayout.putConstraint(SpringLayout.NORTH, lblNumOfConnectedClients, 6, SpringLayout.SOUTH, connectedScrollPane);
		springLayout.putConstraint(SpringLayout.WEST, lblNumOfConnectedClients, 0, SpringLayout.WEST, connectedScrollPane);
		add(lblNumOfConnectedClients);
		
		lblConnClientValue = new JLabel(String.valueOf(clients.size()));
		springLayout.putConstraint(SpringLayout.WEST, lblConnClientValue, 6, SpringLayout.EAST, lblNumOfConnectedClients);
		springLayout.putConstraint(SpringLayout.SOUTH, lblConnClientValue, 0, SpringLayout.SOUTH, lblNumOfConnectedClients);
		add(lblConnClientValue);
		
		disconnectedScrollPane = new JScrollPane();
		disconnectedScrollPane.setBorder(new TitledBorder(null, "Disconnected Clients", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, disconnectedScrollPane, 0, SpringLayout.NORTH, connectedScrollPane);
		springLayout.putConstraint(SpringLayout.WEST, disconnectedScrollPane, 43, SpringLayout.EAST, connectedScrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, disconnectedScrollPane, 0, SpringLayout.SOUTH, connectedScrollPane);
		disconnectedScrollPane.setSize(new Dimension(200, 200));
		disconnectedScrollPane.setPreferredSize(new Dimension(200, 400));
		disconnectedScrollPane.setDoubleBuffered(true);
		add(disconnectedScrollPane);
		disconnectedModel = new DefaultListModel();
		disconnectedClientList = new JList(disconnectedModel);
		disconnectedScrollPane.setViewportView(disconnectedClientList);
		
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		SwingUtilities.invokeLater(new Runnable(){@Override
		public void run(){
		int connectedListSize = connectedModel.getSize();
		if(connectedListSize > 0)
		{
			for(int i = connectedListSize -1; i >= 0 ; i-- )
			{
				connectedModel.remove(i);
			}
		}
		
		int disconnectedListSize = disconnectedModel.getSize();
		if(disconnectedListSize > 0)
		{
			for(int j = disconnectedListSize - 1; j >= 0 ; j--)
			{
				disconnectedModel.remove(j);
			}
		}
		
		for(ClientHandler client : connectedClients)
		{
			boolean clientDisconnected = true;
			for(ClientHandler aClient : clients)
			{
				if(client.getName().equals(aClient.getName()))
				{	
					clientDisconnected = false;
					break;
				}				
			}
			
			if(clientDisconnected)
			{
				boolean clientExists = false;
				for(ClientHandler handler : disconnectedClients)
				{
					if(client.getName().equals(handler.getName()))
					{
						clientExists = true;
						break;
					}
				}
				if(!clientExists)
				{
					disconnectedClients.add(client);
				}
			}else
			{
				if(disconnectedClients.contains(client))
				{
					disconnectedClients.remove(client);
				}
			}
		}
		
		
		for(ClientHandler handler : clients)
		{
			if(!connectedClients.contains(handler))
			{
				connectedClients.add(handler);
			}
			connectedModel.addElement(handler);
		}
		
		
		
		for (ClientHandler handler : disconnectedClients)
		{
			disconnectedModel.addElement(handler);
		}
		lblConnClientValue.setText(String.valueOf(clients.size()));
		}});
	}
}
