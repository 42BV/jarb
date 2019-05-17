package nl._42.jarb.constraint.metadata;

import nl._42.jarb.constraint.ConstraintsTestConfig;
import nl._42.jarb.constraint.DatabaseConstraintsConfigurer;
import nl._42.jarb.constraint.domain.AwesomeCar;
import nl._42.jarb.constraint.domain.Car;
import nl._42.jarb.constraint.domain.Country;
import nl._42.jarb.constraint.domain.Person;
import nl._42.jarb.constraint.domain.Wine;
import nl._42.jarb.constraint.metadata.BeanConstraintServiceTest.CustomConstraintsConfig;
import nl._42.jarb.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import nl._42.jarb.constraint.metadata.factory.EntityFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.ManyToOne;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(classes = { ConstraintsTestConfig.class, CustomConstraintsConfig.class })
public class BeanConstraintServiceTest {

    @Autowired
    private BeanConstraintDescriptor descriptor;

    @Autowired
    private EntityFactory entityFactory;

    private BeanConstraintService service;
    
    @Before
    public void setUp() {
        service = new BeanConstraintService(descriptor);
    }

    @Test
    public void describe_all() {
        assertTrue(service.describeAll().isEmpty());

        service.registerClasses(entityFactory);

        Map<String, Map<String, PropertyConstraintDescription>> constraints = service.describeAll();
        assertFalse(constraints.isEmpty());

        assertEquals(Sets.newSet("Wine", "AwesomeCar", "Car", "Country", "Person"), constraints.keySet());
    }

    @Test
    public void describe_default() {
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
    public void describe_custom() {
        Map<String, PropertyConstraintDescription> wineDescription = service.describe(Wine.class);
        
        assertTrue(wineDescription.get("country").getTypes().contains("reference"));
    }

    @Test
    public void describe_bean() {
        // Testing the Car
        Map<String, PropertyConstraintDescription> properties = service.describe(Car.class);

        PropertyConstraintDescription licenseNumber = properties.get("licenseNumber");
        assertEquals(6, licenseNumber.getMaximumLength().longValue());
        assertTrue(licenseNumber.isRequired());

        PropertyConstraintDescription price = properties.get("price");
        assertEquals(6, price.getMaximumLength().longValue());
        assertFalse(price.isRequired());
        assertEquals(2, price.getFractionLength().longValue());
        assertEquals(10, price.getRadix().longValue());

        PropertyConstraintDescription active = properties.get("active");
        assertFalse(active.isRequired());
    }

    @Test
    public void describe_embeddable() {
        Map<String, PropertyConstraintDescription> properties = service.describe(Person.class);

        PropertyConstraintDescription name = properties.get("name");
        assertEquals(200, name.getMaximumLength().longValue());
        assertTrue(name.isRequired());

        PropertyConstraintDescription contact = properties.get("contact");
        assertNull(contact);

        PropertyConstraintDescription city = properties.get("contact.address.city");
        assertEquals(100, city.getMaximumLength().longValue());
        assertTrue(city.isRequired());

        PropertyConstraintDescription streetAndNumber = properties.get("contact.address.streetAndNumber");
        assertEquals(256, streetAndNumber.getMaximumLength().longValue());
        assertTrue(streetAndNumber.isRequired());
    }

    @Test
    public void describe_embeddable_collection() {
        Map<String, PropertyConstraintDescription> properties = service.describe(AwesomeCar.class);

        PropertyConstraintDescription inspections = properties.get("inspections");
        assertNull(inspections);

        PropertyConstraintDescription date = properties.get("inspections.date");
        assertEquals(Sets.newSet("date"), date.getTypes());
        assertTrue(date.isRequired());

        PropertyConstraintDescription remarks = properties.get("inspections.remarks");
        assertFalse(remarks.isRequired());
    }

    @Test
    public void describe_embeddable_map() {
        Map<String, PropertyConstraintDescription> properties = service.describe(AwesomeCar.class);

        PropertyConstraintDescription type = properties.get("components");
        assertTrue(type.isRequired());

        PropertyConstraintDescription name = properties.get("components.name");
        assertTrue(name.isRequired());

        PropertyConstraintDescription price = properties.get("components.price");
        assertTrue(price.isRequired());
    }

    @Configuration
    public static class CustomConstraintsConfig extends DatabaseConstraintsConfigurer {
        
        @Override
        public void configureConstraintDescriptor(BeanConstraintDescriptor descriptor) {
            descriptor.register(new AnnotationPropertyTypeEnhancer(ManyToOne.class, "reference"));
        }
        
    }

}
