/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * References a bean property.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class PropertyReference {
    private final String name;
    private final Class<?> beanClass;

    public PropertyReference(Class<?> beanClass, String name) {
        this.name = hasText(name, "Property name is required");
        this.beanClass = notNull(beanClass, "Bean class is required");
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
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
}
