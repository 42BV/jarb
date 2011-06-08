package org.jarb.constraint.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jarb.constraint.MutablePropertyConstraintDescription;
import org.jarb.constraint.database.column.EntityAwareColumnMetadataRepository;
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
    private EntityAwareColumnMetadataRepository columnMetadataRepository;

    @Before
    public void setUp() {
        enhancer = new DatabasePropertyConstraintDescriptionEnhancer(columnMetadataRepository);
    }

    /**
     * Database metadata should be enhancing the property description.
     */
    @Test
    public void testEnhance() {
        MutablePropertyConstraintDescription<String> licenseNumberDescription = new MutablePropertyConstraintDescription<String>("licenseNumber", String.class);
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
        MutablePropertyConstraintDescription<Long> idDescription = new MutablePropertyConstraintDescription<Long>("id", Long.class);
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
        MutablePropertyConstraintDescription<String> unmappedPropertyDescription = new MutablePropertyConstraintDescription<String>("unmappedProperty", String.class);
        unmappedPropertyDescription = enhancer.enhance(unmappedPropertyDescription, Car.class);
        assertNull(unmappedPropertyDescription.getMaximumLength());
    }
    
    /**
     * Properties that cannot be mapped to a column should be skipped.
     * Unknown properties can never be mapped to a column.
     */
    @Test
    public void testSkipUnmappedProperty() {
        MutablePropertyConstraintDescription<String> unknownPropertyDescription = new MutablePropertyConstraintDescription<String>("unknownProperty", String.class);
        unknownPropertyDescription = enhancer.enhance(unknownPropertyDescription, Car.class);
        assertNull(unknownPropertyDescription.getMaximumLength());
    }

    /**
     * Beans that cannot be mapped to a table should be skipped.
     * Our bean cannot be mapped because it has no @Table or @Entity.
     */
    @Test
    public void testSkipUnmappedBeans() {
        MutablePropertyConstraintDescription<String> nameDescription = new MutablePropertyConstraintDescription<String>("name", String.class);
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
