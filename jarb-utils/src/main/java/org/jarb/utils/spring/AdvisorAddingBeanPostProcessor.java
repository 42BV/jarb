package org.jarb.utils.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Bean post processor that adds an {@link Advisor} to beans post initialization.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public abstract class AdvisorAddingBeanPostProcessor extends ProxyConfig implements BeanPostProcessor, BeanClassLoaderAware {
    private static final long serialVersionUID = 912342245657548924L;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /** The advisor that should be added **/
    private Advisor advisor = null;
    /** Position of the advisor to add (<b>optional</b>) **/
    private boolean addUpFront = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
    }

    public void setAddUpFront(boolean addUpFront) {
        this.addUpFront = addUpFront;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Assert.state(advisor != null, "Cannot add a null pointer advisor to beans.");

        Object result = bean;
        if (!(bean instanceof AopInfrastructureBean)) {
            if (AopUtils.canApply(advisor, AopUtils.getTargetClass(bean))) {
                if (bean instanceof Advised) {
                    // Bean already has advisors, append new advisor on the desired position
                    if (addUpFront) {
                        ((Advised) bean).addAdvisor(0, advisor);
                    } else {
                        ((Advised) bean).addAdvisor(advisor);
                    }
                } else {
                    // Wrap the bean in a proxy, including our advisor.
                    ProxyFactory proxyFactory = new ProxyFactory(bean);
                    proxyFactory.copyFrom(this);
                    proxyFactory.addAdvisor(advisor);
                    result = proxyFactory.getProxy(beanClassLoader);
                }
            }
        }

        return result;
    }

}
