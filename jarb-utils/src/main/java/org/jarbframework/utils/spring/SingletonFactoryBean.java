package org.jarbframework.utils.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.GenericTypeResolver;

/**
 * Singleton factory bean which does not require your singleton
 * instance to be of an interface type.
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 *
 * @param <T> type of singleton instance
 */
public abstract class SingletonFactoryBean<T> implements FactoryBean<T> {
    
    /** Singleton instance, initially {@code null} **/
    private T instance;

    /**
     * {@inheritDoc}
     */
    @Override
    public T getObject() throws Exception {
        if (instance == null) {
            instance = createObject();
        }
        return instance;
    }

    /**
     * Create a new instance of our singleton.
     * @return new singleton instance
     * @throws Exception whenever an exception occurs
     */
    protected abstract T createObject() throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getObjectType() {
        return GenericTypeResolver.resolveTypeArgument(getClass(), FactoryBean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

}
