package org.jarbframework.constraint;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.DefaultViolationResolver;
import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Causes automatic exception translation on all matching method invocations.
 * 
 * @author Jeroen van Schagen
 */
public class TranslateExceptionsBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder processorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        
        // Create the exception translator
        if (element.hasAttribute("translator")) {
            processorBuilder.addPropertyReference("translator", element.getAttribute("translator"));
        } else {
            BeanDefinition translator = buildExceptionTranslator(element);
            processorBuilder.addPropertyValue("translator", translator);
        }
        
        // Define where our translations should take place
        if (element.hasAttribute("pointcut")) {
            processorBuilder.addPropertyValue("pointcut", buildPointcut(element.getAttribute("pointcut")));
        }
        
        return processorBuilder.getBeanDefinition();
    }

    private BeanDefinition buildExceptionTranslator(Element element) {
        BeanDefinitionBuilder translatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(ExceptionTranslatorFactoryBean.class);
        translatorBuilder.addPropertyValue("basePackage", element.getAttribute("base-package"));
        translatorBuilder.addPropertyReference("dataSource", element.getAttribute("data-source"));
        if (element.hasAttribute("default-factory")) {
            translatorBuilder.addPropertyReference("defaultExceptionFactory", element.getAttribute("default-factory"));
        }
        return translatorBuilder.getBeanDefinition();
    }

    private BeanDefinition buildPointcut(String expression) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(AspectJExpressionPointcut.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanDefinition.setSynthetic(true);
        beanDefinition.getPropertyValues().add("expression", expression);
        return beanDefinition;
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
    
    /**
     * Create an exception translator based on a certain package and data source.
     * 
     * @author Jeroen van Schagen
     */
    public static final class ExceptionTranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
    	
        private String basePackage;
    	
        private DataSource dataSource;

    	private DatabaseConstraintExceptionFactory defaultExceptionFactory;

        @Override
        protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
            DatabaseConstraintViolationResolver violationResolver = new DefaultViolationResolver(dataSource, basePackage);
            DatabaseConstraintExceptionFactory exceptionFactory = new ConfigurableConstraintExceptionFactory(defaultExceptionFactory).registerAll(basePackage);
            return new DatabaseConstraintExceptionTranslator(violationResolver, exceptionFactory);
        }

        public void setBasePackage(String basePackage) {
            this.basePackage = basePackage;
        }
        
        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        
        public void setDefaultExceptionFactory(DatabaseConstraintExceptionFactory defaultExceptionFactory) {
			this.defaultExceptionFactory = defaultExceptionFactory;
		}
                
    }

}
