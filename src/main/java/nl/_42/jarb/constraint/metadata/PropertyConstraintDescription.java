package nl._42.jarb.constraint.metadata;

import static java.util.Collections.unmodifiableSet;
import static nl._42.jarb.utils.Asserts.notNull;

import java.util.LinkedHashSet;
import java.util.Set;

import nl._42.jarb.utils.bean.PropertyReference;

/**
 * Describes a bean property.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class PropertyConstraintDescription {

    private final PropertyReference reference;
    
    private final Class<?> javaType;

    private final Set<String> types = new LinkedHashSet<String>();

    private boolean required;
    
    private Integer minimumLength;
    
    private Integer maximumLength;

    private Integer fractionLength;
    
    private Integer radix;
    
    private String pattern;
    
    private Object min;
    
    private Object max;

    /**
     * Construct a new {@link PropertyConstraintDescription}.
     * 
     * @param reference reference to the property
     * @param javaType class of the property
     */
    public PropertyConstraintDescription(PropertyReference reference, Class<?> javaType) {
        this.reference = notNull(reference, "Reference cannot be null.");
        this.javaType = notNull(javaType, "Java type of '" + reference.toString() + "' cannot be null.");
    }

    public String getName() {
        return reference.getPropertyName();
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
    
    public Object getMin() {
        return min;
    }
    
    public void setMin(Object min) {
        this.min = min;
    }
    
    public Object getMax() {
        return max;
    }
    
    public void setMax(Object max) {
        this.max = max;
    }
    
    public PropertyReference toReference() {
        return reference;
    }
    
    /**
     * Add the min boundary to the current constraints. When a min
     * boundary already exists, we will take the highest.
     * 
     * @param min the min value to add
     */
    public void addMin(long min) {
        if (this.min == null || ((Number) this.min).longValue() < min) {
            this.min = Long.valueOf(min);
        }
    }
    
    /**
     * Add the max boundary to the current constraints. When a max
     * boundary already exists, we will take the lowest.
     * 
     * @param max the max value to add
     */
    public void addMax(long max) {
        if (this.max == null || ((Number) this.max).longValue() > max) {
            this.max = Long.valueOf(max);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return reference.toString();
    }

}
