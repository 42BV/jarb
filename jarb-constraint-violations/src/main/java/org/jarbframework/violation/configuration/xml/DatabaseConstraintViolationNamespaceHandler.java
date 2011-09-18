package org.jarbframework.violation.configuration.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DatabaseConstraintViolationNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("translate-exceptions", new TranslateExceptionsBeanDefinitionParser());
    }

}
