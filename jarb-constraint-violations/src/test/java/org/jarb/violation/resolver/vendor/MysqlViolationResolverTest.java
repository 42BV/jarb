package org.jarb.violation.resolver.vendor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.DatabaseConstraintViolationType;
import org.junit.Before;
import org.junit.Test;

public class MysqlViolationResolverTest {
    private MysqlViolationResolver resolver;

    @Before
    public void setUp() {
        resolver = new MysqlViolationResolver();
    }

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("Column 'name' cannot be null");
        assertEquals(DatabaseConstraintViolationType.NOT_NULL, violation.getViolationType());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testUnique() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("Duplicate entry 'Emp6' for key 'un_employees_first_name'");
        assertEquals(DatabaseConstraintViolationType.UNIQUE_KEY, violation.getViolationType());
        assertEquals("Emp6", violation.getValue());
        assertEquals("un_employees_first_name", violation.getConstraintName());
    }

    @Test
    public void testLength() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("Data truncation: Data too long for column 'first_name' at row 1");
        assertEquals(DatabaseConstraintViolationType.LENGTH_EXCEEDED, violation.getViolationType());
        assertEquals("first_name", violation.getColumnName());
    }

    @Test
    public void testType() {
        DatabaseConstraintViolation violation = resolver.resolveByMessage("Incorrect integer value: 'Project2' for column 'name' at row 1");
        assertEquals(DatabaseConstraintViolationType.INVALID_TYPE, violation.getViolationType());
        assertEquals("integer", violation.getExpectedType());
        assertEquals("Project2", violation.getValue());
        assertEquals("name", violation.getColumnName());
    }

    @Test
    public void testOther() {
        assertNull(resolver.resolveByMessage("unknown"));
    }

}
