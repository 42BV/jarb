package org.jarb.violation.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.resolver.database.Database;
import org.jarb.violation.resolver.database.DatabaseResolver;
import org.jarb.violation.resolver.database.DatabaseSpecificConstraintViolationResolver;
import org.jarb.violation.resolver.vendor.MysqlConstraintViolationResolver;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link DatabaseSpecificConstraintViolationResolver}.
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class DatabaseSpecificConstraintViolationResolverTest {
    private DatabaseSpecificConstraintViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new DatabaseSpecificConstraintViolationResolver();
        resolver.setDatabaseResolver(new DatabaseResolver() {

            @Override
            public Database resolve() {
                return Database.MYSQL;
            }

        });
    }

    /**
     * Resolve should get delegated to the database specific violation resolver.
     */
    @Test
    public void testDelegate() {
        resolver.register(Database.MYSQL, new MysqlConstraintViolationResolver());
        Throwable mysqlException = new SQLException("Column 'name' cannot be null");
        ConstraintViolation violation = resolver.resolve(mysqlException);
        assertNotNull(violation);
        assertEquals(ConstraintViolationType.CANNOT_BE_NULL, violation.getType());
        assertEquals("name", violation.getColumnName());
    }

    /**
     * Whenever there is no database specific resolver registered, return {@code null}.
     */
    @Test
    public void testUnknownDatabase() {
        Throwable mysqlException = new SQLException("Column 'name' cannot be null");
        try {
            resolver.resolve(mysqlException);
            fail("Expected a runtime exception when no violation resolver is registered.");
        } catch (RuntimeException e) {
            assertEquals("No violation resolver has been registered for a 'MYSQL' database.", e.getMessage());
        }
    }
}
