package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses a database constraint exception translator from XML.
 * @author Jeroen van Schagen
 * @since 18-09-2011
 */
public class ExceptionTranslatorBeanDefinitionParser extends AbstractBeanDefinitionParser {
    public static final String RESOLVER_PROPERTY = "resolver";
    public static final String EXCEPTION_FACTORY_PROPERTY = "exception-factory";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder translatorBuilder = genericBeanDefinition(DatabaseConstraintExceptionTranslator.class);
        translatorBuilder.addConstructorArgValue(parseResolver(element, parserContext, translatorBuilder.getBeanDefinition()));
        Object exceptionFactory = parseExceptionFactory(element, parserContext, translatorBuilder.getBeanDefinition());
        if(exceptionFactory != null) {
            translatorBuilder.addConstructorArgValue(exceptionFactory);
        }
        return translatorBuilder.getBeanDefinition();
    }
    
    private Object parseResolver(Element element, ParserContext parserContext, BeanDefinition translatorDefinition) {
        return parsePropertyFromAttributeOrChild(element, RESOLVER_PROPERTY, parserContext, translatorDefinition);
    }
    
    private Object parseExceptionFactory(Element element, ParserContext parserContext, BeanDefinition translatorDefinition) {
        return parsePropertyFromAttributeOrChild(element, EXCEPTION_FACTORY_PROPERTY, parserContext, translatorDefinition);
    }
    
}
