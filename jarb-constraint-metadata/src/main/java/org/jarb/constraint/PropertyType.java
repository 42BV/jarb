package org.jarb.constraint;

import java.util.Date;

public enum PropertyType {
    DATE, NUMBER, TEXT, CUSTOM, EMAIL, CREDID_CARD;
    
    /**
     * Retrieve the type for a specific property class.
     * @param propertyClass the property class we are identifying
     * @return the type related to our class
     */
    public static PropertyType forClass(Class<?> propertyClass) {
        PropertyType type = null;
        if(Date.class.isAssignableFrom(propertyClass)) {
            type = DATE;
        } else  if(Number.class.isAssignableFrom(propertyClass)) {
            type = NUMBER;
        } else if(propertyClass.equals(String.class)) {
            type = TEXT;
        } else {
            // Property class could not be mapped
            type = CUSTOM;
        }
        return type;
    }
    
}
