package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarb.constraint.PropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
import org.jarb.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotNullPropertyConstraintMetadataEnhancerTest {
    private NotNullPropertyConstraintMetadataEnhancer enhancer;
    private PropertyConstraintMetadata<String> licenseMetadata;

    @Before
    public void setUp() {
        enhancer = new NotNullPropertyConstraintMetadataEnhancer();
        PropertyReference reference = new PropertyReference(Car.class, "licenseNumber");
        licenseMetadata = new PropertyConstraintMetadata<String>(reference, String.class);
    }

    @Test
    public void testEnhance() {
        assertFalse(licenseMetadata.isRequired());
        enhancer.enhance(licenseMetadata);
        assertTrue(licenseMetadata.isRequired());
    }

    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference priceReference = new PropertyReference(Car.class, "price");
        PropertyConstraintMetadata<Double> priceMetadata = new PropertyConstraintMetadata<Double>(priceReference, Double.class);
        enhancer.enhance(priceMetadata);
        assertFalse(priceMetadata.isRequired());
    }

}
