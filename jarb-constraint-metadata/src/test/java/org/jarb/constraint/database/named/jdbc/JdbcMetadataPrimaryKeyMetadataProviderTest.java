package org.jarb.constraint.database.named.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import javax.sql.DataSource;

import org.jarb.constraint.database.named.NamedConstraintMetadata;
import org.jarb.constraint.database.named.NamedConstraintType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class JdbcMetadataPrimaryKeyMetadataProviderTest {
    private Collection<NamedConstraintMetadata> namedConstraintMetadataSet;

    @Autowired
    private DataSource dataSource;

    @Before
    public void fetchMetadata() {
        JdbcMetadataPrimaryKeyMetadataProvider constraintsProvider = new JdbcMetadataPrimaryKeyMetadataProvider(dataSource);
        namedConstraintMetadataSet = constraintsProvider.all();
    }

    @Test
    public void testPrimaryKey() {
        NamedConstraintMetadata metadata = findNamedConstraint("pk_cars_id");
        assertNotNull(metadata);
        assertEquals("PK_CARS_ID", metadata.getName());
        assertEquals(NamedConstraintType.PRIMARY_KEY, metadata.getType());
    }

    private NamedConstraintMetadata findNamedConstraint(String constraintName) {
        for (NamedConstraintMetadata namedConstraintMetadata : namedConstraintMetadataSet) {
            if (namedConstraintMetadata.getName().equalsIgnoreCase(constraintName)) {
                return namedConstraintMetadata;
            }
        }
        return null;
    }
}
