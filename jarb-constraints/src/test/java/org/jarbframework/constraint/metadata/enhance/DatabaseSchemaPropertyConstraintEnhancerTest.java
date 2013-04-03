package org.jarbframework.constraint.metadata.enhance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.database.ColumnMetadataRepository;
import org.jarbframework.constraint.metadata.domain.Wine;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.SchemaMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class DatabaseSchemaPropertyConstraintEnhancerTest {
    
    private DatabaseSchemaPropertyConstraintEnhancer propertyEnhancer;

    @Autowired
    private SchemaMapper schemaMapper;
    
    @Autowired
    private ColumnMetadataRepository columnMetadataRepository;

    @Before
    public void setUp() {
        propertyEnhancer = new DatabaseSchemaPropertyConstraintEnhancer(schemaMapper, columnMetadataRepository);
    }

    /**
     * Database meta-data should be enhancing the property description.
     */
    @Test
    public void testEnhance() {
        PropertyReference propertyReference = new PropertyReference(Wine.class, "name");
        PropertyConstraintDescription nameDescription = new PropertyConstraintDescription(propertyReference, String.class);
        nameDescription = propertyEnhancer.enhance(nameDescription);
        assertTrue(nameDescription.isRequired());
        assertEquals(Integer.valueOf(6), nameDescription.getMaximumLength());
        assertNull(nameDescription.getFractionLength());
        assertNull(nameDescription.getRadix());
    }

    /**
     * Properties are only required if they are not nullable and have no generatable value (auto-increment or default).
     */
    @Test
    public void testNotRequiredIfGeneratable() {
        PropertyReference propertyReference = new PropertyReference(Wine.class, "id");
        PropertyConstraintDescription idDescription = new PropertyConstraintDescription(propertyReference, Long.class);
        idDescription = propertyEnhancer.enhance(idDescription);
        assertFalse(idDescription.isRequired());
    }

    /**
     * Properties without column meta-data should be skipped.
     * Our property has no meta-data because it only exists in the mapping,
     * not in the actual database.
     */
    @Test
    public void testSkipPropertyWithoutMetadata() {
        PropertyReference propertyReference = new PropertyReference(Wine.class, "unmappedProperty");
        PropertyConstraintDescription unmappedPropertyDescription = new PropertyConstraintDescription(propertyReference, String.class);
        unmappedPropertyDescription = propertyEnhancer.enhance(unmappedPropertyDescription);
        assertNull(unmappedPropertyDescription.getMaximumLength());
    }

    /**
     * Properties that cannot be mapped to a column should be skipped.
     * Unknown properties can never be mapped to a column.
     */
    @Test
    public void testSkipUnmappedProperty() {
        PropertyReference propertyReference = new PropertyReference(Wine.class, "unknownProperty");
        PropertyConstraintDescription unknownPropertyDescription = new PropertyConstraintDescription(propertyReference, String.class);
        unknownPropertyDescription = propertyEnhancer.enhance(unknownPropertyDescription);
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
        nameDescription = propertyEnhancer.enhance(nameDescription);
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
