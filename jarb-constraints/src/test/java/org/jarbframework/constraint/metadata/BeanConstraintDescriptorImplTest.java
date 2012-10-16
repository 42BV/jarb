package org.jarbframework.constraint.metadata;

import static org.junit.Assert.assertEquals;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.domain.Person;
import org.junit.Before;
import org.junit.Test;

public class BeanConstraintDescriptorImplTest {
    
    private BeanConstraintDescriptorImpl describtor;

    @Before
    public void setUp() {
        describtor = new BeanConstraintDescriptorImpl();
    }

    /**
     * Ensure that each property is present in our description, with name and type.
     */
    @Test
    public void testPropertiesAreDescribed() {
        BeanConstraintDescription<Car> carDescription = describtor.describe(Car.class);
        assertEquals(Long.class, carDescription.getPropertyDescription("id").getJavaType());
        assertEquals(String.class, carDescription.getPropertyDescription("licenseNumber").getJavaType());
        assertEquals(Double.class, carDescription.getPropertyDescription("price").getJavaType());
        assertEquals(Person.class, carDescription.getPropertyDescription("owner").getJavaType());
        assertEquals(Class.class, carDescription.getPropertyDescription("class").getJavaType());
    }

    /**
     * Property enhancers can be used to our description with additional information.
     */
    @Test
    public void testPropertyDescriptionEnhancers() {
        describtor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        BeanConstraintDescription<Car> carDescription = describtor.describe(Car.class);
        PropertyConstraintDescription licenseDescription = carDescription.getPropertyDescription("licenseNumber");
        assertEquals(Integer.valueOf(6), licenseDescription.getMinimumLength());
    }

}
