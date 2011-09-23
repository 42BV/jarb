package org.jarbframework.violation.configuration.xml;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.violation.configuration.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class EnableTranslationsBeanDefinitionParser extends AbstractBeanDefinitionParser {
    private static final String TRANSLATOR_ATTRIBUTE = "translator";
    private static final String POINTCUT_ATTRIBUTE = "pointcut";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        builder.addPropertyReference("translator", element.getAttribute(TRANSLATOR_ATTRIBUTE));
        if(element.hasAttribute(POINTCUT_ATTRIBUTE)) {
            builder.addPropertyValue("pointcut", createPointcut(element.getAttribute(POINTCUT_ATTRIBUTE)));
        }
        return builder.getBeanDefinition();
    }
    
    private BeanDefinition createPointcut(String expression) {
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
