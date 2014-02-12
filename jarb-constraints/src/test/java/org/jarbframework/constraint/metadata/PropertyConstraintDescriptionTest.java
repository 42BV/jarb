package org.jarbframework.constraint.metadata;

import static org.junit.Assert.assertEquals;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that setters validate the provided values.
 * 
 * @author Jeroen van Schagen
 * @since 03-06-2011
 */
public class PropertyConstraintDescriptionTest {
    
    private PropertyConstraintDescription propertyConstraintDescription;

    @Before
    public void setUp() {
        PropertyReference reference = new PropertyReference(Wine.class, "name");
        propertyConstraintDescription = new PropertyConstraintDescription(reference, String.class);
    }

    @Test
    public void testModifyLength() {
        propertyConstraintDescription.setMinimumLength(4);
        propertyConstraintDescription.setMaximumLength(42);
        assertEquals(Integer.valueOf(4), propertyConstraintDescription.getMinimumLength());
        assertEquals(Integer.valueOf(42), propertyConstraintDescription.getMaximumLength());
    }

    @Test(expected = IllegalStateException.class)
    public void testNegativeMinimumLength() {
        propertyConstraintDescription.setMinimumLength(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void testNegativeMaximumLength() {
        propertyConstraintDescription.setMaximumLength(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void testMinimLengthGreaterThanMaximum() {
        propertyConstraintDescription.setMaximumLength(24);
        propertyConstraintDescription.setMinimumLength(42);
    }

    @Test
    public void testModifyFractionLength() {
        propertyConstraintDescription.setFractionLength(4);
        assertEquals(Integer.valueOf(4), propertyConstraintDescription.getFractionLength());
    }

    @Test(expected = IllegalStateException.class)
    public void testnegativeFractionLength() {
        propertyConstraintDescription.setFractionLength(-1);
    }

}
