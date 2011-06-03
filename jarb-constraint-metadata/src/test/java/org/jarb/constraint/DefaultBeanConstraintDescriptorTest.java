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
        assertEquals(Long.class, carDescription.getPropertyDescription("id").getPropertyClass());
        assertEquals(String.class, carDescription.getPropertyDescription("licenseNumber").getPropertyClass());
        assertEquals(Double.class, carDescription.getPropertyDescription("price").getPropertyClass());
        assertEquals(Person.class, carDescription.getPropertyDescription("owner").getPropertyClass());
        assertEquals(Class.class, carDescription.getPropertyDescription("class").getPropertyClass());
    }

    /**
     * Property enhancers can be used to our description with additional information.
     * For example, we can use @Length annotations to retrieve the maximum- and minimum
     * length of a string based property.
     */
    @Test
    public void testPropertyDescriptionEnhancers() {
        PropertyConstraintDescriptionEnhancer lengthAnnotationEnhancer = new LengthPropertyConstraintDescriptionEnhancer();
        beanDescriptor.setPropertyDescriptionEnhancers(Arrays.asList(lengthAnnotationEnhancer));
        BeanConstraintDescription<Car> carDescription = beanDescriptor.describe(Car.class);
        PropertyConstraintDescription<String> licenseDescription = carDescription.getPropertyDescription("licenseNumber", String.class);
        assertEquals(Integer.valueOf(6), licenseDescription.getMinimumLength());
    }

}
