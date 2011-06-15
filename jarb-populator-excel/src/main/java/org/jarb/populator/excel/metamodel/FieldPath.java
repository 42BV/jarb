package org.jarb.populator.excel.metamodel;

import java.lang.reflect.Field;
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
    
    public static FieldPath singleField(Field field) {
        FieldPath path = new FieldPath();
        path.addField(field);
        return path;
    }
    
    /**
     * Create a path of fields based on field names.
     * @param rootClass class of the root element
     * @param fieldNames array of field names
     * @return path to reach the leaf field
     */
    public static FieldPath forNames(Class<?> rootClass, String... fieldNames) {
        FieldPath path = new FieldPath();
        Class<?> containerClass = rootClass;
        for(String fieldName : fieldNames) {
            Field field = ReflectionUtils.findField(containerClass, fieldName);
            if(field == null) {
                String msg = String.format("Field '%s' does not exist in '%s'.", fieldName, containerClass.getSimpleName());
                throw new IllegalStateException(msg);
            }
            path.addField(field);
            containerClass = field.getType();
        }
        return path;
    }
    
    /**
     * Include a field node in this path.
     * @param field the field being added
     */
    private void addField(Field field) {
        nodes.add(new FieldNode(field));
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
    
    public boolean isEmpty() {
        return nodes.isEmpty();
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
        public String getFieldName() {
            return field.getName();
        }
    }
    
}
