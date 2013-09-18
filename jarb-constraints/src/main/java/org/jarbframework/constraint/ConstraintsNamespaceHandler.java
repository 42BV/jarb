package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.EnableMetadataBeanDefinitionParser;
import org.jarbframework.constraint.violation.TranslateExceptionsBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ConstraintsNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("enable-metadata", new EnableMetadataBeanDefinitionParser());
        registerBeanDefinitionParser("translate-exceptions", new TranslateExceptionsBeanDefinitionParser());
    }

}
