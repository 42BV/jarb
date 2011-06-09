package org.jarb.constraint;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

    // Global requirements
    private boolean required;
    private Integer minimumLength;
    private Integer maximumLength;

    // Number specific
    private Integer fractionLength;
    private Integer radix;

    public MutablePropertyConstraintMetadata(String propertyName, Class<T> propertyClass) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getPropertyType() {
        return propertyClass;
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
