package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import java.util.List;

import org.jarbframework.violation.factory.ReflectionConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConstraintNameMatcher;
import org.jarbframework.violation.factory.custom.ExceptionFactoryMapping;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class ConfigurableConstraintExceptionFactoryBeanDefinitionParser extends AbstractBeanDefinitionParser {
    private static final String DEFAULT_FACTORY_ATTRIBUTE = "default-factory";
    
    private static final String EXCEPTION_MAPPING_ATTRIBUTE = "exception-mapping";
    
    private static final String MATCHER_ATTRIBUTE = "matcher";
    private static final String CONSTRAINT_EXPRESSION_ATTRIBUTE = "constraint";
    private static final String NAME_MATCHING_ATTRIBUTE = "name-matching";
    private static final String IGNORE_CASE_ATTRIBUTE = "ignore-case";
    
    private static final String EXCEPTION_FACTORY_ATTRIBUTE = "exception-factory";
    private static final String EXCEPTION_CLASS_ATTRIBUTE = "exception";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factoryBuilder = genericBeanDefinition(ConfigurableConstraintExceptionFactoryParserFactoryBean.class);
        BeanDefinition factoryDefinition = factoryBuilder.getRawBeanDefinition();
        factoryBuilder.addPropertyValue("defaultFactory", parseDefaultExceptionFactory(element, parserContext, factoryDefinition));
        factoryBuilder.addPropertyValue("customFactoryMappings", parseExceptionMappings(element, parserContext, factoryDefinition));
        return factoryBuilder.getBeanDefinition();
    }
    
    private Object parseDefaultExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parent) {
        return parsePropertyFromAttributeOrChild(element, DEFAULT_FACTORY_ATTRIBUTE, parserContext, parent);
    }
    
    private ManagedList<Object> parseExceptionMappings(Element element, ParserContext parserContext, BeanDefinition parent) {
        List<Element> mappingElements = DomUtils.getChildElementsByTagName(element, EXCEPTION_MAPPING_ATTRIBUTE);
        ManagedList<Object> mappingDefinitions = new ManagedList<Object>(mappingElements.size());
        mappingDefinitions.setElementTypeName(ExceptionFactoryMapping.class.getName());
        mappingDefinitions.setSource(parent);
        for(Element mappingElement : mappingElements) {
            mappingDefinitions.add(parseExceptionMapping(mappingElement, parserContext, parent));
        }
        return mappingDefinitions;
    }

    private BeanDefinition parseExceptionMapping(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        BeanDefinitionBuilder mappingBuilder = genericBeanDefinition(ExceptionFactoryMapping.class);
        mappingBuilder.addConstructorArgValue(parseViolationMatcher(mappingElement, parserContext, parent));
        mappingBuilder.addConstructorArgValue(parseMappedExceptionFactory(mappingElement, parserContext, parent));
        return mappingBuilder.getBeanDefinition();
    }

    private Object parseViolationMatcher(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        Object matcher = parsePropertyFromAttributeOrChild(mappingElement, MATCHER_ATTRIBUTE, parserContext, parent);
        if(matcher == null) {
            matcher = createConstraintNameMatcher(mappingElement, parserContext, parent);
        }
        return matcher;
    }
    
    private BeanDefinition createConstraintNameMatcher(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        BeanDefinitionBuilder matcherBuilder = genericBeanDefinition(ConstraintNameMatcher.class);
        matcherBuilder.setFactoryMethod(mappingElement.getAttribute(NAME_MATCHING_ATTRIBUTE));
        matcherBuilder.addConstructorArgValue(mappingElement.getAttribute(CONSTRAINT_EXPRESSION_ATTRIBUTE));
        matcherBuilder.addPropertyValue("ignoreCase", mappingElement.getAttribute(IGNORE_CASE_ATTRIBUTE));
        return matcherBuilder.getBeanDefinition();
    }

    private Object parseMappedExceptionFactory(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        Object exceptionFactory = parsePropertyFromAttributeOrChild(mappingElement, EXCEPTION_FACTORY_ATTRIBUTE, parserContext, parent);
        if(exceptionFactory == null && mappingElement.hasAttribute(EXCEPTION_CLASS_ATTRIBUTE)) {
            exceptionFactory = createReflectionExceptionFactory(mappingElement, parserContext, parent);
        }
        return exceptionFactory;
    }
    
    private BeanDefinition createReflectionExceptionFactory(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        BeanDefinitionBuilder exceptionFactoryBuilder = genericBeanDefinition(ReflectionConstraintExceptionFactory.class);
        exceptionFactoryBuilder.addConstructorArgValue(mappingElement.getAttribute(EXCEPTION_CLASS_ATTRIBUTE));
        return exceptionFactoryBuilder.getBeanDefinition();
    }

}
