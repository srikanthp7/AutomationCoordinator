package gov.ca.calpers.psr.automation.client.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Class ClientProperty.
 */
public class ClientProperty {
    
    /** The instance. */
    private static ClientProperty instance = new ClientProperty();

    /**
     * Gets the intance.
     *
     * @return the intance
     */
    public static ClientProperty getIntance() {
        return instance;
    }

    /** The pro. */
    private Properties pro;
    
    /** The property file. */
    private final File propertyFile;
    
    /** The server. */
    private static String SERVER = "SERVER";
    
    /** The user. */
    private static String USER = "USER";

    /**
     * Instantiates a new client property.
     */
    private ClientProperty() {
        String userTemp = System.getProperty("java.io.tmpdir");
        String propertyFileName = userTemp + "/bc_automation_coordinator_client.properties";
        propertyFile = new File(propertyFileName);
        if (!propertyFile.exists()) {
            try {
                propertyFile.createNewFile();
            } catch (IOException e) {
                // nothing since we don't care that much
            }
        }
        try {
            FileInputStream in = new FileInputStream(propertyFile);
            pro = new Properties();
            pro.load(in);

        } catch (FileNotFoundException e) {
            // nada since we don't care this much
        } catch (IOException e) {
            // nada since we don't care this much
        }
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public String getServer() {
        String val = null;
        if (pro != null) {
            val = pro.getProperty(SERVER);
        }
        if (val == null) {
            val = "";
        }
        return val;
    }

    /**
     * Update server.
     *
     * @param value the value
     */
    public void updateServer(String value) {
        pro.setProperty(SERVER, value);
        saveProperty();
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        String val = null;
        if (pro != null) {
            val = pro.getProperty(USER);
        }
        if (val == null) {
            val = "";
        }
        return val;
    }

    /**
     * Update user.
     *
     * @param value the value
     */
    public void updateUser(String value) {
        pro.setProperty(USER, value);
        saveProperty();
    }

    /**
     * Save property.
     */
    private void saveProperty() {
        try {
            pro.store(new FileOutputStream(propertyFile), null);
        } catch (FileNotFoundException e) {
            // nada since we don't care that much
        } catch (IOException e) {
            // nada since we don't care that much
        }
    }
}
