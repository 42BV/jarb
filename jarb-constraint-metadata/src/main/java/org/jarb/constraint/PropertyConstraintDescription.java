package org.jarb.constraint;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class PropertyConstraintDescription<T> {
    private final String propertyName;
    private final Class<T> propertyClass;

    // Global requirements
    private boolean required;
    private T defaultValue;
    private Integer minimumLength;
    private Integer maximumLength;

    // Number specific
    private Integer fractionLength;
    private Integer radix;

    public PropertyConstraintDescription(String propertyName, Class<T> propertyClass) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<T> getPropertyClass() {
        return propertyClass;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(Integer minimumLength) {
        if (minimumLength != null && minimumLength < 0) {
            throw new IllegalStateException("Minimum length cannot be negative.");
        }
        if (isRange(minimumLength, maximumLength)) {
            this.minimumLength = minimumLength;
        } else {
            String message = String.format("Cannot change minimum length to '%d', as the maximum length '%d' is lower.", minimumLength, maximumLength);
            throw new IllegalStateException(message);
        }
    }

    public Integer getMaximumLength() {
        return maximumLength;
    }

    public void setMaximumLength(Integer maximumLength) {
        if (maximumLength != null && maximumLength < 0) {
            throw new IllegalStateException("Maximum length cannot be negative.");
        }
        if (isRange(minimumLength, maximumLength)) {
            this.maximumLength = maximumLength;
        } else {
            String message = String.format("Cannot change maximum length to '%d', as the minimum length '%d' is greater.", maximumLength, minimumLength);
            throw new IllegalStateException(message);
        }
    }

    private boolean isRange(Integer start, Integer end) {
        return start == null || end == null || end >= start;
    }

    public Integer getFractionLength() {
        return fractionLength;
    }

    public void setFractionLength(Integer fractionLength) {
        if (fractionLength != null && fractionLength < 0) {
            throw new IllegalStateException("Fraction length cannot be negative.");
        }
        this.fractionLength = fractionLength;
    }

    public Integer getRadix() {
        return radix;
    }

    public void setRadix(Integer radix) {
        this.radix = radix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
