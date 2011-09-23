package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class ExceptionTranslatorBeanDefinitionParser extends AbstractBeanDefinitionParser {
    public static final String RESOLVER_ATTRIBUTE = "resolver";
    public static final String DATA_SOURCE_ATTRIBUTE = "data-source";
    public static final String EXCEPTION_FACTORY_ATTRIBUTE = "exception-factory";
    public static final String CONFIGURABLE_EXCEPTION_FACTORY_ELEMENT = "configurable-exception-factory";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder translatorBuilder = genericBeanDefinition(DatabaseConstraintExceptionTranslator.class);
        BeanDefinition translatorDefinition = translatorBuilder.getRawBeanDefinition();
        translatorBuilder.addConstructorArgValue(parseResolver(element, parserContext, translatorDefinition));
        Object exceptionFactory = parseExceptionFactory(element, parserContext, translatorDefinition);
        if(exceptionFactory != null) {
            translatorBuilder.addConstructorArgValue(exceptionFactory);
        }
        return translatorBuilder.getBeanDefinition();
    }
    
    private Object parseResolver(Element element, ParserContext parserContext, BeanDefinition parent) {
        Object resolver = parsePropertyFromAttributeOrChild(element, RESOLVER_ATTRIBUTE, parserContext, parent);
        if(resolver == null) {
            resolver = createDefaultViolationResolver(element.getAttribute(DATA_SOURCE_ATTRIBUTE));
        }
        return resolver;
    }
    
    private BeanDefinition createDefaultViolationResolver(String dataSourceName) {
        BeanDefinitionBuilder resolverBuilder = genericBeanDefinition(DatabaseConstraintViolationResolverParserFactoryBean.class);
        resolverBuilder.addPropertyReference("dataSource", dataSourceName);
        return resolverBuilder.getBeanDefinition();
    }
    
    private Object parseExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parent) {
        Object exceptionFactory = parsePropertyFromAttributeOrChild(element, EXCEPTION_FACTORY_ATTRIBUTE, parserContext, parent);
        if(exceptionFactory == null) {
            Element configurableFactoryElement = DomUtils.getChildElementByTagName(element, CONFIGURABLE_EXCEPTION_FACTORY_ELEMENT);
            if(configurableFactoryElement != null) {
                exceptionFactory = parserContext.getDelegate().parsePropertySubElement(configurableFactoryElement, parent);
            }
        }
        return exceptionFactory;
    }
    
}
