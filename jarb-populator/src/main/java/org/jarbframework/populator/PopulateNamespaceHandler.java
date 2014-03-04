package org.jarbframework.populator;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class PopulateNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("populate", new PopulateBeanDefinitionParser());
    }

    public static class PopulateBeanDefinitionParser implements BeanDefinitionParser {

        @Override
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            AbstractBeanDefinition listener = buildListener(element);
            parserContext.getReaderContext().registerWithGeneratedName(listener);
            return listener;
        }

        private AbstractBeanDefinition buildListener(Element element) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PopulateApplicationListener.class);
            if (element.hasAttribute("initializer")) {
                builder.addPropertyReference("initializer", element.getAttribute("initializer"));
            }
            if (element.hasAttribute("destroyer")) {
                builder.addPropertyReference("destroyer", element.getAttribute("destroyer"));
            }
            return builder.getBeanDefinition();
        }

    }

}
