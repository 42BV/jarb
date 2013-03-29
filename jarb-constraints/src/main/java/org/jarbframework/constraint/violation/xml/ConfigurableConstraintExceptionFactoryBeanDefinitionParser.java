package org.jarbframework.constraint.violation.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.xml.DomUtils.getChildElementsByTagName;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.custom.ConfigurableConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.custom.ExceptionFactoryMapping;
import org.jarbframework.utils.spring.SingletonFactoryBean;
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
        BeanDefinitionBuilder exceptionFactoryBuilder = genericBeanDefinition(ConfigurableConstraintExceptionFactoryParserFactoryBean.class);
        
        BeanDefinition exceptionFactoryDefinition = exceptionFactoryBuilder.getRawBeanDefinition();
        exceptionFactoryBuilder.addPropertyValue("defaultFactory", parseDefaultExceptionFactory(element, parserContext, exceptionFactoryDefinition));
        exceptionFactoryBuilder.addPropertyValue("exceptionFactoryMappings", parseExceptionFactoryMappings(element, parserContext, exceptionFactoryDefinition));
        exceptionFactoryBuilder.addPropertyValue("exceptionPackage", element.getAttribute("scan-exceptions"));
        return exceptionFactoryBuilder.getBeanDefinition();
    }

    private Object parseDefaultExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parent) {
        return parsePropertyFromAttributeOrChild(element, "default-factory", parserContext, parent);
    }

    private ManagedList<Object> parseExceptionFactoryMappings(Element element, ParserContext parserContext, BeanDefinition parentDefinition) {
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
        
        private DatabaseConstraintExceptionFactory defaultExceptionFactory;
        
        private Collection<ExceptionFactoryMapping> exceptionFactoryMappings;
        
        private String exceptionPackage;

        public void setDefaultFactory(DatabaseConstraintExceptionFactory defaultFactory) {
            this.defaultExceptionFactory = defaultFactory;
        }

        public void setExceptionFactoryMappings(Collection<ExceptionFactoryMapping> exceptionFactoryMappings) {
            this.exceptionFactoryMappings = exceptionFactoryMappings;
        }
        
        public void setExceptionPackage(String exceptionPackage) {
            this.exceptionPackage = exceptionPackage;
        }

        @Override
        protected ConfigurableConstraintExceptionFactory createObject() throws Exception {
            ConfigurableConstraintExceptionFactory configurableExceptionFactory = newConfigurableExceptionFactory();
            if (StringUtils.isNotBlank(exceptionPackage)) {
                configurableExceptionFactory.registerAll(exceptionPackage);                
            }
            for (ExceptionFactoryMapping exceptionFactoryMapping : exceptionFactoryMappings) {
                configurableExceptionFactory.register(exceptionFactoryMapping);
            }
            return configurableExceptionFactory;
        }

        private ConfigurableConstraintExceptionFactory newConfigurableExceptionFactory() {
            if (defaultExceptionFactory != null) {
                return new ConfigurableConstraintExceptionFactory(defaultExceptionFactory);
            } else {
                return new ConfigurableConstraintExceptionFactory();
            }
        }

    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
