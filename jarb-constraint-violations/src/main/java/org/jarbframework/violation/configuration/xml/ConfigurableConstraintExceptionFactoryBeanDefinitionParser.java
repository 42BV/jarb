package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.violation.factory.ReflectionConstraintExceptionFactory;
import org.jarbframework.violation.factory.custom.ConstraintViolationMatcher;
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
    private static final String EXPRESSION_ATTRIBUTE = "expression";
    private static final String MATCHING_TYPE_ATTRIBUTE = "matching";
    private static final String MATCHING_STRATEGY_ATTRIBUTE = "matching-strategy";
    private static final String EXCEPTION_FACTORY_ATTRIBUTE = "exception-factory";
    private static final String EXCEPTION_CLASS_ATTRIBUTE = "exception-class";
    
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factoryBuilder = genericBeanDefinition(ConfigurableConstraintExceptionFactoryParserFactoryBean.class);
        BeanDefinition factoryDefinition = factoryBuilder.getRawBeanDefinition();
        factoryBuilder.addPropertyValue("defaultFactory", parseExceptionFactory(element, parserContext, factoryDefinition));
        factoryBuilder.addPropertyValue("customFactoryMappings", parseExceptionMappings(element, parserContext, factoryDefinition));
        return factoryBuilder.getBeanDefinition();
    }
    
    private Object parseExceptionFactory(Element element, ParserContext parserContext, BeanDefinition parent) {
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

    private BeanDefinition parseViolationMatcher(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        BeanDefinitionBuilder violationMatcher = genericBeanDefinition(ConstraintViolationMatcher.class);
        String expression = mappingElement.getAttribute(EXPRESSION_ATTRIBUTE);
        Object matchingStrategy = parseMatchingStrategy(mappingElement, parserContext, parent);
        if(matchingStrategy != null) {
            violationMatcher.addConstructorArgValue(expression);
            violationMatcher.addConstructorArgValue(matchingStrategy);
        } else {
            violationMatcher.setFactoryMethod(mappingElement.getAttribute(MATCHING_TYPE_ATTRIBUTE));
            violationMatcher.addConstructorArgValue(expression);
        }
        return violationMatcher.getBeanDefinition();
    }

    private Object parseMatchingStrategy(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        return parsePropertyFromAttributeOrChild(mappingElement, MATCHING_STRATEGY_ATTRIBUTE, parserContext, parent);
    }
    
    private Object parseMappedExceptionFactory(Element mappingElement, ParserContext parserContext, BeanDefinition parent) {
        Object exceptionFactory = parsePropertyFromAttributeOrChild(mappingElement, EXCEPTION_FACTORY_ATTRIBUTE, parserContext, parent);
        if(exceptionFactory == null) {
            String exceptionClass = mappingElement.getAttribute(EXCEPTION_CLASS_ATTRIBUTE);
            if(StringUtils.isNotBlank(exceptionClass)) {
                BeanDefinitionBuilder exceptionFactoryBuilder = genericBeanDefinition(ReflectionConstraintExceptionFactory.class);
                exceptionFactoryBuilder.addConstructorArgValue(exceptionClass);
                exceptionFactory = exceptionFactoryBuilder.getBeanDefinition();
            }
        }
        return exceptionFactory;
    }

}
