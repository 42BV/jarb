package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
import org.junit.Before;
import org.junit.Test;

public class NotNullPropertyConstraintMetadataEnhancerTest {
    private NotNullPropertyConstraintMetadataEnhancer enhancer;
    private MutablePropertyConstraintMetadata<String> licenseMetadata;

    @Before
    public void setUp() {
        enhancer = new NotNullPropertyConstraintMetadataEnhancer();
        licenseMetadata = new MutablePropertyConstraintMetadata<String>("licenseNumber", String.class);
    }
    
    @Test
    public void testEnhance() {
        assertFalse(licenseMetadata.isRequired());
        enhancer.enhance(licenseMetadata, Car.class);
        assertTrue(licenseMetadata.isRequired());
    }
    
    @Test
    public void testSkipUnmarkedProperty() {
        MutablePropertyConstraintMetadata<Double> priceMetadata = new MutablePropertyConstraintMetadata<Double>("price", Double.class);
        enhancer.enhance(priceMetadata, Car.class);
        assertFalse(priceMetadata.isRequired());
    }
    
}
