package org.jarb.constraint.database.column;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class JdbcColumnMetadataProviderTest {
    private JdbcColumnMetadataProvider constraintsProvider;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        constraintsProvider = new JdbcColumnMetadataProvider(dataSource);
    }

    @Test
    public void testGetColumnConstraints() {
        List<ColumnMetadata> columnConstraints = constraintsProvider.all();
        ColumnMetadata licenseNumberConstraint = findColumnMetadata("CARS", "LICENSE_NUMBER", columnConstraints);
        assertEquals("PUBLIC", licenseNumberConstraint.getSchemaName());
        assertEquals("CARS", licenseNumberConstraint.getTableName());
        assertEquals("LICENSE_NUMBER", licenseNumberConstraint.getColumnName());
        assertTrue(licenseNumberConstraint.isRequired());
        assertFalse(licenseNumberConstraint.isAutoIncrement());
        assertEquals(Integer.valueOf(6), licenseNumberConstraint.getMaximumLength());
        assertNull(licenseNumberConstraint.getFractionLength()); // Has not been defined
    }

    /**
     * Filters the metadata for a specific column.
     * @param tableName table name
     * @param columnName column name
     * @param constraints all database column metadata
     * @return metadata for only the provided column
     */
    private ColumnMetadata findColumnMetadata(String tableName, String columnName, List<ColumnMetadata> constraints) {
        for (ColumnMetadata constraint : constraints) {
            if (constraint.getTableName().equals(tableName) && constraint.getColumnName().equals(columnName)) {
                return constraint;
            }
        }
        throw new AssertionError("Could not find meta data for '" + tableName + "' and '" + columnName + "'.");
    }

}
