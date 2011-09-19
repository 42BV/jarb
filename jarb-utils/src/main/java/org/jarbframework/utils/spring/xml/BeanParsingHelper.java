package org.jarbframework.utils.spring.xml;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public final class BeanParsingHelper {

    public static Object parseBeanInsideCustomElement(Element element, ParserContext context, BeanDefinition containingBeanDefinition) {
        Object result = null;
        List<Element> children = DomUtils.getChildElements(element);
        if(children.size() == 1) {
            Element child = children.get(0);
            result = context.getDelegate().parsePropertySubElement(child, containingBeanDefinition);
        } else {
            context.getReaderContext().error("Expected only one child element inside " + element.getTagName() + ".", element);
        }
        return result;
    }

    private BeanParsingHelper() {
    }
    
}
