package org.jarb.constraint;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.jarb.constraint.domain.Car;
import org.jarb.constraint.domain.Person;
import org.junit.Before;
import org.junit.Test;

public class DefaultBeanConstraintDescriptorTest {
    private DefaultBeanConstraintDescriptor beanDescriptor;

    @Before
    public void setUp() {
        beanDescriptor = new DefaultBeanConstraintDescriptor();
    }

    /**
     * Ensure that each property is present in our description, with name and type.
     */
    @Test
    public void testPropertiesAreDescribed() {
        BeanConstraintDescription<Car> carDescription = beanDescriptor.describe(Car.class);
        assertEquals(Long.class, carDescription.getPropertyDescription("id").getPropertyType());
        assertEquals(String.class, carDescription.getPropertyDescription("licenseNumber").getPropertyType());
        assertEquals(Double.class, carDescription.getPropertyDescription("price").getPropertyType());
        assertEquals(Person.class, carDescription.getPropertyDescription("owner").getPropertyType());
        assertEquals(Class.class, carDescription.getPropertyDescription("class").getPropertyType());
    }

    /**
     * Property enhancers can be used to our description with additional information.
     */
    @Test
    public void testPropertyDescriptionEnhancers() {
        PropertyConstraintDescriptionEnhancer lengthAnnotationEnhancer = new LengthPropertyConstraintDescriptionEnhancer();
        beanDescriptor.setPropertyDescriptionEnhancers(Arrays.asList(lengthAnnotationEnhancer));
        BeanConstraintDescription<Car> carDescription = beanDescriptor.describe(Car.class);
        PropertyConstraintDescription<String> licenseDescription = carDescription.getPropertyDescription("licenseNumber", String.class);
        // Length description enhancer reads the annotations to determine minimum- and maximum length
        assertEquals(Integer.valueOf(6), licenseDescription.getMinimumLength());
    }

}
