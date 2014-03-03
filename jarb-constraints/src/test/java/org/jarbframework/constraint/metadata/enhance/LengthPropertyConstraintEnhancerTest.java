package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class LengthPropertyConstraintEnhancerTest {
    
    private LengthPropertyConstraintEnhancer enhancer;

    private PropertyConstraintDescription description;

    @Before
    public void setUp() {
        enhancer = new LengthPropertyConstraintEnhancer();
        
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        description = new PropertyConstraintDescription(nameReference, String.class);
    }

    /**
     * Enhance 'name' property description with @Length information.
     * We have annotated the getter with a min=5 and field with min=6, thus
     * the expected result is min=6, satisfying both of the restrictions.
     */
    @Test
    public void testEnhance() {
        description.setMaximumLength(6); // Database column length is '6'

        description = enhancer.enhance(description);

        // Minimum length is retrieved from the @Length.min attribute
        assertEquals(Integer.valueOf(6), description.getMinimumLength());

        // Database maximum length is lower than all @Length.max attributes (Integer.MAX_VALUE)
        assertEquals(Integer.valueOf(6), description.getMaximumLength());
    }

    /**
     * Whenever the maximum length, 2 in this case, is smaller than the minimum
     * length retrieved from our @Length information, an exception should be thrown.
     * Minimum length can never be greater than the maximum length.
     */
    @Test(expected = IllegalStateException.class)
    public void testMergeConflict() {
        description.setMaximumLength(2);

        enhancer.enhance(description);
    }

    /**
     * If a property has no @Length annotation, all description attributes should
     * remain unchanged.
     */
    @Test
    public void testNoLength() {
        PropertyReference reference = new PropertyReference(Wine.class, "price");
        PropertyConstraintDescription priceDescription = new PropertyConstraintDescription(reference, Double.class);
        priceDescription.setMaximumLength(9);

        enhancer.enhance(priceDescription);

        // No minimum length is specified, so it should remain null
        assertNull(priceDescription.getMinimumLength());

        // Initially specified maximum length should remain
        assertEquals(Integer.valueOf(9), priceDescription.getMaximumLength());
    }

}
