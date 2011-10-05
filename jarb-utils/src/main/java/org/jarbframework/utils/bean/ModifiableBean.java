package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.notNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessor;

/**
 * Attempts to access properties based on the public
 * getter and setters methods. But whenever no matching
 * method could be found we access the field directly.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class ModifiableBean<T> {
    private final T bean;

    private final BeanWrapper beanWrapper;
    private final PropertyAccessor fieldAccessor;

    private ModifiableBean(T bean) {
        this.bean = notNull(bean, "Wrapped bean cannot be null.");
        this.beanWrapper = new BeanWrapperImpl(bean);
        this.fieldAccessor = new DirectFieldAccessor(bean);
    }

    /**
     * Wrap an existing bean with property modification behavior.
     * @param <T> type of bean
     * @param bean the bean being wrapped
     * @return modifiable bean, wrapping the specified bean
     */
    public static <T> ModifiableBean<T> wrap(T bean) {
        return new ModifiableBean<T>(bean);
    }

    /**
     * Build a new bean with property modification behavior.
     * @param <T> type of bean
     * @param beanClass class of the bean being created and wrapped
     * @return modifiable bean, wrapping a new bean instance
     */
    public static <T> ModifiableBean<T> instantiate(Class<T> beanClass) {
        return wrap(BeanUtils.instantiateClass(beanClass));
    }

    public boolean isReadableProperty(String propertyName) {
        return beanWrapper.isReadableProperty(propertyName) || fieldAccessor.isReadableProperty(propertyName);
    }

    public Object getPropertyValue(String propertyName) {
        try {
            return beanWrapper.getPropertyValue(propertyName);
        } catch (NotReadablePropertyException e) {
            return fieldAccessor.getPropertyValue(propertyName);
        }
    }

    public boolean isWritableProperty(String propertyName) {
        return beanWrapper.isWritableProperty(propertyName) || fieldAccessor.isWritableProperty(propertyName);
    }

    public ModifiableBean<T> setPropertyValue(String propertyName, Object value) {
        try {
            beanWrapper.setPropertyValue(propertyName, value);
        } catch (NotWritablePropertyException e) {
            fieldAccessor.setPropertyValue(propertyName, value);
        }
        return this;
    }

    public T getWrappedBean() {
        return bean;
    }
}
