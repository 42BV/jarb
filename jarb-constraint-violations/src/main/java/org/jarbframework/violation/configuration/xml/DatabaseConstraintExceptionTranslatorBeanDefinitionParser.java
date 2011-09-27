package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.xml.DomUtils.getChildElementByTagName;

import javax.sql.DataSource;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.violation.resolver.DatabaseConstraintViolationResolverFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses a {@link DatabaseConstraintExceptionTranslator} from XML.
 * @author Jeroen van Schagen
 * @since 22-09-2011
 */
public class DatabaseConstraintExceptionTranslatorBeanDefinitionParser extends AbstractBeanDefinitionParser {
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder translatorBuilder = genericBeanDefinition(DatabaseConstraintExceptionTranslator.class);
        BeanDefinition translatorDefinition = translatorBuilder.getRawBeanDefinition();
        translatorBuilder.addConstructorArgValue(parseViolationResolver(element, parserContext, translatorDefinition));
        Object exceptionFactory = parseExceptionFactory(element, parserContext, translatorDefinition);
        if(exceptionFactory != null) {
            translatorBuilder.addConstructorArgValue(exceptionFactory);
        }
        return translatorBuilder.getBeanDefinition();
    }
    
    private Object parseViolationResolver(Element element, ParserContext parserContext, BeanDefinition parentDefinition) {
        Object resolver = parsePropertyFromAttributeOrChild(element, "resolver", parserContext, parentDefinition);
        if(resolver == null) {
            resolver = createDefaultViolationResolver(element.getAttribute("data-source"));
        }
        return resolver;
    }
    
    private BeanDefinition createDefaultViolationResolver(String dataSourceName) {
        BeanDefinitionBuilder resolverBuilder = genericBeanDefinition(DatabaseConstraintViolationResolverParserFactoryBean.class);
        resolverBuilder.addPropertyReference("dataSource", dataSourceName);
        return resolverBuilder.getBeanDefinition();
    }
    
    private Object parseExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parentDefinition) {
        Object exceptionFactory = parsePropertyFromAttributeOrChild(element, "exception-factory", parserContext, parentDefinition);
        if(exceptionFactory == null) {
            exceptionFactory = parseConfigurableExceptionFactory(element, parserContext, parentDefinition);
        }
        return exceptionFactory;
    }
    
    private Object parseConfigurableExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parentDefinition) {
        Object exceptionFactory = null;
        Element configurableFactoryElement = getChildElementByTagName(element, "configurable-exception-factory");
        if(configurableFactoryElement != null) {
            exceptionFactory = parserContext.getDelegate().parsePropertySubElement(configurableFactoryElement, parentDefinition);
        }
        return exceptionFactory;
    }
    
    static final class DatabaseConstraintViolationResolverParserFactoryBean extends SingletonFactoryBean<DatabaseConstraintViolationResolver> {
        private DataSource dataSource;
        
        @Required
        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        protected DatabaseConstraintViolationResolver createObject() throws Exception {
            return new DatabaseConstraintViolationResolverFactory().build(dataSource);
        }
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
    
}
