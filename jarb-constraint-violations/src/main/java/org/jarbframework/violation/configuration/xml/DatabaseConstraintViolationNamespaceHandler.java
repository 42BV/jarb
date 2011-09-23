package org.jarbframework.violation.configuration.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DatabaseConstraintViolationNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("enable-translations", new EnableTranslationsBeanDefinitionParser());
        registerBeanDefinitionParser("translator", new DatabaseConstraintExceptionTranslatorBeanDefinitionParser());
        registerBeanDefinitionParser("configurable-exception-factory", new ConfigurableConstraintExceptionFactoryBeanDefinitionParser());
    }

}
