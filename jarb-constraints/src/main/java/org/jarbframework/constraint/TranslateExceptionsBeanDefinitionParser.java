package org.jarbframework.constraint;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.constraint.violation.factory.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolverFactory;
import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class TranslateExceptionsBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder processorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        if (element.hasAttribute("translator")) {
            processorBuilder.addPropertyReference("translator", element.getAttribute("translator"));
        } else {
            BeanDefinition translator = parseTranslator(element);
            processorBuilder.addPropertyValue("translator", translator);
        }
        if (element.hasAttribute("pointcut")) {
            processorBuilder.addPropertyValue("pointcut", parsePointcut(element.getAttribute("pointcut")));
        }
        return processorBuilder.getBeanDefinition();
    }

    private BeanDefinition parseTranslator(Element element) {
        BeanDefinitionBuilder translatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(TranslatorFactoryBean.class);
        translatorBuilder.addPropertyValue("basePackage", element.getAttribute("base-package"));
        translatorBuilder.addPropertyReference("dataSource", element.getAttribute("data-source"));
        if (element.hasAttribute("default-factory")) {
            translatorBuilder.addPropertyReference("defaultExceptionFactory", element.getAttribute("default-factory"));
        }
        return translatorBuilder.getBeanDefinition();
    }

    private BeanDefinition parsePointcut(String expression) {
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
    
    public static final class TranslatorFactoryBean extends SingletonFactoryBean<DatabaseConstraintExceptionTranslator> {
    	
        private String basePackage;
    	
        private DataSource dataSource;

    	private DatabaseConstraintExceptionFactory defaultExceptionFactory;

        @Override
        protected DatabaseConstraintExceptionTranslator createObject() throws Exception {
            DatabaseConstraintViolationResolver violationResolver = DatabaseConstraintViolationResolverFactory.createResolver(basePackage, dataSource);
            DatabaseConstraintExceptionFactory exceptionFactory = createExceptionFactory();
            return new DatabaseConstraintExceptionTranslator(violationResolver, exceptionFactory);
        }

        private DatabaseConstraintExceptionFactory createExceptionFactory() {
            return new ConfigurableConstraintExceptionFactory(defaultExceptionFactory).registerAll(basePackage);
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
