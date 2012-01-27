package org.jarbframework.utils.spring.xml;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Helps with parsing bean definitions in XML.
 * 
 * @author Jeroen van Schagen
 * @since 19 September 2011
 */
public final class BeanParsingHelper {

    /**
     * Parses the child of an element, for example:
     * <p>
     * <code>
     *  &lt;my-tag&gt;<br>
     *  &#160;&lt;bean class="myproject.MyBean"/&gt;<br>
     *  &lt;my-tag/&gt;
     * </code>
     * <p>
     * Results in a bean definition for <i>MyBean</i>.
     * 
     * @param element the element that should have its child parsed.
     * We expect the provided element to have zero or one child.
     * @param parserContext context of the bean parsing process.
     * @param beanDefinition parent bean definition, used to check
     * uniqueness of bean names.
     * @return containing bean definition, if any
     */
    public static Object parseBeanInsideElement(Element element, ParserContext parserContext, BeanDefinition beanDefinition) {
        Object result = null;
        List<Element> children = DomUtils.getChildElements(element);
        if (children.size() == 1) {
            Element child = children.get(0);
            result = parserContext.getDelegate().parsePropertySubElement(child, beanDefinition);
        } else if (children.size() > 1) {
            parserContext.getReaderContext().error("Expected only one child element inside " + element.getTagName() + ", not multiple.", element);
        }
        return result;
    }

    /**
     * Parses some custom property from an XML element. During the parsing
     * process we first look for a corresponding property attribute in our
     * element. Whenever no attribute is defined, we look for a child element
     * with a corresponding tag name.
     * <p>
     * Property definitions can be defined as reference on the attribute:
     * <p>
     * <code>
     * &lt;my-tag some-property="x"/&gt;<br>
     * </code>
     * <p>
     * Or as custom element, with a bean definition inside:
     * <p>
     * <code>
     * &lt;my-tag&gt;<br>
     * &#160;&lt;some-property&gt;<br>
     * &#160;&#160;&lt;bean class="myproject.MyBean"/&gt;<br>
     * &#160&lt;some-property/&gt;<br>
     * &lt;my-tag/&gt;
     * </code>
     * 
     * @param element the element that contains our property
     * @param propertyName name of the property that should be parsed
     * @param parserContext context of the bean parsing process.
     * @param beanDefinition parent bean definition, used to check
     * uniqueness of bean names.
     * @return property bean definition, if any
     */
    public static Object parsePropertyFromAttributeOrChild(Element element, String propertyName, ParserContext parserContext, BeanDefinition beanDefinition) {
        Object result = null;
        String attributeReference = element.getAttribute(propertyName);
        if (StringUtils.isNotBlank(attributeReference)) {
            result = new RuntimeBeanReference(attributeReference);
        } else {
            Element childElement = DomUtils.getChildElementByTagName(element, propertyName);
            if (childElement != null) {
                result = parseBeanInsideElement(childElement, parserContext, beanDefinition);
            }
        }
        return result;
    }

    private BeanParsingHelper() {
    }

}
