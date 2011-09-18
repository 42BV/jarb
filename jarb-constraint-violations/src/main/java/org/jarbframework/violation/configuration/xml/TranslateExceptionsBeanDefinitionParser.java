package org.jarbframework.violation.configuration.xml;

import org.jarbframework.violation.configuration.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses a database constraint exception translating bean post processor from XML.
 * @author Jeroen van Schagen
 * @since 18-09-2011
 */
public class TranslateExceptionsBeanDefinitionParser extends AbstractBeanDefinitionParser {
    public static final String TRANSLATOR_REF = "translator-ref";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        builder.addPropertyReference("translator", element.getAttribute(TRANSLATOR_REF));
        return builder.getBeanDefinition();
    }
    
    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
