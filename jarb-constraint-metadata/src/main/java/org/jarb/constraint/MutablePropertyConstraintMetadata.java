package org.jarb.constraint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Mutable implementation of {@link PropertyConstraintMetadata}.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 *
 * @param <T> type of property
 */
public class MutablePropertyConstraintMetadata<T> implements PropertyConstraintMetadata<T> {
    private final String propertyName;
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
     * Construct a new {@link MutablePropertyConstraintMetadata}.
     * @param propertyName name of the property
     * @param propertyClass class of the property
     */
    public MutablePropertyConstraintMetadata(String propertyName, Class<T> propertyClass) {
        Assert.hasText(propertyName);
        Assert.notNull(propertyClass);
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        types = new HashSet<PropertyType>();
        types.add(PropertyType.forClass(propertyClass));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return propertyName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getJavaType() {
        return propertyClass;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<PropertyType> getTypes() {
        return Collections.unmodifiableSet(types);
    }
    
    public MutablePropertyConstraintMetadata<T> addType(PropertyType type) {
        types.add(type);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRequired() {
        return required;
    }

    public MutablePropertyConstraintMetadata<T> setRequired(boolean required) {
        this.required = required;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMinimumLength() {
        return minimumLength;
    }

    public MutablePropertyConstraintMetadata<T> setMinimumLength(Integer minimumLength) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMaximumLength() {
        return maximumLength;
    }

    public MutablePropertyConstraintMetadata<T> setMaximumLength(Integer maximumLength) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFractionLength() {
        return fractionLength;
    }

    public MutablePropertyConstraintMetadata<T> setFractionLength(Integer fractionLength) {
        if (fractionLength != null && fractionLength < 0) {
            throw new IllegalStateException("Fraction length cannot be negative.");
        }
        this.fractionLength = fractionLength;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getRadix() {
        return radix;
    }

    public MutablePropertyConstraintMetadata<T> setRadix(Integer radix) {
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
