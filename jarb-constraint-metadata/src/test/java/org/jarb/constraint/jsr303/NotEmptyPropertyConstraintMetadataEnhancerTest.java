package org.jarb.constraint.jsr303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.junit.Before;
import org.junit.Test;

public class NotEmptyPropertyConstraintMetadataEnhancerTest {
    private NotEmptyPropertyConstraintMetadataEnhancer enhancer;
    private MutablePropertyConstraintMetadata<String> requiredNameDescription;

    @Before
    public void setUp() {
        enhancer = new NotEmptyPropertyConstraintMetadataEnhancer();
        requiredNameDescription = new MutablePropertyConstraintMetadata<String>("requiredName", String.class);
    }
    
    /**
     * Whenever our described minimum length is null, and the property is @NotEmpty,
     * we should change the minimum length to one. This is because we require atleast
     * one character for the property value to not be empty.
     */
    @Test
    public void testEnhanceIfNull() {
        assertNull(requiredNameDescription.getMinimumLength());
        enhancer.enhance(requiredNameDescription, SomeBean.class);
        assertEquals(Integer.valueOf(1), requiredNameDescription.getMinimumLength());
    }
    
    /**
     * If our described minimum length is zero, but the property is @NotEmpty,
     * we should also change the minimum length to one.
     */
    @Test
    public void testEnhanceIfZero() {
        requiredNameDescription.setMinimumLength(0);
        enhancer.enhance(requiredNameDescription, SomeBean.class);
        assertEquals(Integer.valueOf(1), requiredNameDescription.getMinimumLength());
    }
    
    /**
     * Whenever our minimum length is already configured, above zero, we do
     * nothing. That is because some other, more specific, enhancer already
     * provided the correct minimum length, and we should not alter it.
     */
    @Test
    public void testSkipIfAlreadyHasPositiveMin() {
        requiredNameDescription.setMinimumLength(42);
        enhancer.enhance(requiredNameDescription, SomeBean.class);
        assertEquals(Integer.valueOf(42), requiredNameDescription.getMinimumLength());
    }
    
    /**
     * Properties that are not annotated with @NotEmpty should not be altered.
     */
    @Test
    public void testSkipUnmarkedProperty() {
        MutablePropertyConstraintMetadata<String> nameDescription = new MutablePropertyConstraintMetadata<String>("name", String.class);
        enhancer.enhance(nameDescription, SomeBean.class);
        assertNull(nameDescription.getMinimumLength());
    }
    
    public static class SomeBean {
        private String name;
        
        @NotEmpty
        private String requiredName;
        
        public String getName() {
            return name;
        }
        
        public String getRequiredName() {
            return requiredName;
        }
    }
    
}
