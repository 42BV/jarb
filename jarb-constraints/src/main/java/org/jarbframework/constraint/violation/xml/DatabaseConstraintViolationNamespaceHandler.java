package org.jarbframework.constraint.violation.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DatabaseConstraintViolationNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("translator", new DatabaseConstraintExceptionTranslatorBeanDefinitionParser());
        registerBeanDefinitionParser("configurable-exception-factory", new ConfigurableConstraintExceptionFactoryBeanDefinitionParser());
        registerBeanDefinitionParser("enable-translations", new EnableTranslationsBeanDefinitionParser());
    }

}
