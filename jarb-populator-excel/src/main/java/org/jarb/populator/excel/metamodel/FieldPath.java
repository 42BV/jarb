package org.jarb.populator.excel.metamodel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.jarb.populator.excel.metamodel.FieldPath.FieldNode;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Indicates a path of fields.
 * 
 * @author Jeroen van Schagen
 * @since 15-06-2011
 */
public final class FieldPath implements Iterable<FieldNode> {
    private final LinkedList<FieldNode> nodes;
    
    /**
     * Construct a new {@link FieldPath}.
     */
    private FieldPath() {
        nodes = new LinkedList<FieldNode>();
    }
    
    /**
     * Construct a new {@link FieldPath}.
     * @param nodes initial nodes
     */
    private FieldPath(Collection<FieldNode> nodes) {
        this.nodes = new LinkedList<FieldNode>(nodes);
    }
    
    /**
     * Construct a new field path starting from the specified field.
     * @param field the field to start our path from
     * @return new field path, containing only the specified field as node
     */
    public static FieldPath startingFrom(Field field) {
        FieldPath fieldPath = new FieldPath();
        fieldPath.addField(field);
        return fieldPath;
    }
    
    /**
     * Construct a new field path starting from the specified field name. Whenever
     * the specified field name is not actually a declared field, we will throw
     * a runtime exception.
     * @param rootClass class containing the field
     * @param fieldName name of the field, as defined in the containing class
     * @return new field path, containing only the specified field as node
     */
    public static FieldPath startingFrom(Class<?> rootClass, String fieldName) {
        Field field = ReflectionUtils.findField(rootClass, fieldName);
        if(field == null) {
            String msg = String.format("Field '%s' does not exist in '%s'.", fieldName, rootClass.getSimpleName());
            throw new IllegalStateException(msg);
        }
        return FieldPath.startingFrom(field);
    }
    
    /**
     * Append a field node to this path, should only be invoked internally.
     * @param field the field to use for our name
     */
    private void addField(Field field) {
        nodes.add(new FieldNode(field));
    }

    /**
     * Create an extended path that goes from our current start field to the
     * newly specified leaf field. Whenever the current leaf field type has
     * not declared the new field, we will throw a runtime exception.
     * @param field the new leaf field
     * @return new path that extends our previous path with an additional field
     */
    public FieldPath to(Field field) {
        final Class<?> leafType = nodes.getLast().getType();
        if(fieldIsDeclaredIn(field, leafType)) {
            FieldPath extendedPath = new FieldPath(nodes);
            extendedPath.addField(field);
            return extendedPath;
        } else {
            throw new IllegalStateException(
                String.format(
                    "Cannot extend path to '%s.%s' as the field is not declared in '%s'.", 
                    field.getDeclaringClass().getSimpleName(),
                    field.getName(),
                    leafType.getSimpleName()
                )
            );
        }
    }
    
    /**
     * Determine if a field is declared inside a specific class.
     * @param field the field to check
     * @param declaringClass the declaring class
     * @return {@code true} if it has been declared, else {@code false}
     */
    private boolean fieldIsDeclaredIn(Field field, Class<?> declaringClass) {
        return field.getDeclaringClass().isAssignableFrom(declaringClass);
    }
    
    /**
     * Create an extended path that goes from our current start field to the
     * newly specified leaf field. Whenever the current leaf field type has
     * not declared the new field, we will throw a runtime exception
     * @param fieldName name of the new leaf field
     * @return new path that extends our previous path with an additional field
     */
    public FieldPath to(String fieldName) {
        final Class<?> leafType = nodes.getLast().getType();
        Field field = ReflectionUtils.findField(leafType, fieldName);
        if(field == null) {
            throw new IllegalStateException(
                String.format("Field '%s' does not exist in '%s'.", fieldName, leafType.getSimpleName())
            );
        }
        return this.to(field);
    }
    
    /**
     * Retrieve the field value of an object. So the field path
     * ["address"."street"."name"] will retrieve the address
     * street name of an object.
     * @param object the object containing our field
     * @return field value of the provided object
     */
    public Object getValueFor(Object object) {
        Object currentValue = object;
        for(FieldNode fieldNode : this) {
            Field field = fieldNode.getField();
            field.setAccessible(true);
            currentValue = ReflectionUtils.getField(field, currentValue);
        }
        return currentValue;
    }
    
    /**
     * Retrieve the first node of our path.
     * @return first field node
     */
    public FieldNode getStart() {
        return nodes.getFirst();
    }
    
    /**
     * Retrieve the last node of our path.
     * @return last field node
     */
    public FieldNode getEnd() {
        return nodes.getLast();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<FieldNode> iterator() {
        return nodes.iterator();
    }

    /**
     * Specific node in our path.
     */
    public static final class FieldNode {
        private final Field field;
        
        /**
         * Construct a new {@link 
         * @param field the field being represented
         */
        private FieldNode(Field field) {
            Assert.notNull(field, "Field cannot be null");
            this.field = field;
        }
        
        /**
         * Retrieve the actual field.
         * @return field
         */
        public Field getField() {
            return field;
        }
        
        /**
         * Retrieve the field name.
         * @return field name
         */
        public String getName() {
            return field.getName();
        }
        
        /**
         * Retrieve the field type.
         * @return field type
         */
        public Class<?> getType() {
            return field.getType();
        }
    }
    
}
