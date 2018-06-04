package gov.ca.calpers.psr.automation.pojo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * The Class HibernateUtil.
 */
public class HibernateUtil {

    /** The session factory. */
    private static SessionFactory sessionFactory;
    
    /** The service registry. */
    private static ServiceRegistry serviceRegistry;
    
    /** The session. */
    private static Session session;
    
    /** The secondary session. */
    private static Session secondarySession;

    static {

        session = getSessionFactory().openSession();
        secondarySession = getSessionFactory().openSession();
    }

    /**
     * Builds the session factory.
     *
     * @return the session factory
     */
    private static SessionFactory buildSessionFactory() {

        try {


            Configuration configuration = new Configuration();            
            configuration.configure();
            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
                    .buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }

    /**
     * Gets the session factory.
     *
     * @return the session factory
     */
    private static SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            buildSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Gets the sesssion.
     *
     * @return the sesssion
     */
    public static Session getSesssion() {
        return getSessionFactory().openSession();
    }

    /**
     * Close session.
     */
    public static void closeSession() {
        session.close();
    }

    /**
     * Gets the secondary sesssion.
     *
     * @return the secondary sesssion
     */
    public static Session getSecondarySesssion() {
        return secondarySession;
    }

    /**
     * Close secondary session.
     */
    public static void closeSecondarySession() {
        secondarySession.close();
    }

    /**
     * Validate field.
     *
     * @param value the value
     * @return the string
     */
    public static String validateField(String value) {
        if (value != null) {
            return value.trim();
        }
        return null;
    }
}
