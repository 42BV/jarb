package org.jarb.populator.excel.workbook.validator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Abstract implementation of {@link WorkbookViolation}.
 * 
 * @author Jeroen van Schagen
 * @since 23-06-2011
 */
public abstract class AbstractWorkbookViolation implements WorkbookViolation {

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getMessage();
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
        return HashCodeBuilder.reflectionHashCode(this);
    }
    
}
