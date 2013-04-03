package org.jarbframework.constraint.metadata;

import static org.junit.Assert.assertEquals;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that setters validate the provided values.
 * @author Jeroen van Schagen
 * @since 03-06-2011
 */
public class PropertyConstraintDescriptionTest {
    
    private PropertyConstraintDescription constraintDescription;

    @Before
    public void setUp() {
        PropertyReference reference = new PropertyReference(Wine.class, "name");
        constraintDescription = new PropertyConstraintDescription(reference, String.class);
    }

    @Test
    public void testModifyLength() {
        constraintDescription.setMinimumLength(4);
        constraintDescription.setMaximumLength(42);
        assertEquals(Integer.valueOf(4), constraintDescription.getMinimumLength());
        assertEquals(Integer.valueOf(42), constraintDescription.getMaximumLength());
    }

    @Test(expected = IllegalStateException.class)
    public void testNegativeMinimumLength() {
        constraintDescription.setMinimumLength(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void testNegativeMaximumLength() {
        constraintDescription.setMaximumLength(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void testMinimLengthGreaterThanMaximum() {
        constraintDescription.setMaximumLength(24);
        constraintDescription.setMinimumLength(42);
    }

    @Test
    public void testModifyFractionLength() {
        constraintDescription.setFractionLength(4);
        assertEquals(Integer.valueOf(4), constraintDescription.getFractionLength());
    }

    @Test(expected = IllegalStateException.class)
    public void testnegativeFractionLength() {
        constraintDescription.setFractionLength(-1);
    }
}
