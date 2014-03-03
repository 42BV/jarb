package org.jarbframework.constraint.metadata;

import static java.util.Collections.unmodifiableSet;
import static org.jarbframework.utils.Asserts.notNull;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jarbframework.utils.bean.PropertyReference;

/**
 * Describes a bean property.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 *
 * @param <T> type of property
 */
public class PropertyConstraintDescription {

    private final PropertyReference reference;
    
    private final Class<?> javaType;

    private final Set<String> types = new HashSet<String>();

    private boolean required;
    
    private Integer minimumLength;
    
    private Integer maximumLength;

    private Integer fractionLength;
    
    private Integer radix;
    
    private String pattern;

    /**
     * Construct a new {@link PropertyConstraintDescription}.
     * 
     * @param reference reference to the property
     * @param javaType class of the property
     */
    public PropertyConstraintDescription(PropertyReference reference, Class<?> javaType) {
        this.reference = notNull(reference, "Reference cannot be null.");
        this.javaType = notNull(javaType, "Java type cannot be null.");
    }

    public String getName() {
        return reference.getName();
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public Set<String> getTypes() {
        return unmodifiableSet(types);
    }

    public PropertyConstraintDescription addType(String type) {
        types.add(type);
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public PropertyConstraintDescription setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public PropertyConstraintDescription setMinimumLength(Integer minimumLength) {
        if (minimumLength != null && minimumLength < 0) {
            throw new IllegalStateException("Minimum length cannot be negative.");
        }
        if (! isRange(minimumLength, maximumLength)) {
        	String message = String.format("Cannot change minimum length to '%d', as the maximum length '%d' is lower.", minimumLength, maximumLength);
            throw new IllegalStateException(message);
        }
        this.minimumLength = minimumLength;
        return this;
    }

    public Integer getMaximumLength() {
        return maximumLength;
    }

    public PropertyConstraintDescription setMaximumLength(Integer maximumLength) {
        if (maximumLength != null && maximumLength < 0) {
            throw new IllegalStateException("Maximum length cannot be negative.");
        }
        if (! isRange(minimumLength, maximumLength)) {
        	String message = String.format("Cannot change maximum length to '%d', as the minimum length '%d' is greater.", maximumLength, minimumLength);
            throw new IllegalStateException(message);
        }
        this.maximumLength = maximumLength;
        return this;
    }

    private boolean isRange(Integer start, Integer end) {
        return start == null || end == null || end >= start;
    }

    public Integer getFractionLength() {
        return fractionLength;
    }

    public PropertyConstraintDescription setFractionLength(Integer fractionLength) {
        if (fractionLength != null && fractionLength < 0) {
            throw new IllegalStateException("Fraction length cannot be negative.");
        }
        this.fractionLength = fractionLength;
        return this;
    }

    public Integer getRadix() {
        return radix;
    }

    public PropertyConstraintDescription setRadix(Integer radix) {
        this.radix = radix;
        return this;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public PropertyReference toReference() {
        return reference;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
