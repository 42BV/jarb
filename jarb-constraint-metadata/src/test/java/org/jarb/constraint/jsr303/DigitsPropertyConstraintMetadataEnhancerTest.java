package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
import org.junit.Before;
import org.junit.Test;

public class DigitsPropertyConstraintMetadataEnhancerTest {
    private DigitsPropertyConstraintMetadataEnhancer enhancer;
    private MutablePropertyConstraintMetadata<Double> priceMetadata;

    @Before
    public void setUp() {
        enhancer = new DigitsPropertyConstraintMetadataEnhancer();
        priceMetadata = new MutablePropertyConstraintMetadata<Double>("price", Double.class);
    }
    
    @Test
    public void testEnhance() {
        assertNull(priceMetadata.getMaximumLength());
        assertNull(priceMetadata.getFractionLength());
        enhancer.enhance(priceMetadata, Car.class);
        assertEquals(Integer.valueOf(5), priceMetadata.getMaximumLength());
        assertEquals(Integer.valueOf(1), priceMetadata.getFractionLength());
    }
    
    @Test
    public void testSkipUnmarkedProperties() {
        MutablePropertyConstraintMetadata<String> licenseMetadata = new MutablePropertyConstraintMetadata<String>("licenseNumber", String.class);
        enhancer.enhance(licenseMetadata, Car.class);
        assertNull(licenseMetadata.getMaximumLength());
        assertNull(licenseMetadata.getFractionLength());
    }
    
}
