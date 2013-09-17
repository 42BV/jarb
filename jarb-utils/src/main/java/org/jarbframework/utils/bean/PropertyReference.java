package org.jarbframework.utils.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jarbframework.utils.Asserts;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

/**
 * References a bean property.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class PropertyReference {

    private static final String PROPERTY_SEPARATOR = ".";

    private final String name;
    
    private final Class<?> beanClass;

    public PropertyReference(Class<?> beanClass, String name) {
        this.name = hasText(name, "Property name is required");
        this.beanClass = notNull(beanClass, "Bean class is required");
    }

    public PropertyReference(PropertyReference parent, String name) {
        this(parent.getBeanClass(), parent.getName() + PROPERTY_SEPARATOR + name);
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return isNestedProperty() ? substringAfterLast(name, PROPERTY_SEPARATOR) : name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isNestedProperty() {
        return name.contains(PROPERTY_SEPARATOR);
    }

    public String getNestedName() {
        return isNestedProperty() ? substringAfter(name, PROPERTY_SEPARATOR) : name;
    }

    public PropertyReference getParent() {
        Asserts.state(isNestedProperty(), "Can only retrieve the parent for a nested property.");
        String parentName = StringUtils.substringBeforeLast(name, PROPERTY_SEPARATOR);
        return new PropertyReference(beanClass, parentName);
    }

    public String[] getPath() {
        return StringUtils.split(name, PROPERTY_SEPARATOR);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return beanClass.getSimpleName() + "." + name;
    }

	public PropertyReference wrap(PropertyReference parent) {
		if (parent == null) {
			return this;
		}
		return new PropertyReference(parent, name);
	}
	
}
