package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.StringUtils;

/**
 * References a bean property.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class PropertyReference {

    private static final String PROPERTY_SEPARATOR = ".";
    
    private final Class<?> beanClass;

    private final String name;
    
    public PropertyReference(Class<?> beanClass, String name) {
        this.beanClass = notNull(beanClass, "Bean class is required");
        this.name = hasText(name, "Property name is required");
    }

    public PropertyReference(PropertyReference parent, String name) {
        this(parent.getBeanClass(), parent.getName() + PROPERTY_SEPARATOR + name);
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return isNestedProperty() ? StringUtils.substringAfterLast(name, PROPERTY_SEPARATOR) : name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isNestedProperty() {
        return name.contains(PROPERTY_SEPARATOR);
    }

    public String getNestedName() {
        return isNestedProperty() ? StringUtils.substringAfter(name, PROPERTY_SEPARATOR) : name;
    }

    public PropertyReference getParent() {
        Asserts.state(isNestedProperty(), "Can only retrieve the parent for a nested property.");
        String parentName = StringUtils.substringBeforeLast(name, PROPERTY_SEPARATOR);
        return new PropertyReference(beanClass, parentName);
    }

    public String[] getPath() {
        return name.split("\\" + PROPERTY_SEPARATOR);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PropertyReference)) {
            return false;
        }
        PropertyReference other = (PropertyReference) obj;
        return other.getBeanClass().equals(beanClass) && other.getName().equals(name);
    }

    @Override
    public int hashCode() {
        return beanClass.hashCode() * name.hashCode();
    }

    @Override
    public String toString() {
        return beanClass.getSimpleName() + "." + name;
    }

}
