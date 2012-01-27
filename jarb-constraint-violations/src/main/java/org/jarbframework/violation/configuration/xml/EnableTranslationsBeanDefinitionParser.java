package org.jarbframework.violation.configuration.xml;

import static org.jarbframework.utils.spring.xml.BeanParsingHelper.parsePropertyFromAttributeOrChild;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

import org.jarbframework.violation.configuration.DatabaseConstraintExceptionTranslatingBeanPostProcessor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses a {@link DatabaseConstraintExceptionTranslatingBeanPostProcessor} from XML.
 * @author Jeroen van Schagen
 * @since 22-09-2011
 */
public class EnableTranslationsBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = genericBeanDefinition(DatabaseConstraintExceptionTranslatingBeanPostProcessor.class);
        builder.addPropertyValue("translator", parseTranslator(element, parserContext, builder.getBeanDefinition()));
        if (element.hasAttribute("pointcut")) {
            builder.addPropertyValue("pointcut", createPointcut(element.getAttribute("pointcut")));
        }
        return builder.getBeanDefinition();
    }

    private Object parseTranslator(Element element, ParserContext parserContext, BeanDefinition parentDefinition) {
        return parsePropertyFromAttributeOrChild(element, "translator", parserContext, parentDefinition);
    }

    private BeanDefinition createPointcut(String expression) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(AspectJExpressionPointcut.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanDefinition.setSynthetic(true);
        beanDefinition.getPropertyValues().add("expression", expression);
        return beanDefinition;
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

}
