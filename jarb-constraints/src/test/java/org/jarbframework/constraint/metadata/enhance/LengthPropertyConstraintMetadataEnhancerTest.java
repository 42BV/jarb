package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.domain.Wine;
import org.jarbframework.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class LengthPropertyConstraintMetadataEnhancerTest {
    
    private LengthPropertyConstraintEnhancer constraintEnhancer;
    private PropertyConstraintDescription nameDescription;

    @Before
    public void setUp() {
        constraintEnhancer = new LengthPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Wine.class, "name");
        nameDescription = new PropertyConstraintDescription(reference, String.class);
    }

    /**
     * Enhance 'name' property description with @Length information.
     * We have annotated the getter with a min=5 and field with min=6, thus
     * the expected result is min=6, satisfying both of the restrictions.
     */
    @Test
    public void testEnhance() {
        nameDescription.setMaximumLength(6); // Database column length is '6'
        nameDescription = constraintEnhancer.enhance(nameDescription);
        // Minimum length is retrieved from the @Length.min attribute
        assertEquals(Integer.valueOf(6), nameDescription.getMinimumLength());
        // Database maximum length is lower than all @Length.max attributes (Integer.MAX_VALUE)
        assertEquals(Integer.valueOf(6), nameDescription.getMaximumLength());
    }

    /**
     * Whenever the maximum length, 2 in this case, is smaller than the minimum
     * length retrieved from our @Length information, an exception should be thrown.
     * Minimum length can never be greater than the maximum length.
     */
    @Test(expected = IllegalStateException.class)
    public void testMergeConflict() {
        nameDescription.setMaximumLength(2);
        constraintEnhancer.enhance(nameDescription);
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
        constraintEnhancer.enhance(priceDescription);
        // No minimum length is specified, so it should remain null
        assertNull(priceDescription.getMinimumLength());
        // Initially specified maximum length should remain
        assertEquals(Integer.valueOf(9), priceDescription.getMaximumLength());
    }

}
