package org.jarbframework.constraint.violation.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;

import org.jarbframework.constraint.violation.resolver.ExceptionFinder;
import org.junit.Test;

public class ExceptionFinderTest {

    @Test
    public void testFindFirstSqlException() {
        SQLException exception = new SQLException();
        assertEquals(exception, ExceptionFinder.findFirstException(exception, SQLException.class));
    }

    @Test
    public void testFindFirstSqlExceptionInHierarchy() {
        SQLException exception = new SQLException();
        RuntimeException wrapper = new RuntimeException(exception);
        assertEquals(exception, ExceptionFinder.findFirstException(wrapper, SQLException.class));
    }

    @Test
    public void testCannotFindSqlException() {
        RuntimeException exception = new RuntimeException();
        assertNull(ExceptionFinder.findFirstException(exception, SQLException.class));
    }

    @Test
    public void testGetRootCause() {
        SQLException exception = new SQLException();
        assertEquals(exception, ExceptionFinder.getRootCause(exception));
    }

    @Test
    public void testGetRootCauseInHierarchy() {
        SQLException exception = new SQLException();
        RuntimeException wrapper = new RuntimeException(exception);
        assertEquals(exception, ExceptionFinder.getRootCause(wrapper));
    }

}
