package org.jarb.constraint.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jarb.constraint.MutablePropertyConstraintMetadata;
import org.jarb.constraint.domain.Car;
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
        MutablePropertyConstraintMetadata<String> licenseNumberDescription = new MutablePropertyConstraintMetadata<String>("licenseNumber", String.class);
        licenseNumberDescription = enhancer.enhance(licenseNumberDescription, Car.class);
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
        MutablePropertyConstraintMetadata<Long> idDescription = new MutablePropertyConstraintMetadata<Long>("id", Long.class);
        idDescription = enhancer.enhance(idDescription, Car.class);
        assertFalse(idDescription.isRequired());
    }

    /**
     * Properties without column metadata should be skipped.
     * Our property has no metadata because it only exists in the mapping,
     * not in the actual database.
     */
    @Test
    public void testSkipPropertyWithoutMetadata() {
        MutablePropertyConstraintMetadata<String> unmappedPropertyDescription = new MutablePropertyConstraintMetadata<String>("unmappedProperty", String.class);
        unmappedPropertyDescription = enhancer.enhance(unmappedPropertyDescription, Car.class);
        assertNull(unmappedPropertyDescription.getMaximumLength());
    }

    /**
     * Properties that cannot be mapped to a column should be skipped.
     * Unknown properties can never be mapped to a column.
     */
    @Test
    public void testSkipUnmappedProperty() {
        MutablePropertyConstraintMetadata<String> unknownPropertyDescription = new MutablePropertyConstraintMetadata<String>("unknownProperty", String.class);
        unknownPropertyDescription = enhancer.enhance(unknownPropertyDescription, Car.class);
        assertNull(unknownPropertyDescription.getMaximumLength());
    }

    /**
     * Beans that cannot be mapped to a table should be skipped.
     * Our bean cannot be mapped because it has no @Table or @Entity.
     */
    @Test
    public void testSkipUnmappedBeans() {
        MutablePropertyConstraintMetadata<String> nameDescription = new MutablePropertyConstraintMetadata<String>("name", String.class);
        nameDescription = enhancer.enhance(nameDescription, NotAnEntity.class);
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
