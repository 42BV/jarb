package org.jarbframework.constraint.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ConstraintsNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("translate-exceptions", new TranslateExceptionsBeanDefinitionParser());
    }

}
