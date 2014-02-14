package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class NotEmptyPropertyConstraintMetadataEnhancerTest {
    
    private NotEmptyPropertyConstraintEnhancer notEmptyEnhancer;

    private PropertyConstraintDescription nameDescription;

    @Before
    public void setUp() {
        notEmptyEnhancer = new NotEmptyPropertyConstraintEnhancer();
        
        PropertyReference nameReference = new PropertyReference(Wine.class, "name");
        nameDescription = new PropertyConstraintDescription(nameReference, String.class);
    }

    /**
     * Whenever our described minimum length is null, and the property is @NotEmpty,
     * we should change the minimum length to one. This is because we require atleast
     * one character for the property value to not be empty.
     */
    @Test
    public void testEnhanceIfNull() {
        assertNull(nameDescription.getMinimumLength());
        
        notEmptyEnhancer.enhance(nameDescription);

        assertEquals(Integer.valueOf(1), nameDescription.getMinimumLength());
    }

    /**
     * If our described minimum length is zero, but the property is @NotEmpty,
     * we should also change the minimum length to one.
     */
    @Test
    public void testEnhanceIfZero() {
        nameDescription.setMinimumLength(0);
        
        notEmptyEnhancer.enhance(nameDescription);

        assertEquals(Integer.valueOf(1), nameDescription.getMinimumLength());
    }

    /**
     * Whenever our minimum length is already configured, above zero, we do
     * nothing. That is because some other, more specific, enhancer already
     * provided the correct minimum length, and we should not alter it.
     */
    @Test
    public void testSkipIfAlreadyHasPositiveMin() {
        nameDescription.setMinimumLength(42);
        
        notEmptyEnhancer.enhance(nameDescription);

        assertEquals(Integer.valueOf(42), nameDescription.getMinimumLength());
    }

    /**
     * Properties that are not annotated with @NotEmpty should not be altered.
     */
    @Test
    public void testSkipUnmarkedProperty() {
        PropertyReference reference = new PropertyReference(Wine.class, "price");
        PropertyConstraintDescription priceDescription = new PropertyConstraintDescription(reference, String.class);
        
        notEmptyEnhancer.enhance(priceDescription);

        assertNull(priceDescription.getMinimumLength());
    }

}
