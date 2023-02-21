package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.Application;
import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.constraint.metadata.database.BeanMetadataRepository;
import nl._42.jarb.domain.Wine;
import nl._42.jarb.utils.bean.PropertyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
public class DatabasePropertyConstraintEnhancerTest {
    
    private DatabasePropertyConstraintEnhancer enhancer;

    @Autowired
    private BeanMetadataRepository beanMetadataRepository;

    @BeforeEach
    public void setUp() {
        enhancer = new DatabasePropertyConstraintEnhancer(beanMetadataRepository);
    }

    /**
     * Database meta-data should be enhancing the property description.
     */
    @Test
    public void testEnhance() {
        PropertyReference propertyReference = new PropertyReference(Wine.class, "name");
        PropertyConstraintDescription nameDescription = new PropertyConstraintDescription(propertyReference, String.class);
        enhancer.enhance(nameDescription);
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
        enhancer.enhance(idDescription);
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
        enhancer.enhance(unmappedPropertyDescription);
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
        enhancer.enhance(unknownPropertyDescription);
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
        enhancer.enhance(nameDescription);
        assertNull(nameDescription.getMaximumLength());
    }

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
