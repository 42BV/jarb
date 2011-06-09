package org.jarb.constraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.domain.Car;
import org.junit.Before;
import org.junit.Test;

public class LengthPropertyConstraintDescriptionEnhancerTest {
    private LengthPropertyConstraintMetadataEnhancer enhancer;
    private MutablePropertyConstraintMetadata<String> propertyDescription;

    @Before
    public void setUp() {
        enhancer = new LengthPropertyConstraintMetadataEnhancer();
        propertyDescription = new MutablePropertyConstraintMetadata<String>("licenseNumber", String.class);
    }

    /**
     * Enhance 'licenseNumber' property description with @Length information.
     * We have annotated the getter with a min=5 and field with min=6, thus
     * the expected result is min=6, satisfying both of the restrictions.
     */
    @Test
    public void testEnhance() {
        propertyDescription.setMaximumLength(6); // Database column length is '6'
        propertyDescription = enhancer.enhance(propertyDescription, Car.class);
        // Minimum length is retrieved from the @Length.min attribute
        assertEquals(Integer.valueOf(6), propertyDescription.getMinimumLength());
        // Database maximum length is lower than all @Length.max attributes (Integer.MAX_VALUE)
        assertEquals(Integer.valueOf(6), propertyDescription.getMaximumLength());
    }

    /**
     * Whenever the maximum length, 2 in this case, is smaller than the minimum
     * length retrieved from our @Length information, an exception should be thrown.
     * Minimum length can never be greater than the maximum length.
     */
    @Test(expected = IllegalStateException.class)
    public void testMergeConflict() {
        propertyDescription.setMaximumLength(2);
        enhancer.enhance(propertyDescription, Car.class);
    }

    /**
     * If a property has no @Length annotation, all description attributes should
     * remain unchanged.
     */
    @Test
    public void testNoLength() {
        MutablePropertyConstraintMetadata<Double> priceDescription = new MutablePropertyConstraintMetadata<Double>("price", Double.class);
        priceDescription.setMaximumLength(9);
        enhancer.enhance(priceDescription, Car.class);
        // No minimum length is specified, so it should remain null
        assertNull(priceDescription.getMinimumLength());
        // Initially specified maximum length should remain
        assertEquals(Integer.valueOf(9), priceDescription.getMaximumLength());
    }

}
