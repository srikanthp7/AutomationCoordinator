/**
 * 
 */
package gov.ca.calpers.psr.automation;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import gov.ca.calpers.psr.automation.pojo.HibernateUtil;


/**
 * The Class ALMServerConfig.
 *
 * @author srikanth
 */
public class ALMServerConfig implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The server name. */
	private String serverName;
	
	/** The port. */
	private int port;
	
	/** The alm domain. */
	private String almDomain;
	
	/** The alm project. */
	private String almProject;
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/** The id. */
	private int id;
	
		
	/**
	 * Instantiates a new ALM server config.
	 */
	public ALMServerConfig()
	{
		
	}	
	
	/**
	 * Gets the server details.
	 *
	 * @return the server details
	 */
	public synchronized static ALMServerConfig getServerDetails()
	 {
		ALMServerConfig serverConfig = null;
		 Session session = HibernateUtil.getSesssion();
	      
		 	if (session != null) {
		 		session.setDefaultReadOnly(true);
		 		
		 		Transaction tx = session.beginTransaction();
		 		//Criteria criteria = session.createCriteria(AutomationTest.class);
		 		@SuppressWarnings("unchecked")
	            Query query = session.createSQLQuery("SELECT * FROM ALM_SERVER_CONFIG").addEntity(ALMServerConfig.class);
		 		//query.setCacheable(false);		 		
	            //List<AutomationTest> list = (criteria.list()); 
		 		@SuppressWarnings("rawtypes")
		 		List<ALMServerConfig> list = (query.list());
		 		//System.out.println("Criteria.toString= " + criteria.toString());
		 		 if (list.size() == 1) {
		                serverConfig = list.get(0);
		            } else {
		                throw new RuntimeException("Duplicated server details..  Not supported.\n*** Remove all but one of the duplicated server details.***");
		            }		       
	            tx.commit();
	            session.close();
		 	}
		 	return serverConfig;
	 }
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL()
	{
		return "http://"+ serverName + ":" + port + "/qcbin/";
	}
	
	/**
	 * Gets the decrypted credential.
	 *
	 * @return the decrypted credential
	 */
	public String getDecryptedCredential()
	{
		String decryptedPass = null;
		if(password != null && !password.isEmpty())
		{
			TripleDES encryptor;
			try {
				encryptor = new TripleDES();
				decryptedPass = encryptor.decrypt(password);
			} catch (Exception e) {				
				e.printStackTrace();
			}			
		}		
		return decryptedPass;
	}
	
	/**
	 * Gets the server name.
	 *
	 * @return the server name
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * Sets the server name.
	 *
	 * @param serverName the new server name
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Gets the alm domain.
	 *
	 * @return the alm domain
	 */
	public String getAlmDomain() {
		return almDomain;
	}
	
	/**
	 * Sets the alm domain.
	 *
	 * @param almDomain the new alm domain
	 */
	public void setAlmDomain(String almDomain) {
		this.almDomain = almDomain;
	}
	
	/**
	 * Gets the alm project.
	 *
	 * @return the alm project
	 */
	public String getAlmProject() {
		return almProject;
	}
	
	/**
	 * Sets the alm project.
	 *
	 * @param almProject the new alm project
	 */
	public void setAlmProject(String almProject) {
		this.almProject = almProject;
	}
	
	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}	
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

}
