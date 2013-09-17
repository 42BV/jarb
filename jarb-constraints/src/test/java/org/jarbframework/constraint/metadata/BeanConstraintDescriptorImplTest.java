package org.jarbframework.constraint.metadata;

import static org.junit.Assert.assertEquals;

import org.jarbframework.constraint.domain.Country;
import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import org.junit.Before;
import org.junit.Test;

public class BeanConstraintDescriptorImplTest {
    
    private BeanConstraintDescriptor constraintDescriptor;

    @Before
    public void setUp() {
        constraintDescriptor = new BeanConstraintDescriptor();
    }

    /**
     * Ensure that each property is present in our description, with name and type.
     */
    @Test
    public void testPropertiesAreDescribed() {
        BeanConstraintDescription wineDescription = constraintDescriptor.describe(Wine.class);
        assertEquals(Long.class, wineDescription.getPropertyDescription("id").getJavaType());
        assertEquals(String.class, wineDescription.getPropertyDescription("name").getJavaType());
        assertEquals(Double.class, wineDescription.getPropertyDescription("price").getJavaType());
        assertEquals(Country.class, wineDescription.getPropertyDescription("country").getJavaType());
        assertEquals(Class.class, wineDescription.getPropertyDescription("class").getJavaType());
    }

    /**
     * Property enhancement can be used to our description with additional information.
     */
    @Test
    public void testPropertyDescriptionEnhancers() {
        constraintDescriptor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        BeanConstraintDescription wineDescription = constraintDescriptor.describe(Wine.class);
        assertEquals(Integer.valueOf(6), wineDescription.getPropertyDescription("name").getMinimumLength());
    }

}
