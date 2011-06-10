package org.jarb.constraint;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jarb.constraint.domain.Car;
import org.jarb.constraint.domain.Person;
import org.jarb.constraint.jsr303.LengthPropertyConstraintMetadataEnhancer;
import org.junit.Before;
import org.junit.Test;

public class DefaultBeanConstraintMetadataTest {
    private DefaultBeanConstraintMetadataGenerator beanDescriptor;

    @Before
    public void setUp() {
        beanDescriptor = new DefaultBeanConstraintMetadataGenerator();
    }

    /**
     * Ensure that each property is present in our description, with name and type.
     */
    @Test
    public void testPropertiesAreDescribed() {
        BeanConstraintMetadata<Car> carDescription = beanDescriptor.describe(Car.class);
        assertEquals(Long.class, carDescription.getPropertyMetadata("id").getJavaType());
        assertEquals(String.class, carDescription.getPropertyMetadata("licenseNumber").getJavaType());
        assertEquals(Double.class, carDescription.getPropertyMetadata("price").getJavaType());
        assertEquals(Person.class, carDescription.getPropertyMetadata("owner").getJavaType());
        assertEquals(Class.class, carDescription.getPropertyMetadata("class").getJavaType());
    }

    /**
     * Property enhancers can be used to our description with additional information.
     */
    @Test
    public void testPropertyDescriptionEnhancers() {
        PropertyConstraintMetadataEnhancer lengthAnnotationEnhancer = new LengthPropertyConstraintMetadataEnhancer();
        beanDescriptor.setPropertyMetadataEnhancers(Arrays.asList(lengthAnnotationEnhancer));
        BeanConstraintMetadata<Car> carDescription = beanDescriptor.describe(Car.class);
        PropertyConstraintMetadata<String> licenseDescription = carDescription.getPropertyMetadata("licenseNumber", String.class);
        // Length description enhancer reads the annotations to determine minimum- and maximum length
        assertEquals(Integer.valueOf(6), licenseDescription.getMinimumLength());
    }

}
