package org.jarbframework.constraint.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.PropertyConstraintDescription;
import org.jarbframework.constraint.database.DatabaseConstraintRepository;
import org.jarbframework.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarbframework.constraint.domain.Car;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class DatabasePropertyConstraintDescriptionEnhancerTest {
    private DatabasePropertyConstraintDescriptionEnhancer enhancer;

    @Autowired
    private DatabaseConstraintRepository databaseConstraintRepository;

    @Before
    public void setUp() {
        enhancer = new DatabasePropertyConstraintDescriptionEnhancer(databaseConstraintRepository);
    }

    /**
     * Database meta-data should be enhancing the property description.
     */
    @Test
    public void testEnhance() {
        PropertyReference propertyReference = new PropertyReference(Car.class, "licenseNumber");
        PropertyConstraintDescription licenseNumberDescription = new PropertyConstraintDescription(propertyReference, String.class);
        licenseNumberDescription = enhancer.enhance(licenseNumberDescription);
        assertTrue(licenseNumberDescription.isRequired());
        assertEquals(Integer.valueOf(6), licenseNumberDescription.getMaximumLength());
        assertNull(licenseNumberDescription.getFractionLength());
        assertNull(licenseNumberDescription.getRadix());
    }

    /**
     * Properties are only required if they are not nullable and have no generatable value (auto-increment or default).
     */
    @Test
    public void testNotRequiredIfGeneratable() {
        PropertyReference propertyReference = new PropertyReference(Car.class, "id");
        PropertyConstraintDescription idDescription = new PropertyConstraintDescription(propertyReference, Long.class);
        idDescription = enhancer.enhance(idDescription);
        assertFalse(idDescription.isRequired());
    }

    /**
     * Properties without column meta-data should be skipped.
     * Our property has no meta-data because it only exists in the mapping,
     * not in the actual database.
     */
    @Test
    public void testSkipPropertyWithoutMetadata() {
        PropertyReference propertyReference = new PropertyReference(Car.class, "unmappedProperty");
        PropertyConstraintDescription unmappedPropertyDescription = new PropertyConstraintDescription(propertyReference, String.class);
        unmappedPropertyDescription = enhancer.enhance(unmappedPropertyDescription);
        assertNull(unmappedPropertyDescription.getMaximumLength());
    }

    /**
     * Properties that cannot be mapped to a column should be skipped.
     * Unknown properties can never be mapped to a column.
     */
    @Test
    public void testSkipUnmappedProperty() {
        PropertyReference propertyReference = new PropertyReference(Car.class, "unknownProperty");
        PropertyConstraintDescription unknownPropertyDescription = new PropertyConstraintDescription(propertyReference, String.class);
        unknownPropertyDescription = enhancer.enhance(unknownPropertyDescription);
        assertNull(unknownPropertyDescription.getMaximumLength());
    }

    /**
     * Beans that cannot be mapped to a table should be skipped.
     * Our bean cannot be mapped because it has no @Table or @Entity.
     */
    @Test
    public void testSkipUnmappedBeans() {
        PropertyReference propertyReference = new PropertyReference(NotAnEntity.class, "name");
        PropertyConstraintDescription nameDescription = new PropertyConstraintDescription(propertyReference, String.class);
        nameDescription = enhancer.enhance(nameDescription);
        assertNull(nameDescription.getMaximumLength());
    }

    // Testing class, represents a simple bean without JPA mapping
    public static class NotAnEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
