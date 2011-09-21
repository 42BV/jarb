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

public class ExceptionTranslatorBeanDefinitionParser extends AbstractBeanDefinitionParser {
    public static final String RESOLVER_ATTRIBUTE = "resolver";
    public static final String DATASOURCE_RESOLVER_ATTRIBUTE = "resolve-by-data-source";
    public static final String EXCEPTION_FACTORY_ATTRIBUTE = "exception-factory";
    
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
            BeanDefinitionBuilder resolverBuilder = genericBeanDefinition(DatabaseConstraintViolationResolverParserFactoryBean.class);
            resolverBuilder.addPropertyReference("dataSource", element.getAttribute(DATASOURCE_RESOLVER_ATTRIBUTE));
            resolver = resolverBuilder.getBeanDefinition();
        }
        return resolver;
    }
    
    private Object parseExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parent) {
        return parsePropertyFromAttributeOrChild(element, EXCEPTION_FACTORY_ATTRIBUTE, parserContext, parent);
    }
    
}
