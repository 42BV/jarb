package org.jarbframework.constraint;

import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatorFactoryBean;
import org.jarbframework.constraint.violation.TranslateExceptionsBeanPostProcessor;
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
        BeanDefinitionBuilder processorBuilder = BeanDefinitionBuilder.genericBeanDefinition(TranslateExceptionsBeanPostProcessor.class);
        
        // Create the exception translator
        if (element.hasAttribute("translator")) {
            processorBuilder.addConstructorArgReference(element.getAttribute("translator"));
        } else {
            processorBuilder.addConstructorArgValue(buildExceptionTranslator(element));
        }
        
        // Define where our translations should take place
        if (element.hasAttribute("pointcut")) {
            processorBuilder.addConstructorArgValue(buildPointcut(element.getAttribute("pointcut")));
        }
        
        return processorBuilder.getBeanDefinition();
    }

    private BeanDefinition buildExceptionTranslator(Element element) {
        BeanDefinitionBuilder translatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatorFactoryBean.class);
        translatorBuilder.addConstructorArgValue(element.getAttribute("base-package"));
        translatorBuilder.addConstructorArgReference(element.getAttribute("data-source"));
        if (element.hasAttribute("default-factory")) {
            translatorBuilder.addConstructorArgReference(element.getAttribute("default-factory"));
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

}
