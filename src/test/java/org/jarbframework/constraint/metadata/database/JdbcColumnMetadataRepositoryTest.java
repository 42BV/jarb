package org.jarbframework.constraint.metadata.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.jarbframework.constraint.TestConstraintsConfig;
import org.jarbframework.utils.orm.ColumnReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(classes = TestConstraintsConfig.class)
public class JdbcColumnMetadataRepositoryTest {

    @Autowired
    private DataSource dataSource;

    private JdbcColumnMetadataRepository jdbcMetadataRepository;

    @Before
    public void setUp() {
        jdbcMetadataRepository = new JdbcColumnMetadataRepository(dataSource);
    }

    /**
     * Plain identifiers are matched as case insensitive, thus a lower cased 'cars' will be matched to our table: 'CARS'.
     */
    @Test
    public void testGetColumnMetaData() {
        ColumnReference reference = new ColumnReference("cars", "license_number");
        
        ColumnMetadata metadata = jdbcMetadataRepository.getMetadata(reference);
        assertEquals(reference, metadata.getColumnReference());
        assertTrue(metadata.isRequired());
        assertFalse(metadata.isAutoIncrement());
        assertEquals(Integer.valueOf(6), metadata.getMaximumLength());
        assertNull(metadata.getFractionLength()); // Has not been defined
    }

    /**
     * Quoted identifiers are matched case sensitive, meaning we have to specify the table and column name in uppercase.
     */
    @Test
    public void testGetQuotedColumnMetaData() {
        ColumnReference reference = new ColumnReference("\"CARS\"", "\"LICENSE_NUMBER\"");
        
        ColumnMetadata metadata = jdbcMetadataRepository.getMetadata(reference);
        assertEquals(reference, metadata.getColumnReference());
        assertTrue(metadata.isRequired());
        assertFalse(metadata.isAutoIncrement());
        assertEquals(Integer.valueOf(6), metadata.getMaximumLength());
        assertNull(metadata.getFractionLength()); // Has not been defined
    }

    /**
     * Whenever a quoted identifier has an invalid casing, no result will be found.
     */
    @Test
    public void testInvalidCasingWhenQuoted() {
        ColumnReference reference = new ColumnReference("\"cars\"", "\"license_number\"");
        
        assertNull(jdbcMetadataRepository.getMetadata(reference));
    }

}
