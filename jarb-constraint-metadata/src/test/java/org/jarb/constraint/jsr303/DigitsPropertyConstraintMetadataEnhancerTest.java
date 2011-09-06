package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.domain.Car;
import org.jarb.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class DigitsPropertyConstraintMetadataEnhancerTest {
    private DigitsPropertyConstraintEnhancer enhancer;
    private PropertyConstraintDescription priceMetadata;

    @Before
    public void setUp() {
        enhancer = new DigitsPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Car.class, "price");
        priceMetadata = new PropertyConstraintDescription(reference, Double.class);
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
        PropertyConstraintDescription licenseMetadata = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(licenseMetadata);
        assertNull(licenseMetadata.getMaximumLength());
        assertNull(licenseMetadata.getFractionLength());
    }

}
