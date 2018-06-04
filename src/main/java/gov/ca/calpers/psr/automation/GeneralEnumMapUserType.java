package gov.ca.calpers.psr.automation;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.EnhancedUserType;

import gov.ca.calpers.psr.automation.interfaces.Enumable;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Hibernate User type for enums.
 *
 * @author Mohammad Norouzi
 * @see Enumable
 */
public class GeneralEnumMapUserType implements EnhancedUserType, Serializable {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5993020929647717601L;
    
    /** The enum class. */
    private Class enumClass;
    
    /** The first enum item. */
    private Enumable FIRST_ENUM_ITEM;

    /**
     * Constructor.
     * @param enumClass enum class
     *
     */
    protected GeneralEnumMapUserType(Class enumClass) {
        this.enumClass = enumClass;
        if(enumClass.getEnumConstants()[0] instanceof Enumable) {
            FIRST_ENUM_ITEM = (Enumable) enumClass.getEnumConstants()[0];
        } else {
            throw new IllegalStateException("The class " + enumClass + " MUST implement Enumable interface!");
        }
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    @Override
    public int[] sqlTypes() {
        return new int[]{StandardBasicTypes.STRING.sqlType()};
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @Override
    public Class returnedClass() {
        return Enum.class;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if(x != null) {
            return x.equals(y);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    @Override
    public int hashCode(Object x) throws HibernateException {
        return x != null ? x.hashCode() : 1978;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], org.hibernate.engine.spi.SessionImplementor, java.lang.Object)
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        String name = rs.getString(names[0]);
        return rs.wasNull() || name == null ? null : FIRST_ENUM_ITEM.getEnumFromValue(name);
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int, org.hibernate.engine.spi.SessionImplementor)
     */
    @Override
    public void nullSafeSet(PreparedStatement st,
                            Object value, int index, SessionImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, StandardBasicTypes.STRING.sqlType());
        } else {
            st.setString(index, ((Enumable)value).getValue());
        }
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    @Override
    public boolean isMutable() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        // TODO Auto-generated method stub
        return (Serializable) value;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        // TODO Auto-generated method stub
        return cached;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        // TODO Auto-generated method stub
        return original;
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.EnhancedUserType#objectToSQLString(java.lang.Object)
     */
    @Override
    public String objectToSQLString(Object value) {
        return '\'' + ((Enumable)value).getValue() + '\'';
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.EnhancedUserType#toXMLString(java.lang.Object)
     */
    @Override
    public String toXMLString(Object value) {
        return ((Enumable) value).getValue();
    }

    /* (non-Javadoc)
     * @see org.hibernate.usertype.EnhancedUserType#fromXMLString(java.lang.String)
     */
    @Override
    public Object fromXMLString(String xmlValue) {
        return xmlValue == null || xmlValue.isEmpty() ? null : FIRST_ENUM_ITEM.getEnumFromValue(xmlValue);
    }
}
