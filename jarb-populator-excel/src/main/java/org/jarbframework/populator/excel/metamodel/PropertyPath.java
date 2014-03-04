package org.jarbframework.populator.excel.metamodel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jarbframework.utils.bean.FlexibleBeanWrapper;
import org.springframework.util.ReflectionUtils;

/**
 * Indicates a path of fields.
 * 
 * @author Jeroen van Schagen
 * @since 15-06-2011
 */
public final class PropertyPath implements Iterable<PropertyNode> {
    private final LinkedList<PropertyNode> nodes;

    /**
     * Construct a new {@link PropertyPath}.
     */
    private PropertyPath() {
        nodes = new LinkedList<PropertyNode>();
    }

    /**
     * Construct a new {@link PropertyPath}.
     * @param nodes initial nodes
     */
    private PropertyPath(Collection<PropertyNode> nodes) {
        this.nodes = new LinkedList<PropertyNode>(nodes);
    }

    /**
     * Construct a new {@link PropertyPath} based on some field.
     * @param propertyField property field on which we start
     * @return new property path
     */
    public static PropertyPath startingFrom(Field propertyField) {
        PropertyPath propertyPath = new PropertyPath();
        propertyPath.addProperty(propertyField);
        return propertyPath;
    }

    /**
     * Construct a new {@link PropertyPath} starting from the specified field name.
     * Whenever the specified field name is not actually a declared field, we will
     * throw a runtime exception.
     * @param rootClass class containing the field
     * @param propertyName name of the field, as defined in the containing class
     * @return new property path, containing only the specified field as node
     */
    public static PropertyPath startingFrom(Class<?> rootClass, String propertyName) {
        Field propertyField = ReflectionUtils.findField(rootClass, propertyName);
        if (propertyField == null) {
            throw new IllegalStateException(String.format("Property '%s' does not exist in '%s'.", propertyName, rootClass.getSimpleName()));
        }
        return PropertyPath.startingFrom(propertyField);
    }

    /**
     * Append a field node to this path, should only be invoked internally.
     * @param field the field to use for our name
     */
    private void addProperty(Field field) {
        nodes.add(new PropertyNode(field));
    }

    /**
     * Create an extended path that goes from our current start field to the
     * newly specified leaf field. Whenever the current leaf field type has
     * not declared the new field, we will throw a runtime exception
     * @param propertyName name of the new leaf field
     * @return new path that extends our previous path with an additional field
     */
    public PropertyPath to(String propertyName) {
        final Class<?> leafType = nodes.getLast().getType();
        Field propertyField = ReflectionUtils.findField(leafType, propertyName);
        if (propertyField == null) {
            throw new IllegalStateException(String.format("Property '%s' does not exist in '%s'.", propertyName, leafType.getSimpleName()));
        } else {
            PropertyPath extendedPath = new PropertyPath(nodes);
            extendedPath.addProperty(propertyField);
            return extendedPath;
        }
    }

    /**
     * Retrieve the field value of an object. So the field path
     * ["address"."street"."name"] will retrieve the address
     * street name of an object.
     * @param object the object containing our field
     * @return field value of the provided object
     */
    public Object traverse(Object object) {
        Object value = object;
        for (PropertyNode propertyNode : this) {
            if (value == null) {
                break; // Quit looping whenever null, as we cannot go any deeper
            }
            value = FlexibleBeanWrapper.wrap(value).getPropertyValue(propertyNode.getName());
        }
        return value;
    }

    /**
     * Retrieve the first node of our path.
     * @return first field node
     */
    public PropertyNode getStart() {
        return nodes.getFirst();
    }

    /**
     * Retrieve the last node of our path.
     * @return last field node
     */
    public PropertyNode getEnd() {
        return nodes.getLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<PropertyNode> iterator() {
        return nodes.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getStart().hashCode();
    }

}
