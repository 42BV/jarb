package org.jarbframework.constraint.violation.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.constraint.violation.factory.ReflectionConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.custom.ExceptionFactoryMapping;
import org.jarbframework.constraint.violation.factory.custom.ViolationNamePredicate;
import org.jarbframework.constraint.violation.factory.custom.NameMatchingStrategy;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses a {@link ExceptionFactoryMapping} from XML.
 * @author Jeroen van Schagen
 * @since 22-09-2011
 */
public class ExceptionFactoryMappingBeanDefinitionParser implements BeanDefinitionParser {
    private final BeanDefinition parentDefinition;

    public ExceptionFactoryMappingBeanDefinitionParser(BeanDefinition parentDefinition) {
        this.parentDefinition = parentDefinition;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder mappingBuilder = genericBeanDefinition(ExceptionFactoryMapping.class);
        mappingBuilder.addConstructorArgValue(parseViolationMatcher(element, parserContext));
        Object exceptionFactory = parseMappedExceptionFactory(element, parserContext);
        if (exceptionFactory != null) {
            mappingBuilder.addConstructorArgValue(exceptionFactory);
        } else {
            parserContext.getReaderContext().error("Exception mapping has declared no exception factory.", element);
        }
        return mappingBuilder.getBeanDefinition();
    }

    private Object parseViolationMatcher(Element element, ParserContext parserContext) {
        Object matcher = parsePropertyFromAttributeOrChild(element, "matcher", parserContext, parentDefinition);
        if (matcher == null) {
            matcher = parseNameBasedViolationMatcher(element, parserContext);
        }
        return matcher;
    }

    private BeanDefinition parseNameBasedViolationMatcher(Element element, ParserContext parserContext) {
        final String matchingStrategyName = element.getAttribute("name-matching").toUpperCase();
        
        BeanDefinitionBuilder matcherBuilder = genericBeanDefinition(ViolationNamePredicate.class);
        matcherBuilder.addConstructorArgValue(element.getAttribute("constraint"));
        matcherBuilder.addConstructorArgValue(NameMatchingStrategy.valueOf(matchingStrategyName));
        return matcherBuilder.getBeanDefinition();
    }

    private Object parseMappedExceptionFactory(Element element, ParserContext parserContext) {
        Object exceptionFactory = parsePropertyFromAttributeOrChild(element, "exception-factory", parserContext, parentDefinition);
        if ((exceptionFactory == null) && (element.hasAttribute("exception"))) {
            exceptionFactory = createReflectionExceptionFactory(element.getAttribute("exception"));
        }
        return exceptionFactory;
    }

    private BeanDefinition createReflectionExceptionFactory(String exceptionClass) {
        BeanDefinitionBuilder exceptionFactoryBuilder = genericBeanDefinition(ReflectionConstraintExceptionFactory.class);
        exceptionFactoryBuilder.addConstructorArgValue(exceptionClass);
        return exceptionFactoryBuilder.getBeanDefinition();
    }

}
