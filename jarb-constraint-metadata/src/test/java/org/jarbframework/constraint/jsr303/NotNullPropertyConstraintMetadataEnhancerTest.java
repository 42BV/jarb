package org.jarbframework.constraint.jsr303;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.PropertyConstraintDescription;
import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.jsr303.NotNullPropertyConstraintEnhancer;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotNullPropertyConstraintMetadataEnhancerTest {
    private NotNullPropertyConstraintEnhancer enhancer;
    private PropertyConstraintDescription licenseMetadata;

    @Before
    public void setUp() {
        enhancer = new NotNullPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Car.class, "licenseNumber");
        licenseMetadata = new PropertyConstraintDescription(reference, String.class);
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
        PropertyConstraintDescription priceMetadata = new PropertyConstraintDescription(priceReference, Double.class);
        enhancer.enhance(priceMetadata);
        assertFalse(priceMetadata.isRequired());
    }

}
