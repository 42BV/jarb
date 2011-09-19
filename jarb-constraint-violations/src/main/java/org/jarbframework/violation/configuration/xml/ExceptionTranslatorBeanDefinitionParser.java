package org.jarbframework.violation.configuration.xml;

import org.jarbframework.utils.spring.xml.BeanParsingHelper;
import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
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
        BeanDefinitionBuilder translatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslator.class);
        translatorBuilder.addConstructorArgValue(parseResolver(element, parserContext));
        Object exceptionFactory = parseExceptionFactory(element, parserContext);
        if(exceptionFactory != null) {
            translatorBuilder.addConstructorArgValue(exceptionFactory);
        }
        return translatorBuilder.getBeanDefinition();
    }
    
    private Object parseResolver(Element translatorElement, ParserContext parserContext) {
        return parseBeanFromCustomTag(translatorElement, RESOLVER_PROPERTY, true, parserContext);
    }
    
    private Object parseExceptionFactory(Element translatorElement, ParserContext parserContext) {
        return parseBeanFromCustomTag(translatorElement, EXCEPTION_FACTORY_PROPERTY, false, parserContext);
    }
    
    private Object parseBeanFromCustomTag(Element containerElement, String tagName, boolean required, ParserContext parserContext) {
        Object bean = null;
        Element resolverElement = DomUtils.getChildElementByTagName(containerElement, tagName);
        if(resolverElement != null) {
            bean = BeanParsingHelper.parseBeanInsideCustomElement(resolverElement, parserContext, null);
        } else if(required) {
            parserContext.getReaderContext().error("The <" + containerElement.getNodeName() + "/> does not have a " + tagName + "tag.", containerElement);
        }
        return bean;
    }

}
