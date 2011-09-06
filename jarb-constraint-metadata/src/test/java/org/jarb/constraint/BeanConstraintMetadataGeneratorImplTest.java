package org.jarb.constraint;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.jarb.constraint.domain.Car;
import org.jarb.constraint.domain.Person;
import org.jarb.constraint.jsr303.LengthPropertyConstraintMetadataEnhancer;
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
        PropertyConstraintEnhancer lengthAnnotationEnhancer = new LengthPropertyConstraintMetadataEnhancer();
        describtor.setPropertyConstraintEnhancers(asList(lengthAnnotationEnhancer));
        BeanConstraintDescription<Car> carDescription = describtor.describe(Car.class);
        PropertyConstraintDescription licenseDescription = carDescription.getPropertyMetadata("licenseNumber");
        // Length description enhancer reads the annotations to determine minimum- and maximum length
        assertEquals(Integer.valueOf(6), licenseDescription.getMinimumLength());
    }

}
