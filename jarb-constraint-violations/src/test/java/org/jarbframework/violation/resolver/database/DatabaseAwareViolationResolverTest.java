package org.jarbframework.violation.resolver.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationType;
import org.jarbframework.violation.resolver.database.DatabaseType;
import org.jarbframework.violation.resolver.database.DatabaseTypeAwareViolationResolver;
import org.jarbframework.violation.resolver.database.DatabaseTypeResolver;
import org.jarbframework.violation.resolver.vendor.MysqlViolationResolver;
import org.junit.Before;
import org.junit.Test;

public class DatabaseAwareViolationResolverTest {
    private DatabaseTypeAwareViolationResolver resolver;

    @Before
    public void setUp() {
        DatabaseTypeResolver mysqlDatabaseResolver = new DatabaseTypeResolver() {

            @Override
            public DatabaseType resolve() {
                return DatabaseType.MYSQL;
            }

        };
        resolver = new DatabaseTypeAwareViolationResolver(mysqlDatabaseResolver);
    }

    /**
     * Resolve should get delegated to the database specific violation resolver.
     */
    @Test
    public void testDelegate() {
        resolver.registerResolver(DatabaseType.MYSQL, new MysqlViolationResolver());
        Throwable mysqlException = new SQLException("Column 'name' cannot be null");
        DatabaseConstraintViolation violation = resolver.resolve(mysqlException);
        assertNotNull(violation);
        assertEquals(DatabaseConstraintViolationType.NOT_NULL, violation.getViolationType());
        assertEquals("name", violation.getColumnName());
    }

    /**
     * Whenever there is no database specific resolver registered, return {@code null}.
     */
    @Test
    public void testUnknownDatabase() {
        // Note that we did not register a mysql specific violation resolver
        Throwable mysqlException = new SQLException("Column 'name' cannot be null");
        try {
            resolver.resolve(mysqlException);
            fail("Expected a runtime exception when no violation resolver is registered.");
        } catch (RuntimeException e) {
            assertEquals("No violation resolver has been registered for a 'MYSQL' database.", e.getMessage());
        }
    }
}
