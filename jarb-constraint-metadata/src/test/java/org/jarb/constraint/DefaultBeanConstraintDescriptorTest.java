package org.jarb.constraint;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jarb.constraint.domain.Car;
import org.jarb.constraint.domain.Person;
import org.junit.Before;
import org.junit.Test;

public class DefaultBeanConstraintDescriptorTest {
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
        assertEquals(Long.class, carDescription.getPropertyMetadata("id").getPropertyType());
        assertEquals(String.class, carDescription.getPropertyMetadata("licenseNumber").getPropertyType());
        assertEquals(Double.class, carDescription.getPropertyMetadata("price").getPropertyType());
        assertEquals(Person.class, carDescription.getPropertyMetadata("owner").getPropertyType());
        assertEquals(Class.class, carDescription.getPropertyMetadata("class").getPropertyType());
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
