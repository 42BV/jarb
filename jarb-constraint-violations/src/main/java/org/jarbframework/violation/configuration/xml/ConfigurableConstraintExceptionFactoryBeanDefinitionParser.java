package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.xml.DomUtils.getChildElementsByTagName;

import java.util.Collection;
import java.util.List;

import org.jarbframework.utils.spring.SingletonFactoryBean;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConfigurableConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ExceptionFactoryMapping;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ConfigurableConstraintExceptionFactoryBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factoryBuilder = genericBeanDefinition(ConfigurableConstraintExceptionFactoryParserFactoryBean.class);
        BeanDefinition factoryDefinition = factoryBuilder.getRawBeanDefinition();
        factoryBuilder.addPropertyValue("defaultFactory", parseDefaultExceptionFactory(element, parserContext, factoryDefinition));
        factoryBuilder.addPropertyValue("customFactoryMappings", parseExceptionMappings(element, parserContext, factoryDefinition));
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
        for(Element mappingElement : mappingElements) {
            mappingDefinitions.add(mappingParser.parse(mappingElement, parserContext));
        }
        return mappingDefinitions;
    }
    
    static final class ConfigurableConstraintExceptionFactoryParserFactoryBean extends SingletonFactoryBean<ConfigurableConstraintExceptionFactory> {
        private Collection<ExceptionFactoryMapping> customFactoryMappings;
        private DatabaseConstraintExceptionFactory defaultFactory;
        
        @Required
        public void setCustomFactoryMappings(Collection<ExceptionFactoryMapping> customFactoryMappings) {
            this.customFactoryMappings = customFactoryMappings;
        }
        
        public void setDefaultFactory(DatabaseConstraintExceptionFactory defaultFactory) {
            this.defaultFactory = defaultFactory;
        }

        @Override
        protected ConfigurableConstraintExceptionFactory createObject() throws Exception {
            ConfigurableConstraintExceptionFactory configurableFactory;
            if(defaultFactory != null) {
                configurableFactory = new ConfigurableConstraintExceptionFactory(defaultFactory);
            } else {
                configurableFactory = new ConfigurableConstraintExceptionFactory();
            }
            for(ExceptionFactoryMapping customFactoryMapping : customFactoryMappings) {
                configurableFactory.registerMapping(customFactoryMapping);
            }
            return configurableFactory;
        }
        
    }
    
    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
