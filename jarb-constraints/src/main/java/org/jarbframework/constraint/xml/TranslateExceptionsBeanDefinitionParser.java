package org.jarbframework.constraint.xml;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
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
        BeanDefinitionBuilder builder = genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        builder.addPropertyValue("translator", buildTranslator(element));
        if (element.hasAttribute("pointcut")) {
            builder.addPropertyValue("pointcut", parsePointcut(element.getAttribute("pointcut")));
        }
        return builder.getBeanDefinition();
    }

    // TODO: Create the correct bean definition
    private BeanDefinition buildTranslator(Element element) {
        if (element.hasAttribute("translator")) {
            String translatorId = element.getAttribute("translator");
        } else {
            String basePackage = element.getAttribute("base-package");
            String dataSourceId = element.getAttribute("data-source");
        }
        return null;
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

}
