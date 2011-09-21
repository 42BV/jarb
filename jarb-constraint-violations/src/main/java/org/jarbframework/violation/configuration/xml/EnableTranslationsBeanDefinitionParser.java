package org.jarbframework.violation.configuration.xml;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.violation.configuration.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class EnableTranslationsBeanDefinitionParser extends AbstractBeanDefinitionParser {
    public static final String TRANSLATOR_ATTRIBUTE = "translator";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        builder.addPropertyReference("translator", element.getAttribute(TRANSLATOR_ATTRIBUTE));
        return builder.getBeanDefinition();
    }
    
    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
