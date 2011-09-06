package org.jarb.constraint;

import static org.junit.Assert.assertEquals;

import org.jarb.constraint.domain.Car;
import org.jarb.constraint.domain.Person;
import org.jarb.constraint.jsr303.LengthPropertyConstraintEnhancer;
import org.junit.Before;
import org.junit.Test;

public class BeanConstraintMetadataGeneratorImplTest {
    private BeanConstraintAccessorImpl describtor;

    @Before
    public void setUp() {
        describtor = new BeanConstraintAccessorImpl();
    }

    /**
     * Ensure that each property is present in our description, with name and type.
     */
    @Test
    public void testPropertiesAreDescribed() {
        BeanConstraintDescription<Car> carDescription = describtor.describe(Car.class);
        assertEquals(Long.class, carDescription.getProperty("id").getJavaType());
        assertEquals(String.class, carDescription.getProperty("licenseNumber").getJavaType());
        assertEquals(Double.class, carDescription.getProperty("price").getJavaType());
        assertEquals(Person.class, carDescription.getProperty("owner").getJavaType());
        assertEquals(Class.class, carDescription.getProperty("class").getJavaType());
    }

    /**
     * Property enhancers can be used to our description with additional information.
     */
    @Test
    public void testPropertyDescriptionEnhancers() {
        describtor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        BeanConstraintDescription<Car> carDescription = describtor.describe(Car.class);
        PropertyConstraintDescription licenseDescription = carDescription.getProperty("licenseNumber");
        assertEquals(Integer.valueOf(6), licenseDescription.getMinimumLength());
    }

}
