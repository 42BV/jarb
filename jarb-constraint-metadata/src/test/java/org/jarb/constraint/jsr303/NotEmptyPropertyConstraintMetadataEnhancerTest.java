package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.domain.Car;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotEmptyPropertyConstraintMetadataEnhancerTest {
    private NotEmptyPropertyConstraintEnhancer enhancer;
    private PropertyConstraintDescription licenseMetadata;

    @Before
    public void setUp() {
        enhancer = new NotEmptyPropertyConstraintEnhancer();
        PropertyReference reference = new PropertyReference(Car.class, "licenseNumber");
        licenseMetadata = new PropertyConstraintDescription(reference, String.class);
    }

    /**
     * Whenever our described minimum length is null, and the property is @NotEmpty,
     * we should change the minimum length to one. This is because we require atleast
     * one character for the property value to not be empty.
     */
    @Test
    public void testEnhanceIfNull() {
        assertNull(licenseMetadata.getMinimumLength());
        enhancer.enhance(licenseMetadata);
        assertEquals(Integer.valueOf(1), licenseMetadata.getMinimumLength());
    }

    /**
     * If our described minimum length is zero, but the property is @NotEmpty,
     * we should also change the minimum length to one.
     */
    @Test
    public void testEnhanceIfZero() {
        licenseMetadata.setMinimumLength(0);
        enhancer.enhance(licenseMetadata);
        assertEquals(Integer.valueOf(1), licenseMetadata.getMinimumLength());
    }

    /**
     * Whenever our minimum length is already configured, above zero, we do
     * nothing. That is because some other, more specific, enhancer already
     * provided the correct minimum length, and we should not alter it.
     */
    @Test
    public void testSkipIfAlreadyHasPositiveMin() {
        licenseMetadata.setMinimumLength(42);
        enhancer.enhance(licenseMetadata);
        assertEquals(Integer.valueOf(42), licenseMetadata.getMinimumLength());
    }

    /**
     * Properties that are not annotated with @NotEmpty should not be altered.
     */
    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference reference = new PropertyReference(Car.class, "price");
        PropertyConstraintDescription priceMetadata = new PropertyConstraintDescription(reference, String.class);
        enhancer.enhance(priceMetadata);
        assertNull(priceMetadata.getMinimumLength());
    }

}
