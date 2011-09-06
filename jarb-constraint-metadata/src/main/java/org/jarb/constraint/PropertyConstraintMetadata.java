package org.jarb.constraint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jarb.utils.bean.PropertyReference;
import org.springframework.util.Assert;

/**
 * Describes a bean property.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 *
 * @param <T> type of property
 */
public class PropertyConstraintMetadata<T> {
    private final PropertyReference propertyReference;
    private final Class<T> propertyClass;

    private Set<PropertyType> types;

    // Global requirements
    private boolean required;
    private Integer minimumLength;
    private Integer maximumLength;

    // Number specific
    private Integer fractionLength;
    private Integer radix;

    /**
     * Construct a new {@link PropertyConstraintMetadata}.
     * @param propertyName name of the property
     * @param propertyClass class of the property
     */
    public PropertyConstraintMetadata(PropertyReference propertyReference, Class<T> propertyClass) {
        Assert.notNull(propertyReference);
        Assert.notNull(propertyClass);
        this.propertyReference = propertyReference;
        this.propertyClass = propertyClass;
        types = new HashSet<PropertyType>();
        types.add(PropertyType.forClass(propertyClass));
    }

    public PropertyReference getPropertyReference() {
        return propertyReference;
    }

    public String getName() {
        return propertyReference.getName();
    }

    public Class<?> getBeanClass() {
        return propertyReference.getBeanClass();
    }

    public Class<T> getJavaType() {
        return propertyClass;
    }

    public Collection<PropertyType> getTypes() {
        return Collections.unmodifiableSet(types);
    }

    public PropertyConstraintMetadata<T> addType(PropertyType type) {
        types.add(type);
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public PropertyConstraintMetadata<T> setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public PropertyConstraintMetadata<T> setMinimumLength(Integer minimumLength) {
        if (minimumLength != null && minimumLength < 0) {
            throw new IllegalStateException("Minimum length cannot be negative.");
        }
        if (isRange(minimumLength, maximumLength)) {
            this.minimumLength = minimumLength;
        } else {
            String message = String.format("Cannot change minimum length to '%d', as the maximum length '%d' is lower.", minimumLength, maximumLength);
            throw new IllegalStateException(message);
        }
        return this;
    }

    public Integer getMaximumLength() {
        return maximumLength;
    }

    public PropertyConstraintMetadata<T> setMaximumLength(Integer maximumLength) {
        if (maximumLength != null && maximumLength < 0) {
            throw new IllegalStateException("Maximum length cannot be negative.");
        }
        if (isRange(minimumLength, maximumLength)) {
            this.maximumLength = maximumLength;
        } else {
            String message = String.format("Cannot change maximum length to '%d', as the minimum length '%d' is greater.", maximumLength, minimumLength);
            throw new IllegalStateException(message);
        }
        return this;
    }

    private boolean isRange(Integer start, Integer end) {
        return start == null || end == null || end >= start;
    }

    public Integer getFractionLength() {
        return fractionLength;
    }

    public PropertyConstraintMetadata<T> setFractionLength(Integer fractionLength) {
        if (fractionLength != null && fractionLength < 0) {
            throw new IllegalStateException("Fraction length cannot be negative.");
        }
        this.fractionLength = fractionLength;
        return this;
    }

    public Integer getRadix() {
        return radix;
    }

    public PropertyConstraintMetadata<T> setRadix(Integer radix) {
        this.radix = radix;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
