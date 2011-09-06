package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.PropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
import org.jarb.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class DigitsPropertyConstraintMetadataEnhancerTest {
    private DigitsPropertyConstraintMetadataEnhancer enhancer;
    private PropertyConstraintMetadata<Double> priceMetadata;

    @Before
    public void setUp() {
        enhancer = new DigitsPropertyConstraintMetadataEnhancer();
        PropertyReference reference = new PropertyReference(Car.class, "price");
        priceMetadata = new PropertyConstraintMetadata<Double>(reference, Double.class);
    }

    @Test
    public void testEnhance() {
        assertNull(priceMetadata.getMaximumLength());
        assertNull(priceMetadata.getFractionLength());
        enhancer.enhance(priceMetadata);
        assertEquals(Integer.valueOf(5), priceMetadata.getMaximumLength());
        assertEquals(Integer.valueOf(1), priceMetadata.getFractionLength());
    }

    @Test
    public void testSkipUnmarkedProperties() {
        PropertyReference reference = new PropertyReference(Car.class, "licenseNumber");
        PropertyConstraintMetadata<String> licenseMetadata = new PropertyConstraintMetadata<String>(reference, String.class);
        enhancer.enhance(licenseMetadata);
        assertNull(licenseMetadata.getMaximumLength());
        assertNull(licenseMetadata.getFractionLength());
    }

}
