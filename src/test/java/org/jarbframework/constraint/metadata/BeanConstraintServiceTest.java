package org.jarbframework.constraint.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.jarbframework.constraint.DatabaseConstraintsConfigurer;
import org.jarbframework.constraint.ConstraintsTestConfig;
import org.jarbframework.constraint.domain.Country;
import org.jarbframework.constraint.domain.Wine;
import org.jarbframework.constraint.metadata.BeanConstraintServiceTest.CustomConstraintsConfig;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(classes = { ConstraintsTestConfig.class, CustomConstraintsConfig.class })
public class BeanConstraintServiceTest {

    @Autowired
    private BeanConstraintDescriptor descriptor;

    private BeanConstraintService service;
    
    @Before
    public void setUp() {
        service = new BeanConstraintService(descriptor);
    }

    @Test
    public void testDefaultEnhancers() throws Exception {
        Map<String, PropertyConstraintDescription> wineDescription = service.describe(Wine.class);
        
        // Retrieved by introspection
        assertNull(wineDescription.get("id"));
        assertEquals(Double.class, wineDescription.get("price").getJavaType());
        assertEquals(Country.class, wineDescription.get("country").getJavaType());
        assertEquals(int.class, wineDescription.get("primitive").getJavaType());
        assertNull(wineDescription.get("class"));

        PropertyConstraintDescription nameDescription = wineDescription.get("name");
        assertEquals(String.class, nameDescription.getJavaType());
        assertTrue(nameDescription.isRequired()); // Retrieved from database
        assertEquals(Integer.valueOf(6), nameDescription.getMinimumLength()); // Retrieved from @Length
        assertEquals(Integer.valueOf(6), nameDescription.getMaximumLength()); // Merged @Length and database
        
        PropertyConstraintDescription primitiveDescription = wineDescription.get("primitive");
        assertEquals(int.class, primitiveDescription.getJavaType());
        assertTrue(primitiveDescription.getTypes().contains("number"));
    }

    @Test
    public void testCustomEnhancer() {
        Map<String, PropertyConstraintDescription> wineDescription = service.describe(Wine.class);
        
        assertTrue(wineDescription.get("country").getTypes().contains("reference"));
    }
    
    @Test
    public void testDescribeAll() {
        assertTrue(service.describeAll().isEmpty());
        
        service.registerAllWithAnnotation(ConstraintsTestConfig.class, Entity.class);

        Map<String, Map<String, PropertyConstraintDescription>> constraints  = service.describeAll();
        assertFalse(constraints.isEmpty());

        // Testing the Person
        Map<String, PropertyConstraintDescription> person = constraints.get("Person");

        PropertyConstraintDescription name = person.get("name");
        assertEquals(255, name.getMaximumLength().longValue());
        assertTrue(name.isRequired());

        PropertyConstraintDescription city = person.get("contact.address.city");
        assertEquals(255, city.getMaximumLength().longValue());
        assertTrue(city.isRequired());

        PropertyConstraintDescription streetAndNumber = person.get("contact.address.streetAndNumber");
        assertEquals(255, streetAndNumber.getMaximumLength().longValue());
        assertTrue(streetAndNumber.isRequired());

        // Testing the Car
        Map<String, PropertyConstraintDescription> car = constraints.get("Car");

        PropertyConstraintDescription licenseNumber = car.get("licenseNumber");
        assertEquals(6, licenseNumber.getMaximumLength().longValue());
        assertTrue(licenseNumber.isRequired());

        PropertyConstraintDescription price = car.get("price");
        assertEquals(6, price.getMaximumLength().longValue());
        assertFalse(price.isRequired());
        assertEquals(2, price.getFractionLength().longValue());
        assertEquals(10, price.getRadix().longValue());

        PropertyConstraintDescription active = car.get("active");
        assertFalse(active.isRequired());
    }

    @Configuration
    public static class CustomConstraintsConfig extends DatabaseConstraintsConfigurer {
        
        @Override
        public void configureConstraintDescriptor(BeanConstraintDescriptor descriptor) {
            descriptor.register(new AnnotationPropertyTypeEnhancer(ManyToOne.class, "reference"));
        }
        
    }

}
