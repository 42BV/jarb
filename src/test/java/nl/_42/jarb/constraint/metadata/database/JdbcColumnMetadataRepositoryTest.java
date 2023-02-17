package nl._42.jarb.constraint.metadata.database;

import nl._42.jarb.Application;
import nl._42.jarb.utils.orm.ColumnReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
public class JdbcColumnMetadataRepositoryTest {

    @Autowired
    private DataSource dataSource;

    private JdbcColumnMetadataRepository jdbcMetadataRepository;

    @BeforeEach
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
