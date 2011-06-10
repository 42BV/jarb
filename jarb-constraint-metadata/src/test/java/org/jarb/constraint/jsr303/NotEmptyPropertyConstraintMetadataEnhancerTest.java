package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
import org.junit.Before;
import org.junit.Test;

public class NotEmptyPropertyConstraintMetadataEnhancerTest {
    private NotEmptyPropertyConstraintMetadataEnhancer enhancer;
    private MutablePropertyConstraintMetadata<String> licenseMetadata;

    @Before
    public void setUp() {
        enhancer = new NotEmptyPropertyConstraintMetadataEnhancer();
        licenseMetadata = new MutablePropertyConstraintMetadata<String>("licenseNumber", String.class);
    }
    
    /**
     * Whenever our described minimum length is null, and the property is @NotEmpty,
     * we should change the minimum length to one. This is because we require atleast
     * one character for the property value to not be empty.
     */
    @Test
    public void testEnhanceIfNull() {
        assertNull(licenseMetadata.getMinimumLength());
        enhancer.enhance(licenseMetadata, Car.class);
        assertEquals(Integer.valueOf(1), licenseMetadata.getMinimumLength());
    }
    
    /**
     * If our described minimum length is zero, but the property is @NotEmpty,
     * we should also change the minimum length to one.
     */
    @Test
    public void testEnhanceIfZero() {
        licenseMetadata.setMinimumLength(0);
        enhancer.enhance(licenseMetadata, Car.class);
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
        enhancer.enhance(licenseMetadata, Car.class);
        assertEquals(Integer.valueOf(42), licenseMetadata.getMinimumLength());
    }
    
    /**
     * Properties that are not annotated with @NotEmpty should not be altered.
     */
    @Test
    public void testSkipUnmarkedProperty() {
        MutablePropertyConstraintMetadata<String> priceMetadata = new MutablePropertyConstraintMetadata<String>("price", String.class);
        enhancer.enhance(priceMetadata, Car.class);
        assertNull(priceMetadata.getMinimumLength());
    }
    
}
