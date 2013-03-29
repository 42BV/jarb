package org.jarbframework.constraint.violation.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.xml.DomUtils.getChildElementsByTagName;

import java.util.Collection;
import java.util.List;

import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.custom.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.custom.ExceptionFactoryMapping;
import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses a {@link ConfigurableConstraintExceptionFactory} from XML.
 * @author Jeroen van Schagen
 * @since 22-09-2011
 */
public class ConfigurableConstraintExceptionFactoryBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factoryBuilder = genericBeanDefinition(ConfigurableConstraintExceptionFactoryParserFactoryBean.class);
        BeanDefinition factoryDefinition = factoryBuilder.getRawBeanDefinition();
        factoryBuilder.addPropertyValue("defaultFactory", parseDefaultExceptionFactory(element, parserContext, factoryDefinition));
        factoryBuilder.addPropertyValue("exceptionMappings", parseExceptionMappings(element, parserContext, factoryDefinition));
        return factoryBuilder.getBeanDefinition();
    }

    private Object parseDefaultExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parent) {
        return parsePropertyFromAttributeOrChild(element, "default-factory", parserContext, parent);
    }

    private ManagedList<Object> parseExceptionMappings(Element element, ParserContext parserContext, BeanDefinition parentDefinition) {
        List<Element> mappingElements = getChildElementsByTagName(element, "exception-mapping");
        ManagedList<Object> mappingDefinitions = new ManagedList<Object>(mappingElements.size());
        mappingDefinitions.setElementTypeName(ExceptionFactoryMapping.class.getName());
        mappingDefinitions.setSource(parentDefinition);
        ExceptionFactoryMappingBeanDefinitionParser mappingParser = new ExceptionFactoryMappingBeanDefinitionParser(parentDefinition);
        for (Element mappingElement : mappingElements) {
            mappingDefinitions.add(mappingParser.parse(mappingElement, parserContext));
        }
        return mappingDefinitions;
    }

    static final class ConfigurableConstraintExceptionFactoryParserFactoryBean extends SingletonFactoryBean<ConfigurableConstraintExceptionFactory> {
        
        private DatabaseConstraintExceptionFactory defaultFactory;
        private Collection<ExceptionFactoryMapping> exceptionMappings;

        public void setDefaultFactory(DatabaseConstraintExceptionFactory defaultFactory) {
            this.defaultFactory = defaultFactory;
        }

        @Required
        public void setExceptionMappings(Collection<ExceptionFactoryMapping> exceptionMappings) {
            this.exceptionMappings = exceptionMappings;
        }

        @Override
        protected ConfigurableConstraintExceptionFactory createObject() throws Exception {
            ConfigurableConstraintExceptionFactory factory = instantateFactory();
            return registerMappings(factory);
        }

        private ConfigurableConstraintExceptionFactory instantateFactory() {
            return defaultFactory != null ? new ConfigurableConstraintExceptionFactory(defaultFactory) : new ConfigurableConstraintExceptionFactory();
        }

        private ConfigurableConstraintExceptionFactory registerMappings(ConfigurableConstraintExceptionFactory factory) {
            for (ExceptionFactoryMapping exceptionMapping : exceptionMappings) {
                factory.register(exceptionMapping);
            }
            return factory;
        }

    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
