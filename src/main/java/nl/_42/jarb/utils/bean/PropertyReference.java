package nl._42.jarb.utils.bean;

import static nl._42.jarb.utils.Asserts.hasText;
import static nl._42.jarb.utils.Asserts.notNull;

import nl._42.jarb.utils.Asserts;
import nl._42.jarb.utils.StringUtils;

/**
 * References a bean property.
 *
 * @author Jeroen van Schagen
 * @since Aug 29, 2011
 */
public class PropertyReference {

    private static final String PROPERTY_SEPARATOR = ".";
    
    /**
     * The base bean class.
     */
    private final Class<?> beanClass;

    /**
     * The full property name.
     */
    private final String propertyName;
    
    public PropertyReference(Class<?> beanClass, String propertyName) {
        this.beanClass = notNull(beanClass, "Bean class is required");
        this.propertyName = hasText(propertyName, "Property name is required");
    }

    public PropertyReference(PropertyReference parent, String propertyName) {
        this(parent.getBeanClass(), parent.getPropertyName() + PROPERTY_SEPARATOR + propertyName);
    }

    /**
     * Reference a property deeper in the hierarchy.
     * 
     * @param propertyName the property name, relative from our current reference
     * @return the new property reference
     */
    public PropertyReference deeper(String propertyName) {
        return new PropertyReference(this, propertyName);
    }

    /**
     * Retrieve the bean class.
     * 
     * @return the bean class
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }
    
    /**
     * Retrieve the full property name.
     * 
     * @return the full property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Retrieve the simple property name, containing no nesting.
     * 
     * @return the simple property name
     */
    public String getSimpleName() {
        return isNestedProperty() ? StringUtils.substringAfterLast(propertyName, PROPERTY_SEPARATOR) : propertyName;
    }

    /**
     * Determine if this property reference is nested.
     * 
     * @return {@code true} if nested, else {@code false}
     */
    public boolean isNestedProperty() {
        return propertyName.contains(PROPERTY_SEPARATOR);
    }

    /**
     * Retrieve the parent reference.
     * 
     * @return the parent
     */
    public PropertyReference getParent() {
        Asserts.state(isNestedProperty(), "Can only retrieve the parent for a nested property.");
        String parentName = StringUtils.substringBeforeLast(propertyName, PROPERTY_SEPARATOR);
        return new PropertyReference(beanClass, parentName);
    }

    /**
     * Retrieve all path elements in our reference.
     * 
     * @return all path elements
     */
    public String[] getPath() {
        return propertyName.split("\\" + PROPERTY_SEPARATOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PropertyReference)) {
            return false;
        }
        PropertyReference other = (PropertyReference) obj;
        return other.getBeanClass().equals(beanClass) && other.getPropertyName().equals(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return beanClass.hashCode() * propertyName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return beanClass.getSimpleName() + "." + propertyName;
    }

}
