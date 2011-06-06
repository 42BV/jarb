package org.jarb.constraint.database.named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class JdbcNamedConstraintMetadataProviderTest {
    private Collection<NamedConstraintMetadata> namedConstraintMetadataSet;

    @Autowired
    private DataSource dataSource;

    @Before
    public void fetchMetadata() {
        JdbcNamedConstraintMetadataProvider constraintsProvider = new JdbcNamedConstraintMetadataProvider(dataSource);
        namedConstraintMetadataSet = constraintsProvider.all();
    }

    @Test
    public void testPrimaryKey() {
        NamedConstraintMetadata metadata = findNamedConstraint("pk_cars_id");
        assertNotNull(metadata);
        assertEquals("PK_CARS_ID", metadata.getName());
        assertEquals(NamedConstraintType.PRIMARY_KEY, metadata.getType());
    }

    // TODO: Figure out why HSQL converts this into SYS_IDX_UK_CARS_LICENSE_NUMBER_0041
    // TODO: Check if other databases also provide a different index name
    //    @Test
    //    public void testUniqueIndex() {
    //        NamedConstraintMetadata metadata = findNamedConstraint("uk_cars_license_number");
    //        assertNotNull(metadata);
    //        assertEquals("UK_CARS_LICENSE_NUMBER", metadata.getName());
    //        assertEquals(NamedConstraintType.UNIQUE_INDEX, metadata.getType());
    //    }

    // TODO: Figure out how to retrieve check information from the database

    private NamedConstraintMetadata findNamedConstraint(String constraintName) {
        for (NamedConstraintMetadata namedConstraintMetadata : namedConstraintMetadataSet) {
            if (namedConstraintMetadata.getName().equalsIgnoreCase(constraintName)) {
                return namedConstraintMetadata;
            }
        }
        return null;
    }
}
