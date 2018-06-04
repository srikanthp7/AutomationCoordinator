package gov.ca.calpers.psr.automation.interfaces;

import java.io.Serializable;

/**
 * To be used with Hibernate user type.  
 * @author Mohammad Norouzi
 */
public interface Enumable extends Serializable {

 /**
  * Returns the internal value of each enum to be persisted in hibernate.
  * @return The internal string value.
  */
 public String getValue();

    /**
     * Returns the enum item from the given value.
     * @param value value.
     * @return enum.
     */
    public Enum getEnumFromValue(String value);

    /**
     * A helper class to find corespondent enum by a value.
     */
    class EnumableHelper {
        /**
         * Method to find corespondent enum by a provide value, if value can't match will throw an exception.
         * This will be used when database field has restricted values and not allow undefined values (constraint).
         * @param e The enum class instance
         * @param value The value to be matched
         * @return Enum which matched the value.
         * @throw IllegalArgumentException
         *          Thrown when the value can not be match to a enum.
         */
        public static Enum getEnumFromValue(Enum e, String value) {
            //Assert.notNull(e, "Enum object cannot be null");

            Enum aE = getEnumFromValue(e, value, null);

            if (aE != null) {
                return aE;
            } else {
                throw new IllegalStateException("Invalid value [" + value + "] for enum class [" + e.getClass() + "]");
            }
        }

        /**
         * Method to find corespondent enum by a provide value, if value can't match will throw an exception.
         * This will be used when database field has restricted values and not allow undefined values (constraint).
         * @param e The enum class instance
         * @param value The value to be matched
         * @param defaultEnum The default Enum will be returned if null detected.
         * @return Enum which matched the value, otherwise return the defaultEnum provided
         */
        public static Enum getEnumFromValue(Enum e, String value, Enum defaultEnum) {
            //Assert.notNull(e, "Enum object cannot be null");
            Enum[] enums = e.getClass().getEnumConstants();

            for (Enum aE : enums) {
                if (!Enumable.class.isAssignableFrom(aE.getClass())) {
                    throw new IllegalArgumentException("Enum Must implement Enumable!");
                }
                final Enumable ge =  (Enumable) aE;

                if ( ("" + ge.getValue()).equals(("" + value)) ) {
                    return aE;
                }
            }

            return defaultEnum;
        }
    }
}
