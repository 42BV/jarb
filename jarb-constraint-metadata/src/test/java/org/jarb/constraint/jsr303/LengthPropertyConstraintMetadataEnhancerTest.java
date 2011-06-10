package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
import org.jarb.constraint.jsr303.LengthPropertyConstraintMetadataEnhancer;
import org.junit.Before;
import org.junit.Test;

public class LengthPropertyConstraintMetadataEnhancerTest {
    private LengthPropertyConstraintMetadataEnhancer enhancer;
    private MutablePropertyConstraintMetadata<String> licenseMetadata;

    @Before
    public void setUp() {
        enhancer = new LengthPropertyConstraintMetadataEnhancer();
        licenseMetadata = new MutablePropertyConstraintMetadata<String>("licenseNumber", String.class);
    }

    /**
     * Enhance 'licenseNumber' property description with @Length information.
     * We have annotated the getter with a min=5 and field with min=6, thus
     * the expected result is min=6, satisfying both of the restrictions.
     */
    @Test
    public void testEnhance() {
        licenseMetadata.setMaximumLength(6); // Database column length is '6'
        licenseMetadata = enhancer.enhance(licenseMetadata, Car.class);
        // Minimum length is retrieved from the @Length.min attribute
        assertEquals(Integer.valueOf(6), licenseMetadata.getMinimumLength());
        // Database maximum length is lower than all @Length.max attributes (Integer.MAX_VALUE)
        assertEquals(Integer.valueOf(6), licenseMetadata.getMaximumLength());
    }

    /**
     * Whenever the maximum length, 2 in this case, is smaller than the minimum
     * length retrieved from our @Length information, an exception should be thrown.
     * Minimum length can never be greater than the maximum length.
     */
    @Test(expected = IllegalStateException.class)
    public void testMergeConflict() {
        licenseMetadata.setMaximumLength(2);
        enhancer.enhance(licenseMetadata, Car.class);
    }

    /**
     * If a property has no @Length annotation, all description attributes should
     * remain unchanged.
     */
    @Test
    public void testNoLength() {
        MutablePropertyConstraintMetadata<Double> priceMetadata = new MutablePropertyConstraintMetadata<Double>("price", Double.class);
        priceMetadata.setMaximumLength(9);
        enhancer.enhance(priceMetadata, Car.class);
        // No minimum length is specified, so it should remain null
        assertNull(priceMetadata.getMinimumLength());
        // Initially specified maximum length should remain
        assertEquals(Integer.valueOf(9), priceMetadata.getMaximumLength());
    }

}
