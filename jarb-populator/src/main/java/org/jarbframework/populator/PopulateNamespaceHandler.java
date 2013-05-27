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
            BeanDefinitionBuilder listenerBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabasePopulateListener.class);

            if (element.hasAttribute("init-ref")) {
                listenerBuilder.addPropertyReference("initializer", element.getAttribute("init-ref"));
            }
            if (element.hasAttribute("destroy-ref")) {
                listenerBuilder.addPropertyReference("destroyer", element.getAttribute("destroy-ref"));
            }

            return listenerBuilder.getBeanDefinition();
        }

    }

}
