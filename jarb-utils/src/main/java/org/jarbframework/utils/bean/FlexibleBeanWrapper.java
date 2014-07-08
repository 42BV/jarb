package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.notNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessor;

/**
 * Attempts to access properties based on the public
 * getter and setters methods. But whenever no matching
 * method could be found we access the field directly.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public final class FlexibleBeanWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FlexibleBeanWrapper.class);
	
    private final Object bean;

    private final BeanWrapper beanWrapper;
    
    private final PropertyAccessor fieldAccessor;

    public FlexibleBeanWrapper(Object bean) {
        this.bean = notNull(bean, "Wrapped bean cannot be null.");
        this.beanWrapper = new BeanWrapperImpl(bean);
        this.fieldAccessor = new DirectFieldAccessor(bean);
    }

    public boolean isReadableProperty(String propertyName) {
        return beanWrapper.isReadableProperty(propertyName) || fieldAccessor.isReadableProperty(propertyName);
    }

    public Object getPropertyValue(String propertyName) {
        if (beanWrapper.isReadableProperty(propertyName)) {
            return beanWrapper.getPropertyValue(propertyName);
        } else {
            return fieldAccessor.getPropertyValue(propertyName);
        }
    }
    
    public Object getPropertyValueNullSafe(String propertyName) {
        Object value = null;
        try {
            value = getPropertyValue(propertyName);
        } catch (NullValueInNestedPathException e) {
            LOGGER.debug("Could not retrieve actual property value.", e);
        }
        return value;
    }

    public boolean isWritableProperty(String propertyName) {
        return beanWrapper.isWritableProperty(propertyName) || fieldAccessor.isWritableProperty(propertyName);
    }

    public FlexibleBeanWrapper setPropertyValue(String propertyName, Object value) {
        if (beanWrapper.isWritableProperty(propertyName)) {
            beanWrapper.setPropertyValue(propertyName, value);
        } else {
            fieldAccessor.setPropertyValue(propertyName, value);
        }
        return this;
    }

    public Object getWrappedBean() {
        return bean;
    }
    
}
